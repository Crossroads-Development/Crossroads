package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Thermometer extends Item{

	public Thermometer(){
		String name = "thermometer";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos).hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null)){
			if(!worldIn.isRemote){
				playerIn.sendMessage(new TextComponentString("Temp: " + worldIn.getTileEntity(pos).getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null).getTemp() + "*C"));
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

}
