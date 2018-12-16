package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxHandler;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;

public class BeaconHarnessTileEntity extends BeamRenderTE implements ILinkTE, IFluxHandler, IInfoTE{

	public float[] renderOld = new float[8];
	public float[] renderNew = new float[8];
	public boolean renderSet = false;
	private int flux;
	private ArrayList<BlockPos> links = new ArrayList<>(getMaxLinks());

	private boolean running;
	private int cycles;

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		for(BlockPos link : links){
			chat.add("Linked Position: X=" + (pos.getX() + link.getX()) + " Y=" + (pos.getY() + link.getY()) + " Z=" + (pos.getZ() + link.getZ()));
		}
	}

	@Override
	public void update(){
		super.update();

		if(world.isRemote || flux == 0 || world.getTotalWorldTime() % FluxUtil.FLUX_TIME != 0){
			return;
		}

		IFluxHandler[] targets = new IFluxHandler[getMaxLinks()];
		int targetCount = 0;
		int moved = 0;

		for(BlockPos link : links){
			BlockPos endPos = pos.add(link);
			TileEntity te = world.getTileEntity(endPos);
			if(te instanceof IFluxHandler && ((IFluxHandler) te).isFluxReceiver() && ((IFluxHandler) te).canReceiveFlux()){
				targets[targetCount] = (IFluxHandler) te;
				targetCount++;
			}
		}

		for(int i = 0; i < targetCount; i++){
			moved += flux / targetCount;
			targets[i].addFlux(flux / targetCount);
			FluxUtil.renderFlux(world, pos, ((TileEntity) targets[i]).getPos(), flux / targetCount);
		}
		flux -= moved;
		markDirty();
	}

	private boolean invalid(Color col, boolean colorSafe, @Nullable BeamUnit last){
		if(!colorSafe){
			if(last == null || last.getVoid() != 0 || (col.getRed() != 0 && last.getEnergy() != 0) || (col.getGreen() != 0 && last.getPotential() != 0) || (col.getBlue() != 0 && last.getStability() != 0)){
				return true;
			}
		}

		return world.getBlockState(pos.offset(EnumFacing.DOWN, 2)).getBlock() != Blocks.BEACON || !world.isAirBlock(pos.offset(EnumFacing.DOWN, 1));
	}

	public void trigger(){
		if(!running && !invalid(null, true, null)){
			running = true;
			ModPackets.network.sendToAllAround(new SendIntToClient(0, BeamManager.toPacket(new BeamUnit(1, 1, 1, 0), 2), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setBoolean("run", running);
		return nbt;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("run", running);
		nbt.setInteger("cycle", cycles);
		nbt.setInteger("flux", flux);
		for(int i = 0; i < links.size(); i++){
			nbt.setLong("link" + i, links.get(i).toLong());
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		running = nbt.getBoolean("run");
		cycles = nbt.getInteger("cycle");
		flux = nbt.getInteger("flux");
		for(int i = 0; i < getMaxLinks(); i++){
			if(nbt.hasKey("link" + i)){
				links.add(BlockPos.fromLong(nbt.getLong("link" + i)));
			}
		}
	}

	@Override
	public TileEntity getTE(){
		return this;
	}

	@Override
	public boolean canLink(ILinkTE otherTE){
		return otherTE instanceof IFluxHandler && ((IFluxHandler) otherTE).isFluxReceiver();
	}

	@Override
	public ArrayList<BlockPos> getLinks(){
		return links;
	}

	@Override
	public boolean canReceiveFlux(){
		return false;
	}

	@Override
	public int getFlux(){
		return flux;
	}

	@Override
	public int getMaxLinks(){
		return 4;
	}

	@Override
	public int getCapacity(){
		return 64;
	}

	@Override
	public int addFlux(int fluxIn){
		flux += fluxIn;
		markDirty();
		if(flux > getCapacity()){
			world.destroyBlock(pos, false);
			world.createExplosion(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0, false);
			FluxUtil.fluxEvent(world, pos, 64);
		}
		return flux;
	}

	@Override
	public boolean isFluxEmitter(){
		return true;
	}

	@Override
	public boolean isFluxReceiver(){
		return false;
	}

	@Override
	protected void doEmit(BeamUnit toEmit){
		if(running){
			++cycles;
			cycles %= 120;
			Color col = Color.getHSBColor(((float) cycles) / 120F, 1, 1);
			if(invalid(col, cycles < 0 || cycles % 40 < 8, toEmit)){
				running = false;
				ModPackets.network.sendToAllAround(new SendIntToClient(0, 0, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				cycles = -9;

				if(beamer[1].emit(null, world)){
					refreshBeam(1);
				}
				return;
			}
			if(cycles >= 0){
				BeamUnit out = new BeamUnit(col.getRed(), col.getGreen(), col.getBlue(), 0);
				out = out.mult(512D / ((double) out.getPower()), false);

				beamer[1].emit(out, world);
				refreshBeam(1);//Assume the beam changed as the color constantly cycles
				prevMag[1] = out;
				addFlux(4);
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
		return new boolean[] {false, true, false, false, false, false};
	}
}
