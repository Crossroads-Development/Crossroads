package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.entity.EntityNitro;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class Nitroglycerin extends Item{

	private static final IDispenseItemBehavior NITRO_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){

		/**
		 * Dispense the specified stack, play the dispense sound and spawn particles.
		 */
		@Override
		public ItemStack dispenseStack(IBlockSource source, ItemStack stack){
			Direction dir = (Direction) source.getBlockState().get(DispenserBlock.FACING);
			World world = source.getWorld();
			EntityNitro entitysnowball = new EntityNitro(world);
			entitysnowball.setPosition(source.getX() + dir.getXOffset() + 0.5D, source.getY() + dir.getYOffset() + 0.5D, source.getZ() + dir.getZOffset() + 0.5D);
			entitysnowball.shoot(dir.getXOffset(), dir.getYOffset(), dir.getZOffset(), 1.5F, 1.0F);
			world.addEntity(entitysnowball);
			stack.shrink(1);
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

	public Nitroglycerin(){
		String name = "nitroglycerin";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		CRItems.toRegister.add(this);
		DispenserBlock.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, NITRO_DISPENSER_BEHAVIOR);
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn){
		ItemStack held = playerIn.getHeldItem(handIn);
		if(!playerIn.capabilities.isCreativeMode){
			held.shrink(1);
		}

		worldIn.playSound((PlayerEntity) null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if(!worldIn.isRemote){
			EntityNitro entitysnowball = new EntityNitro(worldIn, playerIn);
			entitysnowball.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
			worldIn.addEntity(entitysnowball);
		}
		return new ActionResult<ItemStack>(ActionResultType.SUCCESS, held);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Handle with care!");
	}
}
