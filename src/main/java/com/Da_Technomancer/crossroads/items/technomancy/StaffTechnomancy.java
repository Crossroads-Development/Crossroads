package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.effects.IEffect;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.render.RenderUtil;
import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class StaffTechnomancy extends BeamUsingItem{

	public StaffTechnomancy(){
		String name = "staff_technomancy";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setMaxStackSize(1);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count){
		super.onUsingTick(stack, player, count);
		if(!player.world.isRemote && (getMaxItemUseDuration(stack) - count) % 5 == 0){
			if(!stack.hasTagCompound()){
				return;
			}
			ItemStack cage = player.getHeldItem(player.getActiveHand() == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
			if(cage.getItem() != ModItems.beamCage || !cage.hasTagCompound()){
				player.resetActiveHand();
				return;
			}

			NBTTagCompound nbt = stack.getTagCompound();
			int energy = nbt.getInteger(EnumBeamAlignments.ENERGY.name());
			int potential = nbt.getInteger(EnumBeamAlignments.POTENTIAL.name());
			int stability = nbt.getInteger(EnumBeamAlignments.STABILITY.name());
			int voi = nbt.getInteger(EnumBeamAlignments.VOID.name());

			BeamUnit cageBeam = BeamCage.getStored(cage);
			int beamEn = cageBeam == null ? 0 : cageBeam.getEnergy();
			int beamPo = cageBeam == null ? 0 : cageBeam.getPotential();
			int beamSt = cageBeam == null ? 0 : cageBeam.getStability();
			int beamVo = cageBeam == null ? 0 : cageBeam.getVoid();

			if(energy <= beamEn && potential <= beamPo && stability <= beamSt && voi <= beamVo){
				if(energy + potential + stability + voi > 0){
					BeamCage.storeBeam(cage, new BeamUnit(beamEn - energy, beamPo - potential, beamSt - stability, beamVo - voi));


					BeamUnit mag = new BeamUnit(energy, potential, stability, voi);

					double heldOffset = .22D * (player.getActiveHand() == EnumHand.MAIN_HAND ^ player.getPrimaryHand() == EnumHandSide.LEFT ? 1D : -1D);
					Vec3d start = new Vec3d(player.posX - (heldOffset * Math.cos(Math.toRadians(player.rotationYaw))), player.posY + 2.1D, player.posZ - (heldOffset * Math.sin(Math.toRadians(player.rotationYaw))));
					double[] end = new double[] {player.posX, player.getEyeHeight() + player.posY, player.posZ};
					BlockPos endPos = null;
					Vec3d look = player.getLookVec().scale(0.2D);
					EnumFacing effectDir = null;

					for(double d = 0; d < 32; d += 0.2D){
						end[0] += look.x;
						end[1] += look.y;
						end[2] += look.z;

						List<Entity> ents = player.world.getEntitiesInAABBexcluding(player, new AxisAlignedBB(end[0] - 0.1D, end[1] - 0.1D, end[2] - 0.1D, end[0] + 0.1D, end[1] + 0.1D, end[2] + 0.1D), EntitySelectors.IS_ALIVE);
						if(!ents.isEmpty()){
							RayTraceResult res = ents.get(0).getEntityBoundingBox().calculateIntercept(start, new Vec3d(end[0], end[1], end[2]));
							if(res != null && res.hitVec != null){
								end[0] = res.hitVec.x;
								end[1] = res.hitVec.y;
								end[2] = res.hitVec.z;
							}
							break;
						}

						BlockPos newEndPos = new BlockPos(end[0], end[1], end[2]);
						//Speed things up a bit by not rechecking blocks
						if(newEndPos.equals(endPos) || player.world.isOutsideBuildHeight(newEndPos)){
							continue;
						}
						endPos = newEndPos;
						IBlockState state = player.world.getBlockState(endPos);
						AxisAlignedBB bb = state.getBoundingBox(player.world, endPos).offset(endPos);
						if(state.getBlock().canCollideCheck(state, true) && state.getBlock().isCollidable() && BeamManager.solidToBeams(state, player.world, endPos)){
							RayTraceResult res = bb.calculateIntercept(start, new Vec3d(end[0] + look.x * 5D, end[1] + look.y * 5D, end[2] + look.z * 5D));
							if(res != null && res.hitVec != null){
								end[0] = res.hitVec.x;
								end[1] = res.hitVec.y;
								end[2] = res.hitVec.z;
								effectDir = res.sideHit;
								break;
							}
						}
					}

					if(endPos != null){//Should always be true
						TileEntity te = player.world.getTileEntity(endPos);
						IBeamHandler handler;
						if(te != null && (handler = te.getCapability(Capabilities.BEAM_CAPABILITY, effectDir)) != null){
							handler.setMagic(mag);
						}else{
							IEffect effect = EnumBeamAlignments.getAlignment(mag).getMixEffect(mag.getRGB());
							if(effect != null && !player.world.isOutsideBuildHeight(endPos)){
								effect.doEffect(player.world, endPos, Math.min(64, mag.getPower()), effectDir);
							}
						}
					}

					Vec3d beamVec = new Vec3d(end[0] - start.x, end[1] - start.y, end[2] - start.z);
					RenderUtil.addBeam(player.world.provider.getDimension(), start.x, start.y, start.z, beamVec.length(), (float) Math.toDegrees(Math.atan2(-beamVec.y, Math.sqrt(beamVec.x * beamVec.x + beamVec.z * beamVec.z))), (float) Math.toDegrees(Math.atan2(-beamVec.x, beamVec.z)), (byte) Math.round(Math.sqrt(mag.getPower())), mag.getRGB().getRGB());
				}
			}
		}
	}

	@Override
	public void preChanged(ItemStack stack, EntityPlayer player){

	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack){
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		if (slot == EntityEquipmentSlot.MAINHAND){
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 9, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -3.1D, 0));
		}

		return multimap;
	}
}
