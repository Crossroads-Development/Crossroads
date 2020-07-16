package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.items.crafting.recipes.BoboRec;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class BoboRod extends Item{

	private static final IDispenseItemBehavior BOBO_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){

		@Override
		public ItemStack dispenseStack(IBlockSource source, ItemStack stack){
			//Able to do bobo crafting via dispenser
			act(source.getWorld(), source.getBlockPos(), new Vector3d(source.getX(), source.getY(), source.getZ()), null);
			return stack;
		}
	};

	//Items that are considered valid offerings
	private static final ITag<Item> offering = ItemTags.makeWrapperTag(Crossroads.MODID + ":bobo_unlock_key");

	protected BoboRod(){
		super(new Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1));
		String name = "bobo_rod";
		setRegistryName(name);
		CRItems.toRegister.add(this);
		DispenserBlock.registerDispenseBehavior(this, BOBO_DISPENSER_BEHAVIOR);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context){
		return act(context.getWorld(), context.getPos(), context.getHitVec(), context.getPlayer()) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
	}
	
	private static boolean act(World world, BlockPos pos, Vector3d hitVec, @Nullable PlayerEntity player){
		List<ItemEntity> items = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(hitVec.add(-1, -1, -1), hitVec.add(1, 1, 1)), Entity::isAlive);
		if(items.size() == 4){
			Inventory inv = new Inventory(3);
			boolean hasOffering = false;
			for(ItemEntity ent : items){
				if(ent.getItem().getCount() != 1){
					world.playSound(player, pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.PLAYERS, 1F, (float) Math.random());
					return false;
				}
				if(!hasOffering && offering.contains(ent.getItem().getItem())){
					hasOffering = true;
				}else{
					inv.addItem(ent.getItem());
				}
			}
			if(hasOffering){
				Optional<BoboRec> rec = world.getRecipeManager().getRecipe(CRRecipes.BOBO_TYPE, inv, world);
				if(rec.isPresent()){
					items.forEach(Entity::remove);
					InventoryHelper.spawnItemStack(world, hitVec.x, hitVec.y, hitVec.z, rec.get().getCraftingResult(inv));

					//Spawn some particles and sound
					world.addParticle(ParticleTypes.POOF, hitVec.x, hitVec.y, hitVec.z, Math.random() * 0.02, Math.random() * 0.02, Math.random() * 0.02);
					world.playSound(player, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1F, (float) Math.random());
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.bobo_rod.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.bobo_rod.use"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.bobo_rod.quip").func_230530_a_(MiscUtil.TT_QUIP));
	}
}
