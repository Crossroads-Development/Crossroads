package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.fluid.RadiatorTileEntity;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class Radiator extends BaseEntityBlock{

	public Radiator(){
		super(CRBlocks.getMetalProperty());
		String name = "radiator";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new RadiatorTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, RadiatorTileEntity.TYPE);
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
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
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.radiator.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.radiator.heat", RadiatorTileEntity.FLUID_USE * (double) CRConfig.steamWorth.get() / 1000));
		tooltip.add(new TranslatableComponent("tt.crossroads.radiator.water", RadiatorTileEntity.FLUID_USE));
		tooltip.add(new TranslatableComponent("tt.crossroads.radiator.steam", RadiatorTileEntity.FLUID_USE));
	}
}
