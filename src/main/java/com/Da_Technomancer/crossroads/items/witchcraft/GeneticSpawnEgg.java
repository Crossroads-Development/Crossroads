package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.api.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class GeneticSpawnEgg extends Item{

	private static final String KEY = "cr_genetics";


	public GeneticSpawnEgg(){
		super(new Item.Properties());
		String name = "spawn_egg";
		CRItems.queueForRegister(name, this, null);//Not added to any creative tab

		DefaultDispenseItemBehavior dispenseBehavior = new DefaultDispenseItemBehavior(){
			public ItemStack execute(BlockSource source, ItemStack stack){
				Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
				spawnMob(stack, null, source.getLevel(), source.getPos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
				stack.shrink(1);
				return stack;
			}
		};

		DispenserBlock.registerBehavior(this, dispenseBehavior);
	}

	public void withEntityTypeData(ItemStack stack, EntityTemplate template){
		CompoundTag nbt = stack.getOrCreateTag();
		nbt.put(KEY, template.serializeNBT());
		if(template.getCustomName() != null){
			stack.setHoverName(template.getCustomName());//Copy any custom name on the template onto the spawn egg
		}
	}

	public EntityTemplate getEntityTypeData(ItemStack stack){
		CompoundTag nbt = stack.getOrCreateTag();
		EntityTemplate template = new EntityTemplate();
		template.deserializeNBT(nbt.getCompound(KEY));
		return template;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
		EntityTemplate template = getEntityTypeData(stack);
		template.addTooltip(tooltip, 4);
	}

	public boolean spawnMob(ItemStack stack, @Nullable Player player, ServerLevel world, BlockPos pos, MobSpawnType reason, boolean offset, boolean unmapped){
		EntityTemplate template = getEntityTypeData(stack);
		Entity created = EntityTemplate.spawnEntityFromTemplate(template, world, pos, reason, offset, unmapped, stack.hasCustomHoverName() ? stack.getHoverName() : null, player);
		return created != null;
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		//Based on the method in SpawnEggItem
		Level world = context.getLevel();
		if(!(world instanceof ServerLevel)){
			return InteractionResult.SUCCESS;
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

			if(spawnMob(itemstack, context.getPlayer(), (ServerLevel) world, blockpos1, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP)){
				itemstack.shrink(1);
			}

			return InteractionResult.CONSUME;
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand){
		//Based on the method in SpawnEggItem
		ItemStack itemstack = player.getItemInHand(hand);
		BlockHitResult raytraceresult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.SOURCE_ONLY);
		if(raytraceresult.getType() != HitResult.Type.BLOCK){
			return InteractionResultHolder.pass(itemstack);
		}else if(!(world instanceof ServerLevel)){
			return InteractionResultHolder.success(itemstack);
		}else{
			BlockPos blockpos = raytraceresult.getBlockPos();
			if(!(world.getBlockState(blockpos).getBlock() instanceof LiquidBlock)){
				return InteractionResultHolder.pass(itemstack);
			}else if(world.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos, raytraceresult.getDirection(), itemstack)){
				if(!spawnMob(itemstack, player, (ServerLevel) world, blockpos, MobSpawnType.SPAWN_EGG, false, false)){
					return InteractionResultHolder.pass(itemstack);
				}else{
					if(!player.getAbilities().instabuild){
						itemstack.shrink(1);
					}

					//We've gotten this far without acknowledging stats as a mechanic, and we're not starting now
//					player.awardStat(Stats.ITEM_USED.get(this));
					return InteractionResultHolder.consume(itemstack);
				}
			}else{
				return InteractionResultHolder.fail(itemstack);
			}
		}
	}
}
