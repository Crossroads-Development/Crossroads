package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.world.CRWorldGen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class MedicinalMushroom extends MushroomBlock{

	public MedicinalMushroom(){
		super(BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_CYAN).noCollission().randomTicks().instabreak().lightLevel(state -> 1).sound(SoundType.GRASS), () -> CRWorldGen.EMPTY);
		String name = "medicinal_mushroom";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter p_176473_1_, BlockPos p_176473_2_, BlockState p_176473_3_, boolean p_176473_4_){
		return false;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.medicinal_mushroom.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.medicinal_mushroom.spread"));
		tooltip.add(new TranslatableComponent("tt.crossroads.medicinal_mushroom.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random rand){
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
