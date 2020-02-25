package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.RotaryDrillTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
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
		SHAPES[0] = VoxelShapes.or(makeCuboidShape(3, 10, 3, 13, 16, 13), makeCuboidShape(5, 4, 5, 11, 10, 11), makeCuboidShape(7, 0, 7, 9, 4, 9));
		SHAPES[1] = VoxelShapes.or(makeCuboidShape(3, 0, 3, 13, 6, 13), makeCuboidShape(5, 6, 5, 11, 12, 11), makeCuboidShape(7, 12, 7, 9, 16, 9));
		SHAPES[2] = VoxelShapes.or(makeCuboidShape(3, 3, 10, 13, 13, 16), makeCuboidShape(5, 5, 4, 11, 11, 10), makeCuboidShape(7, 7, 0, 9, 9, 4));
		SHAPES[3] = VoxelShapes.or(makeCuboidShape(3, 3, 0, 13, 13, 6), makeCuboidShape(5, 5, 6, 11, 11, 12), makeCuboidShape(7, 7, 12, 9, 9, 16));
		SHAPES[4] = VoxelShapes.or(makeCuboidShape(10, 3, 3, 16, 13, 13), makeCuboidShape(4, 5, 5, 10, 11, 11), makeCuboidShape(0, 7, 7, 4, 9, 9));
		SHAPES[5] = VoxelShapes.or(makeCuboidShape(0, 3, 3, 6, 13, 13), makeCuboidShape(6, 5, 5, 12, 11, 11), makeCuboidShape(12, 7, 7, 16, 9, 9));
	}

	private final boolean golden;

	public RotaryDrill(boolean golden){
		super(Properties.create(Material.IRON).hardnessAndResistance(3).sound(SoundType.METAL));
		this.golden = golden;
		String name = "rotary_drill" + (golden ? "_gold" : "");
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new RotaryDrillTileEntity(golden);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return getDefaultState().with(ESProperties.FACING, context.getNearestLookingDirection());
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPES[state.get(ESProperties.FACING).getIndex()];
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(ESConfig.isWrench(playerIn.getHeldItem(hand))){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycle(ESProperties.FACING));
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos){
		return true;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		if(golden){
			tooltip.add(new TranslationTextComponent("tt.crossroads.drill.desc.gold"));
		}else{
			tooltip.add(new TranslationTextComponent("tt.crossroads.drill.desc"));
		}
		tooltip.add(new TranslationTextComponent("tt.crossroads.drill.power", RotaryDrillTileEntity.ENERGY_USE));
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.inertia", RotaryDrillTileEntity.INERTIA[golden ? 1 : 0]));
		tooltip.add(new TranslationTextComponent("tt.crossroads.drill.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
