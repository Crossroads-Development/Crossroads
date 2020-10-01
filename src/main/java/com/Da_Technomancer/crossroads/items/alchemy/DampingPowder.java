package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.entity.EntityFlameCore;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class DampingPowder extends Item{

	private static final int RANGE = 32;
	private static final Predicate<Entity> FLAME_PREDICATE = entity -> entity instanceof EntityFlameCore;

	private static final IDispenseItemBehavior DAMPING_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){

		/**
		 * Dispense the specified stack, play the dispense sound and spawn particles.
		 */
		@Override
		public ItemStack dispenseStack(IBlockSource source, ItemStack stack){
			stack.shrink(1);
			List<Entity> ents = source.getWorld().getEntitiesInAABBexcluding(null, new AxisAlignedBB(source.getBlockPos()).grow(RANGE), FLAME_PREDICATE);
			for(Entity ent : ents){
				ent.remove();
			}
			if(!ents.isEmpty()){
				source.getWorld().playSound(null, source.getBlockPos(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.BLOCKS, 1, 0);
			}
			Vector3d partPos = Vector3d.copyCentered(source.getBlockPos());
			if(source.getBlockState().hasProperty(DispenserBlock.FACING)){
				partPos = partPos.add(Vector3d.copy(source.getBlockState().get(DispenserBlock.FACING).getDirectionVec()));
			}
			source.getWorld().addParticle(ParticleTypes.END_ROD, partPos.x, partPos.y, partPos.z, 0, 0, 0);
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
		super(new Properties().group(CRItems.TAB_CROSSROADS));
		String name = "damping_powder";
		setRegistryName(name);
		CRItems.toRegister.add(this);
		DispenserBlock.registerDispenseBehavior(this, DAMPING_DISPENSER_BEHAVIOR);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn){
		ItemStack held = playerIn.getHeldItem(handIn);
		held.shrink(1);
		List<Entity> ents = worldIn.getEntitiesInAABBexcluding(playerIn, new AxisAlignedBB(playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), playerIn.getPosX() + 1, playerIn.getPosY() + 1, playerIn.getPosZ() + 1).grow(RANGE), FLAME_PREDICATE);
		for(Entity ent : ents){
			ent.remove();
		}
		Vector3d partPos = playerIn.getEyePosition(1).add(playerIn.getLookVec().scale(0.5));
		worldIn.addParticle(ParticleTypes.END_ROD, partPos.x, partPos.y, partPos.z, 0, 0, 0);
		if(!ents.isEmpty()){
			worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY() + playerIn.getEyeHeight(), playerIn.getPosZ(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1, 0);
		}
		return new ActionResult<>(ActionResultType.SUCCESS, held);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.damp_powder.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.damp_powder.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
