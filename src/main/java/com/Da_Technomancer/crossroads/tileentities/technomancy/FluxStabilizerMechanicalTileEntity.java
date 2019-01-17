package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLongToClient;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.rotary.AxisTypes;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

public class FluxStabilizerMechanicalTileEntity extends AbstractFluxStabilizerTE{

	public static final double EFFICIENCY_SCALE = 0.25D;
	private static final Random RAND = new Random();//Due to seeding, we can't use the world random instance

	private double[] motData = new double[4];
	private double target = EFFICIENCY_SCALE;

	public FluxStabilizerMechanicalTileEntity(){
		super();
	}

	public FluxStabilizerMechanicalTileEntity(boolean crystal){
		super(crystal);
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		chat.add("Target speed: " + MiscUtil.betterRound(target, 1));
		super.addInfo(chat, player, side, hitX, hitY, hitZ);
		chat.add("Speed: " + MiscUtil.betterRound(motData[0], 3));
		chat.add("Energy: " + MiscUtil.betterRound(motData[1], 3));
		chat.add("Power: " + MiscUtil.betterRound(motData[2], 3));
		chat.add("I: 100, Rotation Ratio: " + MiscUtil.betterRound(axleHandler.rotRatio, 3));
	}

	@Override
	public void update(){
		super.update();

		if(!world.isRemote && world.getTotalWorldTime() % FluxUtil.FLUX_TIME == 0){
			int efficiency = 0;
			if(axleHandler.master != null && axleHandler.master.get() != null && axleHandler.master.get().getType() == AxisTypes.NORMAL){
				efficiency = Math.max(0, FluxUtil.getStabilizerLimit(crystal) - (int) (Math.abs(motData[0] - target) / EFFICIENCY_SCALE));
			}

			if(clientRunning ^ (efficiency != 0 && flux != 0)){
				clientRunning = !clientRunning;
				ModPackets.network.sendToAllAround(new SendLongToClient((byte) 4, clientRunning ? 1L : 0L, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}

			if(efficiency != 0 && flux != 0){
				markDirty();
			}
			flux = Math.max(0, flux - efficiency);

			//Adjust target
			if(world.getTotalWorldTime() % (FluxUtil.FLUX_TIME * 240) == 0){
				RAND.setSeed(world.getTotalWorldTime() ^ ((long) pos.getX() | ((long) pos.getZ()) << 32L));
				target = EFFICIENCY_SCALE * (RAND.nextInt(16) + 1);
				markDirty();
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		for(int i = 0; i < 4; i++){
			nbt.setDouble("mot_" + i, motData[i]);
		}
		nbt.setDouble("target", target);
		return nbt;
	}

	@Override public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		for(int i = 0; i < 4; i++){
			motData[i] = nbt.getDouble("mot_" + i);
		}
		target = nbt.getDouble("target");
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.AXLE_CAPABILITY && (facing == null || facing == EnumFacing.UP)){
			return (T) axleHandler;
		}
		if(capability == Capabilities.ADVANCED_REDSTONE_CAPABILITY){
			return (T) redsHandler;
		}
		return super.getCapability(capability, facing);
	}

	private final AxleHandler axleHandler = new AxleHandler();
	private final RedsHandler redsHandler = new RedsHandler();

	private class AxleHandler implements IAxleHandler{

		//Stored in a WeakReference to prevent memory leaks
		private WeakReference<IAxisHandler> master;
		private double rotRatio;
		private byte updateKey;

		@Override
		public double[] getMotionData(){
			return motData;
		}

		@Override
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius, boolean renderOffset){
			//If true, this has already been checked.
			if(key == updateKey || masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
			updateKey = key;
			master = new WeakReference<>(masterIn);
		}

		@Override
		public double getMoInertia(){
			return 100;
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}

		@Override
		public void markChanged(){
			markDirty();
		}

		@Override
		public boolean shouldManageAngle(){
			return false;
		}

		@Override
		public void disconnect(){
			master = null;
		}
	}

	private class RedsHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean measure){
			return measure ? target : 0;
		}
	}
}
