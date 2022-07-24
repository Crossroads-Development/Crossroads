package com.Da_Technomancer.crossroads.items.technomancy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.beams.*;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.integration.curios.CurioHelperSafe;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.List;

public class StaffTechnomancy extends BeamUsingItem{

	private static final int MAX_RANGE = 64;
	private final Multimap<Attribute, AttributeModifier> attributeModifiers;

	public StaffTechnomancy(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
		String name = "staff_technomancy";
		CRItems.toRegister.put(name, this);

		//Attributes
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 9, AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -3.1D, AttributeModifier.Operation.ADDITION));
		attributeModifiers = builder.build();
	}

	@Override
	public int getUseDuration(ItemStack stack){
		//Any large number works. 72000 is used in vanilla code, so it's used here for consistency.
		return 72000;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand){
		playerIn.startUsingItem(hand);
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(hand));
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity player, int count){
		if(!player.level.isClientSide && player.isAlive() && (getUseDuration(stack) - count) % BeamUtil.BEAM_TIME == 0){
			ItemStack cage = CurioHelperSafe.getEquipped(CRItems.beamCage, player);//player.getHeldItem(player.getActiveHand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
			byte[] setting = getSetting(stack);
			BeamUnit cageBeam = BeamCage.getStored(cage);
			if(setting[0] <= cageBeam.getEnergy() && setting[1] <= cageBeam.getPotential() && setting[2] <= cageBeam.getStability() && setting[3] <= cageBeam.getVoid() && setting[0] + setting[1] + setting[2] + setting[3] != 0){
				//Handle beam consumption
				BeamCage.storeBeam(cage, new BeamUnit(cageBeam.getEnergy() - setting[0], cageBeam.getPotential() - setting[1], cageBeam.getStability() - setting[2], cageBeam.getVoid() - setting[3]));
				BeamUnit mag = new BeamUnit(setting[0], setting[1], setting[2], setting[3]);

				//Calculate the start and end point of the fired beam
				double heldOffset = .22D * (player.getUsedItemHand() == InteractionHand.MAIN_HAND ^ player.getMainArm() == HumanoidArm.LEFT ? 1D : -1D);
				Vec3 start = new Vec3(player.getX() - (heldOffset * Math.cos(Math.toRadians(player.getYRot()))), player.getY() + player.getEyeHeight() + 0.4D, player.getZ() - (heldOffset * Math.sin(Math.toRadians(player.getYRot()))));

				Vec3 ray = player.getLookAngle();
				BeamHit beamHitResult = rayTraceBeams(mag, player.level, start, player.getEyePosition(1), ray, player, null, MAX_RANGE);
				Direction effectDir = beamHitResult.getDirection();

//				double[] end = new double[] {player.getPosX(), player.getEyeHeight() + player.getPosY(), player.getPosZ()};
//				BlockPos endPos = null;
//				Vector3d look = player.getLookVec().scale(0.2D);
//				Direction collisionDir = Direction.getFacingFromVector(look.x, look.y, look.z);//Used for beam collision detection
//				Direction effectDir = null;
//				//Raytrace manually along the look direction
//				for(double d = 0; d < MAX_RANGE; d += 0.2D){
//					end[0] += look.x;
//					end[1] += look.y;
//					end[2] += look.z;
//					//Look for entities along the firing path to collide with
//					List<Entity> ents = player.world.getEntitiesInAABBexcluding(player, new AxisAlignedBB(end[0] - 0.1D, end[1] - 0.1D, end[2] - 0.1D, end[0] + 0.1D, end[1] + 0.1D, end[2] + 0.1D), EntityPredicates.IS_ALIVE);
//					if(!ents.isEmpty()){
//						Optional<Vector3d> res = ents.get(0).getBoundingBox().rayTrace(start, new Vector3d(end[0], end[1], end[2]));
//						if(res.isPresent()){
//							Vector3d hitVec = res.get();
//							end[0] = hitVec.x;
//							end[1] = hitVec.y;
//							end[2] = hitVec.z;
//						}
//						break;
//					}
//
//					BlockPos newEndPos = new BlockPos(end[0], end[1], end[2]);
//					//Speed things up a bit by not rechecking blocks
//					if(newEndPos.equals(endPos) || World.isOutsideBuildHeight(newEndPos)){
//						continue;
//					}
//					endPos = newEndPos;
//					BlockState state = player.world.getBlockState(endPos);
//					if(BeamUtil.solidToBeams(state, player.world, endPos, collisionDir, mag.getPower())){
//						//Note: this VoxelShape has no offset
//						VoxelShape shape = state.getRenderShape(player.world, endPos);//.getBoundingBox(player.world, endPos).offset(endPos);
//						BlockRayTraceResult res = shape.rayTrace(start, new Vector3d(end[0] + look.x * 5D, end[1] + look.y * 5D, end[2] + look.z * 5D), endPos);//bb.calculateIntercept(start, new Vec3d(end[0] + look.x * 5D, end[1] + look.y * 5D, end[2] + look.z * 5D));
//						if(res != null){
//							Vector3d hitVec = res.getHitVec();
//							end[0] = hitVec.x;
//							end[1] = hitVec.y;
//							end[2] = hitVec.z;
//							effectDir = res.getFace();
//							break;
//						}
//					}
//				}

				BlockEntity te = player.level.getBlockEntity(beamHitResult.getPos());
				LazyOptional<IBeamHandler> opt;
				if(te != null && (opt = te.getCapability(Capabilities.BEAM_CAPABILITY, effectDir)).isPresent()){
					opt.orElseThrow(NullPointerException::new).setBeam(mag);
				}else{
					EnumBeamAlignments align = EnumBeamAlignments.getAlignment(mag);
					if(!player.level.isOutsideBuildHeight(beamHitResult.getPos())){
						align.getEffect().doBeamEffect(align, mag.getVoid() != 0, Math.min(64, mag.getPower()), beamHitResult);
					}
				}

				Vec3 beamVec = beamHitResult.getHitPos().subtract(start);
				CRRenderUtil.addBeam(player.level, start.x, start.y, start.z, beamVec.length(), (float) Math.toDegrees(Math.atan2(-beamVec.y, Math.sqrt(beamVec.x * beamVec.x + beamVec.z * beamVec.z))), (float) Math.toDegrees(Math.atan2(-beamVec.x, beamVec.z)), (byte) Math.round(Math.sqrt(mag.getPower())), mag.getRGB().getRGB());
			}
		}
	}

	public static BeamHit rayTraceBeams(BeamUnit beam, Level world, Vec3 startPos, Vec3 endSourcePos, Vec3 ray, @Nullable Entity excludedEntity, @Nullable BlockPos ignorePos, double maxRange){
		final double stepSize = CRConfig.beamRaytraceStep.get();
		final double halfStep = stepSize / 2D;
		Direction collisionDir = Direction.getNearest(ray.x, ray.y, ray.z);//Used for beam collision detection
		Vec3 stepRay = ray.scale(stepSize);
		//effect direction is nonnull. Use the direction of the beam if no block collision occurred
		Direction effectDir = Direction.getNearest(-ray.x, -ray.y, -ray.z);
		double[] end = new double[] {endSourcePos.x, endSourcePos.y, endSourcePos.z};
		BlockPos.MutableBlockPos endPos = new BlockPos.MutableBlockPos(end[0], end[1], end[2]);
		BlockPos.MutableBlockPos prevEndPos = new BlockPos.MutableBlockPos(end[0], end[1], end[2]);
		BlockState state = world.getBlockState(endPos);

		//Raytrace manually along the look direction
		for(double d = 0; d < maxRange; d += stepSize){
			end[0] += stepRay.x;
			end[1] += stepRay.y;
			end[2] += stepRay.z;
			endPos = endPos.set(end[0], end[1], end[2]);
			boolean didPosChange = !endPos.equals(prevEndPos);
			if(didPosChange){
				prevEndPos = prevEndPos.set(endPos);
				state = world.getBlockState(endPos);
			}

			//Check for entity collisions
			List<Entity> ents = world.getEntities(excludedEntity, new AABB(end[0] - halfStep, end[1] - halfStep, end[2] - halfStep, end[0] + halfStep, end[1] + halfStep, end[2] + halfStep), BeamUtil.BEAM_COLLIDE_ENTITY);
			if(!ents.isEmpty()){
				Vec3 entVec = ents.get(0).position();
				//Vector component of entity position (relative to beam source) onto beam ray direction, added back to beam source position
				//Gives the point on the beam-path line closest to the entity (the hitVec isn't necessarily on the actual line of the beam)
				Vec3 lineVec = startPos.add(ray.scale(entVec.subtract(startPos).dot(ray)));
				end[0] = lineVec.x;
				end[1] = lineVec.y;
				end[2] = lineVec.z;
				return new BeamHit((ServerLevel) world, endPos.immutable(), effectDir, state, ray, new Vec3(end[0], end[1], end[2]));
			}

			//Check for block collisions
			//Speed things up a bit by not rechecking blocks
			if(didPosChange && !world.isOutsideBuildHeight(endPos) && !endPos.equals(ignorePos)){
				if(BeamUtil.solidToBeams(state, world, endPos, collisionDir, beam.getPower())){
					//Note: this VoxelShape has no offset
					VoxelShape shape = state.getBlockSupportShape(world, endPos);//.getBoundingBox(player.world, endPos).offset(endPos);
					BlockHitResult res = shape.clip(startPos, new Vec3(end[0] + ray.x, end[1] + ray.y, end[2] + ray.z), endPos);
					if(res != null){
						Vec3 hitVec = res.getLocation();
						end[0] = hitVec.x;
						end[1] = hitVec.y;
						end[2] = hitVec.z;
						effectDir = res.getDirection();
						return new BeamHit((ServerLevel) world, endPos.immutable(), effectDir, state, ray, new Vec3(end[0], end[1], end[2]));
					}
				}
			}
		}

		return new BeamHit((ServerLevel) world, endPos.immutable(), effectDir, state, ray, new Vec3(end[0], end[1], end[2]));
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack){
		//Acts as a melee weapon; absolutely a DiscWorld reference
		return slot == EquipmentSlot.MAINHAND ? attributeModifiers : super.getAttributeModifiers(slot, stack);
	}

	@Override
	protected byte maxSetting(){
		return 8;
	}
}
