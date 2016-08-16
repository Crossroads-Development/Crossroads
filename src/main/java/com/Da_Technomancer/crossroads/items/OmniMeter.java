package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.MiscOperators;
import com.Da_Technomancer.crossroads.API.rotary.IRotaryHandler;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatCableTileEntity;

import amerifrance.guideapi.api.util.TextHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

public class OmniMeter extends Item{

	public OmniMeter(){
		String name = "omnimeter";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te == null){
			return EnumActionResult.PASS;
		}
		boolean pass = true;

		if(te.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null)){
			pass = false;
			if(!worldIn.isRemote){
				playerIn.addChatComponentMessage(new TextComponentString("Temp: " + worldIn.getTileEntity(pos).getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null).getTemp() + "*C"));

				if(te instanceof HeatCableTileEntity){
					HeatCableTileEntity heatCable = (HeatCableTileEntity) te;
					playerIn.addChatComponentMessage(new TextComponentString("Insul: " + heatCable.getInsulator() + ", Cond: " + heatCable.getConductor()));
				}
				playerIn.addChatComponentMessage(new TextComponentString("Biome Temp: " + EnergyConverters.BIOME_TEMP_MULT * worldIn.getBiomeGenForCoords(pos).getFloatTemperature(pos) + "*C"));
			}
		}

		if(te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)){
			pass = false;
			if(!worldIn.isRemote){
				IFluidHandler pipe = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

				playerIn.addChatComponentMessage(new TextComponentString(pipe.getTankProperties().length + " internal tank" + (pipe.getTankProperties().length == 1 ? "." : "s.")));
				for(IFluidTankProperties tank : pipe.getTankProperties()){
					playerIn.addChatComponentMessage(new TextComponentString("Amount: " + (tank.getContents() == null ? 0 : tank.getContents().amount) + ", Type: " + (tank.getContents() == null ? "None" : TextHelper.localize(tank.getContents().getFluid().getUnlocalizedName())) + ", Capacity: " + tank.getCapacity()));
				}
			}
		}

		if(te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, facing.getOpposite())){
			pass = false;
			if(!worldIn.isRemote){
				IRotaryHandler gear = te.getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, facing.getOpposite());

				playerIn.addChatComponentMessage(new TextComponentString("Speed: " + MiscOperators.betterRound(gear.getMotionData()[0], 3) + ", Energy: " + MiscOperators.betterRound(gear.getMotionData()[1], 3) + ", Power: " + MiscOperators.betterRound(gear.getMotionData()[2], 3) + ", Mass: " + gear.getPhysData()[1] + ", I: " + gear.getPhysData()[2] + ", Radius: " + gear.getPhysData()[0]));
			}
		}

		return pass ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
	}

}
