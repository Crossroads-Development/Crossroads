package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.beams.CrystalMasterAxisTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class CrystalMasterAxis extends ContainerBlock implements IReadable{
	
	public CrystalMasterAxis(){
		super(Properties.create(Material.ROCK).hardnessAndResistance(3).sound(SoundType.STONE));
		String name = "master_axis_crystal";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return getDefaultState().with(EssentialsProperties.FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof CrystalMasterAxisTileEntity){
			((CrystalMasterAxisTileEntity) te).disconnect();
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand))){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof CrystalMasterAxisTileEntity){
				((CrystalMasterAxisTileEntity) te).disconnect();
			}
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycle(EssentialsProperties.FACING));
			}
			return true;
		}
		return false;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(EssentialsProperties.FACING);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new CrystalMasterAxisTileEntity();

	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot){
		return state.with(EssentialsProperties.FACING, rot.rotate(state.get(EssentialsProperties.FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn){
		return state.rotate(mirrorIn.toRotation(state.get(EssentialsProperties.FACING)));
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos){
		TileEntity te = worldIn.getTileEntity(pos);
		return te instanceof CrystalMasterAxisTileEntity ? RedstoneUtil.clampToVanilla(((CrystalMasterAxisTileEntity) te).getRedstone()) : 0;
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state){
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.crystal_master_axis.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.crystal_master_axis.time"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.crystal_master_axis.reds"));
	}

	@Override
	public float read(World world, BlockPos pos, BlockState blockState){
		TileEntity te = world.getTileEntity(pos);
		return te instanceof CrystalMasterAxisTileEntity ? ((CrystalMasterAxisTileEntity) te).getRedstone() : 0;
	}
}
