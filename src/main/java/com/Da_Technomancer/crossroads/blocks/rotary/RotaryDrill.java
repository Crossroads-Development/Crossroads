package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.RotaryDrillTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.Block;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class RotaryDrill extends ContainerBlock{

	private static final VoxelShape[] SHAPES = new VoxelShape[6];

	static{
		SHAPES[0] = VoxelShapes.or(box(3, 10, 3, 13, 16, 13), box(5, 4, 5, 11, 10, 11), box(7, 0, 7, 9, 4, 9));
		SHAPES[1] = VoxelShapes.or(box(3, 0, 3, 13, 6, 13), box(5, 6, 5, 11, 12, 11), box(7, 12, 7, 9, 16, 9));
		SHAPES[2] = VoxelShapes.or(box(3, 3, 10, 13, 13, 16), box(5, 5, 4, 11, 11, 10), box(7, 7, 0, 9, 9, 4));
		SHAPES[3] = VoxelShapes.or(box(3, 3, 0, 13, 13, 6), box(5, 5, 6, 11, 11, 12), box(7, 7, 12, 9, 9, 16));
		SHAPES[4] = VoxelShapes.or(box(10, 3, 3, 16, 13, 13), box(4, 5, 5, 10, 11, 11), box(0, 7, 7, 4, 9, 9));
		SHAPES[5] = VoxelShapes.or(box(0, 3, 3, 6, 13, 13), box(6, 5, 5, 12, 11, 11), box(12, 7, 7, 16, 9, 9));
	}

	private final boolean golden;

	public RotaryDrill(boolean golden){
		super(CRBlocks.getMetalProperty());
		this.golden = golden;
		String name = "rotary_drill" + (golden ? "_gold" : "");
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new RotaryDrillTileEntity(golden);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return defaultBlockState().setValue(ESProperties.FACING, context.getNearestLookingDirection());
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPES[state.getValue(ESProperties.FACING).get3DDataValue()];
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
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos){
		return true;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		if(golden){
			tooltip.add(new TranslationTextComponent("tt.crossroads.drill.desc.gold"));
		}else{
			tooltip.add(new TranslationTextComponent("tt.crossroads.drill.desc"));
		}
		tooltip.add(new TranslationTextComponent("tt.crossroads.drill.power", golden ? RotaryDrillTileEntity.ENERGY_USE_GOLD : RotaryDrillTileEntity.ENERGY_USE_IRON));
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.inertia", RotaryDrillTileEntity.INERTIA[golden ? 1 : 0]));
		tooltip.add(new TranslationTextComponent("tt.crossroads.drill.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
