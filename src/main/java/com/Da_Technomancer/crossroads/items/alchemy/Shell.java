package com.Da_Technomancer.crossroads.items.alchemy;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.entity.EntityShell;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class Shell extends AbstractGlassware{

	private static final ModelResourceLocation LOCAT_GLASS = new ModelResourceLocation(Main.MODID + ":shell_glass", "inventory");
	private static final ModelResourceLocation LOCAT_CRYSTAL = new ModelResourceLocation(Main.MODID + ":shell_crystal", "inventory");

	public Shell(){
		String name = "shell";
		maxStackSize = 1;
		hasSubtypes = true;
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.tabCrossroads);
		ModItems.toRegister.add(this);
		ModItems.toClientRegister.put(Pair.of((Item) this, 0), LOCAT_GLASS);
		ModItems.toClientRegister.put(Pair.of((Item) this, 1), LOCAT_CRYSTAL);
	}

	@Override
	public double getCapacity(){
		return 25D;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack){
		return stack.getMetadata() == 1 ? "item.shell_cryst" : "item.shell_glass";
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn){
		ItemStack held = playerIn.getHeldItem(handIn);
		Triple<ReagentStack[], Double, Double> contents = getReagants(held);
		if(contents.getRight() > 0){
			if(!playerIn.capabilities.isCreativeMode){
				held = ItemStack.EMPTY;
			}

			worldIn.playSound((EntityPlayer) null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if(!worldIn.isRemote){
				EntityShell entitysnowball = new EntityShell(worldIn, playerIn, contents.getLeft(), contents.getMiddle() / contents.getRight() - 273D);
				entitysnowball.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
				worldIn.spawnEntity(entitysnowball);
			}
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, held);
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, held);
	}
}
