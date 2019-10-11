package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
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

public class CheatWandHeat extends Item{

	public CheatWandHeat(){
		String name = "cheat_wand_heat";
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
		if(te != null && te.hasCapability(Capabilities.HEAT_CAPABILITY, null)){
			IHeatHandler cable = te.getCapability(Capabilities.HEAT_CAPABILITY, null);
			if(playerIn.isSneaking()){
				cable.setTemp(Math.max(HeatUtil.ABSOLUTE_ZERO, cable.getTemp() - 100));
			}else{
				cable.addHeat(100);
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Creative Mode Only");
		tooltip.add("Adds 100°C when used on a cable");
		tooltip.add("Shift clicking removes 100°C");
	}
}
