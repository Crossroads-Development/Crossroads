package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.crafting.CRRecipes;
import com.Da_Technomancer.crossroads.crafting.recipes.BoboRec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class BoboRod extends Item{

	private static final DispenseItemBehavior BOBO_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){

		@Override
		public ItemStack execute(BlockSource source, ItemStack stack){
			//Able to do bobo crafting via dispenser
			act(source.getLevel(), source.getPos(), new Vec3(source.x(), source.y(), source.z()), null);
			return stack;
		}
	};

	//Items that are considered valid offerings
	private static final TagKey<Item> offering = CRItemTags.getTagKey(ForgeRegistries.Keys.ITEMS, new ResourceLocation(Crossroads.MODID, "bobo_unlock_key"));

	protected BoboRod(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
		String name = "bobo_rod";
		setRegistryName(name);
		CRItems.toRegister.add(this);
		DispenserBlock.registerBehavior(this, BOBO_DISPENSER_BEHAVIOR);
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		return act(context.getLevel(), context.getClickedPos(), context.getClickLocation(), context.getPlayer()) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
	}
	
	private static boolean act(Level world, BlockPos pos, Vec3 hitVec, @Nullable Player player){
		List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(hitVec.add(-1, -1, -1), hitVec.add(1, 1, 1)), Entity::isAlive);
		if(items.size() == 4){
			SimpleContainer inv = new SimpleContainer(3);
			boolean hasOffering = false;
			for(ItemEntity ent : items){
				if(ent.getItem().getCount() != 1){
					world.playSound(player, pos, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.PLAYERS, 1F, (float) Math.random());
					return false;
				}
				if(!hasOffering && CRItemTags.tagContains(offering, ent.getItem().getItem())){
					hasOffering = true;
				}else{
					inv.addItem(ent.getItem());
				}
			}
			if(hasOffering){
				Optional<BoboRec> rec = world.getRecipeManager().getRecipeFor(CRRecipes.BOBO_TYPE, inv, world);
				if(rec.isPresent()){
					items.forEach(Entity::kill);
					Containers.dropItemStack(world, hitVec.x, hitVec.y, hitVec.z, rec.get().assemble(inv));

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
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.bobo_rod.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.bobo_rod.use"));
		tooltip.add(new TranslatableComponent("tt.crossroads.bobo_rod.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
