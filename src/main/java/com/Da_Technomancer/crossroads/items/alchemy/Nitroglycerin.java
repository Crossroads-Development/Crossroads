package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.entity.EntityNitro;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

import javax.annotation.Nullable;
import java.util.List;

public class Nitroglycerin extends Item{

	private static final DispenseItemBehavior NITRO_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){

		/**
		 * Dispense the specified stack, play the dispense sound and spawn particles.
		 */
		@Override
		public ItemStack execute(BlockSource source, ItemStack stack){
			Direction dir = source.getBlockState().getValue(DispenserBlock.FACING);
			Level world = source.getLevel();
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

	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn){
		ItemStack held = playerIn.getItemInHand(handIn);
		if(!playerIn.isCreative()){
			held.shrink(1);
		}

		worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (worldIn.random.nextFloat() * 0.4F + 0.8F));

		if(!worldIn.isClientSide){
			EntityNitro nitroEntity = new EntityNitro(worldIn, playerIn);
			//MCP note: Use the method in SnowballItem::onItemRightClick; it is NOT ProjectileEntity::shoot (currently)
			nitroEntity.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0F, 1.5F, 1.0F);
			worldIn.addFreshEntity(nitroEntity);
		}
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, held);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.nitroglycerin.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}
}
