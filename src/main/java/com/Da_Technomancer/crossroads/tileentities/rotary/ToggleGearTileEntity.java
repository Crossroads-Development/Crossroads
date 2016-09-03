package com.Da_Technomancer.crossroads.tileentities.rotary;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.API.packets.IDoubleReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDoubleToClient;
import com.Da_Technomancer.crossroads.API.rotary.IRotaryHandler;
import com.Da_Technomancer.crossroads.API.rotary.ITileMasterAxis;
import com.Da_Technomancer.crossroads.blocks.rotary.MasterAxis;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class ToggleGearTileEntity extends TileEntity implements ITickable, IDoubleReceiver{
	
	private GearTypes type;
	private double[] motionData = new double[4];
	private double[] physData = new double[] {.5D, 0, 0};
	private int key;
	private double angle;
	/**Normal, client*/
	private double[] Q = new double[2];
	private int compOut = 0;
	
	public ToggleGearTileEntity(){
		
	}
	
	public ToggleGearTileEntity(GearTypes type){
		this.type = type;
		physData[1] = type.getDensity() / 8D;
		physData[2] = type.getDensity() / 64D;
	}
	
	private final int tiers = MasterAxis.speedTiers.getInt();
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return (oldState.getBlock() != newState.getBlock());
	}
	
	@Override
	public void update(){
		if(getWorld().isRemote){
			if(Q[1] == Double.POSITIVE_INFINITY){
				angle = 0;
			}else if(Q[1] == Double.NEGATIVE_INFINITY){
				angle = 22.5;
			}else{
				// it's 18 / PI instead of 180 / PI because 20 ticks /
				// second, so 9 / PI, then * 2 because this is Q not w (Q =
				// r * w, r = .5).
				angle += Q[1] * 18D / Math.PI;
			}
		}

		if(!getWorld().isRemote){
			sendQPacket();
			if(compOut != ((int) Math.min((Math.abs(motionData[1] / physData[2])) * 15D, 15))){
				worldObj.updateComparatorOutputLevel(pos, this.blockType);
				compOut = ((int) Math.min((Math.abs(motionData[1] / physData[2])) * 15D, 15));
			}
		}
	}
	
	private void sendQPacket(){
		boolean flag = false;
		if(Q[1] == Double.POSITIVE_INFINITY || Q[1] == Double.NEGATIVE_INFINITY){
			flag = true;
		}else if(MiscOp.centerCeil(Q[0], tiers) * handler.keyType() != Q[1]){
			flag = true;
			Q[1] = MiscOp.centerCeil(Q[0], tiers) * handler.keyType();
		}

		if(flag){
			SendDoubleToClient msg = new SendDoubleToClient("Q", Q[1], this.getPos());
			ModPackets.network.sendToAllAround(msg, new TargetPoint(this.getWorld().provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 512));

			if(Q[1] == Double.POSITIVE_INFINITY || Q[1] == Double.NEGATIVE_INFINITY){
				Q[1] = 0;
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound){
		super.writeToNBT(compound);

		// motionData
		NBTTagCompound motionTags = new NBTTagCompound();
			for(int i = 0; i < 3; i++){
				if(motionData[i] != 0)
					motionTags.setDouble(i + "motion", motionData[i]);
		}
		compound.setTag("motionData", motionTags);

		// member
		if(type != null){
			compound.setString("type", type.name());
		}

		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound){
		super.readFromNBT(compound);

		// motionData
		NBTTagCompound innerMot = compound.getCompoundTag("motionData");
		for(int i = 0; i < 4; i++){
			this.motionData[i] = (innerMot.hasKey(i + "motion")) ? innerMot.getDouble(i + "motion") : 0;
		}

		//type
		this.type = compound.hasKey("type") ? GearTypes.valueOf(compound.getString("type")) : null;
		if(type != null){
			physData[1] = type.getDensity() / 8D;
			physData[2] = type.getDensity() / 64D;
		}
	}
	
	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		if(type != null){
			nbt.setString("type", type.name());
		}
		return nbt;
	}
	
	@Override
	public void receiveDouble(String context, double message){
		if(context.equals("Q")){
			Q[1] = message;
		}
	}
	
	private final Handler handler = new Handler();
	
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.ROTARY_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.DOWN) && worldObj.getBlockState(pos).getValue(Properties.REDSTONE_BOOL)){
			return true;
		}
		return super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.ROTARY_HANDLER_CAPABILITY && (facing == null || (facing == EnumFacing.DOWN && worldObj.getBlockState(pos).getValue(Properties.REDSTONE_BOOL)))){
			return (T) handler;
		}
		
		return super.getCapability(capability, facing);
	}
	
	private class Handler implements IRotaryHandler{

		@Override
		public double[] getMotionData(){
			return motionData;
		}

		@Override
		public void propogate(int keyIn, ITileMasterAxis masterIn){
			if(key * -1 == keyIn){
				// If true, then there is a direction conflict.
				masterIn.lock();
				return;
			}else if(key == keyIn){
				// If true, this has already been checked, and should do nothing
				return;
			}
			if(masterIn.addToList(this)){
				return;
			}

			if(key == 0){
				key = keyIn;
				resetAngle();
			}else{
				key = keyIn;
			}

			if(worldObj.getTileEntity(pos.offset(EnumFacing.DOWN)) instanceof ITileMasterAxis){
				((ITileMasterAxis) worldObj.getTileEntity(pos.offset(EnumFacing.DOWN))).trigger(keyIn, masterIn, EnumFacing.UP);
			}

			for(int i = 2; i < 6; ++i){
				EnumFacing facing = EnumFacing.getFront(i);
				// Adjacent gears
				if(getWorld().getTileEntity(pos.offset(facing)) != null && getWorld().getTileEntity(pos.offset(facing)).hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.DOWN)){
					getWorld().getTileEntity(pos.offset(facing)).getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.DOWN).propogate(keyIn * -1, masterIn);
				}

				// Diagonal gears
				if(!getWorld().getBlockState(pos.offset(facing)).getBlock().isNormalCube(getWorld().getBlockState(pos.offset(facing)), getWorld(), pos.offset(facing)) && getWorld().getTileEntity(pos.offset(facing).offset(EnumFacing.DOWN)) != null && getWorld().getTileEntity(pos.offset(facing).offset(EnumFacing.DOWN)).hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, facing.getOpposite())){
					getWorld().getTileEntity(pos.offset(facing).offset(EnumFacing.DOWN)).getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, facing.getOpposite()).propogate(keyIn * -1, masterIn);
				}
			}
		}

		@Override
		public double[] getPhysData(){
			return physData;
		}

		@Override
		public double keyType(){
			return MiscOp.posOrNeg(key);
		}

		@Override
		public void resetAngle(){
			if(!worldObj.isRemote){
				Q[1] = (keyType() == -1 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
			}
		}

		@Override
		public void setQ(double QIn){
			Q[0] = QIn;
		}

		@Override
		public double getAngle(){
			return angle;
		}

		/**This should never be called for this TileEntity*/
		@Override
		public void updateStates(){
			
		}

		@Override
		public void addEnergy(double energy, boolean allowInvert, boolean absolute){
			if(allowInvert && absolute){
				motionData[1] += energy;
			}else if(allowInvert){
				motionData[1] += energy * MiscOp.posOrNeg(motionData[1]);
			}else if(absolute){
				int sign = (int) MiscOp.posOrNeg(motionData[1]);
				motionData[1] += energy;
				if(sign != 0 && MiscOp.posOrNeg(motionData[1]) != sign){
					motionData[1] = 0;
				}
			}else{
				int sign = (int) MiscOp.posOrNeg(motionData[1]);
				motionData[1] += energy * ((double) sign);
				if(MiscOp.posOrNeg(motionData[1]) != sign){
					motionData[1] = 0;
				}
			}
		}

		/**
		 * This shouldn't be called at all for this TileEntity
		 */
		@Override
		public void setMember(GearTypes membIn){
			
		}

		@Override
		public GearTypes getMember(){
			return type;
		}
		
	}
}
