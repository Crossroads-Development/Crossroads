package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class WindTurbineTileEntity extends ModuleTE{

	private EnumFacing facing = null;
	public static final double MAX_SPEED = 2D;

	public WindTurbineTileEntity(){
		super();
	}

	public WindTurbineTileEntity(boolean newlyPlaced){
		this();
		this.newlyPlaced = newlyPlaced;
	}

	protected EnumFacing getFacing(){
		if(facing == null){
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() != ModBlocks.windTurbine){
				invalidate();
				return EnumFacing.NORTH;
			}
			facing = state.getValue(Properties.HORIZ_FACING);
		}

		return facing;
	}

	public void resetCache(){
		facing = null;
	}

	public static final double POWER_PER_LEVEL = 10D;
	private boolean newlyPlaced = false;
	private int level = 1;
	private boolean running = false;

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	protected AxleHandler createAxleHandler(){
		return new AngleAxleHandler();
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		chat.add("Power Gen: " + POWER_PER_LEVEL * (double) level + "J/t");
		super.addInfo(chat, player, side, hitX, hitY, hitZ);
	}

	public int getRedstoneOutput(){
		return level < 0 ? 15 : 0;
	}

	@Override
	public void update(){
		super.update();

		if(!world.isRemote){
			//Every 30 seconds check whether the placement requirements are valid, and cache the result
			if(newlyPlaced || world.getTotalWorldTime() % 600 == 0){
				newlyPlaced = false;
				running = false;
				EnumFacing facing = getFacing();
				BlockPos offsetPos = pos.offset(facing);
				if(world.canSeeSky(offsetPos)){
					running = true;
					outer:
					for(int i = -2; i <= 2; i++){
						for(int j = -2; j <= 2; j++){
							BlockPos checkPos = offsetPos.add(facing.getZOffset() * i, j, facing.getXOffset() * i);
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

			if(running && axleHandler.connected){
				if(world.getTotalWorldTime() % 10 == 0 && world.rand.nextInt(240) == 0){
					int prevLevel = level;
					level = (world.rand.nextInt(2) + 1) * (world.rand.nextBoolean() ? -1 : 1);//Gen a random number from -2 to 2, other than 0

					//If the redstone output has changed, update the neighbors
					if(level < 0 != prevLevel < 0){
						world.notifyNeighborsOfStateChange(pos, ModBlocks.windTurbine, true);
					}
				}

				if(motData[0] * Math.signum(level) < MAX_SPEED){
					motData[1] += (double) level * POWER_PER_LEVEL;
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		level = nbt.getInteger("level");
		running = nbt.getBoolean("running");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("level", level);
		nbt.setBoolean("running", running);
		return nbt;
	}

	private static final AxisAlignedBB RENDER_BOX = new AxisAlignedBB(-1, -1, -1, 2, 2, 2);

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return RENDER_BOX.offset(pos);
	}

	@Override
	public double getMoInertia(){
		return 200;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.AXLE_CAPABILITY && (facing == null || facing == world.getBlockState(pos).getValue(Properties.HORIZ_FACING).getOpposite())){
			return (T) axleHandler;
		}
		return super.getCapability(capability, facing);
	}
}
