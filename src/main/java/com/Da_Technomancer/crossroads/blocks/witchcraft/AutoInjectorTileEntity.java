package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.gui.container.AutoInjectorContainer;
import com.Da_Technomancer.essentials.api.IItemCapable;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AutoInjectorTileEntity extends InventoryTE implements IItemCapable{

	public static final BlockEntityType<AutoInjectorTileEntity> TYPE = CRTileEntity.createType(AutoInjectorTileEntity::new, CRBlocks.autoInjector);

	public static final int DURATION_CAPACITY = 20 * 60 * 20;//In ticks
	public static final int[] SETTINGS = {10 * 20, 30 * 20, 60 * 20, 2 * 60 * 20, 5 * 60 * 20, 10 * 60 * 20};//In ticks
	public static final int SIZE = 5;
	//This refuses to launch if this field is final. I don't know why- some reflection thing in Forge involving @ObjectHolder
	public static AABB ZONE = new AABB(-SIZE / 2D, -SIZE / 2D, -SIZE / 2D, SIZE / 2D, SIZE / 2D, SIZE / 2D);

	private int mode = 0;
	private Holder<MobEffect> storedEffect = null;
	private int intensity = 0;//There's an offset of 1 on this- 0 is intensity 1, 1 is intensity 2, etc
	private int duration = 0;//In ticks

	private final IItemHandler itemOpt = new ItemHandler();


	public AutoInjectorTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 2);//Index 0: Input; Index 1: Output bottles
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(storedEffect == null || duration <= 0){
			chat.add(Component.translatable("tt.crossroads.auto_injector.stored.empty"));
		}else{
			chat.add(Component.translatable("tt.crossroads.auto_injector.stored", duration / 20, DURATION_CAPACITY / 20, intensity + 1).append(storedEffect.value().getDisplayName()));
		}
		chat.add(Component.translatable("tt.crossroads.auto_injector.duration_setting", getDurationSetting() / 20));
		super.addInfo(chat, player, hit);
	}

	public int getStoredEffectIndex(){
		if(storedEffect.isBound()){
			return BuiltInRegistries.MOB_EFFECT.getId(storedEffect.value());
		}else{
			return -1;
		}
	}

	public int getIntensity(){
		return intensity;
	}

	public int getDuration(){
		return duration;
	}

	public int getDurationSetting(){
		return SETTINGS[mode];
	}

	public int increaseSetting(){
		mode += 1;
		mode %= SETTINGS.length;
		return getDurationSetting();
	}

	@Override
	public void serverTick(){
		super.serverTick();

		//Only run every few ticks to reduce lag
		int runPeriod = 10;
		if(level.getGameTime() % runPeriod == 0){
			attemptRefill();

			//Disabled by redstone
			if(duration > 0 && RedstoneUtil.getRedstoneAtPos(level, worldPosition) == 0){
				//Find the region being affected
				Direction facing = getBlockState().getValue(CRProperties.FACING);
				AABB region = ZONE.move(worldPosition.getX() + 0.5D + facing.getStepX() * SIZE / 2D, worldPosition.getY() + 0.5D + facing.getStepY() * SIZE / 2D, worldPosition.getZ() + 0.5D + facing.getStepZ() * SIZE / 2D);

				List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, region, EntitySelector.LIVING_ENTITY_STILL_ALIVE);

				//Apply the effect
				for(LivingEntity ent : entities){
					MobEffectInstance existingEffect = ent.getEffect(storedEffect);
					//Apply if: no existing effect, or the existing effect is weaker, or the existing effect is the same intensity but is about to run out
					//This is written in such a way that even if we don't have enough for a full dose, we apply whatever duration remains in the machine
					if(existingEffect == null || existingEffect.getAmplifier() < intensity || (existingEffect.getAmplifier() == intensity && existingEffect.getDuration() <= runPeriod)){
						//If there was an existing effect that was about to run out, we discount the remaining time from the amount consumed
						int existingTime = existingEffect != null && existingEffect.getAmplifier() == intensity ? existingEffect.getDuration() : 0;
						int used = Math.max(0, Math.min(duration, getDurationSetting() - existingTime));
						duration -= used;
						ent.addEffect(new MobEffectInstance(storedEffect, used + existingTime, intensity));
						setChanged();
						attemptRefill();
						if(duration <= 0){
							storedEffect = null;
							duration = 0;
							intensity = 0;
							break;
						}
					}
				}
			}
		}
	}

	private void attemptRefill(){
		if(canLoadPotion(inventory[0]) && (inventory[1].isEmpty() || inventory[1].getItem() == Items.GLASS_BOTTLE && inventory[1].getMaxStackSize() > inventory[1].getCount())){//Has space in the output
			PotionContents potion = inventory[0].get(DataComponents.POTION_CONTENTS);
			//As a condition of being able to load a potion, only 1 effect
			//We can only reload with single-effect potions
			for(MobEffectInstance effectInstance : potion.getAllEffects()){
				int timeToAdd = (int) (effectInstance.getDuration() * CRConfig.injectionEfficiency.get());
				if(duration <= 0 || effectInstance.getEffect() == storedEffect && effectInstance.getAmplifier() == intensity && timeToAdd + duration < DURATION_CAPACITY){
					storedEffect = effectInstance.getEffect();
					intensity = effectInstance.getAmplifier();
					duration += timeToAdd;
					inventory[0].shrink(1);
					if(inventory[1].isEmpty()){
						inventory[1] = new ItemStack(Items.GLASS_BOTTLE, 1);
					}else{
						inventory[1].grow(1);
					}
					setChanged();
				}
				return;
			}
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		String effectId = nbt.getString("effect");
		if(!effectId.isEmpty()){
			storedEffect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(nbt.getString("effect"))).orElse(null);
		}else{
			storedEffect = null;
		}
		intensity = nbt.getInt("intensity");
		duration = nbt.getInt("duration");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		if(storedEffect != null){
			nbt.putString("effect", MiscUtil.nullFallback(BuiltInRegistries.MOB_EFFECT.getKey(storedEffect.value()), "").toString());
		}
		nbt.putInt("intensity", intensity);
		nbt.putInt("duration", duration);
	}

	@Nullable
	@Override
	public IItemHandler getItemHandler(Direction direction){
		return itemOpt;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index == 1;//Output slot
	}

	private static boolean canLoadPotion(ItemStack stack){
		if(stack.getItem() != Items.POTION && stack.getItem() != Items.SPLASH_POTION){
			return false;
		}
		PotionContents potion = stack.get(DataComponents.POTION_CONTENTS);
		int effectCount = 0;
		if(potion != null){
			for(MobEffectInstance effect : potion.getAllEffects()){
				effectCount += 1;
				if(effect.getEffect().value().isInstantenous()){
					return false;
				}
			}
		}
		return effectCount == 1;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		if(!super.canPlaceItem(index, stack) || index != 0){
			return false;
		}
		return canLoadPotion(stack);
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.crossroads.auto_injector");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity){
		return new AutoInjectorContainer(id, playerInventory, createContainerBuf());
	}
}
