package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.CRConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class ChargeEffect extends BeamEffect{

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, World worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			TileEntity te = worldIn.getBlockEntity(pos);
			if(voi){
				LazyOptional<IEnergyStorage> energy;
				if(te != null && (energy = te.getCapability(CapabilityEnergy.ENERGY, dir)).isPresent()){
					energy.orElseThrow(NullPointerException::new).extractEnergy(CRConfig.fePerCharge.get() * power, false);
				}
			}else{
				LazyOptional<IEnergyStorage> opt;
				if(te != null && (opt = te.getCapability(CapabilityEnergy.ENERGY, dir)).isPresent()){
					opt.orElseThrow(NullPointerException::new).receiveEnergy(CRConfig.fePerCharge.get() * power, false);
					return;
				}

				if(power >= 16 && CRConfig.chargeSpawnLightning.get() && (CRConfig.undergroundLightning.get() || worldIn.canSeeSky(pos.above()))){
					LightningBoltEntity lightning = EntityType.LIGHTNING_BOLT.create(worldIn);
					lightning.moveTo(Vector3d.atBottomCenterOf(pos));
					worldIn.addFreshEntity(lightning);
				}
			}
		}
	}
}
