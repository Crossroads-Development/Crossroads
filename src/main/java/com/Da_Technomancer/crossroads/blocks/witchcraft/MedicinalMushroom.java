package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.world.CRWorldGen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import javax.annotation.Nullable;
import java.util.List;

public class MedicinalMushroom extends MushroomBlock{

	public MedicinalMushroom(){
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).noCollission().randomTicks().instabreak().lightLevel(state -> 1).sound(SoundType.GRASS), CRWorldGen.EMPTY_KEY);
		String name = "medicinal_mushroom";
		CRBlocks.queueForRegister(name, this);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_){
		return false;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(Component.translatable("tt.crossroads.medicinal_mushroom.desc"));
		tooltip.add(Component.translatable("tt.crossroads.medicinal_mushroom.spread"));
		tooltip.add(Component.translatable("tt.crossroads.medicinal_mushroom.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand){
		//These spread much faster than normal mushrooms
		//They also do NOT stop spreading when there are too many nearby (unlimited spread)
		final int spreadRange = 2;
		if(rand.nextDouble() < CRConfig.medicinalMushroomSpread.get()){
			BlockPos checkPos = pos.offset(rand.nextInt(2 * spreadRange + 1) - spreadRange, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(2 * spreadRange + 1) - spreadRange);
			if(world.isEmptyBlock(checkPos) && state.canSurvive(world, checkPos)){
				world.setBlockAndUpdate(checkPos, state);
			}
		}
	}
}
