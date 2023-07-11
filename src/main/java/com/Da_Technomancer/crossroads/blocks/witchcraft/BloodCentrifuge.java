package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.TEBlock;
import com.Da_Technomancer.essentials.api.redstone.IReadable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class BloodCentrifuge extends TEBlock implements IReadable{

	private static final VoxelShape SHAPE = Shapes.or(box(0, 0, 0, 16, 4, 16), box(6, 4, 6, 10, 16, 10));

	public BloodCentrifuge(){
		super(CRBlocks.getMetalProperty());
		String name = "blood_centrifuge";
		CRBlocks.queueForRegister(name, this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.CONTENTS, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.CONTENTS);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new BloodCentrifugeTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, BloodCentrifugeTileEntity.TYPE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE;
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		BlockEntity te;
		if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos)) instanceof MenuProvider){
			//Open the UI
			NetworkHooks.openScreen((ServerPlayer) playerIn, (MenuProvider) te, pos);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(Component.translatable("tt.crossroads.blood_centrifuge.desc", BloodCentrifugeTileEntity.HIGH_SPEED));
		tooltip.add(Component.translatable("tt.crossroads.blood_centrifuge.degradation"));
		tooltip.add(Component.translatable("tt.crossroads.blood_centrifuge.redstone"));
		tooltip.add(Component.translatable("tt.crossroads.boilerplate.inertia", BloodCentrifugeTileEntity.INERTIA));
		tooltip.add(Component.translatable("tt.crossroads.blood_centrifuge.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public float read(Level world, BlockPos blockPos, BlockState blockState){
		BlockEntity te = world.getBlockEntity(blockPos);
		if(te instanceof BloodCentrifugeTileEntity){
			return (float) ((BloodCentrifugeTileEntity) te).getTargetSpeed();
		}
		return 0;
	}
}
