package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.blocks.rotary.RotaryDrill;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import com.Da_Technomancer.essentials.shared.IAxisHandler;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class RotaryDrillTileEntity extends TileEntity implements ITickable, IInfoTE{

	private static final DamageSource DRILL = new DamageSource("drill");

	public RotaryDrillTileEntity(){
		super();
	}

	public RotaryDrillTileEntity(boolean golden){
		super();
		this.golden = golden;
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side){
		chat.add("Speed: " + MiscOp.betterRound(motionData[0], 3));
		chat.add("Energy: " + MiscOp.betterRound(motionData[1], 3));
		chat.add("Power: " + MiscOp.betterRound(motionData[2], 3));
		chat.add("I: " + axleHandler.getMoInertia() + ", Rotation Ratio: " + axleHandler.getRotationRatio());
	}

	private int ticksExisted = 0;
	private boolean golden;
	public static final double ENERGY_USE = .5D;
	private static final double SPEED_PER_HARDNESS = .1D;

	public boolean isGolden(){
		return golden;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	/**
	 * This uses the angle of the attached gear instead of calculating it's own for a few reasons. It will always be attached when it should spin, and should always have the same angle as the attached gear (no point calculating).
	 */
	private float angle = 0;
	private final double[] motionData = new double[4];

	@Override
	public void update(){
		if(!(world.getBlockState(pos).getBlock() instanceof RotaryDrill)){
			invalidate();
			return;
		}
		EnumFacing facing = world.getBlockState(pos).getValue(EssentialsProperties.FACING);
		if(world.isRemote){
			TileEntity attachedTE = world.getTileEntity(pos.offset(facing.getOpposite()));
			if(attachedTE != null && attachedTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing)){
				angle = (float) attachedTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing).getAngle();
			}
			return;
		}

		if(Math.abs(motionData[1]) >= ENERGY_USE){
			axleHandler.addEnergy(-ENERGY_USE, false, false);
			if(++ticksExisted % 10 == 0){
				if(world.getBlockState(pos.offset(facing)).getBlock().canCollideCheck(world.getBlockState(pos.offset(facing)), false)){
					float hardness = world.getBlockState(pos.offset(facing)).getBlockHardness(world, pos.offset(facing));
					if(hardness >= 0 && Math.abs(motionData[0]) >= hardness * SPEED_PER_HARDNESS){
						world.destroyBlock(pos.offset(facing), true);
					}
				}else{
					List<EntityLivingBase> ents = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.offset(facing)), EntitySelectors.IS_ALIVE);
					for(EntityLivingBase ent : ents){
						ent.attackEntityFrom(golden ? new EntityDamageSource("drill", FakePlayerFactory.get((WorldServer) world, new GameProfile(null, "drill_player_" + world.provider.getDimension()))) : DRILL, (float) Math.abs(motionData[0] / SPEED_PER_HARDNESS));
					}
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		for(int i = 0; i < 4; i++){
			nbt.setDouble("motion" + i, motionData[i]);
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		for(int i = 0; i < 4; i++){
			motionData[i] = nbt.getDouble("motion" + i);
		}
	}

	public float getAngle(){
		return angle;
	}

	private IAxleHandler axleHandler = new AxleHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		return (cap == Capabilities.AXLE_HANDLER_CAPABILITY && side == world.getBlockState(pos).getValue(EssentialsProperties.FACING).getOpposite()) || super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.AXLE_HANDLER_CAPABILITY && side == world.getBlockState(pos).getValue(EssentialsProperties.FACING).getOpposite()){
			return (T) axleHandler;
		}
		return super.getCapability(cap, side);
	}

	private class AxleHandler implements IAxleHandler{

		@Override
		public double[] getMotionData(){
			return motionData;
		}

		private double rotRatio;
		private byte updateKey;

		@Override
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
			//If true, this has already been checked.
			if(key == updateKey || masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
			updateKey = key;
		}

		@Override
		public double getMoInertia(){
			return golden ? 100 : 50;
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
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
		public void markChanged(){
			markDirty();
		}

		@Override
		public boolean shouldManageAngle(){
			return false;
		}
	}
}
