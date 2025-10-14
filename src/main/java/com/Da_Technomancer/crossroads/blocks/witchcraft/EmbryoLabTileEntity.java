package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.api.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.EmbryoLabMorphRec;
import com.Da_Technomancer.crossroads.entity.mob_effects.CRPotions;
import com.Da_Technomancer.crossroads.gui.container.EmbryoLabContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.witchcraft.BloodSample;
import com.Da_Technomancer.essentials.api.packets.INBTReceiver;
import com.Da_Technomancer.essentials.api.packets.SendNBTToTE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EmbryoLabTileEntity extends InventoryTE implements INBTReceiver{

	public static final BlockEntityType<EmbryoLabTileEntity> TYPE = CRTileEntity.createType(EmbryoLabTileEntity::new, CRBlocks.embryoLab);

	public EntityTemplate template = null;//Kept synced to the client

	private final IItemHandler itemHandler = new ItemHandler();

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(template == null){
			chat.add(Component.translatable("tt.crossroads.embryo_lab.empty"));
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
		CRPackets.sendPacketAround(level, worldPosition, new SendNBTToTE(nbt, worldPosition));
	}

	public void createOutput(){
		if(template != null && inventory[0].isEmpty()){
			inventory[0] = new ItemStack(CRItems.embryo);
			CRItems.embryo.withEntityTypeData(inventory[0], template, false);
			IPerishable.setSpoilTime(inventory[0], CRItems.embryo.getLifetime(), level.getGameTime());
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
			if(IPerishable.isSpoiled(stack, level)){
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
			if(stack.getItem() == CRItems.bloodSample && !template.isLoyal()){
				//Blood sample from a player can act to set imprinting, targeting the specific player
				EntityTemplate imprintingBloodTemplate = BloodSample.getEntityTypeData(stack);
				if(imprintingBloodTemplate.getOriginatingUUID() != null && imprintingBloodTemplate.getEntityType() == EntityType.PLAYER){
					template.setLoyal(true);
					template.setImprintingPlayer(imprintingBloodTemplate.getOriginatingUUID());
					setChanged();
					syncTemplate();
					return new ItemStack(CRItems.bloodSampleEmpty);
				}
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
			if(stack.getItem() == Items.POTION || stack.getItem() == Items.SPLASH_POTION){
				PotionContents potion = stack.get(DataComponents.POTION_CONTENTS);
				if(potion != null){
					//Add potion effects which can be made permanent and do not already exist on the template
					boolean foundLegalEffect = false;
					NextPotionEffect:
					for(MobEffectInstance potionEffect : potion.getAllEffects()){
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
			}

			//Handle entity type morphing
			List<RecipeHolder<EmbryoLabMorphRec>> recipes = level.getRecipeManager().getAllRecipesFor(CRRecipes.EMBRYO_LAB_MORPH_TYPE);
			for(RecipeHolder<EmbryoLabMorphRec> recHolder : recipes){
				EmbryoLabMorphRec rec = recHolder.value();
				if(rec.isEnabled() && rec.getInputMob().equals(template.getEntityName()) && rec.getIngr().test(stack)){
					template.setEntityName(rec.getOutputMob());
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
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		if(nbt.contains("template")){
			template = new EntityTemplate();
			template.deserializeNBT(nbt.getCompound("template"));
		}else{
			template = null;
		}
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		if(template != null){
			nbt.put("template", template.serializeNBT());
		}
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries){
		CompoundTag nbt = super.getUpdateTag(pRegistries);
		if(template != null){
			nbt.put("template", template.serializeNBT());
		}
		return nbt;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction dir){
		//Items can be removed from the output
		return index == 0;
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.crossroads.embryo_lab");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player){
		return new EmbryoLabContainer(id, playerInventory, createContainerBuf());
	}

	@Nullable
	@Override
	public IItemHandler getItemHandler(Direction direction){
		return itemHandler;
	}
}
