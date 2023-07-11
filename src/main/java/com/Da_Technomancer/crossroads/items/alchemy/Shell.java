package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.entity.EntityShell;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class Shell extends AbstractGlassware{

	private static final DispenseItemBehavior SHELL_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){

		/**
		 * Dispense the specified stack, play the dispense sound and spawn particles.
		 */
		@Override
		public ItemStack execute(BlockSource source, ItemStack stack){
			ReagentMap contents = CRItems.shellGlass.getReagants(stack);
			if(contents.getTotalQty() != 0){
				Direction dir = source.getBlockState().getValue(DispenserBlock.FACING);
				Level world = source.getLevel();
				EntityShell shellEnt = new EntityShell(world, contents, stack);
				shellEnt.setPos(source.x() + dir.getStepX() + 0.5D, source.y() + dir.getStepY() + 0.5D, source.z() + dir.getStepZ() + 0.5D);
				shellEnt.shoot(dir.getStepX(), dir.getStepY(), dir.getStepZ(), 0.8F, 1.0F);
				world.addFreshEntity(shellEnt);
				stack.shrink(1);
			}
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

	public Shell(boolean crystal){
		super(GlasswareTypes.SHELL, crystal);
		String name = "shell_" + (crystal ? "cryst" : "glass");
		CRItems.queueForRegister(name, this);
		DispenserBlock.registerBehavior(this, SHELL_DISPENSER_BEHAVIOR);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn){
		ItemStack held = playerIn.getItemInHand(handIn);
		ReagentMap contents = getReagants(held);
		if(contents.getTotalQty() != 0){
			worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (worldIn.random.nextFloat() * 0.4F + 0.8F));

			if(!worldIn.isClientSide){
				EntityShell shellEnt = new EntityShell(worldIn, playerIn, contents, held);
				shellEnt.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0, 0.8F, 1.0F);
				worldIn.addFreshEntity(shellEnt);
			}

			if(!playerIn.isCreative()){
				held = ItemStack.EMPTY;
			}

			return new InteractionResultHolder<>(InteractionResult.SUCCESS, held);
		}
		return new InteractionResultHolder<>(InteractionResult.PASS, held);
	}
}
