package com.Da_Technomancer.crossroads.blocks;

import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CandleLilyPad extends BlockLilyPad{

	protected CandleLilyPad(){
		String name = "candle_lilypad";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		setSoundType(SoundType.PLANT);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos){
		return 14;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos){
		IBlockState soil = worldIn.getBlockState(pos.down());
		return super.canPlaceBlockAt(worldIn, pos) && soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, (IPlantable) Blocks.WATERLILY);
	}

	@Override
	public EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos){
		return EnumPlantType.Water;
	}

}
