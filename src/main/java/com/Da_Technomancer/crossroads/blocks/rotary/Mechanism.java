package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.IMechanism;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.List;

public class Mechanism extends ContainerBlock implements IReadable{

//	private static final VoxelShape BREAK_ALL_BB = Block.makeCuboidShape(5, 5, 5, 11, 11, 11);

	public Mechanism(){
		super(CRBlocks.METAL_PROPERTY.variableOpacity());
		//The variableOpacity flag is important
		//This trait name is poorly mapped by MCP- it controls whether BlockState instances are allowed to cache the results of several common methods
		//Most importantly, the getShape() method (and its variants). As this block varies shape with TE data instead of state, we cannot use the cache
		String name = "mechanism";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new MechanismTileEntity();
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player){
		TileEntity te = world.getTileEntity(pos);
		if(!(te instanceof MechanismTileEntity)){
			return ItemStack.EMPTY;
		}
		MechanismTileEntity mte = (MechanismTileEntity) te;
		Vector3d relVec = target.getHitVec().subtract(pos.getX(), pos.getY(), pos.getZ());

		for(int i = 0; i < 7; i++){
			if(mte.boundingBoxes[i] != null && voxelContains(mte.boundingBoxes[i], relVec)){
				return mte.members[i].getDrop(mte.mats[i]);
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		//Used for selection. Adds break all cube if the axle slot is empty
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof MechanismTileEntity){
//			MechanismTileEntity mte = (MechanismTileEntity) te;
//			if(mte.members[6] != null){
			return getCollisionShape(state, worldIn, pos, context);
//			}else{
//				return VoxelShapes.or(getCollisionShape(state, worldIn, pos, context), BREAK_ALL_BB);
//			}
		}
		return VoxelShapes.empty();
//		return BREAK_ALL_BB;//Shouldn't happen unless network weirdness.
	}

	@Override
	public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos){
		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		//There's some sort of issue with this being cached on a per-state level, and all mechanisms use the same blockstate

		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof MechanismTileEntity){
			MechanismTileEntity mte = (MechanismTileEntity) te;
			VoxelShape shape = VoxelShapes.empty();
			for(VoxelShape s : mte.boundingBoxes){
				if(s != null){
					shape = VoxelShapes.or(shape, s);
				}
			}
			return shape;
		}
		return VoxelShapes.empty();//Shouldn't happen unless network weirdness.
	}

	@Override
	public boolean removedByPlayer(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid){
		RotaryUtil.increaseMasterKey(false);
		return super.removedByPlayer(state, worldIn, pos, player, willHarvest, fluid);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder){
		ArrayList<ItemStack> drops = new ArrayList<>();
		TileEntity te = builder.get(LootParameters.BLOCK_ENTITY);
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
	private int getAimedSide(MechanismTileEntity te, Vector3d start, Vector3d end){
		double minDist = Float.MAX_VALUE;
		int target = -1;
		for(int i = 0; i < te.boundingBoxes.length; i++){
			BlockRayTraceResult res;
			if(te.boundingBoxes[i] != null && (res = te.boundingBoxes[i].rayTrace(start, end, BlockPos.ZERO)) != null){
				double dist = res.getHitVec().subtract(start).lengthSquared();
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
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos, false);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		RotaryUtil.increaseMasterKey(true);

		if(worldIn.isRemote){
			return;
		}

		TileEntity rawTE = worldIn.getTileEntity(pos);
		if(!(rawTE instanceof MechanismTileEntity)){
			return;
		}
		MechanismTileEntity te = (MechanismTileEntity) rawTE;

		for(Direction side : Direction.values()){
			IMechanism<?> mechanism = te.members[side.getIndex()];
			if(mechanism != null && mechanism.requiresSupport() && !RotaryUtil.solidToGears(worldIn, pos.offset(side), side.getOpposite())){
				spawnAsEntity(worldIn, pos, mechanism.getDrop(te.mats[side.getIndex()]));
				te.setMechanism(side.getIndex(), null, null, null, false);
			}
		}
		if(te.members[0] == null && te.members[1] == null && te.members[2] == null && te.members[3] == null && te.members[4] == null && te.members[5] == null && te.members[6] == null){
			worldIn.destroyBlock(pos, false);
		}

		te.updateRedstone();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit){
		if(ESConfig.isWrench(player.getHeldItem(hand))){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof MechanismTileEntity){
				MechanismTileEntity gear = (MechanismTileEntity) te;
				double reDist = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();//Player reach distance
				Vector3d start = new Vector3d(player.prevPosX, player.prevPosY + (double) player.getEyeHeight(), player.prevPosZ).subtract(pos.getX(), pos.getY(), pos.getZ());
				Vector3d end = start.add(player.getLook(0F).x * reDist, player.getLook(0F).y * reDist, player.getLook(0F).z * reDist);

				int out = getAimedSide(gear, start, end);

				if(out == -1){
					//Didn't actually hit
					return ActionResultType.FAIL;
				}

//				Break-all cube (index 7, BB that could be targeted to remove entire block) was removed
//				if(out == 7){
//					//Player hit the "break all cube" in the center
//					//Spawn drops, as applicable
//					for(int i = 0; i < 7; i++){
//						if(gear.members[i] != null){
//							spawnAsEntity(worldIn, pos, gear.members[i].getDrop(gear.mats[i]));
//						}
//					}
//
//					//Destroy the TE
//					worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
//				}else{

				if(!worldIn.isRemote){
					//Don't remove on the client side. If the client and server have slightly different player data (common occurrence when removing several quickly), removing on the client could lead to an invisible, solid mechanism that exists on the server

					//Spawn an item as applicable
					if(!player.isCreative()){
						spawnAsEntity(worldIn, pos, gear.members[out].getDrop(gear.mats[out]));
					}

					gear.setMechanism(out, null, null, null, false);//Delete the destroyed mechanism
					if(gear.members[0] == null && gear.members[1] == null && gear.members[2] == null && gear.members[3] == null && gear.members[4] == null && gear.members[5] == null && gear.members[6] == null){
						//If the mechanism is now empty, set it to air
						worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
					}
				}
//				}
				RotaryUtil.increaseMasterKey(!worldIn.isRemote);
				return ActionResultType.SUCCESS;
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
		return ActionResultType.PASS;
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(worldIn, pos, blockState));
	}

	@Override
	public float read(World world, BlockPos pos, BlockState blockState){
		TileEntity te = world.getTileEntity(pos);
		return te instanceof MechanismTileEntity ? ((MechanismTileEntity) te).getRedstone() : 0;
	}

	/**
	 * You would think this would be built into the VoxelShape class, but noooooooo
	 * "Contains" is defined as either inside the shape, or on the edge of the shape
	 * @param shape The voxelshape to check if contains the passed point
	 * @param point The 3 dimensional point to check if is contained by the passed shape
	 * @return Whether the passed VoxelShape contains the passed point
	 */
	public static boolean voxelContains(VoxelShape shape, Vector3d point){
		//We use a size 1 array because lambdas aren't supposed to use non-final variables
		final boolean[] contained = new boolean[1];
		shape.forEachBox((double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) -> {
			if(!contained[0]){
				contained[0] = xMin <= point.x && xMax >= point.x && yMin <= point.y && yMax >= point.y && zMin <= point.z && zMax >= point.z;
			}
		});
		return contained[0];
	}
}
