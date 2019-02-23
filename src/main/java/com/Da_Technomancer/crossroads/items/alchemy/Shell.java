package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.entity.EntityShell;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class Shell extends AbstractGlassware{

	private final boolean crystal;

	private static final IBehaviorDispenseItem SHELL_DISPENSER_BEHAVIOR = new BehaviorDefaultDispenseItem(){

		/**
		 * Dispense the specified stack, play the dispense sound and spawn particles.
		 */
		@Override
		public ItemStack dispenseStack(IBlockSource source, ItemStack stack){
			ReagentMap contents = ModItems.shellGlass.getReagants(stack);
			if(contents.getTotalQty() != 0){
				EnumFacing dir = (EnumFacing) source.getBlockState().getValue(BlockDispenser.FACING);
				World world = source.getWorld();
				EntityShell entitysnowball = new EntityShell(world, contents, contents.getTempC());
				entitysnowball.setPosition(source.getX() + dir.getXOffset() + 0.5D, source.getY() + dir.getYOffset() + 0.5D, source.getZ() + dir.getZOffset() + 0.5D);
				entitysnowball.shoot(dir.getXOffset(), dir.getYOffset(), dir.getZOffset(), 1.5F, 1.0F);
				world.spawnEntity(entitysnowball);
				stack.shrink(1);
				return stack;
			}else{
				return stack;
			}
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
		this.crystal = crystal;
		String name = "shell_" + (crystal ? "cryst" : "glass");
		maxStackSize = 1;
		hasSubtypes = true;
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, SHELL_DISPENSER_BEHAVIOR);
	}

	@Override
	public int getCapacity(){
		return 25;
	}

	@Override
	public boolean isCrystal(){
		return crystal;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn){
		ItemStack held = playerIn.getHeldItem(handIn);
		ReagentMap contents = getReagants(held);
		if(contents.getTotalQty() != 0){
			if(!playerIn.capabilities.isCreativeMode){
				held = ItemStack.EMPTY;
			}

			worldIn.playSound((EntityPlayer) null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if(!worldIn.isRemote){
				EntityShell entitysnowball = new EntityShell(worldIn, playerIn, contents, contents.getTempC());
				entitysnowball.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
				worldIn.spawnEntity(entitysnowball);
			}
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, held);
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, held);
	}
}
