package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
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
import net.minecraft.util.Direction;
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
	public static TileEntityType<BeaconHarnessTileEntity> type = null;

	public static final int FLUX_GEN = 4;
	private static final int LOOP_TIME = 120;//Time to make one full rotation around the color wheel in cycles. Must be a multiple of 3
	private static final int SAFETY_BUFFER = 8;//Duration of the switchover period in cycles
	private static final int POWER = 512;//Power of the created beam

	private boolean running;
	private int cycles;
	//Flux related fields
	private HashSet<BlockPos> links = new HashSet<>(1);
	private int flux = 0;
	private int fluxToTrans = 0;

	public BeaconHarnessTileEntity(){
		super(type);
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		FluxUtil.addFluxInfo(chat, this, running ? FLUX_GEN : 0);
	}

	@Override
	public void tick(){
		super.tick();

		// Actual beam production is in the emit() method

		if(world.isRemote){
			return;
		}

		//Handle flux
		long stage = world.getGameTime() % FluxUtil.FLUX_TIME;
		if(stage == 0 && flux != 0){
			fluxToTrans += flux;
			flux = 0;
			markDirty();
		}else if(stage == 1){
			flux += FluxUtil.performTransfer(this, links, fluxToTrans);
			fluxToTrans = 0;
			FluxUtil.checkFluxOverload(this);
		}
	}

	@Override
	public int getReadingFlux(){
		return FluxUtil.findReadingFlux(this, flux, fluxToTrans);
	}

	private boolean invalid(Color col, BeamUnit last){
		if(last.isEmpty() || last.getVoid() != 0 || (col.getRed() != 0 && last.getEnergy() != 0) || (col.getGreen() != 0 && last.getPotential() != 0) || (col.getBlue() != 0 && last.getStability() != 0)){
			return true;
		}

		return positionInvalid();
	}

	//Requires beneath a beacon, and all blocks between this and the beacon are legal beacon bases
	private boolean positionInvalid(){
		BlockPos.Mutable checkPos = new BlockPos.Mutable(pos);
		for(int y = 0; y < 5; y++){
			checkPos.move(Direction.UP);
			BlockState state = world.getBlockState(checkPos);
			if(state.getBlock() == Blocks.BEACON){
				return false;
			}
			if(!state.isBeaconBase(world, checkPos, checkPos)){//The position passed for beaconPos (third arg) will be most likely wrong, but the argument is currently unused by all known implementations
				return true;
			}
		}
		return true;
	}

	public void trigger(){
		if(!running && !positionInvalid()){
			running = true;
		}
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
//		nbt.putBoolean("run", running);
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
		nbt.putInt("flux_trans", fluxToTrans);
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
		fluxToTrans = nbt.getInt("flux_trans");
		if(nbt.contains("link")){
			links.add(BlockPos.fromLong(nbt.getLong("link")));
		}else{
			links.clear();
		}
	}

	@Override
	protected void doEmit(BeamUnit input){
		if(running){
			++cycles;
			cycles %= LOOP_TIME;
			//The color calculation takes advantage of the fact that the "color wheel" as most people know it is the slice of the HSB color cylinder with saturation=1. The outer rim is brightness=1. The angle is controlled by hue
			Color outColor = Color.getHSBColor(((float) cycles) / LOOP_TIME, 1, 1);
			if(cycles >= 0){
				//Don't check color during a safety period
				if(cycles % (LOOP_TIME / 3) >= SAFETY_BUFFER && invalid(outColor, input)){
					//Wrong input- shut down
					running = false;
					cycles = -11;//Easy way of adding a startup cooldown- 10 cycles

					if(beamer[0].emit(BeamUnit.EMPTY, world)){
						refreshBeam(0);
					}
				}else{
					BeamUnit out = new BeamUnit(outColor.getRed(), outColor.getGreen(), outColor.getBlue(), 0);
					out = out.mult(POWER / ((double) out.getPower()), false);

					beamer[0].emit(out, world);
					refreshBeam(0);//Assume the beam changed as the color constantly cycles
					prevMag[0] = out;
					addFlux(FLUX_GEN);
					markDirty();
				}
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
		if(flux != newFlux){
			flux = newFlux;
			markDirty();
		}
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
