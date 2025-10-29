package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.DetailedCrafterRec;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.PathSigil;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public class DetailedAutoCrafter extends CrafterBlock{

	public DetailedAutoCrafter(){
		super(CRBlocks.getMetalProperty());
		String name = "detailed_auto_crafter";
		CRBlocks.queueForRegister(name, this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new DetailedAutoCrafterTileEntity(pos, state);
	}

	private static final HashMap<Item, RecipeHolder<DetailedCrafterRec>[]> RECIPE_CACHE = new HashMap<>();

	@Nullable
	public static RecipeHolder<DetailedCrafterRec> getRecipe(ServerLevel world, DetailedAutoCrafterTileEntity te, boolean updateSigil){
		ItemStack sigilStack = te.sigilSlotContainer.getItem(0);
		if(!sigilStack.isEmpty() && sigilStack.getItem() instanceof PathSigil sigilItem){
			PathSigil.DetailedCrafterRecipeReference recipeReference = sigilStack.get(CRItems.SIGIL_RECIPE_DATA);
			if(recipeReference != null){
				//Only allows recipes matching the installed sigil
				Item craftedItem = recipeReference.craftedItem();
				if(craftedItem == null){
					//The item for this recipe isn't even registered
					return null;
				}
				RecipeHolder<DetailedCrafterRec>[] possibleRecipes = RECIPE_CACHE.get(craftedItem);
				if(possibleRecipes == null){
					possibleRecipes = world.getRecipeManager().getAllRecipesFor(CRRecipes.DETAILED_TYPE).parallelStream().filter(rec -> rec.value().getResultItem().is(craftedItem)).toArray(RecipeHolder[]::new);
					RECIPE_CACHE.put(craftedItem, possibleRecipes);
				}
				for(RecipeHolder<DetailedCrafterRec> rec : possibleRecipes){
					if(rec.value().matches(te.asCraftInput(), world) && rec.value().getPath() == sigilItem.getPath()){
						return rec;
					}
				}
				return null;
			}else{
				//Allow any recipe and set the sigil once crafted
				List<RecipeHolder<DetailedCrafterRec>> recipes = world.getRecipeManager().getRecipesFor(CRRecipes.DETAILED_TYPE, te.asCraftInput(), world);
				for(RecipeHolder<DetailedCrafterRec> rec : recipes){
					if(rec.value().getPath() == sigilItem.getPath()){
						if(updateSigil){
							sigilStack.set(CRItems.SIGIL_RECIPE_DATA, new PathSigil.DetailedCrafterRecipeReference(MiscUtil.getRegistryName(rec.value().getResultItem().getItem(), BuiltInRegistries.ITEM)));
							te.sigilSlotContainer.setItem(0, sigilStack);
						}
						return rec;
					}
				}
			}
		}
		return null;
	}

	@Override
	protected void dispenseFrom(BlockState pState, ServerLevel pLevel, BlockPos pPos){
		if(pLevel.getBlockEntity(pPos) instanceof DetailedAutoCrafterTileEntity te){
			CraftingInput craftinginput = te.asCraftInput();
			RecipeHolder<DetailedCrafterRec> recipeholder = getRecipe(pLevel, te, true);
			if(recipeholder == null){
				pLevel.levelEvent(1050, pPos, 0);
			}else{
				ItemStack itemstack = recipeholder.value().assemble(craftinginput, pLevel.registryAccess());
				if(itemstack.isEmpty()){
					pLevel.levelEvent(1050, pPos, 0);
				}else{
					te.setCraftingTicksRemaining(6);
					pLevel.setBlock(pPos, pState.setValue(CRAFTING, true), 2);
					itemstack.onCraftedBySystem(pLevel);
					dispenseItemReimplement(pLevel, pPos, te, itemstack, pState, recipeholder);

					for(ItemStack itemstack1 : recipeholder.value().getRemainingItems(craftinginput)){
						if(!itemstack1.isEmpty()){
							dispenseItemReimplement(pLevel, pPos, te, itemstack1, pState, recipeholder);
						}
					}

					te.getItems().forEach(p_307295_ -> {
						if(!p_307295_.isEmpty()){
							p_307295_.shrink(1);
						}
					});
					te.setChanged();
				}
			}
		}
	}

	private void dispenseItemReimplement(ServerLevel pLevel, BlockPos pPos, CrafterBlockEntity pCrafter, ItemStack pStack, BlockState pState, RecipeHolder<DetailedCrafterRec> pRecipe){
		//Re-implement of version in superclass
		Direction direction = pState.getValue(BlockStateProperties.ORIENTATION).front();
		Container container = HopperBlockEntity.getContainerAt(pLevel, pPos.relative(direction));
		ItemStack itemstack = pStack.copy();
		if(container != null && (container instanceof CrafterBlockEntity || pStack.getCount() > container.getMaxStackSize(pStack))){
			while(!itemstack.isEmpty()){
				ItemStack itemstack2 = itemstack.copyWithCount(1);
				ItemStack itemstack1 = HopperBlockEntity.addItem(pCrafter, container, itemstack2, direction.getOpposite());
				if(!itemstack1.isEmpty()){
					break;
				}

				itemstack.shrink(1);
			}
		}else if(container != null){
			while(!itemstack.isEmpty()){
				int i = itemstack.getCount();
				itemstack = HopperBlockEntity.addItem(pCrafter, container, itemstack, direction.getOpposite());
				if(i == itemstack.getCount()){
					break;
				}
			}
		}

		if(!itemstack.isEmpty()){
			Vec3 vec3 = Vec3.atCenterOf(pPos);
			Vec3 vec31 = vec3.relative(direction, 0.7);
			DefaultDispenseItemBehavior.spawnItem(pLevel, itemstack, 6, direction, vec31);

			for(ServerPlayer serverplayer : pLevel.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(vec3, 17.0, 17.0, 17.0))){
				CriteriaTriggers.CRAFTER_RECIPE_CRAFTED.trigger(serverplayer, pRecipe.id(), pCrafter.getItems());
			}

			pLevel.levelEvent(1049, pPos, 0);
			pLevel.levelEvent(2010, pPos, direction.get3DDataValue());
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		//TODO
		tooltip.add(Component.translatable("tt.crossroads.detailed_auto_crafter.basic"));
		tooltip.add(Component.translatable("tt.crossroads.detailed_auto_crafter.sigil"));
		tooltip.add(Component.translatable("tt.crossroads.detailed_auto_crafter.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	protected MapCodec<CrafterBlock> codec(){
		return CRBlocks.DETAILED_AUTO_CRAFTER_TYPE.value();
	}
}
