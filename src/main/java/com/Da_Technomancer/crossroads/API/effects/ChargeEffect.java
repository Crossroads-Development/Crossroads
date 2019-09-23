package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CrossroadsConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ChargeEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
		TileEntity te = worldIn.getTileEntity(pos);
		LazyOptional<IEnergyStorage> opt;
		if(te != null && (opt = te.getCapability(CapabilityEnergy.ENERGY, dir)).isPresent()){
			opt.orElseThrow(NullPointerException::new).receiveEnergy(CrossroadsConfig.fePerCharge.get(), false);
			return;
		}

		if(mult >= 16){
			((ServerWorld) worldIn).addLightningBolt(new LightningBoltEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), false));
		}

		BlockState state = worldIn.getBlockState(pos);
		if(state.getMaterial() == Material.ROCK && MiscUtil.canBreak(state, false)){
			worldIn.setBlockState(pos, Blocks.REDSTONE_BLOCK.getDefaultState());
		}
	}
	
	public static class VoidChargeEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
			TileEntity te = worldIn.getTileEntity(pos);
			LazyOptional<IEnergyStorage> energy;
			if(te != null && (energy = te.getCapability(CapabilityEnergy.ENERGY, dir)).isPresent()){
				energy.orElseThrow(NullPointerException::new).extractEnergy(CrossroadsConfig.fePerCharge.get(), false);
				return;
			}

			if(worldIn.getBlockState(pos).getBlock() == Blocks.REDSTONE_BLOCK){
				worldIn.setBlockState(pos, Blocks.STONE.getDefaultState());
			}
		}
	}
}
