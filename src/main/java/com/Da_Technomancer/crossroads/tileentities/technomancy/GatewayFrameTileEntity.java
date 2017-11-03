package com.Da_Technomancer.crossroads.tileentities.technomancy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.GameProfileNonPicky;
import com.Da_Technomancer.crossroads.API.IInfoDevice;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.magic.BeamManager;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
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
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;

public class GatewayFrameTileEntity extends TileEntity implements ITickable, IInfoTE{


	private final IMagicHandler magicHandler = new MagicHandler();
	private GameProfileNonPicky owner;
	private double savedCoord = 0;

	public void setOwner(GameProfileNonPicky owner){
		this.owner = owner;
		markDirty();
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void addInfo(ArrayList<String> chat, IInfoDevice device, EntityPlayer player, @Nullable EnumFacing side){
		if(device == EnumGoggleLenses.EMERALD && world.getBlockState(pos) == ModBlocks.gatewayFrame.getDefaultState().withProperty(Properties.FACING, EnumFacing.UP)){
			GatewayFrameTileEntity xTE = dialedCoord(Axis.X);
			if(xTE != null){
				chat.add("Dialed: " + xTE.getCoord() + ", " + getCoord() + ", " + dialedCoord(Axis.Z).getCoord());
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

							GatewayTeleporter porter = inDim ? new GatewayTeleporter(DimensionManager.getWorld(0), dialedCoord(Axis.X).savedCoord, savedCoord, dialedCoord(Axis.Z).savedCoord) : new GatewayTeleporter(DimensionManager.getWorld(dim), .5D, 33, .5D);

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


			double prevCoord = savedCoord;
			EnumFacing facing = world.getBlockState(pos).getValue(Properties.FACING);

			if(facing == EnumFacing.UP){
				for(EnumFacing dir : EnumFacing.HORIZONTALS){
					TileEntity te = world.getTileEntity(pos.offset(dir));
					if(te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, dir.getOpposite())){
						savedCoord = Math.max(0, Math.min(250, savedCoord + te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, dir.getOpposite()).getMotionData()[0] / 20D));
						break;
					}
				}
			}else if(facing == EnumFacing.DOWN){
				savedCoord = 0;
			}else if(world.getBlockState(pos.offset(facing, 2).offset(EnumFacing.UP, 2)) == ModBlocks.gatewayFrame.getDefaultState().withProperty(Properties.FACING, EnumFacing.UP)){
				TileEntity te = world.getTileEntity(pos.offset(facing.getOpposite()));
				if(te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing)){
					savedCoord += te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing).getMotionData()[0] / 20D;
				}
			}else{
				savedCoord = 0;
			}

			if(savedCoord != prevCoord){
				markDirty();
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
	public GatewayFrameTileEntity dialedCoord(Axis axis){
		//The frame in the negative direction controls the X coord. 
		if(getAlignment() == null){
			return null;
		}
		return (GatewayFrameTileEntity) world.getTileEntity(pos.offset(EnumFacing.DOWN, 2).offset(EnumFacing.getFacingFromAxis(axis == Axis.X ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE, getAlignment()), 2));
	}

	public double getCoord(){
		return MiscOp.betterRound(savedCoord, 3);
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && (side == EnumFacing.UP || side == null) && world.getBlockState(pos).getValue(Properties.FACING).getAxis() == Axis.Y){
			return true;
		}

		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && (side == EnumFacing.UP || side == null) && world.getBlockState(pos).getValue(Properties.FACING).getAxis() == Axis.Y){
			return (T) magicHandler;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setDouble("coord", savedCoord);
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
		savedCoord = nbt.getDouble("coord");
		owner = GameProfileNonPicky.readFromNBT(nbt, "own", world.getMinecraftServer() == null ? null : world.getMinecraftServer().getPlayerProfileCache());
		if(owner != null && owner.isNewlyCompleted()){
			markDirty();
		}
	}

	private boolean magicPassed = false;

	private static class GatewayTeleporter extends Teleporter{

		private final WorldServer worldOther;
		private final double coordX;
		private final double coordY;
		private final double coordZ;

		public GatewayTeleporter(WorldServer worldIn, double coordXIn, double coordYIn, double coordZIn){
			super(worldIn);
			worldOther = worldIn;
			coordX = coordXIn;
			coordY = coordYIn;
			coordZ = coordZIn;
		}

		@Override
		public void placeInPortal(@Nonnull Entity entity, float rotationYaw) {
			worldOther.getBlockState(new BlockPos(coordX, coordY, coordZ));
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
			if(MagicElements.getElement(mag) == MagicElements.RIFT){
				magicPassed = true;
				markDirty();
			}
		}
	}
}
