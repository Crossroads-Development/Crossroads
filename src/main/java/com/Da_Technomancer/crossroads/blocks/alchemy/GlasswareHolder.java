package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.GlasswareHolderTileEntity;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.*;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GlasswareHolder extends ContainerBlock{

	private static final AxisAlignedBB BB = new AxisAlignedBB(0.3125D, 0, 0.3125D, 0.6875D, 1, 0.6875D);

	public GlasswareHolder(){
		super(Material.IRON);
		String name = "glassware_holder";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(2);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new GlasswareHolderTileEntity();
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos){
		return BB;
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
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof GlasswareHolderTileEntity){
			((GlasswareHolderTileEntity) te).onBlockDestroyed(blockstate);
		}
		super.breakBlock(world, pos, blockstate);
	}

	@Override
	public int getMetaFromState(BlockState state){
		return (state.get(Properties.CRYSTAL) ? 1 : 0) + (state.get(Properties.ACTIVE) ? 2 : 0) + (state.get(EssentialsProperties.REDSTONE_BOOL) ? 4 : 0) + (state.get(Properties.CONTAINER_TYPE) ? 8 : 0);
	}

	@OnlyIn(Dist.CLIENT)
	public void initModel(){
		StateMapperBase glasswareMapper = new StateMapperBase(){			
			@Override
			protected ModelResourceLocation getModelResourceLocation(BlockState state){
				if(state.get(Properties.CONTAINER_TYPE)){
					return new ModelResourceLocation(Crossroads.MODID + ":glassware_holder_florence", getPropertyString(state.getProperties()));
				}else{
					return new ModelResourceLocation(Crossroads.MODID + ":glassware_holder_phial", getPropertyString(state.getProperties()));
				}
			}
		};
		ModelLoader.setCustomStateMapper(this, glasswareMapper);
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(Properties.CRYSTAL, (meta & 1) == 1).with(Properties.ACTIVE, (meta & 2) == 2).with(EssentialsProperties.REDSTONE_BOOL, (meta & 4) == 4).with(Properties.CONTAINER_TYPE, (meta & 8) == 8);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(worldIn.isBlockPowered(pos)){
			if(!state.get(EssentialsProperties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.with(EssentialsProperties.REDSTONE_BOOL, true));
				worldIn.updateComparatorOutputLevel(pos, this);
			}
		}else if(state.get(EssentialsProperties.REDSTONE_BOOL)){
			worldIn.setBlockState(pos, state.with(EssentialsProperties.REDSTONE_BOOL, false));
			worldIn.updateComparatorOutputLevel(pos, this);
		}
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.CRYSTAL, Properties.ACTIVE, EssentialsProperties.REDSTONE_BOOL, Properties.CONTAINER_TYPE);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!worldIn.isRemote){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof GlasswareHolderTileEntity){
				playerIn.setHeldItem(hand, ((GlasswareHolderTileEntity) te).rightClickWithItem(playerIn.getHeldItem(hand), playerIn.isSneaking(), playerIn, hand));
			}
		}
		return true;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return BlockFaceShape.UNDEFINED;
	}
}
