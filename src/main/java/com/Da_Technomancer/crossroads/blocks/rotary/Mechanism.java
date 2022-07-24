package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.api.rotary.IMechanism;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.rotary.mechanisms.MechanismTileEntity;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.redstone.IReadable;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Mechanism extends BaseEntityBlock implements IReadable{

//	private static final VoxelShape BREAK_ALL_BB = Block.makeCuboidShape(5, 5, 5, 11, 11, 11);

	public Mechanism(){
		super(CRBlocks.getMetalProperty().dynamicShape());
		//The variableOpacity flag is important
		//This trait name is poorly mapped by MCP- it controls whether BlockState instances are allowed to cache the results of several common methods
		//Most importantly, the getShape() method (and its variants). As this block varies shape with TE data instead of state, we cannot use the cache
		String name = "mechanism";
		CRBlocks.toRegister.put(name, this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new MechanismTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, MechanismTileEntity.TYPE);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player){
		BlockEntity te = world.getBlockEntity(pos);
		if(!(te instanceof MechanismTileEntity mte)){
			return ItemStack.EMPTY;
		}
		Vec3 relVec = target.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());

		for(int i = 0; i < 7; i++){
			if(mte.boundingBoxes[i] != null && voxelContains(mte.boundingBoxes[i], relVec)){
				return mte.members[i].getDrop(mte.mats[i]);
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		//Used for selection. Adds break all cube if the axle slot is empty
		BlockEntity te = worldIn.getBlockEntity(pos);
		if(te instanceof MechanismTileEntity){
//			MechanismTileEntity mte = (MechanismTileEntity) te;
//			if(mte.members[6] != null){
			return getCollisionShape(state, worldIn, pos, context);
//			}else{
//				return VoxelShapes.or(getCollisionShape(state, worldIn, pos, context), BREAK_ALL_BB);
//			}
		}
		return Shapes.empty();
//		return BREAK_ALL_BB;//Shouldn't happen unless network weirdness.
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter worldIn, BlockPos pos){
		return Shapes.empty();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		//There's some sort of issue with this being cached on a per-state level, and all mechanisms use the same blockstate

		BlockEntity te = worldIn.getBlockEntity(pos);
		if(te instanceof MechanismTileEntity mte){
			VoxelShape shape = Shapes.empty();
			for(VoxelShape s : mte.boundingBoxes){
				if(s != null){
					shape = Shapes.or(shape, s);
				}
			}
			return shape;
		}
		return Shapes.empty();//Shouldn't happen unless network weirdness.
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level worldIn, BlockPos pos, Player player, boolean willHarvest, FluidState fluid){
		RotaryUtil.increaseMasterKey(false);
		return super.onDestroyedByPlayer(state, worldIn, pos, player, willHarvest, fluid);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder){
		ArrayList<ItemStack> drops = new ArrayList<>();
		BlockEntity te = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if(te instanceof MechanismTileEntity){
			MechanismTileEntity mte = (MechanismTileEntity) te;
			for(int i = 0; i < 7; i++){
				if(mte.members[i] != null){
					drops.add(mte.members[i].getDrop(mte.mats[i]));
				}
			}
		}
		return drops;
	}

	/**
	 *
	 * @param te The TileEntity aimed at
	 * @param start Start vector, subtract position first
	 * @param end End vector, subtract position first
	 * @return The index of the aimed component, -1 if none, 6 for axle
	 */
	private int getAimedSide(MechanismTileEntity te, Vec3 start, Vec3 end){
		double minDist = Float.MAX_VALUE;
		int target = -1;
		for(int i = 0; i < te.boundingBoxes.length; i++){
			BlockHitResult res;
			if(te.boundingBoxes[i] != null && (res = te.boundingBoxes[i].clip(start, end, BlockPos.ZERO)) != null){
				double dist = res.getLocation().subtract(start).lengthSqr();
				if(dist < minDist){
					minDist = dist;
					target = i;
				}
			}
		}

		//Include break-all-cube if no axle
//		BlockRayTraceResult res;
//		if(te.boundingBoxes[6] == null && (res = BREAK_ALL_BB.rayTrace(start, end, BlockPos.ZERO)) != null){
//			if(res.getHitVec().subtract(start).lengthSquared() < minDist){
//				target = 7;
//			}
//		}
		return target;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos, false);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		RotaryUtil.increaseMasterKey(true);

		if(worldIn.isClientSide){
			return;
		}

		BlockEntity rawTE = worldIn.getBlockEntity(pos);
		if(!(rawTE instanceof MechanismTileEntity te)){
			return;
		}

		for(Direction side : Direction.values()){
			IMechanism<?> mechanism = te.members[side.get3DDataValue()];
			if(mechanism != null && mechanism.requiresSupport() && !RotaryUtil.solidToGears(worldIn, pos.relative(side), side.getOpposite())){
				popResource(worldIn, pos, mechanism.getDrop(te.mats[side.get3DDataValue()]));
				te.setMechanism(side.get3DDataValue(), null, null, null, false);
			}
		}
		if(te.members[0] == null && te.members[1] == null && te.members[2] == null && te.members[3] == null && te.members[4] == null && te.members[5] == null && te.members[6] == null){
			worldIn.destroyBlock(pos, false);
		}

		te.updateRedstone();
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
		if(ConfigUtil.isWrench(player.getItemInHand(hand))){
			BlockEntity te = worldIn.getBlockEntity(pos);
			if(te instanceof MechanismTileEntity gear){
				double reDist = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();//Player reach distance
				Vec3 start = new Vec3(player.xo, player.yo + (double) player.getEyeHeight(), player.zo).subtract(pos.getX(), pos.getY(), pos.getZ());
				Vec3 end = start.add(player.getViewVector(0F).x * reDist, player.getViewVector(0F).y * reDist, player.getViewVector(0F).z * reDist);

				int out = getAimedSide(gear, start, end);

				if(out == -1){
					//Didn't actually hit
					return InteractionResult.FAIL;
				}

				if(!worldIn.isClientSide){
					//Don't remove on the client side. If the client and server have slightly different player data (common occurrence when removing several quickly), removing on the client could lead to an invisible, solid mechanism that exists on the server

					//Spawn an item as applicable
					if(!player.isCreative()){
						popResource(worldIn, pos, gear.members[out].getDrop(gear.mats[out]));
					}

					gear.setMechanism(out, null, null, null, false);//Delete the destroyed mechanism
					//Block update on self, to check if any members are missing block support
					neighborChanged(state, worldIn, pos, this, pos, false);
					if(gear.members[0] == null && gear.members[1] == null && gear.members[2] == null && gear.members[3] == null && gear.members[4] == null && gear.members[5] == null && gear.members[6] == null){
						//If the mechanism is now empty, set it to air
						worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
					}
				}
				RotaryUtil.increaseMasterKey(!worldIn.isClientSide);
				return InteractionResult.SUCCESS;
			}


//			TileEntity te = worldIn.getTileEntity(pos);
//			if(te instanceof MechanismTileEntity){
//				MechanismTileEntity mte = (MechanismTileEntity) te;
//				if(mte.axleAxis != null){
//					RotaryUtil.increaseMasterKey(false);
//					if(!worldIn.isRemote){
//						mte.setMechanism(6, mte.members[6], mte.mats[6], Direction.Axis.values()[(mte.axleAxis.ordinal() + 1) % 3], false);
//					}
//					return true;
//				}
//			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(worldIn, pos, blockState));
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState blockState){
		BlockEntity te = world.getBlockEntity(pos);
		return te instanceof MechanismTileEntity ? ((MechanismTileEntity) te).getRedstone() : 0;
	}

	/**
	 * You would think this would be built into the VoxelShape class, but noooooooo
	 * "Contains" is defined as either inside the shape, or on the edge of the shape
	 * @param shape The voxelshape to check if contains the passed point
	 * @param point The 3 dimensional point to check if is contained by the passed shape
	 * @return Whether the passed VoxelShape contains the passed point
	 */
	public static boolean voxelContains(VoxelShape shape, Vec3 point){
		//We use a size 1 array because lambdas aren't supposed to use non-final variables
		final boolean[] contained = new boolean[1];
		shape.forAllBoxes((double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) -> {
			if(!contained[0]){
				contained[0] = xMin <= point.x && xMax >= point.x && yMin <= point.y && yMax >= point.y && zMin <= point.z && zMax >= point.z;
			}
		});
		return contained[0];
	}
}
