package com.Da_Technomancer.crossroads.tileentities.witchcraft;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.API.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.EmbryoLabMorphRec;
import com.Da_Technomancer.crossroads.entity.mob_effects.CRPotions;
import com.Da_Technomancer.crossroads.gui.container.EmbryoLabContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.witchcraft.BloodSample;
import com.Da_Technomancer.essentials.packets.INBTReceiver;
import com.Da_Technomancer.essentials.packets.SendNBTToClient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@ObjectHolder(Crossroads.MODID)
public class EmbryoLabTileEntity extends InventoryTE implements INBTReceiver{

	@ObjectHolder("embryo_lab")
	public static BlockEntityType<EmbryoLabTileEntity> TYPE = null;

	public EntityTemplate template = null;//Kept synced to the client

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(template == null){
			chat.add(new TranslatableComponent("tt.crossroads.embryo_lab.empty"));
		}else{
			template.addTooltip(chat, 13);
		}
		super.addInfo(chat, player, hit);
	}

	public EmbryoLabTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 1);
		//Index 0: output
	}

	private void syncTemplate(){
		CompoundTag nbt = new CompoundTag();
		if(template != null){
			nbt.put("template", template.serializeNBT());
		}
		CRPackets.sendPacketAround(level, worldPosition, new SendNBTToClient(nbt, worldPosition));
	}

	public void createOutput(){
		if(template != null && inventory[0].isEmpty()){
			inventory[0] = new ItemStack(CRItems.embryo);
			CRItems.embryo.withEntityTypeData(inventory[0], template, false);
			CRItems.embryo.setSpoilTime(inventory[0], CRItems.embryo.getLifetime(), level.getGameTime());
			template = null;
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CRProperties.ACTIVE, false));
			setChanged();
			syncTemplate();
		}
	}

	public ItemStack addItem(ItemStack stack){
		if(stack.getItem() == CRItems.separatedBloodSample && template == null){
			//Add blood to an empty lab
			EntityTemplate bloodTemplate = BloodSample.getEntityTypeData(stack);

			//Check if the entity is on the blacklist. If so, refuse to add it
			if(!EntityTemplate.isCloningAllowed(bloodTemplate.getEntityName())){
				return stack;
			}

			template = bloodTemplate;

			//If the blood sample was spoiled, increase degradation
			if(CRItems.separatedBloodSample.isSpoiled(stack, level)){
				template.setDegradation(template.getDegradation() + 1);
			}
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CRProperties.ACTIVE, true));
			setChanged();
			syncTemplate();
			return new ItemStack(CRItems.bloodSampleEmpty);
		}

		if(template != null){
			if(stack.getItem() == Items.NAME_TAG && !template.isLoyal()){
				//Apply the imprinting trait and consume the item
				template.setLoyal(true);
				stack = stack.copy();
				stack.shrink(1);
				setChanged();
				syncTemplate();
				return stack;
			}
			if(stack.getItem() == CRItems.soulCluster && !template.isRespawning()){
				//Apply the respawning trait and consume the item
				template.setRespawning(true);
				stack = stack.copy();
				stack.shrink(1);
				setChanged();
				syncTemplate();
				return stack;
			}
			Potion potion = PotionUtils.getPotion(stack);
			if(potion != Potions.EMPTY){
				//Add potion effects which can be made permanent and do not already exist on the template
				boolean foundLegalEffect = false;
				NextPotionEffect: for(MobEffectInstance potionEffect : potion.getEffects()){
					//Special case curative to remove all potion effects
					if(potionEffect.getEffect() == CRPotions.CURATIVE_EFFECT){
						foundLegalEffect = true;
						template.getEffects().clear();
					}else if(CRPotions.canBePermanentEffect(potionEffect)){
						//Check that the effect isn't already part of the template
						for(MobEffectInstance templateEffect : template.getEffects()){
							if(templateEffect.getEffect() == potionEffect.getEffect() && templateEffect.getAmplifier() >= potionEffect.getAmplifier()){
								//This effect already exists in permanent form in an equal or stronger intensity
								continue NextPotionEffect;
							}
						}
						//This is a legal effect
						template.getEffects().add(potionEffect);
						foundLegalEffect = true;
					}
				}
				if(foundLegalEffect){
					setChanged();
					syncTemplate();
					return new ItemStack(Items.GLASS_BOTTLE);
				}
			}

			//Handle entity type morphing
			List<EmbryoLabMorphRec> recipes = level.getRecipeManager().getAllRecipesFor(CRRecipes.EMBRYO_LAB_MORPH_TYPE);
			for(EmbryoLabMorphRec rec : recipes){
				if(rec.isEnabled() && rec.getInputMob().equals(template.getEntityName()) && rec.getIngr().test(stack)){
					template.setEntityName(rec.getOutputMob());
					template.setAdditionalSaveData(null);//Reset any mob-type-specific save data
					stack.shrink(1);
					setChanged();
					syncTemplate();
					return stack;
				}
			}
		}
		return stack;
	}

	@Override
	public void receiveNBT(CompoundTag nbt, @Nullable ServerPlayer player){
		if(nbt.contains("template")){
			template = new EntityTemplate();
			template.deserializeNBT(nbt.getCompound("template"));
		}else{
			template = null;
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		if(nbt.contains("template")){
			template = new EntityTemplate();
			template.deserializeNBT(nbt.getCompound("template"));
		}else{
			template = null;
		}
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		if(template != null){
			nbt.put("template", template.serializeNBT());
		}
		return nbt;
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		if(template != null){
			nbt.put("template", template.serializeNBT());
		}
		return nbt;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		itemOpt.invalidate();
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction dir){
		//Items can be removed from the output
		return index == 0;
	}

	@Override
	public Component getDisplayName(){
		return new TranslatableComponent("container.crossroads.embryo_lab");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player){
		return new EmbryoLabContainer(id, playerInventory, createContainerBuf());
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
}
