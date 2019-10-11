package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class HandCrank extends Item{

	public HandCrank(){
		String name = "hand_crank";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setMaxStackSize(1);
		CRItems.toRegister.add(this);
		CRItems.itemAddQue(this);
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity playerIn, World worldIn, BlockPos pos, Hand hand, Direction side, BlockRayTraceResult hit){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te != null && te.hasCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite())){
			if(playerIn.isSneaking()){
				te.getCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite()).addEnergy(-50, true, true);
			}else{
				te.getCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite()).addEnergy(50, true, true);
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}
}
