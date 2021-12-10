package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.beams.QuartzStabilizerTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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

public class QuartzStabilizer extends BeamBlock implements IReadable{

	private static final VoxelShape[] SHAPE = new VoxelShape[6];

	static{
		SHAPE[0] = VoxelShapes.or(
				box(3, 7, 3, 13, 16, 13),
				box(4, 5, 4, 12, 7, 12),
				box(5, 3, 5, 11, 5, 11),
				box(6, 0, 6, 10, 3, 10));
		SHAPE[1] = VoxelShapes.or(
				box(3, 0, 3, 13, 9, 13),
				box(4, 9, 4, 12, 11, 12),
				box(5, 11, 5, 11, 13, 11),
				box(6, 13, 6, 10, 16, 10));
		SHAPE[2] = VoxelShapes.or(
				box(3, 3, 7, 13, 13, 16),
				box(4, 4, 5, 12, 12, 7),
				box(5, 5, 3, 11, 11, 5),
				box(6, 6, 0, 10, 10, 3));
		SHAPE[3] = VoxelShapes.or(
				box(3, 3, 0, 13, 13, 9),
				box(4, 4, 9, 12, 12, 11),
				box(5, 5, 11, 11, 11, 13),
				box(6, 6, 13, 10, 10, 16));
		SHAPE[4] = VoxelShapes.or(
				box(7, 3, 3, 16, 13, 13),
				box(5, 4, 4, 7, 12, 12),
				box(3, 5, 5, 5, 11, 11),
				box(0, 6, 6, 3, 10, 10));
		SHAPE[5] = VoxelShapes.or(
				box(0, 3, 3, 9, 13, 13),
				box(9, 4, 4, 11, 12, 12),
				box(11, 5, 5, 13, 11, 11),
				box(13, 6, 6, 16, 10, 10));
	}

	public QuartzStabilizer(){
		super("quartz_stabilizer");
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new QuartzStabilizerTileEntity();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPE[state.getValue(ESProperties.FACING).get3DDataValue()];
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(ESConfig.isWrench(playerIn.getItemInHand(hand))){
			if(!worldIn.isClientSide){
				TileEntity te = worldIn.getBlockEntity(pos);
				if(!playerIn.isShiftKeyDown()){
					worldIn.setBlockAndUpdate(pos, state.cycle(ESProperties.FACING));
				}else if(te instanceof QuartzStabilizerTileEntity){
					MiscUtil.chatMessage(playerIn, new TranslationTextComponent("tt.crossroads.quartz_stabilizer.setting", ((QuartzStabilizerTileEntity) te).adjustSetting()));
				}
			}
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.quartz_stabilizer.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.quartz_stabilizer.wrench"));
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, World worldIn, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(worldIn, pos, blockState));
	}

	@Override
	public float read(World world, BlockPos pos, BlockState blockState){
		TileEntity te = world.getBlockEntity(pos);
		return te instanceof QuartzStabilizerTileEntity ? ((QuartzStabilizerTileEntity) te).getRedstone() : 0;
	}
}
