package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.tileentities.RatiatorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.CrystalMasterAxisTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
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
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te == null || worldIn.isRemote){
			return EnumActionResult.PASS;
		}
		boolean pass = true;

		if(te.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null)){
			pass = false;
			playerIn.sendMessage(new TextComponentString("Temp: " + worldIn.getTileEntity(pos).getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null).getTemp() + "*C"));
			playerIn.sendMessage(new TextComponentString("Biome Temp: " + EnergyConverters.BIOME_TEMP_MULT * worldIn.getBiomeForCoordsBody(pos).getFloatTemperature(pos) + "*C"));
		}

		if(te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)){
			pass = false;
			IFluidHandler pipe = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
			playerIn.sendMessage(new TextComponentString(pipe.getTankProperties().length + " internal tank" + (pipe.getTankProperties().length == 1 ? "." : "s.")));
			for(IFluidTankProperties tank : pipe.getTankProperties()){
				playerIn.sendMessage(new TextComponentString("Amount: " + (tank.getContents() == null ? 0 : tank.getContents().amount) + ", Type: " + (tank.getContents() == null ? "None" : tank.getContents().getLocalizedName()) + ", Capacity: " + tank.getCapacity()));
			}
		}

		if(te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite())){
			pass = false;
			IAxleHandler gear = te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite());
			playerIn.sendMessage(new TextComponentString("Speed: " + MiscOp.betterRound(gear.getMotionData()[0], 3) + ", Energy: " + MiscOp.betterRound(gear.getMotionData()[1], 3) + ", Power: " + MiscOp.betterRound(gear.getMotionData()[2], 3) + ", Mass: " + gear.getPhysData()[0] + ", I: " + gear.getPhysData()[1]));
		}else if(te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing)){
			pass = false;
			IAxleHandler gear = te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing);
			playerIn.sendMessage(new TextComponentString("Speed: " + MiscOp.betterRound(gear.getMotionData()[0], 3) + ", Energy: " + MiscOp.betterRound(gear.getMotionData()[1], 3) + ", Power: " + MiscOp.betterRound(gear.getMotionData()[2], 3) + ", Mass: " + gear.getPhysData()[0] + ", I: " + gear.getPhysData()[1]));
		}

		if(te instanceof BeamRenderTE){
			pass = false;
			MagicUnit[] mag = ((BeamRenderTE) te).getLastFullSent();
			if(mag != null){
				for(int i = 0; i < mag.length; i++){
					MagicUnit check = mag[i];
					if(check != null){
						NBTTagCompound nbt = MiscOp.getPlayerTag(playerIn);
						if(!nbt.hasKey("elements")){
							nbt.setTag("elements", new NBTTagCompound());
						}
						nbt = nbt.getCompoundTag("elements");

						if(!nbt.hasKey(MagicElements.getElement(check).name())){
							nbt.setBoolean(MagicElements.getElement(check).name(), true);
							playerIn.sendMessage(new TextComponentString(TextFormatting.BOLD.toString() + "New Element Discovered: " + MagicElements.getElement(check).toString()));
							StoreNBTToClient.syncNBTToClient((EntityPlayerMP) playerIn, false);
						}

						playerIn.sendMessage(new TextComponentString(EnumFacing.getFront(i).toString() + ": " + check.toString()));
					}
				}
			}
		}

		if(te instanceof CrystalMasterAxisTileEntity){
			pass = false;
			playerIn.sendMessage(new TextComponentString("Element: " + ((((CrystalMasterAxisTileEntity) te).getElement() == null) ? "NONE" : (((CrystalMasterAxisTileEntity) te).getElement().toString() + (((CrystalMasterAxisTileEntity) te).isVoid() ? " (VOID), " : ", ") + "Time: " + ((CrystalMasterAxisTileEntity) te).getTime()))));
		}

		if(te instanceof RatiatorTileEntity){
			pass = false;
			playerIn.sendMessage(new TextComponentString("Out: " + ((RatiatorTileEntity) te).getOutput()));
		}

		return pass ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
	}
}
