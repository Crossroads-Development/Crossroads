package com.Da_Technomancer.crossroads.tileentities.witchcraft;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
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
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@ObjectHolder(Crossroads.MODID)
public class EmbryoLabTileEntity extends InventoryTE implements INBTReceiver{

	@ObjectHolder("embryo_lab")
	public static TileEntityType<EmbryoLabTileEntity> type = null;

	public EntityTemplate template = null;//Kept synced to the client

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		if(template == null){
			chat.add(new TranslationTextComponent("tt.crossroads.embryo_lab.empty"));
		}else{
			template.addTooltip(chat, 13);
		}
		super.addInfo(chat, player, hit);
	}

	public EmbryoLabTileEntity(){
		super(type, 1);
		//Index 0: output
	}

	private void syncTemplate(){
		CompoundNBT nbt = new CompoundNBT();
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
				stack.shrink(1);
				setChanged();
				syncTemplate();
				return stack;
			}
			if(stack.getItem() == CRItems.soulCluster && !template.isRespawning()){
				//Apply the respawning trait and consume the item
				template.setRespawning(true);
				stack.shrink(1);
				setChanged();
				syncTemplate();
				return stack;
			}
			Potion potion = PotionUtils.getPotion(stack);
			if(potion != Potions.EMPTY){
				//Add potion effects which can be made permanent and do not already exist on the template
				boolean foundLegalEffect = false;
				NextPotionEffect: for(EffectInstance potionEffect : potion.getEffects()){
					//Special case curative to remove all potion effects
					if(potionEffect.getEffect() == CRPotions.CURATIVE_EFFECT){
						foundLegalEffect = true;
						template.getEffects().clear();
					}else if(CRPotions.canBePermanentEffect(potionEffect)){
						//Check that the effect isn't already part of the template
						for(EffectInstance templateEffect : template.getEffects()){
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
	public void receiveNBT(CompoundNBT nbt, @Nullable ServerPlayerEntity player){
		if(nbt.contains("template")){
			template = new EntityTemplate();
			template.deserializeNBT(nbt.getCompound("template"));
		}else{
			template = null;
		}
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		if(nbt.contains("template")){
			template = new EntityTemplate();
			template.deserializeNBT(nbt.getCompound("template"));
		}else{
			template = null;
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		if(template != null){
			nbt.put("template", template.serializeNBT());
		}
		return nbt;
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
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
	public ITextComponent getDisplayName(){
		return new TranslationTextComponent("container.crossroads.embryo_lab");
	}

	@Nullable
	@Override
	public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player){
		return new EmbryoLabContainer(id, playerInventory, createContainerBuf());
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);
	private final LazyOptional<BeamHandler> beamOpt = LazyOptional.of(BeamHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}
		if(capability == Capabilities.BEAM_CAPABILITY){
			return (LazyOptional<T>) beamOpt;
		}

		return super.getCapability(capability, facing);
	}

	/*
	 * TODO
	 *  This is a temporary mechanic for finalizing the crafting
	 * 	The intended mechanic will involve being struck by lightning
	 * 	It will be implemented once Crossroads is in MC1.17+ and the lightning rod block is available
	 */
	private class BeamHandler implements IBeamHandler{

		@Override
		public void setBeam(@Nonnull BeamUnit mag){
			//Finalize the craft when receiving a charge beam
			if(EnumBeamAlignments.getAlignment(mag) == EnumBeamAlignments.CHARGE){
				createOutput();
			}
		}
	}
}
