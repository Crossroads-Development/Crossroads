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
		public ItemStack execute(IBlockSource source, ItemStack stack){
			stack.shrink(1);
			List<Entity> ents = source.getLevel().getEntities((Entity) null, new AxisAlignedBB(source.getPos()).inflate(RANGE), FLAME_PREDICATE);
			for(Entity ent : ents){
				ent.remove();
			}
			if(!ents.isEmpty()){
				source.getLevel().playSound(null, source.getPos(), SoundEvents.TOTEM_USE, SoundCategory.BLOCKS, 1, 0);
			}
			Vector3d partPos = Vector3d.atCenterOf(source.getPos());
			if(source.getBlockState().hasProperty(DispenserBlock.FACING)){
				partPos = partPos.add(Vector3d.atLowerCornerOf(source.getBlockState().getValue(DispenserBlock.FACING).getNormal()));
			}
			source.getLevel().addParticle(ParticleTypes.END_ROD, partPos.x, partPos.y, partPos.z, 0, 0, 0);
			return stack;
		}

		/**
		 * Play the dispense sound from the specified block.
		 */
		@Override
		protected void playSound(IBlockSource source){
			source.getLevel().levelEvent(1000, source.getPos(), 0);
		}
	};

	public DampingPowder(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS));
		String name = "damping_powder";
		setRegistryName(name);
		CRItems.toRegister.add(this);
		DispenserBlock.registerBehavior(this, DAMPING_DISPENSER_BEHAVIOR);
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn){
		ItemStack held = playerIn.getItemInHand(handIn);
		held.shrink(1);
		List<Entity> ents = worldIn.getEntities(playerIn, new AxisAlignedBB(playerIn.getX(), playerIn.getY(), playerIn.getZ(), playerIn.getX() + 1, playerIn.getY() + 1, playerIn.getZ() + 1).inflate(RANGE), FLAME_PREDICATE);
		for(Entity ent : ents){
			ent.remove();
		}
		Vector3d partPos = playerIn.getEyePosition(1).add(playerIn.getLookAngle().scale(0.5));
		worldIn.addParticle(ParticleTypes.END_ROD, partPos.x, partPos.y, partPos.z, 0, 0, 0);
		if(!ents.isEmpty()){
			worldIn.playSound(null, playerIn.getX(), playerIn.getY() + playerIn.getEyeHeight(), playerIn.getZ(), SoundEvents.TOTEM_USE, SoundCategory.PLAYERS, 1, 0);
		}
		return new ActionResult<>(ActionResultType.SUCCESS, held);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.damp_powder.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.damp_powder.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
