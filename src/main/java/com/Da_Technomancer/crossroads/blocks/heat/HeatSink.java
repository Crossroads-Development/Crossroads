package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatSinkTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class HeatSink extends BaseEntityBlock{

	public HeatSink(){
		super(CRBlocks.getMetalProperty());
		String name = "heat_sink";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new HeatSinkTileEntity(pos, state);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(ESConfig.isWrench(playerIn.getItemInHand(hand))){
			if(!worldIn.isClientSide){
				BlockEntity te = worldIn.getBlockEntity(pos);
				if(te instanceof HeatSinkTileEntity){
					int mode = ((HeatSinkTileEntity) te).cycleMode();
					MiscUtil.chatMessage(playerIn, new TranslatableComponent("tt.crossroads.heat_sink.loss", HeatSinkTileEntity.MODES[mode]));
				}
			}
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}


	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.heat_sink.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.heat_sink.rate", HeatSinkTileEntity.MODES[0], HeatSinkTileEntity.MODES[1], HeatSinkTileEntity.MODES[2], HeatSinkTileEntity.MODES[3], HeatSinkTileEntity.MODES[4]));
	}
}
