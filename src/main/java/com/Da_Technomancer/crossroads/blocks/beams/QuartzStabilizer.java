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
		//Very crude shape to match the angled model- may be worth refining later
		SHAPE[0] = box(4, 0, 4, 12, 16, 12);
		SHAPE[1] = box(4, 0, 4, 12, 16, 12);
		SHAPE[2] = box(4, 4, 0, 12, 12, 16);
		SHAPE[3] = box(4, 4, 0, 12, 12, 16);
		SHAPE[4] = box(0, 4, 0, 16, 12, 12);
		SHAPE[5] = box(0, 4, 0, 16, 12, 12);
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
