package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendElementNBTToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.tileentities.RatiatorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatCableTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.RedstoneHeatCableTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.CrystalMasterAxisTileEntity;

import amerifrance.guideapi.api.util.TextHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
				if(te instanceof RedstoneHeatCableTileEntity){
					RedstoneHeatCableTileEntity heatCable = (RedstoneHeatCableTileEntity) te;
					playerIn.addChatComponentMessage(new TextComponentString("Insul: " + heatCable.getInsulator() + ", Cond: " + heatCable.getConductor()));
				}
				playerIn.addChatComponentMessage(new TextComponentString("Biome Temp: " + EnergyConverters.BIOME_TEMP_MULT * worldIn.getBiomeForCoordsBody(pos).getFloatTemperature(pos) + "*C"));
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

		if(te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite())){
			pass = false;
			if(!worldIn.isRemote){
				IAxleHandler gear = te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite());

				playerIn.addChatComponentMessage(new TextComponentString("Speed: " + MiscOp.betterRound(gear.getMotionData()[0], 3) + ", Energy: " + MiscOp.betterRound(gear.getMotionData()[1], 3) + ", Power: " + MiscOp.betterRound(gear.getMotionData()[2], 3) + ", Mass: " + gear.getPhysData()[0] + ", I: " + gear.getPhysData()[1]));
			}
		}
		
		if(te instanceof BeamRenderTE){
			pass = false;
			if(!worldIn.isRemote){
				MagicUnit[] mag = ((BeamRenderTE) te).getLastFullSent();
				if(mag != null){
					for(MagicUnit check : mag){
						if(check != null){
							NBTTagCompound nbt = MiscOp.getPlayerTag(playerIn);
							if(!nbt.hasKey("elements")){
								nbt.setTag("elements", new NBTTagCompound());
							}
							nbt = nbt.getCompoundTag("elements");
							
							if(!nbt.hasKey(MagicElements.getElement(check).name())){
								nbt.setBoolean(MagicElements.getElement(check).name(), true);
								playerIn.addChatComponentMessage(new TextComponentString(TextFormatting.BOLD.toString() + "New Element Discovered: " + MagicElements.getElement(check).toString()));
								ModPackets.network.sendTo(new SendElementNBTToClient(nbt), (EntityPlayerMP) playerIn);
							}
							
							playerIn.addChatComponentMessage(new TextComponentString(check.toString()));
						}
					}
				}
			}
		}

		if(te instanceof CrystalMasterAxisTileEntity){
			pass = false;
			if(!worldIn.isRemote){
				playerIn.addChatComponentMessage(new TextComponentString("Element: " + ((CrystalMasterAxisTileEntity) te).getElement() == null ? "NONE" : (((CrystalMasterAxisTileEntity) te).getElement().toString() + (((CrystalMasterAxisTileEntity) te).isVoid() ? " (VOID), " : ", ") + "Time: " + ((CrystalMasterAxisTileEntity) te).getTime())));
			}
		}
		
		if(te instanceof RatiatorTileEntity && !worldIn.isRemote){
			playerIn.addChatComponentMessage(new TextComponentString("Out: " + ((RatiatorTileEntity) te).getOutput()));
		}

		return pass ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
	}

}
