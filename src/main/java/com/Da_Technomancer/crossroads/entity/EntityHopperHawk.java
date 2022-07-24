package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.essentials.blocks.ESBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.ShoulderRidingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class EntityHopperHawk extends ShoulderRidingEntity implements FlyingAnimal{

	private static final Ingredient FOOD_INGREDIENT = Ingredient.of(Items.HOPPER, ESBlocks.sortingHopper, ESBlocks.speedHopper);

	public static EntityType<EntityHopperHawk> type;

	protected float flap;
	protected float flapSpeed;
	protected float oFlapSpeed;
	protected float oFlap;
	private float flapping = 1.0F;
	private float nextFlap = 1.0F;

	public EntityHopperHawk(EntityType<EntityHopperHawk> type, Level worldIn){
		super(type, worldIn);
		moveControl = new FlyingMoveControl(this, 10, false);
		//Copied from parrots
		setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
		setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
		setPathfindingMalus(BlockPathTypes.COCOA, -1.0F);
	}

	public static AttributeSupplier createAttributes(){
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0D).add(Attributes.FLYING_SPEED, 0.8F).add(Attributes.MOVEMENT_SPEED, 0.4F).build();
	}

	@Override
	protected PathNavigation createNavigation(Level world){
		FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, world);
		flyingpathnavigator.setCanOpenDoors(false);
		flyingpathnavigator.setCanFloat(true);
		flyingpathnavigator.setCanPassDoors(true);
		return flyingpathnavigator;
	}

	@Override
	public boolean causeFallDamage(float p_225503_1_, float p_225503_2_, DamageSource source){
		return false;
	}

	@Override
	protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_){
		//No-op
	}

	@Override
	protected boolean isFlapping(){
		return flyDist > nextFlap;
	}

	@Override
	protected void onFlap() {
		playSound(SoundEvents.PARROT_FLY, 0.15F, 1.0F);
		nextFlap = this.flyDist + this.flapSpeed / 2.0F;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damage){
		return SoundEvents.PARROT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound(){
		return SoundEvents.PARROT_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state){
		this.playSound(SoundEvents.PARROT_STEP, 0.15F, 1.0F);
	}

	@Override
	public void aiStep(){
		super.aiStep();
		calculateFlapping();
	}

	private void calculateFlapping(){
		oFlap = flap;
		oFlapSpeed = flapSpeed;
		flapSpeed = (float) ((double) flapSpeed + (double) (!onGround && !isPassenger() ? 4 : -1) * 0.3D);
		flapSpeed = Mth.clamp(flapSpeed, 0.0F, 1.0F);
		if(!onGround && flapping < 1.0F){
			flapping = 1.0F;
		}

		flapping = (float) ((double) flapping * 0.9D);
		Vec3 vector3d = getDeltaMovement();
		if(!onGround && vector3d.y < 0.0D){
			setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
		}

		flap += flapping * 2.0F;
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand){
		//Based on parrots

		ItemStack itemstack = player.getItemInHand(hand);
		//Try taming
		if(!isTame() && FOOD_INGREDIENT.test(itemstack)){
			if(!player.getAbilities().instabuild){
				itemstack.shrink(1);
			}

			if(!isSilent()){
				level.playSound(null, getX(), getY(), getZ(), SoundEvents.PARROT_EAT, getSoundSource(), 1.0F, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F);
			}

			if(!level.isClientSide){
				//Taming chance of 1/3
				if(random.nextInt(3) == 0 && !ForgeEventFactory.onAnimalTame(this, player)){
					tame(player);
					level.broadcastEntityEvent(this, (byte) 7);
				}else{
					level.broadcastEntityEvent(this, (byte) 6);
				}
			}

			return InteractionResult.sidedSuccess(level.isClientSide);
		}else if(!isFlying() && isTame() && isOwnedBy(player)){
			if(!level.isClientSide){
				setOrderedToSit(!isOrderedToSit());
			}

			return InteractionResult.sidedSuccess(level.isClientSide);
		}else{
			return super.mobInteract(player, hand);
		}
	}

	@Override
	public Packet<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);//Required for modded entities
	}

	@Override
	public boolean canMate(Animal mate){
		return false;
	}

	@Nullable
	@Override
	public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob partner){
		return null;
	}

	@Override
	public boolean isFood(ItemStack stack){
		return FOOD_INGREDIENT.test(stack);
	}

	public boolean isFlying(){
		return !onGround;
	}

	@Override
	protected void registerGoals(){
		goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
		goalSelector.addGoal(0, new FloatGoal(this));
//		goalSelector.addGoal(1, new LookAtGoal(this, PlayerEntity.class, 8.0F));
		goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
		goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0D, 8.0F, 3.0F, true));
		goalSelector.addGoal(3, new CollectItemGoal(this));
		goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
		goalSelector.addGoal(5, new LandOnOwnersShoulderGoal(this));
	}

	private static class CollectItemGoal extends Goal{

		private final EntityHopperHawk mob;
		private final PathNavigation navigation;
		private static final float COLLECTION_RANGE_SMALL = 4;
		private static final float COLLECTION_RANGE = 10;
		private static final float COLLECTION_RANGE_SQR = COLLECTION_RANGE * COLLECTION_RANGE;
		private static final float SPEED_MULTIPLIER = 2;

		private ItemEntity targetEntity;
		private float oldWaterCost;
		private boolean canFitTargetCache = false;
		private long cacheExpireTime = 0;
		private int timeToRecalcPath = 0;

		public CollectItemGoal(EntityHopperHawk mob){
			this.mob = mob;
			this.navigation = mob.getNavigation();
			//From what I've seen, flags only indicate that this goal should be disabled if another AI behaviours with the same flag is running
			setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		}

		private boolean isValidTarget(ItemEntity ent, boolean useCache){
			LivingEntity owner = mob.getOwner();
			return ent != null && ent.isAlive() && owner instanceof Player && owner.distanceToSqr(ent) <= COLLECTION_RANGE_SQR && canFitTargetCached(ent, (Player) owner, useCache);
		}

		private boolean canFitTargetCached(ItemEntity ent, Player owner, boolean useCache){
			if(!useCache){
				return canOwnerFitItem(ent, owner);
			}
			long gametime = mob.level.getGameTime();
			if(gametime >= cacheExpireTime || ent != targetEntity){
				cacheExpireTime = gametime + 10;
				canFitTargetCache = canOwnerFitItem(ent, owner);
			}
			return canFitTargetCache;
		}

		private boolean canOwnerFitItem(ItemEntity ent, Player owner){
			//This call is somewhat expensive, so we cache the result and only re-verify based on time
			Inventory inv = owner.getInventory();
			ItemStack stack = ent.getItem();
			return inv.getFreeSlot() >= 0 || inv.getSlotWithRemainingSpace(stack) >= 0;
		}

		private ItemEntity findNewTarget(){
			//To prevent multiple hopper hawks always targeting the same items, items are selected in two rounds
			//Pick the closest 'nearby' item
			float range = COLLECTION_RANGE_SMALL;
			List<ItemEntity> list = mob.level.getEntities(EntityType.ITEM, new AABB(mob.getX() - range, mob.getY() - range, mob.getZ() - range, mob.getX() + range, mob.getY() + range, mob.getZ() + range), (ItemEntity e) -> isValidTarget(e, false));
			if(!list.isEmpty()){
				//Get the closest item in the list
				return list.stream().min((e1, e2) -> (int) (e1.distanceToSqr(mob) - e2.distanceToSqr(mob))).orElse(null);
			}
			//If no items are 'nearby', use the larger range and select a target at random
			range = COLLECTION_RANGE;
			list = mob.level.getEntities(EntityType.ITEM, new AABB(mob.getX() - range, mob.getY() - range, mob.getZ() - range, mob.getX() + range, mob.getY() + range, mob.getZ() + range), (ItemEntity e) -> isValidTarget(e, false));
			if(!list.isEmpty()){
				return list.get(mob.level.random.nextInt(list.size()));
			}
			return null;
		}

		@Override
		public boolean canUse(){
			return findNewTarget() != null;
		}

		@Override
		public boolean canContinueToUse(){
			return targetEntity != null && isValidTarget(targetEntity, true);
		}

		@Override
		public void start(){
			super.start();
			//Disable attempting to avoid water while collecting an item
			oldWaterCost = mob.getPathfindingMalus(BlockPathTypes.WATER);
			mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);

			targetEntity = findNewTarget();
			canFitTargetCache = true;
			cacheExpireTime = mob.level.getGameTime() + 10;
			timeToRecalcPath = 0;

		}

		@Override
		public void stop(){
			super.stop();
			targetEntity = null;
			navigation.stop();
			mob.setPathfindingMalus(BlockPathTypes.WATER, oldWaterCost);
		}

		@Override
		public void tick(){
			super.tick();
			mob.getLookControl().setLookAt(targetEntity, 10.0F, mob.getMaxHeadXRot());
			if(--timeToRecalcPath <= 0 && targetEntity != null){
				timeToRecalcPath = 10;
				if(!mob.isLeashed() && !mob.isPassenger()){
					navigation.moveTo(targetEntity, SPEED_MULTIPLIER);
				}

				//Pick up the item if we're close to it
				LivingEntity owner = mob.getOwner();
				if(targetEntity.distanceToSqr(mob) <= 1F && owner instanceof Player){
					targetEntity.playerTouch((Player) owner);
				}
			}
		}
	}
}
