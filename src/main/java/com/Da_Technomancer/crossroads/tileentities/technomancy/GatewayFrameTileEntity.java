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
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GatewayFrameTileEntity extends TileEntity implements ITickableTileEntity, IInfoTE{

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
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void addInfo(ArrayList<String> chat, PlayerEntity player, @Nullable Direction side, BlockRayTraceResult hit){
		chat.add("Temporal Entropy: " + EntropySavedData.getEntropy(world) + "%");
		BlockState state = world.getBlockState(pos);
		if(state.getBlock() == CRBlocks.gatewayFrame && state.get(EssentialsProperties.FACING).getAxis() == Direction.Axis.Y){
			BlockPos target = dialedCoord();
			if(target != null){
				chat.add("Dialed: " + target.getX() + ", " + target.getY() + ", " + target.getZ());
			}
		}
	}

	@Override
	public void tick(){
		if(!world.isRemote){
			if(BeamManager.beamStage == 1){
				if(EntropySavedData.getSeverity(world).getRank() >= EntropySavedData.Severity.DESTRUCTIVE.getRank()){
					FluxUtil.overloadFlux(world, pos);
					return;
				}

				cacheValid = false;
				if(world.getBlockState(pos).get(EssentialsProperties.FACING).getAxis() == Axis.Y){
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
							world.setBlockState(pos, CRBlocks.gatewayFrame.getDefaultState().with(EssentialsProperties.FACING, Direction.UP), 2);

							EntropySavedData.addEntropy(world, BeamManager.BEAM_TIME * FLUX_MAINTAIN);

							List<Entity> toTransport = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX() - 1, pos.getY() - 3, pos.getZ() - 1, pos.getX() + 1, pos.getY() - 1, pos.getZ() + 1), EntityPredicates.IS_ALIVE);
							if(!toTransport.isEmpty()){
								int currentDim = world.provider.getDimension();

								if(dim != currentDim && DimensionManager.getWorld(dim) == null){
									DimensionManager.initDimension(dim);
								}

								BlockPos target = dialedCoord();

								if(target != null){
									if(dim != currentDim){
										GatewayTeleporter porter = new GatewayTeleporter(DimensionManager.getWorld(dim), target.getX(), target.getY(), target.getZ());
										PlayerList playerList = world.getMinecraftServer().getPlayerList();

										for(Entity ent : toTransport){
											EntropySavedData.addEntropy(world, FLUX_TRANSPORT);


											if(ent instanceof ServerPlayerEntity){
												playerList.transferPlayerToDimension((ServerPlayerEntity) ent, dim, porter);
											}else{
												playerList.transferEntityToWorld(ent, currentDim, (WorldServer) world, DimensionManager.getWorld(dim), porter);
											}
										}
									}else{
										for(Entity ent : toTransport){
											EntropySavedData.addEntropy(world, FLUX_TRANSPORT);

											ent.setPosition(target.getX() + 0.5F, target.getY(), target.getZ());
										}
									}
								}
							}
						}else{
							world.setBlockState(pos, CRBlocks.gatewayFrame.getDefaultState().with(EssentialsProperties.FACING, Direction.DOWN), 2);
						}
					}else{
						world.setBlockState(pos, CRBlocks.gatewayFrame.getDefaultState().with(EssentialsProperties.FACING, Direction.DOWN), 2);
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
		BlockPos midPos = pos.offset(Direction.DOWN, 2);
		BlockState baseState = CRBlocks.gatewayFrame.getDefaultState();
		if(world.getBlockState(midPos.offset(Direction.EAST, 2)) == baseState.with(EssentialsProperties.FACING, Direction.WEST) && world.getBlockState(midPos.offset(Direction.WEST, 2)) == baseState.with(EssentialsProperties.FACING, Direction.EAST)){
			cached = Axis.X;
			return Axis.X;
		}
		if(world.getBlockState(midPos.offset(Direction.NORTH, 2)) == baseState.with(EssentialsProperties.FACING, Direction.SOUTH) && world.getBlockState(midPos.offset(Direction.SOUTH, 2)) == baseState.with(EssentialsProperties.FACING, Direction.NORTH)){
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
		TileEntity te = world.getTileEntity(pos.offset(Direction.DOWN, 2).offset(Direction.getFacingFromAxis(AxisDirection.NEGATIVE, align), 3));
		if(te != null && te.hasCapability(Capabilities.AXLE_CAPABILITY, Direction.getFacingFromAxis(AxisDirection.POSITIVE, align))){
			coords[0] = (int) Math.round(te.getCapability(Capabilities.AXLE_CAPABILITY, Direction.getFacingFromAxis(AxisDirection.POSITIVE, align)).getMotionData()[0]);
		}
		te = world.getTileEntity(pos.offset(Direction.DOWN, 2).offset(Direction.getFacingFromAxis(AxisDirection.POSITIVE, align), 3));
		if(te != null && te.hasCapability(Capabilities.AXLE_CAPABILITY, Direction.getFacingFromAxis(AxisDirection.NEGATIVE, align))){
			coords[2] = (int) Math.round(te.getCapability(Capabilities.AXLE_CAPABILITY, Direction.getFacingFromAxis(AxisDirection.NEGATIVE, align)).getMotionData()[0]);
		}
		te = world.getTileEntity(pos.offset(Direction.UP));
		if(te != null && te.hasCapability(Capabilities.AXLE_CAPABILITY, Direction.DOWN)){
			coords[1] = (int) Math.round(te.getCapability(Capabilities.AXLE_CAPABILITY, Direction.DOWN).getMotionData()[0]);
		}

		return new BlockPos(coords[0], coords[1], coords[2]);
	}

	@Override
	public boolean hasCapability(Capability<?> cap, Direction side){
		if(cap == Capabilities.BEAM_CAPABILITY && world.getBlockState(pos).get(EssentialsProperties.FACING).getAxis() == Axis.Y){
			return true;
		}

		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.BEAM_CAPABILITY && world.getBlockState(pos).get(EssentialsProperties.FACING).getAxis() == Axis.Y){
			return (T) magicHandler;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
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
	public void read(CompoundNBT nbt){
		super.read(nbt);
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
			if(entity instanceof PlayerEntity){
				((PlayerEntity) entity).addExperienceLevel(0);
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
