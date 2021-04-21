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
import net.minecraft.item.Rarity;
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
		public ItemStack execute(IBlockSource source, ItemStack stack){
			Direction dir = source.getBlockState().getValue(DispenserBlock.FACING);
			World world = source.getLevel();
			EntityNitro nitro = EntityNitro.type.create(world);
			nitro.setPos(source.x() + dir.getStepX() + 0.5D, source.y() + dir.getStepY() + 0.5D, source.z() + dir.getStepZ() + 0.5D);
			nitro.shoot(dir.getStepX(), dir.getStepY() + 0.1F, dir.getStepZ(), 1.5F, 1.0F);
			world.addFreshEntity(nitro);
			stack.shrink(1);
			return stack;
		}
	};

	public Nitroglycerin(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS));
		String name = "nitroglycerin";
		setRegistryName(name);
		CRItems.toRegister.add(this);
		DispenserBlock.registerBehavior(this, NITRO_DISPENSER_BEHAVIOR);
	}

	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn){
		ItemStack held = playerIn.getItemInHand(handIn);
		if(!playerIn.isCreative()){
			held.shrink(1);
		}

		worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

		if(!worldIn.isClientSide){
			EntityNitro nitroEntity = new EntityNitro(worldIn, playerIn);
			//MCP note: Use the method in SnowballItem::onItemRightClick; it is NOT ProjectileEntity::shoot (currently)
			nitroEntity.shootFromRotation(playerIn, playerIn.xRot, playerIn.yRot, 0F, 1.5F, 1.0F);
			worldIn.addFreshEntity(nitroEntity);
		}
		return new ActionResult<>(ActionResultType.SUCCESS, held);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.nitroglycerin.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}
}
