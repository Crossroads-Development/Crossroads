package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class CheatWandRotary extends Item{

	public CheatWandRotary(){
		String name = "cheat_wand_rotary";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setMaxStackSize(1);
		CRItems.toRegister.add(this);
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity playerIn, World worldIn, BlockPos pos, Hand hand, Direction side, BlockRayTraceResult hit){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te != null && te.hasCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite())){
			if(playerIn.isSneaking()){
				te.getCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite()).addEnergy(-10000, true, true);
			}else{
				te.getCapability(Capabilities.AXLE_CAPABILITY, side.getOpposite()).addEnergy(10000, true, true);
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Creative Mode Only");
		tooltip.add("Adds 10,000J when used on a gear");
		tooltip.add("Shift clicking removes 10,000J");
	}
}
