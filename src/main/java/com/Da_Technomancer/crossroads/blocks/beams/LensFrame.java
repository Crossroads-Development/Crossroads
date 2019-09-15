package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.tileentities.beams.LensFrameTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class LensFrame extends ContainerBlock{

	public LensFrame(){
		super(Material.ROCK);
		String name = "lens_frame";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setHardness(3);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new LensFrameTileEntity();
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state){
		return true;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return face.getAxis() != state.get(EssentialsProperties.AXIS) ? BlockFaceShape.CENTER_SMALL : BlockFaceShape.UNDEFINED;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos){
		return Math.min(15, (int) ((LensFrameTileEntity) worldIn.getTileEntity(pos)).getRedstone() / 3);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}

	@Override
	public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction blockFaceClickedOn, BlockRayTraceResult hit, int meta, LivingEntity placer){
		return getDefaultState().with(EssentialsProperties.AXIS, (placer == null) ? Axis.X : blockFaceClickedOn.getAxis());
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, EssentialsProperties.AXIS);
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(EssentialsProperties.AXIS, Direction.Axis.values()[meta]);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!worldIn.isRemote){
			ItemStack stack = playerIn.getHeldItem(hand);

			if(EssentialsConfig.isWrench(stack, false)){
				worldIn.setBlockState(pos, state.cycle(EssentialsProperties.AXIS));
				((LensFrameTileEntity) worldIn.getTileEntity(pos)).refresh();
			}else{
				TileEntity te = worldIn.getTileEntity(pos);
				if(!(te instanceof LensFrameTileEntity)){
					return false;
				}
				LensFrameTileEntity lens = (LensFrameTileEntity) te;
				ItemStack held = lens.getItem();
				if(!held.isEmpty()){
					if(!playerIn.inventory.addItemStackToInventory(held)){
						ItemEntity dropped = playerIn.dropItem(held, false);
						dropped.setNoPickupDelay();
						dropped.setOwner(playerIn.getName());
					}
					lens.setContents(0);
				}else if(!stack.isEmpty()){
					int id = lens.getIDFromItem(stack);
					if(id != 0){
						lens.setContents(id);
						stack.shrink(1);
					}
				}
			}
		}
		return true;
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof LensFrameTileEntity){
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((LensFrameTileEntity) te).getItem());
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public int getMetaFromState(BlockState state){
		return state.get(EssentialsProperties.AXIS).ordinal();
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	private static final AxisAlignedBB[] BB = new AxisAlignedBB[] {new AxisAlignedBB(.375D, 0, 0, .625D, 1, 1), new AxisAlignedBB(0, .375D, 0, 1, .625D, 1), new AxisAlignedBB(0, 0, .375D, 1, 1, .625D)};

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos){
		return BB[state.get(EssentialsProperties.AXIS).ordinal()];
	}

	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean stuff){
		addCollisionBoxToList(pos, mask, list, getBoundingBox(state, worldIn, pos));
	}
}
