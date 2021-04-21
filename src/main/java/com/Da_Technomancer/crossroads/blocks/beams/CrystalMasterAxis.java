package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.beams.CrystalMasterAxisTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
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
		super(CRBlocks.getRockProperty());
		String name = "master_axis_crystal";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return defaultBlockState().setValue(ESProperties.FACING, context.getNearestLookingDirection().getOpposite());
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(ESConfig.isWrench(playerIn.getItemInHand(hand))){
			if(!worldIn.isClientSide){
				worldIn.setBlockAndUpdate(pos, state.cycle(ESProperties.FACING));
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new CrystalMasterAxisTileEntity();

	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot){
		return state.setValue(ESProperties.FACING, rot.rotate(state.getValue(ESProperties.FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn){
		return state.rotate(mirrorIn.getRotation(state.getValue(ESProperties.FACING)));
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos){
		TileEntity te = worldIn.getBlockEntity(pos);
		return te instanceof CrystalMasterAxisTileEntity ? RedstoneUtil.clampToVanilla(((CrystalMasterAxisTileEntity) te).getRedstone()) : 0;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.crystal_master_axis.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.crystal_master_axis.time"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.crystal_master_axis.reds"));
	}

	@Override
	public float read(World world, BlockPos pos, BlockState blockState){
		TileEntity te = world.getBlockEntity(pos);
		return te instanceof CrystalMasterAxisTileEntity ? ((CrystalMasterAxisTileEntity) te).getRedstone() : 0;
	}
}
