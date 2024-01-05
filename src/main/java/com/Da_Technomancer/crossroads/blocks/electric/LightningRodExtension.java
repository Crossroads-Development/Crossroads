package com.Da_Technomancer.crossroads.blocks.electric;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class LightningRodExtension extends LightningRodBlock{

	public LightningRodExtension(){
		super(BlockBehaviour.Properties.of().sound(SoundType.COPPER).mapColor(MapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(3.0F, 6.0F).noOcclusion());
		String name = "lightning_rod_extension";
		CRBlocks.queueForRegister(name, this);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.lightning_rod_extension.desc"));
		tooltip.add(Component.translatable("tt.crossroads.lightning_rod_extension.charging"));
		tooltip.add(Component.translatable("tt.crossroads.decoration"));
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag){
		if(fromPos != null && state.getBlock() == this){
			BlockState srcState = worldIn.getBlockState(fromPos);
			//Block updates from a charged lightning rod are assumed to represent a new lightning strike (note: there are some issues with this approach)
			//Lightning rod charge propagates in both directions
			//Lightning rod extension charge only propagates away from the source, to prevent infinite recursion
			if(srcState.getBlock() instanceof LightningRodBlock){
				boolean srcRod = srcState.getBlock() == Blocks.LIGHTNING_ROD;
				Direction srcFacing = srcState.getValue(FACING);
				Direction facing = state.getValue(FACING);
				Direction relativeDir = Direction.getNearest((float)(pos.getX() - fromPos.getX()), (float)(pos.getY() - fromPos.getY()), (float)(pos.getZ() - fromPos.getZ()));
				if(relativeDir.getAxis() == facing.getAxis() && srcState.getValue(POWERED) && (srcRod && facing.getAxis() == srcFacing.getAxis() || !srcRod && relativeDir == srcFacing.getOpposite())){
					onLightningStrike(state.setValue(FACING, relativeDir.getOpposite()), worldIn, pos);
				}
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(ConfigUtil.isWrench(playerIn.getItemInHand(hand))){
			if(!worldIn.isClientSide){
				Direction facing = state.getValue(FACING);
				Direction newFacing = Direction.from3DDataValue(facing.get3DDataValue() + 1);
				if(newFacing == facing.getOpposite()){
					newFacing = Direction.from3DDataValue(newFacing.get3DDataValue() + 1);
				}
				worldIn.setBlockAndUpdate(pos, state.setValue(FACING, newFacing));
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}
}
