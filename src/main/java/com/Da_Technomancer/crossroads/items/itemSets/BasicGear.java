package com.Da_Technomancer.crossroads.items.itemSets;

import java.util.List;

import com.Da_Technomancer.crossroads.ServerProxy;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.API.rotary.IRotaryHandler;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.SidedGearHolderTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BasicGear extends Item{

	private GearTypes type;

	public BasicGear(GearTypes typeIn){
		setUnlocalizedName("gear" + typeIn.toString());
		setRegistryName("gear" + typeIn.toString());
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
		OreDictionary.registerOre("gear" + typeIn.toString(), this);
		type = typeIn;
		ModItems.itemAddQue(this);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(this, 8), " ? ", "?#?", " ? ", '#', "block" + typeIn.toString(), '?', "ingot" + typeIn.toString()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
		tooltip.add("Mass: " + MiscOp.betterRound(type.getDensity() / 8, 2));
		tooltip.add("I: " + MiscOp.betterRound(type.getDensity() / 8, 2) * .125);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(worldIn.isRemote){
			return EnumActionResult.PASS;
		}

		if(worldIn.getTileEntity(pos.offset(side)) instanceof SidedGearHolderTileEntity && !worldIn.getTileEntity(pos.offset(side)).hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, side.getOpposite()) && worldIn.isSideSolid(pos, side)){
			if(!playerIn.capabilities.isCreativeMode && --playerIn.getHeldItem(hand).stackSize <= 0){
				playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, (ItemStack) null);
			}

			IRotaryHandler handler = worldIn.getTileEntity(pos.offset(side)).getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, side.getOpposite());
			handler.setMember(type);
			handler.updateStates();
			ServerProxy.masterKey++;
		}else if(worldIn.getBlockState(pos.offset(side)).getBlock().isReplaceable(worldIn, pos.offset(side)) && worldIn.isSideSolid(pos, side)){
			if(!playerIn.capabilities.isCreativeMode && --playerIn.getHeldItem(hand).stackSize <= 0){
				playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, (ItemStack) null);
			}

			worldIn.setBlockState(pos.offset(side), ModBlocks.sidedGearHolder.getDefaultState(), 3);
			IRotaryHandler te = worldIn.getTileEntity(pos.offset(side)).getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, side.getOpposite());
			te.setMember(type);
			ServerProxy.masterKey++;
		}

		return EnumActionResult.PASS;
	}
}
