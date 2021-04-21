package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.entity.EntityShell;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class Shell extends AbstractGlassware{

	private static final IDispenseItemBehavior SHELL_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){

		/**
		 * Dispense the specified stack, play the dispense sound and spawn particles.
		 */
		@Override
		public ItemStack execute(IBlockSource source, ItemStack stack){
			ReagentMap contents = CRItems.shellGlass.getReagants(stack);
			if(contents.getTotalQty() != 0){
				Direction dir = source.getBlockState().getValue(DispenserBlock.FACING);
				World world = source.getLevel();
				EntityShell shellEnt = new EntityShell(world, contents, stack);
				shellEnt.setPos(source.x() + dir.getStepX() + 0.5D, source.y() + dir.getStepY() + 0.5D, source.z() + dir.getStepZ() + 0.5D);
				shellEnt.shoot(dir.getStepX(), dir.getStepY(), dir.getStepZ(), 1.5F, 1.0F);
				world.addFreshEntity(shellEnt);
				stack.shrink(1);
			}
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

	public Shell(boolean crystal){
		super(GlasswareTypes.SHELL, crystal);
		String name = "shell_" + (crystal ? "cryst" : "glass");
		setRegistryName(name);
		CRItems.toRegister.add(this);
		DispenserBlock.registerBehavior(this, SHELL_DISPENSER_BEHAVIOR);
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn){
		ItemStack held = playerIn.getItemInHand(handIn);
		ReagentMap contents = getReagants(held);
		if(contents.getTotalQty() != 0){
			worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

			if(!worldIn.isClientSide){
				EntityShell shellEnt = new EntityShell(worldIn, playerIn, contents, held);
				shellEnt.shootFromRotation(playerIn, playerIn.xRot, playerIn.yRot, 0, 1.5F, 1.0F);
				worldIn.addFreshEntity(shellEnt);
			}

			if(!playerIn.isCreative()){
				held = ItemStack.EMPTY;
			}

			return new ActionResult<>(ActionResultType.SUCCESS, held);
		}
		return new ActionResult<>(ActionResultType.PASS, held);
	}
}
