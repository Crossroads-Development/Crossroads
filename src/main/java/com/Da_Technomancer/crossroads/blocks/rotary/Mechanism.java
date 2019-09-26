package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mechanism extends ContainerBlock{

	private static final AxisAlignedBB BREAK_ALL_BB = new AxisAlignedBB(.3125D, .3125D, .3125D, .6875D, .6875D, .6875D);

	public Mechanism(){
		super(Material.IRON);
		String name = "mechanism";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(1);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new MechanismTileEntity();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player){
		TileEntity te = world.getTileEntity(pos);
		if(!(te instanceof MechanismTileEntity)){
			return ItemStack.EMPTY;
		}
		MechanismTileEntity mte = (MechanismTileEntity) te;
		Vec3d relVec = target.hitVec.subtract(pos.getX(), pos.getY(), pos.getZ());

		for(int i = 0; i < 7; i++){
			if(mte.boundingBoxes[i] != null && mte.boundingBoxes[i].minX <= relVec.x && mte.boundingBoxes[i].maxX >= relVec.x && mte.boundingBoxes[i].minY <= relVec.y && mte.boundingBoxes[i].maxY >= relVec.y && mte.boundingBoxes[i].minZ <= relVec.z && mte.boundingBoxes[i].maxZ >= relVec.z){
				return mte.members[i].getDrop(mte.mats[i]);
			}
		}

		return ItemStack.EMPTY;
	}


	@Override
	@OnlyIn(Dist.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(BlockState state, World worldIn, BlockPos pos){
		TileEntity te = worldIn.getTileEntity(pos);
		if(!(te instanceof MechanismTileEntity)){
			return BREAK_ALL_BB.offset(pos);
		}
		MechanismTileEntity mte = (MechanismTileEntity) te;
		PlayerEntity play = Minecraft.getInstance().player;
		float reDist = Minecraft.getInstance().playerController.getBlockReachDistance();
		Vec3d start = play.getEyePosition(0F).subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
		Vec3d end = start.add(play.getLook(0F).x * reDist, play.getLook(0F).y * reDist, play.getLook(0F).z * reDist);
		int out = getAimedSide(mte, start, end, true);
		return (out == -1 || out == 7 ? BREAK_ALL_BB : mte.boundingBoxes[out]).offset(pos);
	}

	@Override
	@Nullable
	public RayTraceResult collisionRayTrace(BlockState state, World worldIn, BlockPos pos, Vec3d start, Vec3d end){
		TileEntity te = worldIn.getTileEntity(pos);
		if(!(te instanceof MechanismTileEntity)){
			return null;
		}
		MechanismTileEntity mte = (MechanismTileEntity) te;
		start = start.subtract(pos.getX(), pos.getY(), pos.getZ());
		end = end.subtract(pos.getX(), pos.getY(), pos.getZ());
		int out = getAimedSide(mte, start, end, true);
		if(out == -1){
			return null;
		}else{
			RayTraceResult untransformed = (out == 7 ? BREAK_ALL_BB : mte.boundingBoxes[out]).calculateIntercept(start, end);
			return new RayTraceResult(untransformed.hitVec.add((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()), untransformed.sideHit, pos);
		}
	}

	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean nothingProbably){
		TileEntity te = worldIn.getTileEntity(pos);
		if(!(te instanceof MechanismTileEntity)){
			return;
		}
		MechanismTileEntity mte = (MechanismTileEntity) te;
		for(int i = 0; i < 7; i++){
			if(mte.boundingBoxes[i] != null){
				addCollisionBoxToList(pos, mask, list, mte.boundingBoxes[i]);
			}
		}
	}

	@Override
	public boolean removedByPlayer(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, boolean canHarvest){
		RotaryUtil.increaseMasterKey(false);
		if(worldIn.isRemote){
			return false;
		}

		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof MechanismTileEntity){
			MechanismTileEntity gear = (MechanismTileEntity) te;
			float reDist = player.isCreative() ? 5F : 4.5F;
			Vec3d start = new Vec3d(player.prevPosX, player.prevPosY + (double) player.getEyeHeight(), player.prevPosZ).subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
			Vec3d end = start.add(player.getLook(0F).x * reDist, player.getLook(0F).y * reDist, player.getLook(0F).z * reDist);

			int out = getAimedSide(gear, start, end, true);

			if(out == -1){
				return false;
			}

			if(out == 7){
				if(canHarvest){
					for(int i = 0; i < 7; i++){
						if(gear.members[i] != null){
							spawnAsEntity(worldIn, pos, gear.members[i].getDrop(gear.mats[i]));
						}
					}
				}
				worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
				return true;
			}else{
				if(canHarvest){
					spawnAsEntity(worldIn, pos, gear.members[out].getDrop(gear.mats[out]));
				}
				gear.setMechanism(out, null, null, null, false);
				if(gear.members[0] == null && gear.members[1] == null && gear.members[2] == null && gear.members[3] == null && gear.members[4] == null && gear.members[5] == null && gear.members[6] == null){
					worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
					return true;
				}

				return false;
			}
		}else{
			return super.removedByPlayer(state, worldIn, pos, player, canHarvest);
		}
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, BlockState state, int fortune){
		MechanismTileEntity te = (MechanismTileEntity) world.getTileEntity(pos);
		if(te != null){
			for(int i = 0; i < 7; i++){
				if(te.members[i] != null){
					drops.add(te.members[i].getDrop(te.mats[i]));
				}
			}
		}
	}

	/**
	 *
	 * @param te The TileEntity aimed at
	 * @param start Start vector, subtract position first
	 * @param end End vector, subtract position first
	 * @param useCenter whether or not to include the breakall cube when raytracing
	 * @return The index of the aimed component, 7 if the breakall cube, -1 if none
	 */
	private int getAimedSide(MechanismTileEntity te, Vec3d start, Vec3d end, boolean useCenter){
		ArrayList<AxisAlignedBB> list = new ArrayList<>();
		Collections.addAll(list, te.boundingBoxes);
		if(useCenter && list.get(6) == null){
			list.add(BREAK_ALL_BB);
		}
		AxisAlignedBB aimed = BlockUtil.selectionRaytrace(list, start, end);

		return aimed == null ? -1 : list.indexOf(aimed);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		RotaryUtil.increaseMasterKey(true);

		if(worldIn.isRemote){
			return;
		}

		MechanismTileEntity te = (MechanismTileEntity) worldIn.getTileEntity(pos);

		for(Direction side : Direction.VALUES){
			if(te.members[side.getIndex()] != null && !RotaryUtil.solidToGears(worldIn, pos.offset(side), side.getOpposite())){
				spawnAsEntity(worldIn, pos, te.members[side.getIndex()].getDrop(te.mats[side.getIndex()]));
				te.setMechanism(side.getIndex(), null, null, null, false);
			}
		}
		if(te.members[0] == null && te.members[1] == null && te.members[2] == null && te.members[3] == null && te.members[4] == null && te.members[5] == null && te.members[6] == null){
			worldIn.destroyBlock(pos, false);
		}

		te.updateRedstone();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean shouldSideBeRendered(BlockState blockState, IBlockAccess blockAccess, BlockPos pos, Direction side){
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isSideSolid(BlockState state, IBlockAccess world, BlockPos pos, Direction side){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof MechanismTileEntity){
			MechanismTileEntity mte = (MechanismTileEntity) te;
			return mte.members[6] != null && mte.axleAxis == side.getAxis();
		}
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof MechanismTileEntity){
				MechanismTileEntity mte = (MechanismTileEntity) te;
				if(mte.axleAxis != null){
					RotaryUtil.increaseMasterKey(false);
					if(!worldIn.isRemote){
						mte.setMechanism(6, mte.members[6], mte.mats[6], Direction.Axis.values()[(mte.axleAxis.ordinal() + 1) % 3], false);
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos){
		TileEntity te = worldIn.getTileEntity(pos);
		return te instanceof MechanismTileEntity ? (int) Math.min(15, ((MechanismTileEntity) te).getRedstone()) : 0;
	}
}
