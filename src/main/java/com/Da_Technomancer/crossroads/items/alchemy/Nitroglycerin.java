package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.entity.EntityNitro;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
			EntityNitro nitro = EntityNitro.type.create(world);
			nitro.setPosition(source.getX() + dir.getXOffset() + 0.5D, source.getY() + dir.getYOffset() + 0.5D, source.getZ() + dir.getZOffset() + 0.5D);
			nitro.shoot(dir.getXOffset(), dir.getYOffset(), dir.getZOffset(), 1.5F, 1.0F);
			world.addEntity(nitro);
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
		super(CRItems.itemProp);
		String name = "nitroglycerin";
		setRegistryName(name);
		CRItems.toRegister.add(this);
		DispenserBlock.registerDispenseBehavior(this, NITRO_DISPENSER_BEHAVIOR);
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn){
		ItemStack held = playerIn.getHeldItem(handIn);
		if(!playerIn.isCreative()){
			held.shrink(1);
		}

		worldIn.playSound((PlayerEntity) null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

		if(!worldIn.isRemote){
			EntityNitro entitysnowball = new EntityNitro(worldIn, playerIn);
			entitysnowball.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
			worldIn.addEntity(entitysnowball);
		}
		return new ActionResult<>(ActionResultType.SUCCESS, held);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.nitroglycerin.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
