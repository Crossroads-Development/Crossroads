package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.SamplingBenchTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
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

public class SamplingBench extends BlockContainer{

	private static final AxisAlignedBB BB = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.75D, 1D);

	public SamplingBench(){
		super(Material.WOOD);
		String name = "sampling_bench";
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(2);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setSoundType(SoundType.WOOD);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().withProperty(Properties.ACTIVE, false).withProperty(Properties.CRYSTAL, false));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new SamplingBenchTileEntity();
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
		if(te instanceof SamplingBenchTileEntity){
			SamplingBenchTileEntity bench = (SamplingBenchTileEntity) te;
			InventoryHelper.spawnItemStack(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, bench.getGlassware());
		}
		super.breakBlock(world, pos, blockstate);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return (state.getValue(Properties.CRYSTAL) ? 1 : 0) + (state.getValue(Properties.ACTIVE) ? 2 : 0) + (state.getValue(Properties.HORIZONTAL_FACING).getHorizontalIndex() << 2);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.CRYSTAL, (meta & 1) == 1).withProperty(Properties.ACTIVE, (meta & 2) == 2).withProperty(Properties.HORIZONTAL_FACING, EnumFacing.getHorizontal(meta >> 2));
	}
	
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getDefaultState().withProperty(Properties.HORIZONTAL_FACING, (placer == null) ? EnumFacing.NORTH : placer.getHorizontalFacing().getOpposite());
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.CRYSTAL, Properties.ACTIVE, Properties.HORIZONTAL_FACING);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			playerIn.openGui(Main.instance, GuiHandler.SAMPLING_BENCH_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}
}
