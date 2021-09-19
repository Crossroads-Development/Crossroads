package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.essentials.blocks.ESBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.passive.ShoulderRidingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.IPacket;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

//@ObjectHolder(Crossroads.MODID)
public class EntityHopperHawk extends ShoulderRidingEntity implements IFlyingAnimal{

	private static final Ingredient FOOD_INGREDIENT = Ingredient.of(Items.HOPPER, ESBlocks.sortingHopper, ESBlocks.speedHopper);

	static{
		//We have to create the type early so we can use it for the spawn egg
		type = CREntities.createType(EntityType.Builder.of(EntityHopperHawk::new, EntityClassification.CREATURE).sized(0.5F, 0.9F).clientTrackingRange(8), "hopper_hawk");
	}

	//	@ObjectHolder("hopper_hawk")
	public static EntityType<EntityHopperHawk> type;

	protected float flap;
	protected float flapSpeed;
	protected float oFlapSpeed;
	protected float oFlap;
	private float flapping = 1.0F;

	public EntityHopperHawk(EntityType<EntityHopperHawk> type, World worldIn){
		super(type, worldIn);
		moveControl = new FlyingMovementController(this, 10, false);
		//Copied from parrots
		setPathfindingMalus(PathNodeType.DANGER_FIRE, -1.0F);
		setPathfindingMalus(PathNodeType.DAMAGE_FIRE, -1.0F);
		setPathfindingMalus(PathNodeType.COCOA, -1.0F);
	}

	public static AttributeModifierMap createAttributes(){
		return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0D).add(Attributes.FLYING_SPEED, 0.8F).add(Attributes.MOVEMENT_SPEED, 0.4F).build();
	}

	@Override
	protected PathNavigator createNavigation(World world){
		FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, world);
		flyingpathnavigator.setCanOpenDoors(false);
		flyingpathnavigator.setCanFloat(true);
		flyingpathnavigator.setCanPassDoors(true);
		return flyingpathnavigator;
	}

	@Override
	public boolean causeFallDamage(float p_225503_1_, float p_225503_2_){
		return false;
	}

	@Override
	protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_){
		//No-op
	}

	@Override
	protected float playFlySound(float p_191954_1_){
		playSound(SoundEvents.PARROT_FLY, 0.15F, 1.0F);
		return p_191954_1_ + this.flapSpeed / 2.0F;
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
		flapSpeed = MathHelper.clamp(flapSpeed, 0.0F, 1.0F);
		if(!onGround && flapping < 1.0F){
			flapping = 1.0F;
		}

		flapping = (float) ((double) flapping * 0.9D);
		Vector3d vector3d = getDeltaMovement();
		if(!onGround && vector3d.y < 0.0D){
			setDeltaMovement(vector3d.multiply(1.0D, 0.6D, 1.0D));
		}

		flap += flapping * 2.0F;
	}

	@Override
	public ActionResultType mobInteract(PlayerEntity player, Hand hand){
		//Based on parrots

		ItemStack itemstack = player.getItemInHand(hand);
		//Try taming
		if(!isTame() && FOOD_INGREDIENT.test(itemstack)){
			if(!player.abilities.instabuild){
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

			return ActionResultType.sidedSuccess(level.isClientSide);
		}else if(!isFlying() && isTame() && isOwnedBy(player)){
			if(!level.isClientSide){
				setOrderedToSit(!isOrderedToSit());
			}

			return ActionResultType.sidedSuccess(level.isClientSide);
		}else{
			return super.mobInteract(player, hand);
		}
	}

	@Override
	public IPacket<?> getAddEntityPacket(){
		return NetworkHooks.getEntitySpawningPacket(this);//Required for modded entities
	}

	@Override
	public boolean canMate(AnimalEntity mate){
		return false;
	}

	@Nullable
	@Override
	public AgeableEntity getBreedOffspring(ServerWorld world, AgeableEntity partner){
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
		goalSelector.addGoal(0, new SwimGoal(this));
//		goalSelector.addGoal(1, new LookAtGoal(this, PlayerEntity.class, 8.0F));
		goalSelector.addGoal(1, new SitGoal(this));
		goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0D, 8.0F, 3.0F, true));
		goalSelector.addGoal(3, new CollectItemGoal(this));
		goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
		goalSelector.addGoal(5, new LandOnOwnersShoulderGoal(this));
	}

	private static class CollectItemGoal extends Goal{

		private final EntityHopperHawk mob;
		private final PathNavigator navigation;
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
			return ent != null && ent.isAlive() && owner instanceof PlayerEntity && owner.distanceToSqr(ent) <= COLLECTION_RANGE_SQR && canFitTargetCached(ent, (PlayerEntity) owner, useCache);
		}

		private boolean canFitTargetCached(ItemEntity ent, PlayerEntity owner, boolean useCache){
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

		private boolean canOwnerFitItem(ItemEntity ent, PlayerEntity owner){
			//This call is somewhat expensive, so we cache the result and only re-verify based on time
			PlayerInventory inv = owner.inventory;
			ItemStack stack = ent.getItem();
			return inv.getFreeSlot() >= 0 || inv.getSlotWithRemainingSpace(stack) >= 0;
		}

		private ItemEntity findNewTarget(){
			//To prevent multiple hopper hawks always targeting the same items, items are selected in two rounds
			//Pick the closest 'nearby' item
			float range = COLLECTION_RANGE_SMALL;
			List<ItemEntity> list = mob.level.getEntities(EntityType.ITEM, new AxisAlignedBB(mob.getX() - range, mob.getY() - range, mob.getZ() - range, mob.getX() + range, mob.getY() + range, mob.getZ() + range), (ItemEntity e) -> isValidTarget(e, false));
			if(!list.isEmpty()){
				//Get the closest item in the list
				return list.stream().min((e1, e2) -> (int) (e1.distanceToSqr(mob) - e2.distanceToSqr(mob))).orElse(null);
			}
			//If no items are 'nearby', use the larger range and select a target at random
			range = COLLECTION_RANGE;
			list = mob.level.getEntities(EntityType.ITEM, new AxisAlignedBB(mob.getX() - range, mob.getY() - range, mob.getZ() - range, mob.getX() + range, mob.getY() + range, mob.getZ() + range), (ItemEntity e) -> isValidTarget(e, false));
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
			oldWaterCost = mob.getPathfindingMalus(PathNodeType.WATER);
			mob.setPathfindingMalus(PathNodeType.WATER, 0.0F);

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
			mob.setPathfindingMalus(PathNodeType.WATER, oldWaterCost);
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
				if(targetEntity.distanceToSqr(mob) <= 1F && owner instanceof PlayerEntity){
					targetEntity.playerTouch((PlayerEntity) owner);
				}
			}
		}
	}
}
