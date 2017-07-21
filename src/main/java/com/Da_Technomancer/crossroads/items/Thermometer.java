package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class Thermometer extends Item{

	public Thermometer(){
		String name = "thermometer";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.tabCrossroads);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		TileEntity te = worldIn.getTileEntity(pos);
		if(!worldIn.isRemote && te != null && te.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null)){
			playerIn.sendMessage(new TextComponentString("Temp: " + te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null).getTemp() + "°C"));
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}
}
