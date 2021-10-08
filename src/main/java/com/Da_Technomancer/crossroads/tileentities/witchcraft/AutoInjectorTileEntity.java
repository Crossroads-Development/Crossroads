package com.Da_Technomancer.crossroads.tileentities.witchcraft;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.container.AutoInjectorContainer;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import com.Da_Technomancer.crossroads.API.templates.InventoryTE.ItemHandler;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

@ObjectHolder(Crossroads.MODID)
public class AutoInjectorTileEntity extends InventoryTE{

	@ObjectHolder("auto_injector")
	public static BlockEntityType<AutoInjectorTileEntity> type = null;

	public static final int DURATION_CAPACITY = 20 * 60 * 20;//In ticks
	public static final int[] SETTINGS = {10 * 20, 30 * 20, 60 * 20, 2 * 60 * 20, 5 * 60 * 20, 10 * 60 * 20};//In ticks
	public static final int SIZE = 5;
	//This refuses to launch if this field is final. I don't know why- some reflection thing in Forge involving @ObjectHolder
	public static AABB ZONE = new AABB(-SIZE / 2D, -SIZE / 2D, -SIZE / 2D, SIZE / 2D, SIZE / 2D, SIZE / 2D);

	private int mode = 0;
	private MobEffect storedEffect = null;
	private int intensity = 0;//There's an offset of 1 on this- 0 is intensity 1, 1 is intensity 2, etc
	private int duration = 0;//In ticks

	public AutoInjectorTileEntity(){
		super(type, 2);//Index 0: Input; Index 1: Output bottles
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(storedEffect == null || duration <= 0){
			chat.add(new TranslatableComponent("tt.crossroads.auto_injector.stored.empty"));
		}else{
			chat.add(new TranslatableComponent("tt.crossroads.auto_injector.stored", duration / 20, DURATION_CAPACITY / 20, intensity + 1).append(storedEffect.getDisplayName()));
		}
		chat.add(new TranslatableComponent("tt.crossroads.auto_injector.duration_setting", getDurationSetting() / 20));
		super.addInfo(chat, player, hit);
	}

	public int getStoredEffectIndex(){
		return MobEffect.getId(storedEffect);
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
	public void tick(){
		super.tick();

		if(level.isClientSide){
			return;
		}

		//Only run every few ticks to reduce lag
		int runPeriod = 10;
		if(level.getGameTime() % runPeriod == 0){
			attemptRefill();

			//Disabled by redstone
			if(duration > 0 && RedstoneUtil.getRedstoneAtPos(level, worldPosition) == 0){
				//Find the region being affected
				Direction facing = getBlockState().getValue(ESProperties.FACING);
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
		if(canPlaceItem(0, inventory[0]) && (inventory[1].isEmpty() || inventory[1].getItem() == Items.GLASS_BOTTLE && inventory[1].getMaxStackSize() > inventory[1].getCount())){//Has space in the output
			Potion input = PotionUtils.getPotion(inventory[0]);
			List<MobEffectInstance> effectList = input.getEffects();
			//We can only reload with single-effect potions
			if(effectList.size() == 1){
				MobEffectInstance effectInstance = effectList.get(0);
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
			}
		}
	}

	@Override
	public AABB getRenderBoundingBox(){
		return new AABB(worldPosition).inflate(SIZE);
	}

	@Override
	public void load(BlockState state, CompoundTag nbt){
		super.load(state, nbt);
		int effectID = nbt.getInt("effect_id");
		if(effectID <= 0){
			storedEffect = null;
		}else{
			storedEffect = MobEffect.byId(effectID - 1);
		}
		intensity = nbt.getInt("intensity");
		duration = nbt.getInt("duration");
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		if(storedEffect == null){
			nbt.putInt("effect_id", 0);
		}else{
			//We offset the id by one in the NBT, so we can use 0 for null
			nbt.putInt("effect_id", MobEffect.getId(storedEffect) + 1);
		}
		nbt.putInt("intensity", intensity);
		nbt.putInt("duration", duration);
		return nbt;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		itemOpt.invalidate();
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return index == 1;//Output slot
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack){
		if(!super.canPlaceItem(index, stack) || index != 0){
			return false;
		}
		Potion potion = PotionUtils.getPotion(stack);
		return (stack.getItem() == Items.POTION || stack.getItem() == Items.SPLASH_POTION) && potion != null && potion != Potions.EMPTY && !potion.hasInstantEffects();
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.crossroads.auto_injector");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity){
		return new AutoInjectorContainer(id, playerInventory, createContainerBuf());
	}
}
