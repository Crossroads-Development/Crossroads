package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.effects.mechArm.*;
import com.Da_Technomancer.crossroads.API.packets.IDoubleReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDoubleToClient;
import com.Da_Technomancer.crossroads.API.redstone.RedstoneUtil;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.entity.EntityArmRidable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import java.util.UUID;

public class MechanicalArmTileEntity extends TileEntity implements ITickable, IDoubleReceiver{

	public static final double MAX_HEIGHT = 5;
	public static final double MAX_LENGTH = 5;

	private static final IMechArmEffect[] EFFECTS = {new MechArmPickupEntityEffect(), new MechArmPickupBlockEffect(), new MechArmPickupFromInvEffect(), new MechArmUseEffect(), new MechArmDepositEffect(), new MechArmDropEntityEffect(), new MechArmThrowEntityEffect(), new MechArmPickupOneFromInvEffect()};

	private double[][] motionData = new double[3][4];
	public double[] angle = {0, MAX_HEIGHT, MAX_LENGTH};
	/** A record of last tick's angles, for rendering movement animation & for release effect*/
	public double[] angleRecord = new double[3];
	/**Server side: A record of the last speeds sent to the render.*/
	private double[] lastSentAngle = new double[3];
	/**
	 * (redstone_red_side - 1)%effects_length corresponds to action type, which are:
	 * 0: Pickup entity, 1: Pickup block, 2: Pickup from inventory, 3: Use, 4: Deposit into inventory, 5: Drop entity, 6: Throw entity, 7: Pickup one from inventory.
	 * redstone_yellow_side % 6 corresponds to an (optional) EnumFacing. Only some action types (2, 3, 4, 7) vary based on EnumFacing.
	 */
	private int redstone = 0;
	public EntityArmRidable ridable;
	private UUID ridableID;

	private static final double PHYS_DATA = 0;
	private static final float CLIENT_SPEED_MARGIN = (float) ModConfig.speedPrecision.getDouble();

