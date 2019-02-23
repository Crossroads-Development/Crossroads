package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.entity.EntityFlameCore;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.google.common.base.Predicate;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class DampingPowder extends Item{

	private static final int RANGE = 16;
	private static final Predicate<Entity> FLAME_PREDICATE = new Predicate<Entity>(){
		@Override
		public boolean apply(Entity entity){
			return entity instanceof EntityFlameCore;
		}
	};

	private static final IBehaviorDispenseItem DAMPING_DISPENSER_BEHAVIOR = new BehaviorDefaultDispenseItem(){

		/**
		 * Dispense the specified stack, play the dispense sound and spawn particles.
		 */
		@Override
		public ItemStack dispenseStack(IBlockSource source, ItemStack stack){
			stack.shrink(1);
			List<Entity> ents = source.getWorld().getEntitiesInAABBexcluding(null, new AxisAlignedBB(source.getBlockPos()).grow(RANGE), FLAME_PREDICATE);
			for(Entity ent : ents){
				ent.setDead();
			}
			if(!ents.isEmpty()){
				source.getWorld().playSound(null, source.getBlockPos(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.BLOCKS, 1, 0);
			}
			return stack;
		}

		/**
		 * Play the dispense sound from the specified block.
		 */
		@Override
		protected void playDispenseSound(IBlockSource source){
			source.getWorld().playEvent(1000, source.getBlockPos(), 0);
		}
	};

	public DampingPowder(){
		String name = "damping_powder";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, DAMPING_DISPENSER_BEHAVIOR);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn){
		ItemStack held = playerIn.getHeldItem(handIn);
		held.shrink(1);
		List<Entity> ents = worldIn.getEntitiesInAABBexcluding(playerIn, new AxisAlignedBB(playerIn.getPosition()).grow(RANGE), FLAME_PREDICATE);
		for(Entity ent : ents){
			ent.setDead();
		}
		if(!ents.isEmpty()){
			worldIn.playSound(null, playerIn.posX, playerIn.posY + playerIn.getEyeHeight(), playerIn.posZ, SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1, 0);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, held);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add("Extinguishes any nearby flame clouds on use");
		tooltip.add("An Alchemist's fire extinguisher");
	}
}
