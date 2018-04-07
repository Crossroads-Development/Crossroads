package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.GlasswareHolderTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GlasswareHolder extends BlockContainer{

	private static final AxisAlignedBB BB = new AxisAlignedBB(0.3125D, 0, 0.3125D, 0.6875D, 1, 0.6875D);

	public GlasswareHolder(){
		super(Material.IRON);
		String name = "glassware_holder";
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(2);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setSoundType(SoundType.METAL);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new GlasswareHolderTileEntity();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return BB;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer(){
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState blockstate){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof GlasswareHolderTileEntity){
			((GlasswareHolderTileEntity) te).onBlockDestroyed(blockstate);
		}
		super.breakBlock(world, pos, blockstate);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return (state.getValue(Properties.CRYSTAL) ? 1 : 0) + (state.getValue(Properties.ACTIVE) ? 2 : 0) + (state.getValue(Properties.REDSTONE_BOOL) ? 4 : 0) + (state.getValue(Properties.CONTAINER_TYPE) ? 8 : 0);
	}

	@SideOnly(Side.CLIENT)
	public void initModel(){
		StateMapperBase glasswareMapper = new StateMapperBase(){			
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state){
				if(state.getValue(Properties.CONTAINER_TYPE)){
					return new ModelResourceLocation(Main.MODID + ":glassware_holder_florence", getPropertyString(state.getProperties()));
				}else{
					return new ModelResourceLocation(Main.MODID + ":glassware_holder_phial", getPropertyString(state.getProperties()));
				}
			}
		};
		ModelLoader.setCustomStateMapper(this, glasswareMapper);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.CRYSTAL, (meta & 1) == 1).withProperty(Properties.ACTIVE, (meta & 2) == 2).withProperty(Properties.REDSTONE_BOOL, (meta & 4) == 4).withProperty(Properties.CONTAINER_TYPE, (meta & 8) == 8);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(worldIn.isBlockPowered(pos)){
			if(!state.getValue(Properties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.withProperty(Properties.REDSTONE_BOOL, true));
				worldIn.updateComparatorOutputLevel(pos, this);
			}
		}else if(state.getValue(Properties.REDSTONE_BOOL)){
			worldIn.setBlockState(pos, state.withProperty(Properties.REDSTONE_BOOL, false));
			worldIn.updateComparatorOutputLevel(pos, this);
		}
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {Properties.CRYSTAL, Properties.ACTIVE, Properties.REDSTONE_BOOL, Properties.CONTAINER_TYPE});
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof GlasswareHolderTileEntity){
				playerIn.setHeldItem(hand, ((GlasswareHolderTileEntity) te).rightClickWithItem(playerIn.getHeldItem(hand), playerIn.isSneaking()));
			}
		}
		return true;
	}
}
