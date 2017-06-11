package com.Da_Technomancer.crossroads.tileentities.rotary;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.API.packets.IDoubleReceiver;
import com.Da_Technomancer.crossroads.API.packets.IStringReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDoubleToClient;
import com.Da_Technomancer.crossroads.API.packets.SendStringToClient;
import com.Da_Technomancer.crossroads.API.rotary.DefaultAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class LargeGearMasterTileEntity extends TileEntity implements IDoubleReceiver, ITickable, IStringReceiver{

	private boolean valid = false;
	private GearTypes type;
	private double[] motionData = new double[4];
	private double[] physData = new double[2];
	private boolean borken = false;
	/**
	 * 0: angle, 1: clientW
	 */
	private double[] angleW = new double[2];

	public void initSetup(GearTypes typ){
		type = typ;

		if(!world.isRemote){
			SendStringToClient msg = new SendStringToClient("memb", type == null ? "" : type.name(), pos);
			ModPackets.network.sendToAllAround(msg, new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}

		physData[0] = type == null ? 0 : MiscOp.betterRound(type.getDensity() * 4.5D, 2);
		physData[1] = physData[0] * 1.125D;
		//1.125 because r*r/2 so 1.5*1.5/2
	}

	public GearTypes getMember(){
		//IRON is returned instead of null to prevent edge case crashes.
		return type == null ? GearTypes.IRON : type;
	}

	private static final AxisAlignedBB RENDER_BOX = new AxisAlignedBB(-1, -1, -1, 2, 2, 2);

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return RENDER_BOX.offset(pos);
	}

	public void breakGroup(EnumFacing side, boolean drop){
		if(borken){
			return;
		}
		borken = true;
		for(int i = -1; i < 2; ++i){
			for(int j = -1; j < 2; ++j){
				world.setBlockToAir(pos.offset(side.getAxis() == Axis.X ? EnumFacing.UP : EnumFacing.EAST, i).offset(side.getAxis() == Axis.Z ? EnumFacing.UP : EnumFacing.NORTH, j));
			}
		}
		if(drop){
			world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(GearFactory.LARGE_GEARS.get(type), 1)));
		}
	}

	@Override
	public void update(){
		if(world.isRemote){
			if(angleW[1] == Double.POSITIVE_INFINITY){
				angleW[0] = 0;
			}else if(angleW[1] == Double.NEGATIVE_INFINITY){
				angleW[0] = 22.5;
			}else{
				angleW[0] += angleW[1] * 9D / Math.PI;
			}
		}

		valid = true;

		if(!world.isRemote){
			sendWPacket();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		// motionData
		NBTTagCompound innerMot = nbt.getCompoundTag("motionData");
		for(int j = 0; j < 4; j++){
			this.motionData[j] = (innerMot.hasKey(j + "motion")) ? innerMot.getDouble(j + "motion") : 0;
		}
		// member
		type = nbt.hasKey("memb") ? GearTypes.valueOf(nbt.getString("memb")) : null;
		valid = true;
		physData[0] = type == null ? 0 : MiscOp.betterRound(type.getDensity() * 4.5D, 2);
		physData[1] = physData[0] * 1.125D;
		//1.125 because r*r/2 so 1.5*1.5/2
		
		angleW[1] = nbt.getDouble("clientW");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		// motionData
		NBTTagCompound motionTags = new NBTTagCompound();
		for(int j = 0; j < 3; j++){
			if(motionData[j] != 0)
				motionTags.setDouble(j + "motion", motionData[j]);
		}
		nbt.setTag("motionData", motionTags);

		// member
		if(type != null){
			nbt.setString("memb", type.name());
		}

		nbt.setBoolean("new", true);
		nbt.setDouble("clientW", angleW[1]);
		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		if(type != null){
			nbt.setString("memb", type.name());
		}
		nbt.setBoolean("new", true);
		nbt.setDouble("clientW", angleW[1]);
		return nbt;
	}

	private static final int tiers = ModConfig.speedTiers.getInt() * 3;

	@Override
	public void receiveDouble(String context, double message){
		if(context.equals("w")){
			angleW[1] = message;
		}
	}

	private void sendWPacket(){
		boolean flag = false;
		if(angleW[1] == Double.POSITIVE_INFINITY || angleW[1] == Double.NEGATIVE_INFINITY){
			flag = true;
		}else if(MiscOp.tiersRound(motionData[0], tiers) != angleW[1]){
			flag = true;
			angleW[1] = MiscOp.tiersRound(motionData[0], tiers);
		}

		if(flag){
			SendDoubleToClient msg = new SendDoubleToClient("w", angleW[1], pos);
			ModPackets.network.sendToAllAround(msg, new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));

			if(angleW[1] == Double.POSITIVE_INFINITY || angleW[1] == Double.NEGATIVE_INFINITY){
				angleW[1] = 0;
			}
		}
	}

	@Override
	public void receiveString(String context, String message, EntityPlayerMP player){
		if(context.equals("memb")){
			type = message.equals("") ? null : GearTypes.valueOf(message);
		}
	}

	private final AxleHandler handlerMain = new AxleHandler();

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing == world.getBlockState(pos).getValue(Properties.FACING)){
			return type != null;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing == world.getBlockState(pos).getValue(Properties.FACING)){
			return (T) handlerMain;
		}
		return super.getCapability(capability, facing);
	}

	private class AxleHandler implements IAxleHandler{

		private byte updateKey;
		private double rotRatio;

		@Override
		public double[] getMotionData(){
			return motionData;
		}

		@Override
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
			if(type == null || !valid){
				return;
			}

			EnumFacing sid = world.getBlockState(pos).getValue(Properties.FACING);

			if(lastRadius != 0){
				rotRatioIn *= -lastRadius / 1.5D;
			}else if(rotRatioIn == 0){
				rotRatioIn = 1D;
			}else if(sid.getAxisDirection() == AxisDirection.POSITIVE){
				rotRatioIn *= -1D;
			}

			//If true, this has already been checked.
			if(key == updateKey){
				//If true, there is rotation conflict.
				if(rotRatio != rotRatioIn){
					masterIn.lock();
				}
				return;
			}

			if(masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn;

			if(updateKey == 0){
				resetAngle();
			}
			updateKey = key;

			TileEntity connectTE = world.getTileEntity(pos.offset(sid));
			if(connectTE != null){
				if(connectTE.hasCapability(Capabilities.AXIS_HANDLER_CAPABILITY, sid.getOpposite())){
					connectTE.getCapability(Capabilities.AXIS_HANDLER_CAPABILITY, sid.getOpposite()).trigger(masterIn, key);
				}
				if(connectTE.hasCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, sid.getOpposite())){
					masterIn.addAxisToList(connectTE.getCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, sid.getOpposite()), sid.getOpposite());
				}

				if(connectTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, sid.getOpposite())){
					connectTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, sid.getOpposite()).propogate(masterIn, key, sid.getAxisDirection() == AxisDirection.POSITIVE ? -rotRatio : rotRatio, 0);
				}
			}

			for(EnumFacing sideN : EnumFacing.values()){
				if(sideN != sid && sideN != sid.getOpposite()){
					// Adjacent gears
					TileEntity adjTE = world.getTileEntity(pos.offset(sideN, 2));
					if(adjTE != null && adjTE.hasCapability(Capabilities.COG_HANDLER_CAPABILITY, sid)){
						adjTE.getCapability(Capabilities.COG_HANDLER_CAPABILITY, sid).connect(masterIn, key, rotRatio, 1.5D);
					}

					// Diagonal gears
					TileEntity diagTE = world.getTileEntity(pos.offset(sideN, 2).offset(sid));
					if(diagTE != null && diagTE.hasCapability(Capabilities.COG_HANDLER_CAPABILITY, sideN.getOpposite()) && DefaultAxleHandler.canConnectThrough(world, pos.offset(sideN, 2), sideN.getOpposite(), sid) && diagTE != null){
						diagTE.getCapability(Capabilities.COG_HANDLER_CAPABILITY, sideN.getOpposite()).connect(masterIn, key, rotRatio, 1.5D);
					}
				}
			}
		}

		@Override
		public double[] getPhysData(){
			return physData;
		}

		@Override
		public void resetAngle(){
			if(!world.isRemote){
				angleW[1] = (Math.signum(rotRatio) == -1 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
			}
		}

		@Override
		public double getAngle(){
			return angleW[0];
		}

		@Override
		public void addEnergy(double energy, boolean allowInvert, boolean absolute){
			if(allowInvert && absolute){
				motionData[1] += energy;
			}else if(allowInvert){
				motionData[1] += energy * Math.signum(motionData[1]);
			}else if(absolute){
				int sign = (int) Math.signum(motionData[1]);
				motionData[1] += energy;
				if(sign != 0 && Math.signum(motionData[1]) != sign){
					motionData[1] = 0;
				}
			}else{
				int sign = (int) Math.signum(motionData[1]);
				motionData[1] += energy * ((double) sign);
				if(Math.signum(motionData[1]) != sign){
					motionData[1] = 0;
				}
			}
			markDirty();
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}

		@Override
		public void markChanged(){
			markDirty();
		}
	}
}
