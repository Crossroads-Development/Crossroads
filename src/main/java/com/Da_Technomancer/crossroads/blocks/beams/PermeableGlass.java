package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class PermeableGlass extends Block{

	public PermeableGlass(){
		super(Properties.create(Material.GLASS).hardnessAndResistance(.5F).sound(SoundType.GLASS).notSolid());
		String name = "permeable_glass";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader world, BlockPos pos){
		return 1.0F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader world, BlockPos pos){
		return true;
	}

	@Override
	public boolean causesSuffocation(BlockState state, IBlockReader world, BlockPos pos){
		return false;
	}

	@Override
	public boolean isNormalCube(BlockState p_220081_1_, IBlockReader p_220081_2_, BlockPos p_220081_3_){
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isSideInvisible(BlockState state, BlockState otherState, Direction dir) {
		return otherState.getBlock() == this || super.isSideInvisible(state, otherState, dir);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader player, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.beam_permeable"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.decor"));
	}
}
