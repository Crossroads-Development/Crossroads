package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.ModConfig;
import net.minecraft.block.material.Material;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ChargeEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te != null && te.hasCapability(CapabilityEnergy.ENERGY, null)){
			IEnergyStorage energy = te.getCapability(CapabilityEnergy.ENERGY, null);
			energy.receiveEnergy(ModConfig.getConfigInt(ModConfig.fePerCharge, false), false);
			return;
		}

		if(mult >= 8){
			worldIn.addWeatherEffect(new EntityLightningBolt(worldIn, pos.getX(), pos.getY(), pos.getZ(), false));
		}

		if(worldIn.getBlockState(pos).getMaterial() == Material.ROCK){
			worldIn.setBlockState(pos, Blocks.REDSTONE_BLOCK.getDefaultState());
		}

	}
	
	public static class VoidChargeEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te != null && te.hasCapability(CapabilityEnergy.ENERGY, null)){
				IEnergyStorage energy = te.getCapability(CapabilityEnergy.ENERGY, null);
				energy.extractEnergy(ModConfig.getConfigInt(ModConfig.fePerCharge, false), false);
				return;
			}

			if(worldIn.getBlockState(pos).getBlock() == Blocks.REDSTONE_BLOCK){
				worldIn.setBlockState(pos, Blocks.STONE.getDefaultState());
			}
		}
	}
}
