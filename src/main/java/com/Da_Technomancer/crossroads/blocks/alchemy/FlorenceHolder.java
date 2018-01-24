package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyGlasswareHolderTE;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.FlorenceHolderTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FlorenceHolder extends BlockContainer{

	private static final AxisAlignedBB BB = new AxisAlignedBB(0.3125D, 0, 0.3125D, 0.6875D, 1, 0.6875D);

	public FlorenceHolder(){
		super(Material.IRON);
		String name = "florence_holder";
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(2);
		setCreativeTab(ModItems.tabCrossroads);
		setSoundType(SoundType.METAL);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new FlorenceHolderTileEntity();
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
		if(te instanceof AlchemyGlasswareHolderTE){
			((AlchemyGlasswareHolderTE) te).onBlockDestoyed(blockstate);
		}
		super.breakBlock(world, pos, blockstate);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return (state.getValue(Properties.LIGHT) ? 1 : 0) + (state.getValue(Properties.ACTIVE) ? 2 : 0) + (state.getValue(Properties.REDSTONE_BOOL) ? 4 : 0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.LIGHT, (meta & 1) == 1).withProperty(Properties.ACTIVE, (meta & 2) == 2).withProperty(Properties.REDSTONE_BOOL, meta >> 2 == 1);
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
		//On this device, light is being re-used. True means crystal, false means glass. 
		return new BlockStateContainer(this, new IProperty[] {Properties.LIGHT, Properties.ACTIVE, Properties.REDSTONE_BOOL});
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof AlchemyGlasswareHolderTE){
				playerIn.setHeldItem(hand, ((AlchemyGlasswareHolderTE) te).rightClickWithItem(playerIn.getHeldItem(hand)));
			}
		}
		return true;
	}
}
