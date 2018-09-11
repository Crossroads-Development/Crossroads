package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.GameProfileNonPicky;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.magic.BeamManager;
import com.Da_Technomancer.crossroads.API.magic.EnumMagicElements;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GatewayFrameTileEntity extends TileEntity implements ITickable, IInfoTE{


	private final IMagicHandler magicHandler = new MagicHandler();
	private GameProfileNonPicky owner;

	public void setOwner(GameProfileNonPicky owner){
		this.owner = owner;
		markDirty();
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side){
		if(world.getBlockState(pos) == ModBlocks.gatewayFrame.getDefaultState().withProperty(Properties.FACING, EnumFacing.UP)){
			BlockPos target = dialedCoord();
			if(target != null){
				chat.add("Dialed: " + target.getX() + ", " + target.getY() + ", " + target.getZ());
			}
		}
	}

	private boolean cacheValid;
	public float alpha;

	@Override
	public void update(){
		if(world.isRemote){
			alpha = (((float) Math.sin((double) world.getTotalWorldTime() / 10D) + 1F) / 6F) + (2F / 3F);
		}

		if(!world.isRemote){
			if(BeamManager.beamStage == 1){
				cacheValid = false;
				if(world.getBlockState(pos).getValue(Properties.FACING).getAxis() == Axis.Y){
					if(owner != null && magicPassed && getAlignment() != null && world.provider.getDimension() != 1){
						world.setBlockState(pos, ModBlocks.gatewayFrame.getDefaultState().withProperty(Properties.FACING, EnumFacing.UP));

						List<Entity> toTransport = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX() - 1, pos.getY() - 3, pos.getZ() - 1, pos.getX() + 1, pos.getY() - 1, pos.getZ() + 1), EntitySelectors.IS_ALIVE);
						if(toTransport != null && !toTransport.isEmpty()){
							int dim = ModDimensions.getDimForPlayer(owner);
							int currentDim = world.provider.getDimension();
							boolean inDim = dim == currentDim;

							if(!inDim && DimensionManager.getWorld(dim) == null){
								DimensionManager.initDimension(dim);
							}

							BlockPos target = dialedCoord();

							GatewayTeleporter porter = inDim ? new GatewayTeleporter(DimensionManager.getWorld(0), target.getX(), target.getY(), target.getZ()) : new GatewayTeleporter(DimensionManager.getWorld(dim), .5D, 33, .5D);

							PlayerList playerList = world.getMinecraftServer().getPlayerList();
							for(Entity ent : toTransport){
								if(ent instanceof EntityPlayerMP){
									playerList.transferPlayerToDimension((EntityPlayerMP) ent, inDim ? 0 : dim, porter);
								}else{
									playerList.transferEntityToWorld(ent, currentDim, (WorldServer) world, DimensionManager.getWorld(inDim ? 0 : dim), porter);
								}
							}
						}
					}else{
						world.setBlockState(pos, ModBlocks.gatewayFrame.getDefaultState().withProperty(Properties.FACING, EnumFacing.DOWN));
					}
				}
				magicPassed = false;
			}
		}
	}

	private static final AxisAlignedBB RENDER_BOX = new AxisAlignedBB(-1, -3, -1, 2, 0, 2);

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return RENDER_BOX.offset(pos);
	}

	private Axis cached;

	/**
	 * Returns null unless called on the top frame and there is a valid placement of 2 other frames.
	 */
	@Nullable
	public Axis getAlignment(){
		if(cacheValid){
			return cached;
		}
		cacheValid = true;
		BlockPos midPos = pos.offset(EnumFacing.DOWN, 2);
		IBlockState baseState = ModBlocks.gatewayFrame.getDefaultState();
		if(world.getBlockState(midPos.offset(EnumFacing.EAST, 2)) == baseState.withProperty(Properties.FACING, EnumFacing.WEST) && world.getBlockState(midPos.offset(EnumFacing.WEST, 2)) == baseState.withProperty(Properties.FACING, EnumFacing.EAST)){
			cached = Axis.X;
			return Axis.X;
		}
		if(world.getBlockState(midPos.offset(EnumFacing.NORTH, 2)) == baseState.withProperty(Properties.FACING, EnumFacing.SOUTH) && world.getBlockState(midPos.offset(EnumFacing.SOUTH, 2)) == baseState.withProperty(Properties.FACING, EnumFacing.NORTH)){
			cached = Axis.Z;
			return Axis.Z;
		}
		cached = null;
		return null;
	}

	@Nullable
	private BlockPos dialedCoord(){
		//The frame in the negative direction controls the X coord.
		Axis align = getAlignment();
		if(align == null){
			return null;
		}
		int[] coords = new int[3];
		TileEntity te = world.getTileEntity(pos.offset(EnumFacing.DOWN, 2).offset(EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, align), 3));
		if(te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, align))){
			coords[0] = (int) Math.round(te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, align)).getMotionData()[0]);
		}
		te = world.getTileEntity(pos.offset(EnumFacing.DOWN, 2).offset(EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, align), 3));
		if(te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, align))){
			coords[2] = (int) Math.round(te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, align)).getMotionData()[0]);
		}
		te = world.getTileEntity(pos.offset(EnumFacing.UP));
		if(te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
			coords[1] = (int) Math.round(te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN).getMotionData()[0]);
		}

		return new BlockPos(coords[0], coords[1], coords[2]);
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && (side == null || side.getAxis() != Axis.Y) && world.getBlockState(pos).getValue(Properties.FACING).getAxis() == Axis.Y){
			return true;
		}

		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && (side == null || side.getAxis() != Axis.Y) && world.getBlockState(pos).getValue(Properties.FACING).getAxis() == Axis.Y){
			return (T) magicHandler;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(owner != null){
			owner.writeToNBT(nbt, "own");
		}

		return nbt;
	}

	@Override
	public void setWorldCreate(World world){
		this.world = world;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		owner = GameProfileNonPicky.readFromNBT(nbt, "own", world.getMinecraftServer() == null ? null : world.getMinecraftServer().getPlayerProfileCache());
		if(owner != null && owner.isNewlyCompleted()){
			markDirty();
		}
	}

	private boolean magicPassed = false;

	private static class GatewayTeleporter implements ITeleporter{

		private final double coordX;
		private final double coordY;
		private final double coordZ;

		public GatewayTeleporter(WorldServer worldIn, double coordXIn, double coordYIn, double coordZIn){
			coordX = coordXIn;
			coordY = coordYIn;
			coordZ = coordZIn;
		}

		@Override
		public void placeEntity(World world, Entity entity, float yaw){
			world.getBlockState(new BlockPos(coordX, coordY, coordZ));
			entity.setPosition(coordX, coordY, coordZ);
			entity.motionX = 0;
			entity.motionY = 0;
			entity.motionZ = 0;
			if(entity instanceof EntityPlayer){
				((EntityPlayer) entity).addExperienceLevel(0);
			}
		}
	}

	private class MagicHandler implements IMagicHandler{

		@Override
		public void setMagic(MagicUnit mag){
			if(EnumMagicElements.getElement(mag) == EnumMagicElements.RIFT){
				magicPassed = true;
				markDirty();
			}
		}
	}
}
