package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxHandler;
import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.tileentities.rotary.MasterAxisTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class FluxStabilizerMechanicalTileEntity extends MasterAxisTileEntity implements ILinkTE, IFluxHandler, IInfoTE{

	private int flux = 0;
	private static final double maxChange = 100;
	private boolean stable = true;
	private static final double energyScale = 100;
	private int prevEfficiency = 0;

	@Override
	protected EnumFacing getFacing(){
		return EnumFacing.UP;
	}

	@Override
	public void update(){
		if(world.getTotalWorldTime() % FluxUtil.FLUX_TIME == FluxUtil.FLUX_TIME - 1){
			//Add the energy a tick in advance
			if(validRotary()){
				double speedSign = Math.signum(rotaryMembers.get(0).getMotionData()[0]);
				if(speedSign == 0){
					rotaryMembers.get(0).getMotionData()[1] += (2D * Math.random() - 1D) * maxChange;
				}else{
					rotaryMembers.get(0).getMotionData()[1] += Math.random() * maxChange * speedSign;
				}
				markDirty();
			}
		}else if(world.getTotalWorldTime() % FluxUtil.FLUX_TIME == 0){
			if(validRotary()){
				prevEfficiency = 8 - (int) Math.min(Math.abs(sumEnergy) / energyScale, 8);
				flux -= prevEfficiency;
				flux = Math.max(0, flux);
				markDirty();
			}
			stable = true;
		}

		stable &= !locked;

		super.update();
	}

	/**
	 * @return Whether the current rotary setup allows for draining flux
	 */
	private boolean validRotary(){
		if(!stable || locked || rotaryMembers.isEmpty()){
			return false;
		}

		for(IAxleHandler axle : rotaryMembers){
			if(axle.getMoInertia() != 0){
				return true;
			}
		}
		return false;
	}

	@Override
	public int getCapacity(){
		return 64;
	}

	@Override
	public int getFlux(){
		return flux;
	}

	@Override
	public boolean isFluxEmitter(){
		return false;
	}

	@Override
	public boolean isFluxReceiver(){
		return true;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable EntityPlayerMP sendingPlayer){

	}

	@Override
	public int addFlux(int fluxIn){
		flux += fluxIn;
		markDirty();
		return flux;
	}

	@Override
	public int canAccept(){
		return getCapacity() - flux;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("flux", flux);
		nbt.setBoolean("stable", stable);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		flux = nbt.getInteger("flux");
		stable = nbt.getBoolean("stable");
	}

	@Override
	public TileEntity getTE(){
		return this;
	}

	@Override
	public boolean canLink(ILinkTE otherTE){
		return false;
	}

	@Override
	public ArrayList<BlockPos> getLinks(){
		return new ArrayList<>(0);
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		chat.add("Efficiency: " + prevEfficiency + "/8 flux/cycle");
	}
}