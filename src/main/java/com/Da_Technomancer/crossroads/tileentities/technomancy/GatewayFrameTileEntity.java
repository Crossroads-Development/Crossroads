package com.Da_Technomancer.crossroads.tileentities.technomancy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.GameProfileNonPicky;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.effects.goggles.IGoggleInfoTE;
import com.Da_Technomancer.crossroads.API.enums.GoggleLenses;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
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

public class GatewayFrameTileEntity extends TileEntity implements ITickable, IGoggleInfoTE{


	private final IMagicHandler magicHandler = new MagicHandler();
	public GameProfileNonPicky owner;
	private double savedCoord = 0;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return (oldState.getBlock() != newState.getBlock());
	}
	
	@Override
	public void addInfo(ArrayList<String> chat, GoggleLenses lens, EntityPlayer player, @Nullable EnumFacing side){
		if(lens == GoggleLenses.EMERALD && world.getBlockState(pos) == ModBlocks.gatewayFrame.getDefaultState().withProperty(Properties.FACING, EnumFacing.UP)){
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
		if(world.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 1){
			cacheValid = false;
		}
		if(!world.isRemote && world.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 1){
			if(world.getBlockState(pos).getValue(Properties.FACING).getAxis() == Axis.Y){
				if(owner != null && magicPassed && checkStructure() && world.provider.getDimension() != 1){
					world.setBlockState(pos, ModBlocks.gatewayFrame.getDefaultState().withProperty(Properties.FACING, EnumFacing.UP));
					List<Entity> toTransport = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX() - 1, pos.getY() - 3, pos.getZ() - 1, pos.getX() + 1, pos.getY() - 1, pos.getZ() + 1), EntitySelectors.IS_ALIVE);
					if(toTransport != null && !toTransport.isEmpty()){
						int dim = ModDimensions.getDimForPlayer(owner);
						if(DimensionManager.getWorld(dim) == null){
							DimensionManager.initDimension(dim);
						}
						int currentDim = world.provider.getDimension();
						boolean inDim = dim == currentDim;
						GatewayTeleporter porter = inDim ? new GatewayTeleporter(DimensionManager.getWorld(0), dialedCoord(Axis.X).savedCoord, savedCoord, dialedCoord(Axis.Z).savedCoord) : new GatewayTeleporter(DimensionManager.getWorld(dim), .5D, 33, .5D);

						for(Entity ent : toTransport){
							if(ent instanceof EntityPlayerMP){
								DimensionManager.getWorld(0).getMinecraftServer().getPlayerList().transferPlayerToDimension((EntityPlayerMP) ent, inDim ? 0 : dim, porter);
							}else{
								DimensionManager.getWorld(0).getMinecraftServer().getPlayerList().transferEntityToWorld(ent, currentDim, (WorldServer) world, DimensionManager.getWorld(inDim ? 0 : dim), porter);
							}
						}
					}
				}else{
					world.setBlockState(pos, ModBlocks.gatewayFrame.getDefaultState().withProperty(Properties.FACING, EnumFacing.DOWN));
				}
			}
			magicPassed = false;
		}

		if(!world.isRemote && owner != null){
			IBlockState active = ModBlocks.gatewayFrame.getDefaultState().withProperty(Properties.FACING, EnumFacing.UP);
			switch(world.getBlockState(pos).getValue(Properties.FACING)){
				case EAST:
					if(world.getBlockState(pos.offset(EnumFacing.EAST, 2).offset(EnumFacing.UP, 2)) == active){
						TileEntity te = world.getTileEntity(pos.offset(EnumFacing.WEST));
						if(te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.EAST)){
							savedCoord += te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.EAST).getMotionData()[0] / 20D;
						}
					}else{
						savedCoord = 0;
					}
					break;
				case NORTH:
					if(world.getBlockState(pos.offset(EnumFacing.NORTH, 2).offset(EnumFacing.UP, 2)) == active){
						TileEntity te = world.getTileEntity(pos.offset(EnumFacing.SOUTH));
						if(te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.NORTH)){
							savedCoord += te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.NORTH).getMotionData()[0] / 20D;
						}
					}else{
						savedCoord = 0;
					}
					break;
				case SOUTH:
					if(world.getBlockState(pos.offset(EnumFacing.SOUTH, 2).offset(EnumFacing.UP, 2)) == active){
						TileEntity te = world.getTileEntity(pos.offset(EnumFacing.NORTH));
						if(te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.SOUTH)){
							savedCoord += te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.SOUTH).getMotionData()[0] / 20D;
						}
					}else{
						savedCoord = 0;
					}
					break;
				case UP:
					for(EnumFacing dir : EnumFacing.HORIZONTALS){
						TileEntity te = world.getTileEntity(pos.offset(dir));
						if(te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, dir.getOpposite())){
							savedCoord = Math.max(0, Math.min(250, savedCoord + te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, dir.getOpposite()).getMotionData()[0] / 20D));
							break;
						}
					}
					break;
				case WEST:
					if(world.getBlockState(pos.offset(EnumFacing.WEST, 2).offset(EnumFacing.UP, 2)) == active){
						TileEntity te = world.getTileEntity(pos.offset(EnumFacing.EAST));
						if(te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.WEST)){
							savedCoord += te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.WEST).getMotionData()[0] / 20D;
						}
					}else{
						savedCoord = 0;
					}
					break;
				default:
					savedCoord = 0;
					break;

			}
		}
	}

	private boolean checkStructure(){
		if(getAlignment() == null){
			return false;
		}

		for(int i = 1; i < 4; i++){
			BlockPos checking = pos.offset(EnumFacing.DOWN, i);
			IBlockState state = world.getBlockState(checking);
			if(!state.getBlock().isAir(state, world, checking)){
				return false;
			}
		}
		for(int i = -1; i < 2; i++){
			BlockPos checking = pos.offset(EnumFacing.DOWN, 2).offset(getAlignment() == Axis.X ? EnumFacing.EAST : EnumFacing.NORTH, i);
			IBlockState state = world.getBlockState(checking);
			if(!state.getBlock().isAir(state, world, checking)){
				return false;
			}
		}
		return true;
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
			nbt.setString("name", owner.getName());
			if(owner.getId() != null){
				nbt.setLong("id_least", owner.getId().getLeastSignificantBits());
				nbt.setLong("id_most", owner.getId().getMostSignificantBits());
			}
		}

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		savedCoord = nbt.getDouble("coord");
		owner = nbt.hasKey("id_least") || nbt.hasKey("name") ? new GameProfileNonPicky(nbt.hasKey("id_least") ? new UUID(nbt.getLong("id_most"), nbt.getLong("id_least")) : null, nbt.hasKey("name") ? nbt.getString("name") : null) : null;
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
			entity.motionX = 0.0f;
			entity.motionY = 0.0f;
			entity.motionZ = 0.0f;
		}
	}

	private class MagicHandler implements IMagicHandler{

		@Override
		public void setMagic(MagicUnit mag){
			if(MagicElements.getElement(mag) == MagicElements.RIFT){
				magicPassed = true;
			}
		}
	}
}
