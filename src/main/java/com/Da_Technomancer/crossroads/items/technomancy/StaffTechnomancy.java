package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.*;
import com.Da_Technomancer.crossroads.integration.curios.CurioHelper;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;
import java.util.Optional;

public class StaffTechnomancy extends BeamUsingItem{

	private static final int MAX_RANGE = 64;

	public StaffTechnomancy(){
		super(new Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1));
		String name = "staff_technomancy";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public int getUseDuration(ItemStack stack){
		//Any large number works. 72000 is used in vanilla code, so it's used here for consistency.
		return 72000;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand){
		playerIn.setActiveHand(hand);
		return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity player, int count){
		if(!player.world.isRemote && player.isAlive() && (getUseDuration(stack) - count) % BeamUtil.BEAM_TIME == 0){
			ItemStack cage = CurioHelper.getEquipped(CRItems.beamCage, player);//player.getHeldItem(player.getActiveHand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
			byte[] setting = getSetting(stack);
			BeamUnit cageBeam = BeamCage.getStored(cage);
			if(setting[0] <= cageBeam.getEnergy() && setting[1] <= cageBeam.getPotential() && setting[2] <= cageBeam.getStability() && setting[3] <= cageBeam.getVoid() && setting[0] + setting[1] + setting[2] + setting[3] != 0){
				//Handle beam consumption
				BeamCage.storeBeam(cage, new BeamUnit(cageBeam.getEnergy() - setting[0], cageBeam.getPotential() - setting[1], cageBeam.getStability() - setting[2], cageBeam.getVoid() - setting[3]));
				BeamUnit mag = new BeamUnit(setting[0], setting[1], setting[2], setting[3]);

				//Calculate the start and end point of the fired beam
				double heldOffset = .22D * (player.getActiveHand() == Hand.MAIN_HAND ^ player.getPrimaryHand() == HandSide.LEFT ? 1D : -1D);
				Vec3d start = new Vec3d(player.posX - (heldOffset * Math.cos(Math.toRadians(player.rotationYaw))), player.posY + player.getEyeHeight() + 0.4D, player.posZ - (heldOffset * Math.sin(Math.toRadians(player.rotationYaw))));
				double[] end = new double[] {player.posX, player.getEyeHeight() + player.posY, player.posZ};
				BlockPos endPos = null;
				Vec3d look = player.getLookVec().scale(0.2D);
				Direction collisionDir = Direction.getFacingFromVector(look.x, look.y, look.z);//Used for beam collision detection
				Direction effectDir = null;
				//Raytrace manually along the look direction
				for(double d = 0; d < MAX_RANGE; d += 0.2D){
					end[0] += look.x;
					end[1] += look.y;
					end[2] += look.z;
					//Look for entities along the firing path to collide with
					List<Entity> ents = player.world.getEntitiesInAABBexcluding(player, new AxisAlignedBB(end[0] - 0.1D, end[1] - 0.1D, end[2] - 0.1D, end[0] + 0.1D, end[1] + 0.1D, end[2] + 0.1D), EntityPredicates.IS_ALIVE);
					if(!ents.isEmpty()){
						Optional<Vec3d> res = ents.get(0).getBoundingBox().rayTrace(start, new Vec3d(end[0], end[1], end[2]));
						if(res.isPresent()){
							Vec3d hitVec = res.get();
							end[0] = hitVec.x;
							end[1] = hitVec.y;
							end[2] = hitVec.z;
						}
						break;
					}

					BlockPos newEndPos = new BlockPos(end[0], end[1], end[2]);
					//Speed things up a bit by not rechecking blocks
					if(newEndPos.equals(endPos) || World.isOutsideBuildHeight(newEndPos)){
						continue;
					}
					endPos = newEndPos;
					BlockState state = player.world.getBlockState(endPos);
					if(BeamUtil.solidToBeams(state, player.world, endPos, collisionDir, mag.getPower())){
						//Note: this VoxelShape has no offset
						VoxelShape shape = state.getRenderShape(player.world, endPos);//.getBoundingBox(player.world, endPos).offset(endPos);
						BlockRayTraceResult res = shape.rayTrace(start, new Vec3d(end[0] + look.x * 5D, end[1] + look.y * 5D, end[2] + look.z * 5D), endPos);//bb.calculateIntercept(start, new Vec3d(end[0] + look.x * 5D, end[1] + look.y * 5D, end[2] + look.z * 5D));
						if(res != null){
							Vec3d hitVec = res.getHitVec();
							end[0] = hitVec.x;
							end[1] = hitVec.y;
							end[2] = hitVec.z;
							effectDir = res.getFace();
							break;
						}
					}
				}

				if(endPos != null){//Should always be true
					TileEntity te = player.world.getTileEntity(endPos);
					LazyOptional<IBeamHandler> opt;
					if(te != null && (opt = te.getCapability(Capabilities.BEAM_CAPABILITY, effectDir)).isPresent()){
						opt.orElseThrow(NullPointerException::new).setBeam(mag);
					}else{
						EnumBeamAlignments align = EnumBeamAlignments.getAlignment(mag);
						if(!World.isOutsideBuildHeight(endPos)){
							align.getEffect().doBeamEffect(align, mag.getVoid() != 0, Math.min(64, mag.getPower()), player.world, endPos, effectDir);
						}
					}
				}

				Vec3d beamVec = new Vec3d(end[0] - start.x, end[1] - start.y, end[2] - start.z);
				CRRenderUtil.addBeam(player.world, start.x, start.y, start.z, beamVec.length(), (float) Math.toDegrees(Math.atan2(-beamVec.y, Math.sqrt(beamVec.x * beamVec.x + beamVec.z * beamVec.z))), (float) Math.toDegrees(Math.atan2(-beamVec.x, beamVec.z)), (byte) Math.round(Math.sqrt(mag.getPower())), mag.getRGB().getRGB());
			}
		}
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack){
		//Acts as a melee weapon; absolutely a DiscWorld reference
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		if (slot == EquipmentSlotType.MAINHAND){
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 9, AttributeModifier.Operation.ADDITION));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -3.1D, AttributeModifier.Operation.ADDITION));
		}

		return multimap;
	}

	@Override
	protected byte maxSetting(){
		return 8;
	}
}
