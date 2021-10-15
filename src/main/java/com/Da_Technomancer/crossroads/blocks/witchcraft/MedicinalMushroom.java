package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class MedicinalMushroom extends MushroomBlock{

	public MedicinalMushroom(){
		super(BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_CYAN).noCollission().randomTicks().instabreak().sound(SoundType.GRASS), EmptyFeature::new);
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
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.medicinal_mushroom.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.medicinal_mushroom.quip").setStyle(MiscUtil.TT_QUIP));

	}

	private static class EmptyFeature extends ConfiguredFeature{

		public EmptyFeature(){
			super(null, null);
		}

		@Override
		public boolean place(WorldGenLevel pReader, ChunkGenerator pChunkGenerator, Random pRandom, BlockPos pPos){
			//No-op
			return false;
		}
	}
}
