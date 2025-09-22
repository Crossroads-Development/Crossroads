package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.FixedRecipeInput;
import com.Da_Technomancer.crossroads.crafting.BoboRec;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class BoboRod extends Item{

	private static final DispenseItemBehavior BOBO_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){

		@Override
		public ItemStack execute(BlockSource source, ItemStack stack){
			//Able to do bobo crafting via dispenser
			BlockPos actPos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
			act(source.level(), actPos, actPos.getCenter(), null);
			return stack;
		}
	};

	//Items that are considered valid offerings
	private static final TagKey<Item> offering = CraftingUtil.getTagKey(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "bobo_unlock_key"));

	protected BoboRod(){
		super(new Properties().stacksTo(1).rarity(CRItems.BOBO_RARITY));
		String name = "bobo_rod";
		CRItems.queueForRegister(name, this);
		DispenserBlock.registerBehavior(this, BOBO_DISPENSER_BEHAVIOR);
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		return act(context.getLevel(), context.getClickedPos(), context.getClickLocation(), context.getPlayer()) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
	}

	private static boolean act(Level world, BlockPos pos, Vec3 hitVec, @Nullable Player player){
		List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(hitVec.add(-1, -1, -1), hitVec.add(1, 1, 1)), Entity::isAlive);
		if(items.size() == 4){
			ItemStack[] recipeItems = new ItemStack[3];
			int i = 0;
			boolean hasOffering = false;
			for(ItemEntity ent : items){
				if(ent.getItem().getCount() != 1){
					world.playSound(player, pos, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.PLAYERS, 1F, (float) Math.random());
					return false;
				}
				if(!hasOffering && CraftingUtil.tagContains(offering, ent.getItem().getItem())){
					hasOffering = true;
				}else{
					recipeItems[i] = ent.getItem();
					i++;
				}
			}
			if(hasOffering && i == 3){
				Optional<RecipeHolder<BoboRec>> rec = world.getRecipeManager().getRecipeFor(CRRecipes.BOBO_TYPE, new FixedRecipeInput(null, recipeItems), world);
				if(rec.isPresent()){
					items.forEach(Entity::kill);
					Containers.dropItemStack(world, hitVec.x, hitVec.y, hitVec.z, rec.get().value().getResultItem().copy());

					//Spawn some particles and sound
					world.addParticle(ParticleTypes.POOF, hitVec.x, hitVec.y, hitVec.z, Math.random() * 0.02, Math.random() * 0.02, Math.random() * 0.02);
					world.playSound(player, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1F, (float) Math.random());
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("tt.crossroads.bobo_rod.desc"));
		tooltip.add(Component.translatable("tt.crossroads.bobo_rod.use"));
		tooltip.add(Component.translatable("tt.crossroads.bobo_rod.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
