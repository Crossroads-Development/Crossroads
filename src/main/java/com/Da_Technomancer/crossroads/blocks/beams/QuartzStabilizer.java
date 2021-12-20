package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.beams.QuartzStabilizerTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class QuartzStabilizer extends BeamBlock implements IReadable{

	private static final VoxelShape[] SHAPE = new VoxelShape[6];

	static{
		SHAPE[0] = Shapes.or(
				box(3, 7, 3, 13, 16, 13),
				box(4, 5, 4, 12, 7, 12),
				box(5, 3, 5, 11, 5, 11),
				box(6, 0, 6, 10, 3, 10));
		SHAPE[1] = Shapes.or(
				box(3, 0, 3, 13, 9, 13),
				box(4, 9, 4, 12, 11, 12),
				box(5, 11, 5, 11, 13, 11),
				box(6, 13, 6, 10, 16, 10));
		SHAPE[2] = Shapes.or(
				box(3, 3, 7, 13, 13, 16),
				box(4, 4, 5, 12, 12, 7),
				box(5, 5, 3, 11, 11, 5),
				box(6, 6, 0, 10, 10, 3));
		SHAPE[3] = Shapes.or(
				box(3, 3, 0, 13, 13, 9),
				box(4, 4, 9, 12, 12, 11),
				box(5, 5, 11, 11, 11, 13),
				box(6, 6, 13, 10, 10, 16));
		SHAPE[4] = Shapes.or(
				box(7, 3, 3, 16, 13, 13),
				box(5, 4, 4, 7, 12, 12),
				box(3, 5, 5, 5, 11, 11),
				box(0, 6, 6, 3, 10, 10));
		SHAPE[5] = Shapes.or(
				box(0, 3, 3, 9, 13, 13),
				box(9, 4, 4, 11, 12, 12),
				box(11, 5, 5, 13, 11, 11),
				box(13, 6, 6, 16, 10, 10));
	}

	public QuartzStabilizer(){
		super("quartz_stabilizer");
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new QuartzStabilizerTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, QuartzStabilizerTileEntity.TYPE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE[state.getValue(ESProperties.FACING).get3DDataValue()];
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(ESConfig.isWrench(playerIn.getItemInHand(hand))){
			if(!worldIn.isClientSide){
				BlockEntity te = worldIn.getBlockEntity(pos);
				if(!playerIn.isShiftKeyDown()){
					worldIn.setBlockAndUpdate(pos, state.cycle(ESProperties.FACING));
				}else if(te instanceof QuartzStabilizerTileEntity){
					MiscUtil.chatMessage(playerIn, new TranslatableComponent("tt.crossroads.quartz_stabilizer.setting", ((QuartzStabilizerTileEntity) te).adjustSetting()));
				}
			}
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.quartz_stabilizer.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.quartz_stabilizer.wrench"));
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState blockState){
		BlockEntity te = world.getBlockEntity(pos);
		return te instanceof QuartzStabilizerTileEntity ? ((QuartzStabilizerTileEntity) te).getRedstone() : 0;
	}
}
