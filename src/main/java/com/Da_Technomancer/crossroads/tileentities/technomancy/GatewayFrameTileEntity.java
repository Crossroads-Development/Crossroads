package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.FlexibleGameProfile;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.technomancy.EntropySavedData;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
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

	public static final int FLUX_MAINTAIN = 1;
	public static final int FLUX_TRANSPORT = 16;

	private final IBeamHandler magicHandler = new BeamHandler();
	private FlexibleGameProfile owner;
	private boolean cacheValid;
	private EnumBeamAlignments element;
	private Axis cached;

	public void resetCache(){
		cacheValid = false;
	}

	public void setOwner(FlexibleGameProfile owner){
		this.owner = owner;
		markDirty();
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		chat.add("Temporal Entropy: " + EntropySavedData.getEntropy(world) + "%");
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() == ModBlocks.gatewayFrame && state.getValue(EssentialsProperties.FACING).getAxis() == EnumFacing.Axis.Y){
			BlockPos target = dialedCoord();
			if(target != null){
				chat.add("Dialed: " + target.getX() + ", " + target.getY() + ", " + target.getZ());
			}
		}
	}

	@Override
	public void update(){
		if(!world.isRemote){
			if(BeamManager.beamStage == 1){
				if(EntropySavedData.getSeverity(world).getRank() >= EntropySavedData.Severity.DESTRUCTIVE.getRank()){
					FluxUtil.overloadFlux(world, pos);
					return;
				}

				cacheValid = false;
				if(world.getBlockState(pos).getValue(EssentialsProperties.FACING).getAxis() == Axis.Y){
					if(owner != null && element != null && getAlignment() != null){
						Integer dim = null;
						switch(element){
							case VOID:
								dim = 1;
								break;
							case ENERGY:
								dim = -1;
								break;
							case POTENTIAL:
								dim = 0;
								break;
							case RIFT:
								dim = ModDimensions.getDimForPlayer(owner);
								break;
							default:
								break;
						}

						if(dim != null){
							world.setBlockState(pos, ModBlocks.gatewayFrame.getDefaultState().withProperty(EssentialsProperties.FACING, EnumFacing.UP), 2);

							EntropySavedData.addEntropy(world, BeamManager.BEAM_TIME * FLUX_MAINTAIN);

							List<Entity> toTransport = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX() - 1, pos.getY() - 3, pos.getZ() - 1, pos.getX() + 1, pos.getY() - 1, pos.getZ() + 1), EntitySelectors.IS_ALIVE);
							if(!toTransport.isEmpty()){
								int currentDim = world.provider.getDimension();

								if(dim != currentDim && DimensionManager.getWorld(dim) == null){
									DimensionManager.initDimension(dim);
								}

								BlockPos target = dialedCoord();

								GatewayTeleporter porter = new GatewayTeleporter(DimensionManager.getWorld(dim), target.getX(), target.getY(), target.getZ());

								PlayerList playerList = world.getMinecraftServer().getPlayerList();
								for(Entity ent : toTransport){
									EntropySavedData.addEntropy(world, FLUX_TRANSPORT);
									if(ent instanceof EntityPlayerMP){
										playerList.transferPlayerToDimension((EntityPlayerMP) ent, dim, porter);
									}else{
										playerList.transferEntityToWorld(ent, currentDim, (WorldServer) world, DimensionManager.getWorld(dim), porter);
									}
								}
							}
						}else{
							world.setBlockState(pos, ModBlocks.gatewayFrame.getDefaultState().withProperty(EssentialsProperties.FACING, EnumFacing.DOWN), 2);
						}
					}else{
						world.setBlockState(pos, ModBlocks.gatewayFrame.getDefaultState().withProperty(EssentialsProperties.FACING, EnumFacing.DOWN), 2);
					}
				}
				element = null;
			}
		}
	}
	private static final AxisAlignedBB RENDER_BOX = new AxisAlignedBB(-1, -3, -1, 2, 0, 2);


	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return RENDER_BOX.offset(pos);
	}

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
		if(world.getBlockState(midPos.offset(EnumFacing.EAST, 2)) == baseState.withProperty(EssentialsProperties.FACING, EnumFacing.WEST) && world.getBlockState(midPos.offset(EnumFacing.WEST, 2)) == baseState.withProperty(EssentialsProperties.FACING, EnumFacing.EAST)){
			cached = Axis.X;
			return Axis.X;
		}
		if(world.getBlockState(midPos.offset(EnumFacing.NORTH, 2)) == baseState.withProperty(EssentialsProperties.FACING, EnumFacing.SOUTH) && world.getBlockState(midPos.offset(EnumFacing.SOUTH, 2)) == baseState.withProperty(EssentialsProperties.FACING, EnumFacing.NORTH)){
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
		if(te != null && te.hasCapability(Capabilities.AXLE_CAPABILITY, EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, align))){
			coords[0] = (int) Math.round(te.getCapability(Capabilities.AXLE_CAPABILITY, EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, align)).getMotionData()[0]);
		}
		te = world.getTileEntity(pos.offset(EnumFacing.DOWN, 2).offset(EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, align), 3));
		if(te != null && te.hasCapability(Capabilities.AXLE_CAPABILITY, EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, align))){
			coords[2] = (int) Math.round(te.getCapability(Capabilities.AXLE_CAPABILITY, EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, align)).getMotionData()[0]);
		}
		te = world.getTileEntity(pos.offset(EnumFacing.UP));
		if(te != null && te.hasCapability(Capabilities.AXLE_CAPABILITY, EnumFacing.DOWN)){
			coords[1] = (int) Math.round(te.getCapability(Capabilities.AXLE_CAPABILITY, EnumFacing.DOWN).getMotionData()[0]);
		}

		return new BlockPos(coords[0], coords[1], coords[2]);
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.BEAM_CAPABILITY && world.getBlockState(pos).getValue(EssentialsProperties.FACING).getAxis() == Axis.Y){
			return true;
		}

		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.BEAM_CAPABILITY && world.getBlockState(pos).getValue(EssentialsProperties.FACING).getAxis() == Axis.Y){
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
		owner = FlexibleGameProfile.readFromNBT(nbt, "own", world.getMinecraftServer() == null ? null : world.getMinecraftServer().getPlayerProfileCache());
		if(owner != null && owner.isNewlyCompleted()){
			markDirty();
		}
	}

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

	private class BeamHandler implements IBeamHandler{

		@Override
		public void setMagic(BeamUnit mag){
			if(mag != null){
				element = EnumBeamAlignments.getAlignment(mag);
				markDirty();
			}
		}
	}
}