	@Override
	public void update(){
		if(world.getTotalWorldTime() % 2 == 0){
			System.arraycopy(angle, 0, angleRecord, 0, 3);

			if(!world.isRemote){
				angle[0] = -motionData[0][0];
				angle[1] = MAX_HEIGHT - Math.min(MAX_HEIGHT, Math.abs(motionData[1][0]));
				angle[2] = MAX_LENGTH - Math.min(MAX_LENGTH, Math.abs(motionData[2][0]));

				for(int i = 0; i < 3; i++){
					if(Math.abs(angle[i] - lastSentAngle[i]) >= CLIENT_SPEED_MARGIN){
						lastSentAngle[i] = angle[i];
						ModPackets.network.sendToAllAround(new SendDoubleToClient(Integer.toString(i), (float) angle[i], pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					}
				}

				if(ridable == null || ridable.isDead){
					if(ridableID != null){
						for(EntityArmRidable ent : world.getEntities(EntityArmRidable.class, (EntityArmRidable filter) -> true)){
							if(ridableID.equals(ent.getUniqueID())){
								ridable = ent;
								break;
							}
						}
					}
					if(ridable == null || ridable.isDead || !pos.equals(ridable.getOwnerPos())){//The position not matching could occur due to things like this block being copied with prototyping.
						ridable = new EntityArmRidable(world);
						ridableID = ridable.getUniqueID();
						ridable.setOwnerPos(pos);
						ridable.setPosition(.5D + (double) pos.getX(), 1D + (double) pos.getY(), .5D + (double) pos.getZ());
						world.spawnEntity(ridable);
					}
				}


				double posX = angle[2] * Math.cos(angle[0]) + 0.5D + (double) pos.getX();
				double posY = angle[1] + 1D + (double) pos.getY();
				double posZ = angle[2] * Math.sin(angle[0]) + 0.5D + (double) pos.getZ();
				BlockPos endPos = new BlockPos(posX, posY, posZ);
				ridable.setPositionAndUpdate(posX, posY, posZ);

				if(redstone > 0){
					EnumFacing side = EnumFacing.byIndex(((int) Math.round(RedstoneUtil.getPowerOnSide(world, pos, EnumFacing.SOUTH))) % 6);
					EFFECTS[(redstone - 1) % EFFECTS.length].onTriggered(world, endPos, posX, posY, posZ, side, ridable, this);
					world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, .5F, .75F);
					redstone = -redstone;
					markDirty();
				}
			}
		}
	}

	public boolean isRidable(EntityArmRidable ridableIn){
		return ridableIn == ridable;
	}

	@Override
	public void receiveDouble(String context, double message){
		switch(context){
			case "0":
				angle[0] = message;
				break;
			case "1":
				angle[1] = message;
				break;
			case "2":
				angle[2] = message;
				break;
			default:
		}
	}

	public void setRedstone(int redstoneIn){
		if(redstone != redstoneIn && -redstone != redstoneIn){
			redstone = (int) Math.round(redstoneIn);
			markDirty();
		}
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setDouble("speed0", motionData[0][0]);
		nbt.setDouble("speed1", motionData[1][0]);
		nbt.setDouble("speed2", motionData[2][0]);
		nbt.setDouble("angle0", angle[0]);
		nbt.setDouble("angle1", angle[1]);
		nbt.setDouble("angle2", angle[2]);
		return nbt;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setDouble("speed0", motionData[0][0]);
		nbt.setDouble("speed1", motionData[1][0]);
		nbt.setDouble("speed2", motionData[2][0]);
		nbt.setDouble("angle0", angle[0]);
		nbt.setDouble("angle1", angle[1]);
		nbt.setDouble("angle2", angle[2]);

		nbt.setDouble("l_angle0", angleRecord[0]);
		nbt.setDouble("l_angle1", angleRecord[1]);
		nbt.setDouble("l_angle2", angleRecord[2]);

		nbt.setInteger("redstone", redstone);

		if(ridableID != null){
			nbt.setLong("id_greater", ridableID.getMostSignificantBits());
			nbt.setLong("id_lesser", ridableID.getLeastSignificantBits());
		}		
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		motionData[0][0] = nbt.getDouble("speed0");
		motionData[1][0] = nbt.getDouble("speed1");
		motionData[2][0] = nbt.getDouble("speed2");
		angle[0] = nbt.getDouble("angle0");
		angle[1] = nbt.getDouble("angle1");
		angle[2] = nbt.getDouble("angle2");

		angleRecord[0] = nbt.getDouble("l_angle0");
		angleRecord[1] = nbt.getDouble("l_angle1");
		angleRecord[2] = nbt.getDouble("l_angle2");

		redstone = nbt.getInteger("redstone");

		ridableID = nbt.hasKey("id_lesser") ? new UUID(nbt.getLong("id_greater"), nbt.getLong("id_lesser")) : null;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
	}

	//Down: Rotation about y-axis, East: Base bar angle, West: Upper bar angle.
	private final AxleHandler[] axles = {new AxleHandler(0), new AxleHandler(1), new AxleHandler(2)};

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.AXLE_CAPABILITY){
			if(side == EnumFacing.DOWN || side == EnumFacing.EAST || side == EnumFacing.WEST){
				return true;
			}
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.AXLE_CAPABILITY){
			if(side == EnumFacing.DOWN){
				return (T) axles[0];
			}else if(side == EnumFacing.EAST){
				return (T) axles[1];
			}else if(side == EnumFacing.WEST){
				return (T) axles[2];
			}
		}
		return super.getCapability(cap, side);
	}

	private class AxleHandler implements IAxleHandler{

		private final int index;

		public AxleHandler(int index){
			this.index = index;
		}

		@Override
		public double[] getMotionData(){
			return motionData[index];
		}

		private double rotRatio;
		private byte updateKey;

		@Override
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius, boolean renderOffset){
			//If true, this has already been checked.
			if(key == updateKey || masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn == 0 ? 1 : index == 2 ? -rotRatioIn : rotRatioIn;
			updateKey = key;
		}

		@Override
		public double getMoInertia(){
			return PHYS_DATA;
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}

		@Override
		public void resetAngle(){
			//No effect, as in this the angle is used for non-rendering purposes.
		}

		@Override
		public float getAngle(){
			return (float) angle[index];
		}

		@Override
		public void addEnergy(double energy, boolean allowInvert, boolean absolute){
			if(allowInvert && absolute){
				motionData[index][1] += energy;
			}else if(allowInvert){
				motionData[index][1] += energy * Math.signum(motionData[index][1]);
			}else if(absolute){
				int sign = (int) Math.signum(motionData[index][1]);
				motionData[index][1] += energy;
				if(sign != 0 && Math.signum(motionData[index][1]) != sign){
					motionData[index][1] = 0;
				}
			}else{
				int sign = (int) Math.signum(motionData[index][1]);
				motionData[index][1] += energy * ((double) sign);
				if(Math.signum(motionData[index][1]) != sign){
					motionData[index][1] = 0;
				}
			}
			markDirty();
		}

		@Override
		public void markChanged(){
			markDirty();
		}

		@Override
		public boolean shouldManageAngle(){
			return false;
		}
	}
}
