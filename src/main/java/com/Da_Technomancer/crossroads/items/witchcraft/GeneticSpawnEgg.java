package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.API.CRReflection;
import com.Da_Technomancer.crossroads.API.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.entity.mob_effects.CRPotions;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.ReflectionUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GeneticSpawnEgg extends Item{

	private static final String KEY = "cr_genetics";

	private static final Method OFFSPRING_SPAWNING_METHOD = ReflectionUtil.reflectMethod(CRReflection.OFFSPRING_SPAWN_EGG);

	public GeneticSpawnEgg(){
		super(new Item.Properties());//Not added to any creative tab
		String name = "spawn_egg";
		setRegistryName(name);
		CRItems.toRegister.add(this);

		DefaultDispenseItemBehavior dispenseBehavior = new DefaultDispenseItemBehavior(){
			public ItemStack execute(IBlockSource source, ItemStack stack){
				Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
				spawnMob(stack, null, source.getLevel(), source.getPos().relative(direction), SpawnReason.DISPENSER, direction != Direction.UP, false);
				stack.shrink(1);
				return stack;
			}
		};

		DispenserBlock.registerBehavior(this, dispenseBehavior);
	}

	public void withEntityTypeData(ItemStack stack, EntityTemplate template){
		CompoundNBT nbt = stack.getOrCreateTag();
		nbt.put(KEY, template.serializeNBT());
	}

	public EntityTemplate getEntityTypeData(ItemStack stack){
		CompoundNBT nbt = stack.getOrCreateTag();
		EntityTemplate template = new EntityTemplate();
		template.deserializeNBT(nbt.getCompound(KEY));
		return template;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag){
		EntityTemplate template = getEntityTypeData(stack);
		template.addTooltip(tooltip, 4);
	}
	
	public boolean spawnMob(ItemStack stack, @Nullable PlayerEntity player, ServerWorld world, BlockPos pos, SpawnReason reason, boolean offset, boolean unmapped){
		EntityTemplate template = getEntityTypeData(stack);
		EntityType<?> type = template.getEntityType();
		if(type == null){
			return false;
		}

		//Don't pass the itemstack to the spawn method
		//That parameter is designed for the vanilla spawn egg NBT structure, which we don't use
		//We have to adjust the mob manually after spawning as a result
		Entity created = type.spawn(world, null, stack.hasCustomHoverName() ? stack.getHoverName() : null, player, pos, reason, offset, unmapped);
		LivingEntity entity;
		if(created == null){
			return false;
		}

		//NBT traits
		CompoundNBT nbt = created.getPersistentData();
		nbt.putBoolean(EntityTemplate.LOYAL_KEY, template.isLoyal());
		nbt.putBoolean(EntityTemplate.RESPAWNING_KEY, template.isRespawning());

		if(created instanceof LivingEntity){
			entity = (LivingEntity) created;

			//Degradation
			if(template.getDegradation() > 0){
				entity.addEffect(new EffectInstance(CRPotions.HEALTH_PENALTY_EFFECT, Integer.MAX_VALUE, template.getDegradation() - 1));
			}

			//Potion effects
			ArrayList<EffectInstance> rawEffects = template.getEffects();
			for(EffectInstance effect : rawEffects){
				CRPotions.applyAsPermanent(entity, effect);
			}

			//Loyalty
			if(template.isLoyal() && player != null){
				//There isn't a single method for this. The correct way to set something as tamed varies based on the mob
				//New vanilla tamable mobs may require changes here, and modded tameable mobs are unlikely to work
				if(created instanceof TameableEntity){
					((TameableEntity) created).tame(player);
				}else if(created instanceof AbstractHorseEntity){
					((AbstractHorseEntity) created).tameWithName(player);
				}else if(created instanceof MobEntity && OFFSPRING_SPAWNING_METHOD != null){
					//As of vanilla MC1.16.5, this is literally only applicable to foxes
					try{
						OFFSPRING_SPAWNING_METHOD.invoke(created, player, created);
					}catch(IllegalAccessException | InvocationTargetException e){
						Crossroads.logger.catching(e);
					}
				}
			}
		}

		return true;
	}

	@Override
	public ActionResultType useOn(ItemUseContext context){
		//Based on the method in SpawnEggItem
		World world = context.getLevel();
		if(!(world instanceof ServerWorld)){
			return ActionResultType.SUCCESS;
		}else{
			ItemStack itemstack = context.getItemInHand();
			BlockPos blockpos = context.getClickedPos();
			Direction direction = context.getClickedFace();
			BlockState blockstate = world.getBlockState(blockpos);

			/*
			if(blockstate.is(Blocks.SPAWNER)){
				TileEntity tileentity = world.getBlockEntity(blockpos);
				if(tileentity instanceof MobSpawnerTileEntity){
					AbstractSpawner abstractspawner = ((MobSpawnerTileEntity) tileentity).getSpawner();
					EntityType<?> entitytype1 = this.getType(itemstack.getTag());
					abstractspawner.setEntityId(entitytype1);
					tileentity.setChanged();
					world.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
					itemstack.shrink(1);
					return ActionResultType.CONSUME;
				}
			}
			 */

			BlockPos blockpos1;
			if(blockstate.getCollisionShape(world, blockpos).isEmpty()){
				blockpos1 = blockpos;
			}else{
				blockpos1 = blockpos.relative(direction);
			}

			if(spawnMob(itemstack, context.getPlayer(), (ServerWorld) world, blockpos1, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP)){
				itemstack.shrink(1);
			}

			return ActionResultType.CONSUME;
		}
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
		//Based on the method in SpawnEggItem
		ItemStack itemstack = player.getItemInHand(hand);
		BlockRayTraceResult raytraceresult = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
		if(raytraceresult.getType() != RayTraceResult.Type.BLOCK){
			return ActionResult.pass(itemstack);
		}else if(!(world instanceof ServerWorld)){
			return ActionResult.success(itemstack);
		}else{
			BlockPos blockpos = raytraceresult.getBlockPos();
			if(!(world.getBlockState(blockpos).getBlock() instanceof FlowingFluidBlock)){
				return ActionResult.pass(itemstack);
			}else if(world.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos, raytraceresult.getDirection(), itemstack)){
				if(!spawnMob(itemstack, player, (ServerWorld) world, blockpos, SpawnReason.SPAWN_EGG, false, false)){
					return ActionResult.pass(itemstack);
				}else{
					if(!player.abilities.instabuild){
						itemstack.shrink(1);
					}

					//We've gotten this far without acknowledging stats as a mechanic, and we're not starting now
//					player.awardStat(Stats.ITEM_USED.get(this));
					return ActionResult.consume(itemstack);
				}
			}else{
				return ActionResult.fail(itemstack);
			}
		}
	}
}
