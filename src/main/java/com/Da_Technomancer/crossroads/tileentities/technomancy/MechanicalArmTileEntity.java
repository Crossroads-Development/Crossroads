package com.Da_Technomancer.crossroads.tileentities.technomancy;

import java.util.UUID;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.effects.mechArm.IMechArmEffect;
import com.Da_Technomancer.crossroads.API.effects.mechArm.MechArmDepositEffect;
import com.Da_Technomancer.crossroads.API.effects.mechArm.MechArmDropEntityEffect;
import com.Da_Technomancer.crossroads.API.effects.mechArm.MechArmPickupBlockEffect;
import com.Da_Technomancer.crossroads.API.effects.mechArm.MechArmPickupEntityEffect;
import com.Da_Technomancer.crossroads.API.effects.mechArm.MechArmPickupFromInvEffect;
import com.Da_Technomancer.crossroads.API.effects.mechArm.MechArmPickupOneFromInvEffect;
import com.Da_Technomancer.crossroads.API.effects.mechArm.MechArmReleaseEntityEffect;
import com.Da_Technomancer.crossroads.API.effects.mechArm.MechArmUseEffect;
import com.Da_Technomancer.crossroads.API.packets.IDoubleReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDoubleToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
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

public class MechanicalArmTileEntity extends TileEntity implements ITickable, IDoubleReceiver{

	public static final double LOWER_ARM_LENGTH = 3;
	public static final double UPPER_ARM_LENGTH = 5;
	private static final double MAXIMUM_LOWER_ANGLE = 17D * Math.PI / 36D;//In radians, from horizontal.
	private static final double MINIMUM_LOWER_ANGLE = Math.PI / 6D;//In radians, from horizontal.
	private static final double MAXIMUM_UPPER_ANGLE = .75D * Math.PI;//In radians, from straight down.
	private static final double MINIMUM_UPPER_ANGLE = Math.PI / 4D;//In radians, from straight down.
	private static final int TIERS = ModConfig.speedTiers.getInt();

	private static final IMechArmEffect[] EFFECTS = {new MechArmPickupEntityEffect(), new MechArmPickupBlockEffect(), new MechArmPickupFromInvEffect(), new MechArmUseEffect(), new MechArmDepositEffect(), new MechArmDropEntityEffect(), new MechArmReleaseEntityEffect(), new MechArmPickupOneFromInvEffect()};

	public double[][] motionData = new double[3][4];
	/** In radians. */
	public double[] angle = {0, MAXIMUM_LOWER_ANGLE, MINIMUM_UPPER_ANGLE};
	/** A record of the last speeds sent to the client.*/
	private double[] speedRecord = new double[3];
	private static final double[] PHYS_DATA = new double[2];
	/**
	 * Math.min((redstone - 2) / 6, EFFECTS.length - 1) corresponds to action type, which are:
	 * 0: Pickup entity, 1: Pickup block, 2: Pickup from inventory, 3: Use, 4: Deposit into inventory, 5: Drop entity, 6: Release entity with momentum, 7: Pickup one from inventory.
	 * EnumFacing.getFront((redstone - 2) % 6) corresponds to an EnumFacing. Only some action types (2, 3, 4) vary based on EnumFacing. 
	 */
	private int redstone = -1;
	public EntityArmRidable ridable;
	private UUID ridableID;

