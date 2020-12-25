package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.BeamCannonTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BeamCannon extends ContainerBlock{

	private static final VoxelShape[] SHAPES = new VoxelShape[6];
	static{
		SHAPES[0] = VoxelShapes.or(makeCuboidShape(0, 7, 0, 16, 16, 16));
		SHAPES[1] = VoxelShapes.or(makeCuboidShape(0, 0, 0, 16, 9, 16));
		SHAPES[2] = VoxelShapes.or(makeCuboidShape(0, 0, 7, 16, 16, 16));
		SHAPES[3] = VoxelShapes.or(makeCuboidShape(0, 0, 0, 16, 16, 9));
		SHAPES[4] = VoxelShapes.or(makeCuboidShape(7, 0, 0, 16, 16, 16));
		SHAPES[5] = VoxelShapes.or(makeCuboidShape(0, 0, 0, 9, 16, 16));
	}

	public BeamCannon(){
		super(CRBlocks.getMetalProperty());
		String name = "beam_cannon";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		setDefaultState(getDefaultState());
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPES[state.get(ESProperties.FACING).getIndex()];
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return getDefaultState().with(ESProperties.FACING, context.getFace());
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		ItemStack held = playerIn.getHeldItem(hand);
		if(ESConfig.isWrench(held)){
			if(playerIn.isSneaking()){
				//Sneak clicking- lock/unlock
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof BeamCannonTileEntity){
					((BeamCannonTileEntity) te).updateLock(playerIn);
				}
				return ActionResultType.SUCCESS;
			}else{
				//Rotate this machine
				worldIn.setBlockState(pos, state.func_235896_a_(ESProperties.FACING));
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new BeamCannonTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_cannon.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_cannon.angle"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_cannon.lockable"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.inertia", BeamCannonTileEntity.INERTIA));
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_cannon.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
