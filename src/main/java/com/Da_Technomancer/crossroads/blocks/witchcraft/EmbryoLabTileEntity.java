package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.api.witchcraft.EntityTemplate;
import com.Da_Technomancer.crossroads.api.witchcraft.IEntityModifier;
import com.Da_Technomancer.crossroads.api.witchcraft.IEntityModifierType;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.EmbryoLabModifierRec;
import com.Da_Technomancer.crossroads.crafting.EmbryoLabMorphRec;
import com.Da_Technomancer.crossroads.gui.container.EmbryoLabContainer;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.witchcraft.BloodSample;
import com.Da_Technomancer.essentials.api.IItemCapable;
import com.Da_Technomancer.essentials.api.packets.INBTReceiver;
import com.Da_Technomancer.essentials.api.packets.SendNBTToTE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmbryoLabTileEntity extends InventoryTE implements INBTReceiver, IItemCapable{

	public static final BlockEntityType<EmbryoLabTileEntity> TYPE = CRTileEntity.createType(EmbryoLabTileEntity::new, CRBlocks.embryoLab);

	public EntityTemplate template = null;//Kept synced to the client

	private final IItemHandler itemHandler = new ItemHandler();

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(template == null){
			chat.add(Component.translatable("tt.crossroads.embryo_lab.empty"));
		}else{
			template.addTooltip(chat, player.level());
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
			nbt.put("template", template.serializeNBT(level.registryAccess()));
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
		if(stack.getItem() instanceof BloodSample && template == null){
			//Add blood to an empty lab

//			//Check if the entity is on the blacklist. If so, refuse to add it
//			if(EntityTemplate.isCloningForbidden(bloodTemplate.entityID())){
//				return stack;
//			}
			template = BloodSample.getAdjustedTemplate(stack, level);
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(CRProperties.ACTIVE, true));
			stack.shrink(1);
			setChanged();
			syncTemplate();
			return new ItemStack(CRItems.bloodSampleEmpty);
		}

		if(template != null){
			//Handle applying a modifier
			List<RecipeHolder<EmbryoLabModifierRec>> modifierRecipes = level.getRecipeManager().getAllRecipesFor(CRRecipes.EMBRYO_LAB_MODIFIER_TYPE);
			for(RecipeHolder<EmbryoLabModifierRec> recHolder : modifierRecipes){
				EmbryoLabModifierRec rec = recHolder.value();
				if(rec.isEnabled() && rec.getIngr().test(stack)){
					IEntityModifierType<?> modifierType = rec.getModifierType();
					IEntityModifier modifier = rec.createModifier(stack);
					if(modifier == null){
						//Invalid item - reject
						continue;
					}
					//Not allowed to modify returned map from template.modifiers() - copy it first
					Map<IEntityModifierType<?>, IEntityModifier> modiferMap = new HashMap<>(template.modifiers());
					IEntityModifier prevMod = modiferMap.get(modifierType);
					if(prevMod != null){
						//Merge it with the pre-existing modifier and update the template
						modiferMap.put(modifierType, modifierType.mergeModifiers(prevMod, modifier));
					}else{
						//No pre-existing modifier; insert the new one
						modiferMap.put(modifierType, modifier);
					}
					template = template.withModifiers(modiferMap);
					stack.shrink(1);
					setChanged();
					syncTemplate();
					return rec.assemble(this, level.registryAccess());
				}
			}

			//Handle entity type morphing
			List<RecipeHolder<EmbryoLabMorphRec>> recipes = level.getRecipeManager().getAllRecipesFor(CRRecipes.EMBRYO_LAB_MORPH_TYPE);
			for(RecipeHolder<EmbryoLabMorphRec> recHolder : recipes){
				EmbryoLabMorphRec rec = recHolder.value();
				if(rec.isEnabled() && rec.getInputMob().equals(template.entityID()) && rec.getIngr().test(stack)){
					template = template.withMob(rec.getOutputMob());
					stack.shrink(1);
					setChanged();
					syncTemplate();
					return ItemStack.EMPTY;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void receiveNBT(CompoundTag nbt, @Nullable ServerPlayer player){
		if(nbt.contains("template") && level != null){
			template = EntityTemplate.deserializeNBT(level.registryAccess(), nbt.getCompound("template"));
		}else{
			template = null;
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		if(nbt.contains("template")){
			template = EntityTemplate.deserializeNBT(registries, nbt.getCompound("template"));
		}else{
			template = null;
		}
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		if(template != null){
			nbt.put("template", template.serializeNBT(pRegistries));
		}
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries){
		CompoundTag nbt = super.getUpdateTag(pRegistries);
		if(template != null){
			nbt.put("template", template.serializeNBT(pRegistries));
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
