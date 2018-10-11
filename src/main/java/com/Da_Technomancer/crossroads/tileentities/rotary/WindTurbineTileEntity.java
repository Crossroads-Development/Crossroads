package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.packets.ILongReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLongToClient;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.essentials.shared.IAxisHandler;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class WindTurbineTileEntity extends TileEntity implements ITickable, IInfoTE, ILongReceiver{

	public WindTurbineTileEntity(){
		super();
	}

	public WindTurbineTileEntity(boolean newlyPlaced){
		this();
		this.newlyPlaced = newlyPlaced;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	private boolean newlyPlaced = false;
	private double[] motionData = new double[4];
	/**
	 * 0: angle, 1: clientW
	 */
	private float[] angleW = new float[2];
	public static final double POWER_PER_LEVEL = 5D;
	private int level = 1;
	private boolean running = false;

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side){
		chat.add("Speed: " + MiscOp.betterRound(motionData[0], 3));
		chat.add("Energy: " + MiscOp.betterRound(motionData[1], 3));
		chat.add("Power: " + MiscOp.betterRound(motionData[2], 3));
		chat.add("I: " + handler.getMoInertia() + ", Rotation Ratio: " + handler.getRotationRatio());
		chat.add("Power Gen: " + POWER_PER_LEVEL * (double) level + "J/t");
	}

	public int getRedstoneOutput(){
		return level < 0 ? 15 : 0;
	}

	@Override
	public void update(){
		if(world.isRemote){
			angleW[0] += angleW[1] * 9D / Math.PI;
		}else{
			//Every 30 seconds check whether the placement requirements are valid, and cache the result
			if(newlyPlaced || world.getTotalWorldTime() % 600 == 0){
				newlyPlaced = false;
				running = false;
				IBlockState state = world.getBlockState(pos);
				if(state.getBlock() != ModBlocks.windTurbine){
					invalidate();
					return;
				}

				EnumFacing dir = state.getValue(Properties.HORIZONTAL_FACING);
				BlockPos offsetPos = pos.offset(dir.getOpposite());
				if(world.canSeeSky(offsetPos)){
					running = true;
					outer:
					for(int i = -2; i <= 2; i++){
						for(int j = -2; j <= 2; j++){
							BlockPos checkPos = offsetPos.add(dir.getFrontOffsetZ() * i, j, dir.getFrontOffsetX() * i);
							IBlockState checkState = world.getBlockState(checkPos);
							if(!checkState.getBlock().isAir(checkState, world, checkPos)){
								running = false;
								break outer;
							}
						}
					}
				}

				markDirty();
			}

			if(running && handler.hasMaster){
				if(world.getTotalWorldTime() % 10 == 0 && world.rand.nextInt(240) == 0){
					int prevLevel = level;
					level = (world.rand.nextInt(2) + 1) * (world.rand.nextBoolean() ? -1 : 1);//Gen a random number from -2 to 2, other than 0

					//If the redstone output has changed, update the neighbors
					if(level < 0 != prevLevel < 0){
						world.notifyNeighborsOfStateChange(pos, ModBlocks.windTurbine, true);
					}
				}

				motionData[1] += (double) level * POWER_PER_LEVEL;
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		// motionData
		for(int j = 0; j < 4; j++){
			motionData[j] = nbt.getDouble("[" + j + "]mot");
		}

		angleW[0] = nbt.getFloat("angle");
		angleW[1] = nbt.getFloat("cl_w");
		level = nbt.getInteger("level");
		running = nbt.getBoolean("running");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		// motionData
		for(int j = 0; j < 3; j++){
			if(motionData[j] != 0)
				nbt.setDouble("[" + j + "]mot", motionData[j]);
		}

		nbt.setFloat("angle", angleW[0]);
		nbt.setFloat("cl_w", angleW[1]);
		nbt.setInteger("level", level);
		nbt.setBoolean("running", running);
		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setFloat("angle", angleW[0]);
		nbt.setFloat("cl_w", angleW[1]);
		return nbt;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable EntityPlayerMP sendingPlayer){
		if(identifier == 0){
			float angle = Float.intBitsToFloat((int) (message & 0xFFFFFFFFL));
			angleW[0] = Math.abs(angle - angleW[0]) > 5F ? angle : angleW[0];
			angleW[1] = Float.intBitsToFloat((int) (message >>> 32L));
		}
	}

	private static final AxisAlignedBB RENDER_BOX = new AxisAlignedBB(-1, -1, -1, 2, 2, 2);

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return RENDER_BOX.offset(pos);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing == world.getBlockState(pos).getValue(Properties.HORIZONTAL_FACING)){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing == world.getBlockState(pos).getValue(Properties.HORIZONTAL_FACING)){
			return (T) handler;
		}
		return super.getCapability(capability, facing);
	}

	public final AxleHandler handler = new AxleHandler();

	public class AxleHandler implements IAxleHandler{

		private byte updateKey;
		private double rotRatio;
		private boolean hasMaster;

		@Override
		public double[] getMotionData(){
			return motionData;
		}

		@Override
		public void propogate(@Nonnull IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
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
			hasMaster = true;
		}

		@Override
		public double getMoInertia(){
			return 200;
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}

		@Override
		public void resetAngle(){
			if(!world.isRemote){
				angleW[1] = 0;
				angleW[0] = Math.signum(rotRatio) == -1 ? 22.5F : 0F;
				ModPackets.network.sendToAllAround(new SendLongToClient((byte) 0, (Float.floatToIntBits(angleW[0]) & 0xFFFFFFFFL) | ((long) Float.floatToIntBits(angleW[1]) << 32L), pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
		}

		@Override
		public float getAngle(){
			return angleW[0];
		}

		@Override
		public void setAngle(float angleIn){
			angleW[0] = angleIn;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public float getNextAngle(){
			return angleW[0] + (angleW[1] * 9F / (float) Math.PI);
		}

		@Override
		public void markChanged(){
			markDirty();
		}

		@Override
		public float getClientW(){
			return angleW[1];
		}

		@Override
		public void syncAngle(){
			angleW[1] = (float) motionData[0];
			ModPackets.network.sendToAllAround(new SendLongToClient((byte) 0, (Float.floatToIntBits(angleW[0]) & 0xFFFFFFFFL) | ((long) Float.floatToIntBits(angleW[1]) << 32L), pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}

		@Override
		public boolean shouldManageAngle(){
			return true;
		}

		@Override
		public void disconnect(){
			hasMaster = false;
		}
	}
}
