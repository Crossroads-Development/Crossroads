package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HandCrank extends Item{

	public HandCrank(){
		String name = "hand_crank";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	protected double getEfficiency(){
		return 10;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side.getOpposite())){
			if(playerIn.isSneaking()){
				te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side.getOpposite()).addEnergy(-getEfficiency(), true, true);
			}else{
				te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side.getOpposite()).addEnergy(getEfficiency(), true, true);
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}
}
