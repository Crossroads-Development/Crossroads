package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DebugHeatWriter extends Item{

	public DebugHeatWriter(){
		setUnlocalizedName("debugHeatWriter");
		setRegistryName("debugHeatWriter");
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos).hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null)){
			IHeatHandler cable = worldIn.getTileEntity(pos).getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null);
			if(playerIn.isSneaking()){
				cable.addHeat(-100);
				return EnumActionResult.SUCCESS;
			}else{
				cable.addHeat(100);
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}
}
