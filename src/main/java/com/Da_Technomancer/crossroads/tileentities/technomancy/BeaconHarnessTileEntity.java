package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@ObjectHolder(Crossroads.MODID)
public class BeaconHarnessTileEntity extends BeamRenderTE implements IFluxLink{

	@ObjectHolder("beacon_harness")
	private static TileEntityType<BeaconHarnessTileEntity> type = null;

	public static final int FLUX_GEN = 4;
	public float angle = 0;//Used for rendering. Client side only

	private HashSet<BlockPos> links = new HashSet<>(1);
	private boolean running;
	private int cycles;
	private int flux = 0;

	public BeaconHarnessTileEntity(){
		super(type);
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		FluxUtil.addFluxInfo(chat, this, running ? 0 : FLUX_GEN);
	}

	@Override
	public void tick(){
		super.tick();

		if(world.isRemote || world.getGameTime() % FluxUtil.FLUX_TIME != 1){
			return;
		}

		//Handle flux
		FluxUtil.performTransfer(this, links);
		FluxUtil.checkFluxOverload(this);
	}

	private boolean invalid(Color col, @Nullable BeamUnit last){
		if(last == null || last.getVoid() != 0 || (col.getRed() != 0 && last.getEnergy() != 0) || (col.getGreen() != 0 && last.getPotential() != 0) || (col.getBlue() != 0 && last.getStability() != 0)){
			return true;
		}

		return positionInvalid();
	}

	private boolean positionInvalid(){
		BlockPos checkPos = pos;
		for(int y = 0; y < 4; y++){
			checkPos = checkPos.up(1);
			BlockState state = world.getBlockState(checkPos);
			if(!state.isAir(world, checkPos) && !state.isBeaconBase(world, checkPos, checkPos.up())){//The position passed for beaconPos (third arg) will be most likely wrong, but the argument is currently unused by all known implementations
				return true;
			}
		}

		return world.getBlockState(pos.up(5)).getBlock() != Blocks.BEACON;
	}

	public void trigger(){
		if(!running && !positionInvalid()){
			running = true;
			CrossroadsPackets.sendPacketAround(world, pos, new SendIntToClient((byte) 1, BeamManager.toPacket(new BeamUnit(3, 3, 3, 0), 2), pos));//Add a beacon beam
		}
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putBoolean("run", running);
		for(BlockPos linked : links){
			nbt.putLong("link", linked.toLong());
		}
		return nbt;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putBoolean("run", running);
		nbt.putInt("cycle", cycles);
		nbt.putInt("flux", flux);
		for(BlockPos linked : links){
			nbt.putLong("link", linked.toLong());
		}
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		running = nbt.getBoolean("run");
		cycles = nbt.getInt("cycle");
		flux = nbt.getInt("flux");
		if(nbt.contains("link")){
			links.add(BlockPos.fromLong(nbt.getLong("link")));
		}else{
			links.clear();
		}
	}

	@Override
	protected void doEmit(BeamUnit toEmit){
		if(running){
			++cycles;
			cycles %= 120;
			Color col = Color.getHSBColor(((float) cycles) / 120F, 1, 1);
			if(!(cycles < 0 || cycles % 40 < 8) && invalid(col, toEmit)){//Don't check color during a safety period
				running = false;
				CrossroadsPackets.sendPacketAround(world, pos, new SendIntToClient((byte) 1, 0, pos));//Wipe the beacon beam
				cycles = -9;//Easy way of adding a startup cooldown

				if(beamer[0].emit(BeamUnit.EMPTY, world)){
					refreshBeam(0);
				}
				return;
			}
			if(cycles >= 0){
				BeamUnit out = new BeamUnit(col.getRed(), col.getGreen(), col.getBlue(), 0);
				out = out.mult(512D / ((double) out.getPower()), false);

				beamer[0].emit(out, world);
				refreshBeam(1);//Assume the beam changed as the color constantly cycles
				prevMag[0] = out;
				addFlux(FLUX_GEN);
				markDirty();
			}
		}
	}

	@Override
	protected boolean[] inputSides(){
		return new boolean[] {false, false, true, true, true, true};
	}

	@Override
	protected boolean[] outputSides(){
		return new boolean[] {true, false, false, false, false, false};
	}

	@Override
	public Set<BlockPos> getLinks(){
		return links;
	}

	@Override
	public int getFlux(){
		return flux;
	}

	@Override
	public void setFlux(int newFlux){
		flux = newFlux;
		markDirty();
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity serverPlayerEntity){
		if(identifier == LINK_PACKET_ID){
			links.add(BlockPos.fromLong(message));
			markDirty();
		}else if(identifier == CLEAR_PACKET_ID){
			links.clear();
			markDirty();
		}
	}
}
