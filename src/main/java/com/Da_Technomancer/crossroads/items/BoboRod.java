package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import com.Da_Technomancer.crossroads.items.crafting.recipes.BoboRec;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class BoboRod extends Item{

	private static final int DURABILITY = 100;
	//Items that are considered valid offerings
	private static final Tag<Item> offering = new ItemTags.Wrapper(new ResourceLocation(Crossroads.MODID, "bobo_unlock_key"));

	protected BoboRod(){
		super(CRItems.itemProp.maxStackSize(1).maxDamage(DURABILITY));
		String name = "bobo_rod";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context){
		Vec3d hitVec = context.getHitVec();
		List<ItemEntity> items = context.getWorld().getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(hitVec.add(-1, -1, -1), hitVec.add(1, 1, 1)), Entity::isAlive);
		if(items.size() == 4){
			Inventory inv = new Inventory(3);
			boolean hasOffering = false;
			for(ItemEntity ent : items){
				if(ent.getItem().getCount() != 1){
					context.getWorld().playSound(context.getPlayer(), context.getPos(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.PLAYERS, 1F, (float) Math.random());
					return ActionResultType.FAIL;
				}
				if(!hasOffering && offering.contains(ent.getItem().getItem())){
					hasOffering = true;
				}else{
					inv.addItem(ent.getItem());
				}
			}
			if(hasOffering){
				Optional<BoboRec> rec = context.getWorld().getRecipeManager().getRecipe(RecipeHolder.BOBO_TYPE, inv, context.getWorld());
				if(rec.isPresent()){
					items.forEach(Entity::remove);
					InventoryHelper.spawnItemStack(context.getWorld(), hitVec.x, hitVec.y, hitVec.z, rec.get().getRecipeOutput());

					//Spawn some particles and sound
					context.getWorld().addParticle(ParticleTypes.POOF, hitVec.x, hitVec.y, hitVec.z, Math.random() * 0.02, Math.random() * 0.02, Math.random() * 0.02);
					context.getWorld().playSound(context.getPlayer(), context.getPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1F, (float) Math.random());
					return ActionResultType.SUCCESS;
				}
			}
		}
		context.getWorld().playSound(context.getPlayer(), context.getPos(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.PLAYERS, 1F, (float) Math.random());
		return ActionResultType.FAIL;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.bobo_rod.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.bobo_rod.use"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.bobo_rod.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
