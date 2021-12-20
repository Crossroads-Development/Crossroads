package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.fluid.WaterCentrifugeTileEntity;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class WaterCentrifuge extends BaseEntityBlock{
	
	public WaterCentrifuge(){
		super(CRBlocks.getMetalProperty());
		String name = "water_centrifuge";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new WaterCentrifugeTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, WaterCentrifugeTileEntity.TYPE);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		BlockEntity te;
		if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos)) instanceof MenuProvider){
			NetworkHooks.openGui((ServerPlayer) playerIn, (MenuProvider) te, pos);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof Container && newState.getBlock() != state.getBlock()){
			Containers.dropContents(world, pos, (Container) te);
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos){
		BlockEntity te = worldIn.getBlockEntity(pos);
		if(te instanceof WaterCentrifugeTileEntity && ((WaterCentrifugeTileEntity) te).isNeg()){
			return 1;
		}
		return 0;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.water_centrifuge.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.water_centrifuge.spin", WaterCentrifugeTileEntity.TIP_POINT));
		tooltip.add(new TranslatableComponent("tt.crossroads.water_centrifuge.reds"));
		tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.inertia", WaterCentrifugeTileEntity.INERTIA));
		tooltip.add(new TranslatableComponent("tt.crossroads.water_centrifuge.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