	@Override
	public void update(){
		angle[0] += motionData[0][0] / 20D;
		angle[1] = Math.min(MAXIMUM_LOWER_ANGLE, Math.max(MINIMUM_LOWER_ANGLE, angle[1] + (motionData[1][0] / 20D)));
		angle[2] = Math.min(MAXIMUM_UPPER_ANGLE, Math.max(MINIMUM_UPPER_ANGLE, angle[2] + (motionData[2][0] / 20D)));

		if(!world.isRemote){
			for(int i = 0; i < 3; i++){
				if(MiscOp.tiersRound(motionData[i][0], TIERS) != speedRecord[i]){
					speedRecord[i] = MiscOp.tiersRound(motionData[i][0], TIERS);
					ModPackets.network.sendToAllAround(new SendDoubleToClient("w" + i, speedRecord[i], pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					//The exact angle is of such importance to this device that it is synced in full each time the speed is synced. 
					ModPackets.network.sendToAllAround(new SendDoubleToClient("a" + i, angle[i], pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				}
			}
		}

		if(!world.isRemote){
			if(redstone == -1){
				setRedstone(ModBlocks.ratiator.getPowerOnSide(world, pos, EnumFacing.NORTH, false));
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

			int actionType = -1;
			if(redstone == 1){
				angle[0] = 0;
				angle[1] = MAXIMUM_LOWER_ANGLE;
				angle[2] = MINIMUM_UPPER_ANGLE;
				ModPackets.network.sendToAllAround(new SendDoubleToClient("a_reset", 0, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}else if(redstone != 0){
				actionType = Math.min((redstone - 2) / 6, EFFECTS.length - 1);
			}

			EnumFacing side = EnumFacing.getFront((redstone - 2) % 6);

			double lengthCross = Math.sqrt(Math.pow(LOWER_ARM_LENGTH, 2) + Math.pow(UPPER_ARM_LENGTH, 2) - (2D * LOWER_ARM_LENGTH * UPPER_ARM_LENGTH * Math.cos(angle[2])));
			double thetaD = angle[1] + angle[2] + Math.asin(Math.sin(angle[2]) * LOWER_ARM_LENGTH / lengthCross);
			double holder = -Math.cos(thetaD) * lengthCross;

			double posX = (holder * Math.cos(angle[0])) + .5D + (double) pos.getX();
			double posY = (-Math.sin(thetaD) * lengthCross) + 1D + (double) pos.getY();
			double posZ = (holder * Math.sin(angle[0])) + .5D + (double) pos.getZ();
			BlockPos endPos = new BlockPos(posX, posY, posZ);

			ridable.setPositionAndUpdate(posX, posY, posZ);

			if(actionType != -1 && EFFECTS[actionType].onTriggered(world, endPos, posX, posY, posZ, side, ridable, this)){
				world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, .5F, .75F);
			}
		}
	}

	public boolean isRidable(EntityArmRidable ridableIn){
		return ridableIn == ridable;
	}

	@Override
	public void receiveDouble(String context, double message){
		char char0 = context.charAt(0);
		char char1 = context.charAt(1);
		if(char0 == 'w'){
			int i = char1 == '0' ? 0 : char1 == '1' ? 1 : 2;
			motionData[i][0] = message;
		}else if(char0 == 'a'){
			if(context.equals("a_reset")){
				angle[0] = 0;
				angle[1] = MAXIMUM_LOWER_ANGLE;
				angle[2] = MINIMUM_UPPER_ANGLE;
				return;
			}
			int i = char1 == '0' ? 0 : char1 == '1' ? 1 : 2;
			angle[i] = message;
		}
	}

	public void setRedstone(double redstoneIn){
		redstone = Math.min((int) Math.round(redstoneIn), 42);
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

		speedRecord[0] = motionData[0][0];
		speedRecord[1] = motionData[1][0];
		speedRecord[2] = motionData[2][0];

		ridableID = nbt.hasKey("id_lesser") ? new UUID(nbt.getLong("id_greater"), nbt.getLong("id_lesser")) : null;
	}

	private static final AxisAlignedBB RENDER_BOX = new AxisAlignedBB(-(LOWER_ARM_LENGTH + UPPER_ARM_LENGTH + 2), -(LOWER_ARM_LENGTH + UPPER_ARM_LENGTH + 1), -(LOWER_ARM_LENGTH + UPPER_ARM_LENGTH + 2), LOWER_ARM_LENGTH + UPPER_ARM_LENGTH + 2, LOWER_ARM_LENGTH + UPPER_ARM_LENGTH + 2, LOWER_ARM_LENGTH + UPPER_ARM_LENGTH + 2);

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return RENDER_BOX.offset(pos);
	}

	//Down: Rotation about y-axis, East: Base bar angle, West: Upper bar angle.
	private final AxleHandler[] axles = {new AxleHandler(0), new AxleHandler(1), new AxleHandler(2)};

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.AXLE_HANDLER_CAPABILITY){
			if(side == EnumFacing.DOWN || side == EnumFacing.EAST || side == EnumFacing.WEST){
				return true;
			}
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.AXLE_HANDLER_CAPABILITY){
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
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
			//If true, this has already been checked.
			if(key == updateKey || masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn == 0 ? 1 : index == 2 ? -rotRatioIn : rotRatioIn;
			updateKey = key;
		}

		@Override
		public double[] getPhysData(){
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
		public double getAngle(){
			return angle[index];
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
	}
}
