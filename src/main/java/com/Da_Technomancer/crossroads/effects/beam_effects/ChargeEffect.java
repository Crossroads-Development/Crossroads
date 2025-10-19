package com.Da_Technomancer.crossroads.effects.beam_effects;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.beams.BeamHit;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.integration.curios.CurioHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class ChargeEffect extends BeamEffect{

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, BeamHit beamHit){
		if(!performTransmute(align, voi, power, beamHit)){
			IEnergyStorage energy = beamHit.getEndCapability(Capabilities.EnergyStorage.BLOCK, false);

			if(voi){
				//Discharge machine
				if(energy != null){
					energy.extractEnergy(CRConfig.fePerCharge.get() * power, false);
					return;
				}

				//Attempt to discharge items in inventory
				final int[] availableFE = new int[] {CRConfig.fePerCharge.get() * power};
				List<Player> players = beamHit.getNearbyEntities(Player.class, BeamHit.WITHIN_BLOCK_RANGE, null);
				for(Player player : players){
					CurioHelper.forAllInventoryItems(player, (ItemStack item) -> {
						if(!item.isEmpty() && item.getCapability(Capabilities.EnergyStorage.ITEM) != null){
							ItemStack copy = item.copy();
							availableFE[0] -= copy.getCapability(Capabilities.EnergyStorage.ITEM).extractEnergy(availableFE[0], false);
							return copy;
						}
						return item;
					});

					if(availableFE[0] <= 0){
						break;
					}
				}
			}else{
				//Charge machine
				if(energy != null){
					energy.receiveEnergy(CRConfig.fePerCharge.get() * power, false);
					return;
				}

				//Attempt to charge items in inventory
				final int[] availableFE = new int[] {CRConfig.fePerCharge.get() * power};
				List<Player> players = beamHit.getNearbyEntities(Player.class, BeamHit.WITHIN_BLOCK_RANGE, null);
				for(Player player : players){
					CurioHelper.forAllInventoryItems(player, (ItemStack item) -> {
						if(!item.isEmpty() && item.getCapability(Capabilities.EnergyStorage.ITEM) != null){
							ItemStack copy = item.copy();
							availableFE[0] -= copy.getCapability(Capabilities.EnergyStorage.ITEM).receiveEnergy(availableFE[0], false);
							return copy;
						}
						return item;
					});

					if(availableFE[0] <= 0){
						break;
					}
				}

				//Lightning
				if(power >= 16 && CRConfig.chargeSpawnLightning.get() && (CRConfig.undergroundLightning.get() || beamHit.getWorld().canSeeSky(beamHit.getPos().above()))){
					LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(beamHit.getWorld());
					lightning.moveTo(Vec3.atBottomCenterOf(beamHit.getPos().above()));
					beamHit.getWorld().addFreshEntity(lightning);
				}
			}
		}
	}
}
