package com.Da_Technomancer.crossroads.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class FluidGauge extends Item{

	public FluidGauge(){
		String name = "fluid_gauge";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		TileEntity te = worldIn.getTileEntity(pos);

		if(te != null && !worldIn.isRemote && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)){
			IFluidHandler pipe = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

			playerIn.sendMessage(new TextComponentString(pipe.getTankProperties().length + " internal tank" + (pipe.getTankProperties().length == 1 ? "." : "s.")));
			for(IFluidTankProperties tank : pipe.getTankProperties()){
				playerIn.sendMessage(new TextComponentString("% full: " + (tank.getContents() == null ? 0 : tank.getContents().amount) * 100 / tank.getCapacity()));
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}
}
