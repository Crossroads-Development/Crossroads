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
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class QuartzStabilizer extends BeamBlock implements IReadable{

	private static final VoxelShape[] SHAPE = new VoxelShape[6];

	static{
		//TODO Very crude shape to match the angled model- may be worth refining later
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
	@OnlyIn(Dist.CLIENT)
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
