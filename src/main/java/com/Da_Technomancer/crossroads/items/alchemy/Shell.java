package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.entity.EntityShell;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class Shell extends AbstractGlassware{

	private static final IDispenseItemBehavior SHELL_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){

		/**
		 * Dispense the specified stack, play the dispense sound and spawn particles.
		 */
		@Override
		public ItemStack dispenseStack(IBlockSource source, ItemStack stack){
			ReagentMap contents = CRItems.shellGlass.getReagants(stack);
			if(contents.getTotalQty() != 0){
				Direction dir = source.getBlockState().get(DispenserBlock.FACING);
				World world = source.getWorld();
				EntityShell shellEnt = new EntityShell(world, contents, stack);
				shellEnt.setPosition(source.getX() + dir.getXOffset() + 0.5D, source.getY() + dir.getYOffset() + 0.5D, source.getZ() + dir.getZOffset() + 0.5D);
				shellEnt.shoot(dir.getXOffset(), dir.getYOffset(), dir.getZOffset(), 1.5F, 1.0F);
				world.addEntity(shellEnt);
				stack.shrink(1);
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

	public Shell(boolean crystal){
		super(GlasswareTypes.SHELL, crystal);
		String name = "shell_" + (crystal ? "cryst" : "glass");
		setRegistryName(name);
		CRItems.toRegister.add(this);
		DispenserBlock.registerDispenseBehavior(this, SHELL_DISPENSER_BEHAVIOR);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn){
		ItemStack held = playerIn.getHeldItem(handIn);
		ReagentMap contents = getReagants(held);
		if(contents.getTotalQty() != 0){
			worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

			if(!worldIn.isRemote){
				EntityShell shellEnt = new EntityShell(worldIn, playerIn, contents, held);
				shellEnt.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0, 1.5F, 1.0F);
				worldIn.addEntity(shellEnt);
			}

			if(!playerIn.isCreative()){
				held = ItemStack.EMPTY;
			}

			return new ActionResult<>(ActionResultType.SUCCESS, held);
		}
		return new ActionResult<>(ActionResultType.PASS, held);
	}
}
