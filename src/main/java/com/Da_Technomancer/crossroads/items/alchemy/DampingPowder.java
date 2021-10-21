package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.entity.EntityFlameCore;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item.Properties;

public class DampingPowder extends Item{

	private static final int RANGE = 32;
	private static final Predicate<Entity> FLAME_PREDICATE = entity -> entity instanceof EntityFlameCore;

	private static final DispenseItemBehavior DAMPING_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){

		/**
		 * Dispense the specified stack, play the dispense sound and spawn particles.
		 */
		@Override
		public ItemStack execute(BlockSource source, ItemStack stack){
			stack.shrink(1);
			List<Entity> ents = source.getLevel().getEntities((Entity) null, new AABB(source.getPos()).inflate(RANGE), FLAME_PREDICATE);
			for(Entity ent : ents){
				ent.remove(Entity.RemovalReason.KILLED);
			}
			if(!ents.isEmpty()){
				source.getLevel().playSound(null, source.getPos(), SoundEvents.TOTEM_USE, SoundSource.BLOCKS, 1, 0);
			}
			Vec3 partPos = Vec3.atCenterOf(source.getPos());
			if(source.getBlockState().hasProperty(DispenserBlock.FACING)){
				partPos = partPos.add(Vec3.atLowerCornerOf(source.getBlockState().getValue(DispenserBlock.FACING).getNormal()));
			}
			source.getLevel().addParticle(ParticleTypes.END_ROD, partPos.x, partPos.y, partPos.z, 0, 0, 0);
			return stack;
		}

		/**
		 * Play the dispense sound from the specified block.
		 */
		@Override
		protected void playSound(BlockSource source){
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
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn){
		ItemStack held = playerIn.getItemInHand(handIn);
		held.shrink(1);
		List<Entity> ents = worldIn.getEntities(playerIn, new AABB(playerIn.getX(), playerIn.getY(), playerIn.getZ(), playerIn.getX() + 1, playerIn.getY() + 1, playerIn.getZ() + 1).inflate(RANGE), FLAME_PREDICATE);
		for(Entity ent : ents){
			ent.remove(Entity.RemovalReason.KILLED);
		}
		Vec3 partPos = playerIn.getEyePosition(1).add(playerIn.getLookAngle().scale(0.5));
		worldIn.addParticle(ParticleTypes.END_ROD, partPos.x, partPos.y, partPos.z, 0, 0, 0);
		if(!ents.isEmpty()){
			worldIn.playSound(null, playerIn.getX(), playerIn.getY() + playerIn.getEyeHeight(), playerIn.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1, 0);
		}
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, held);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.damp_powder.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.damp_powder.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
