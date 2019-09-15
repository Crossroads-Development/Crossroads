package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.WindTurbineTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class WindTurbine extends ContainerBlock{

	public WindTurbine(){
		super(Material.WOOD);
		String name = "wind_turbine";
		setTranslationKey(name);
		setHardness(2);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new WindTurbineTileEntity(true);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof WindTurbineTileEntity){
					((WindTurbineTileEntity) te).resetCache();
				}
				worldIn.setBlockState(pos, state.with(Properties.HORIZ_FACING, state.get(Properties.HORIZ_FACING).rotateY()));
			}
			return true;
		}
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hit, int meta, LivingEntity placer, Hand hand){
		return getDefaultState().with(Properties.HORIZ_FACING, placer.getHorizontalFacing());
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.HORIZ_FACING);
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(Properties.HORIZ_FACING, Direction.byIndex(meta));
	}

	@Override
	public int getMetaFromState(BlockState state){
		return state.get(Properties.HORIZ_FACING).getIndex();
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockAccess world, BlockPos pos, Direction side){
		return true;
	}

	@Override
	public boolean canProvidePower(BlockState state){
		return true;
	}

	@Override
	public int getWeakPower(BlockState state, IBlockAccess blockAccess, BlockPos pos, Direction side){
		TileEntity te = blockAccess.getTileEntity(pos);
		if(te instanceof WindTurbineTileEntity){
			return ((WindTurbineTileEntity) te).getRedstoneOutput();
		}else{
			return 0;
		}
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public boolean isNormalCube(BlockState state, IBlockAccess world, BlockPos pos){
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Produces: Up to " + 2 * WindTurbineTileEntity.POWER_PER_LEVEL + "J/t");
		tooltip.add("Useless above " + WindTurbineTileEntity.MAX_SPEED + "rad/s");
		tooltip.add("I: 200");
		tooltip.add("Randomly changes power direction with the wind");
		tooltip.add("Requires a view of the sky and a 5x5 empty space");
	}
}
