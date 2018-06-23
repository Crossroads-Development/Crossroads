package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.entity.EntityNitro;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class Nitroglycerin extends Item{

	private static final IBehaviorDispenseItem NITRO_DISPENSER_BEHAVIOR = new BehaviorDefaultDispenseItem(){

		/**
		 * Dispense the specified stack, play the dispense sound and spawn particles.
		 */
		@Override
		public ItemStack dispenseStack(IBlockSource source, ItemStack stack){
			EnumFacing dir = (EnumFacing) source.getBlockState().getValue(BlockDispenser.FACING);
			World world = source.getWorld();
			EntityNitro entitysnowball = new EntityNitro(world);
			entitysnowball.setPosition(source.getX() + dir.getFrontOffsetX() + 0.5D, source.getY() + dir.getFrontOffsetY() + 0.5D, source.getZ() + dir.getFrontOffsetZ() + 0.5D);
			entitysnowball.shoot(dir.getFrontOffsetX(), dir.getFrontOffsetY(), dir.getFrontOffsetZ(), 1.5F, 1.0F);
			world.spawnEntity(entitysnowball);
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
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, NITRO_DISPENSER_BEHAVIOR);
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn){
		ItemStack held = playerIn.getHeldItem(handIn);
		if(!playerIn.capabilities.isCreativeMode){
			held.shrink(1);
		}

		worldIn.playSound((EntityPlayer) null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if(!worldIn.isRemote){
			EntityNitro entitysnowball = new EntityNitro(worldIn, playerIn);
			entitysnowball.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
			worldIn.spawnEntity(entitysnowball);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, held);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Handle with care!");
	}
}
