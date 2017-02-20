package com.Da_Technomancer.crossroads.items;

import java.util.List;

import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendElementNBTToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.tileentities.RatiatorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatCableTileEntity;
import com.Da_Technomancer.crossroads.tileentities.magic.CrystalMasterAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.SidedGearHolderTileEntity;

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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DebugReader extends Item{

	public DebugReader(){
		setUnlocalizedName("debugReader");
		setRegistryName("debugReader");
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(worldIn.isRemote && worldIn.getTileEntity(pos) instanceof SidedGearHolderTileEntity){
			for(int i = 0; i < 6; i++){
				SidedGearHolderTileEntity gear = (SidedGearHolderTileEntity) worldIn.getTileEntity(pos);
				playerIn.sendMessage(new TextComponentString("Angle=" + (gear.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFront(i)) ? gear.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFront(i)).getAngle() : "NONE")));
				playerIn.sendMessage(new TextComponentString("Member=" + (gear.getMembers()[i] == null ? "NONE" : gear.getMembers()[i].toString())));
			}
		}

		if(worldIn.isRemote){
			return EnumActionResult.PASS;
		}

		TileEntity te = worldIn.getTileEntity(pos);

		for(int i = 0; i < 6; i++){
			if(te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFront(i))){
				double[] gear = te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFront(i)).getMotionData();
				playerIn.sendMessage(new TextComponentString("w=" + gear[0] + ", E=" + gear[1] + ", P=" + gear[2] + ", lastE=" + gear[3]));

			}
		}

		if(te instanceof IAxisHandler){
			playerIn.sendMessage(new TextComponentString(Boolean.toString(((IAxisHandler) te).isLocked())));
			playerIn.sendMessage(new TextComponentString(((IAxisHandler) te).getTotalEnergy() + " Energy Total"));
		}else{
			playerIn.sendMessage(new TextComponentString(Integer.toString(CommonProxy.masterKey)));
		}

		if(te != null && te.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null)){
			IHeatHandler cable = te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null);
			playerIn.sendMessage(new TextComponentString("Temp = " + cable.getTemp()));

			if(te instanceof HeatCableTileEntity){
				HeatCableTileEntity heatCable = (HeatCableTileEntity) te;
				playerIn.sendMessage(new TextComponentString("Insul = " + heatCable.getInsulator() + ", Cond = " + heatCable.getConductor()));
			}
		}

		if(te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)){
			IFluidHandler pipe = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

			for(IFluidTankProperties tank : pipe.getTankProperties()){
				playerIn.sendMessage(new TextComponentString("Amount = " + (tank.getContents() == null ? 0 : tank.getContents().amount) + " Type = " + (tank.getContents() == null ? "None" : tank.getContents().getFluid().getUnlocalizedName()) + " Capacity = " + tank.getCapacity()));
			}
		}
		
		if(te instanceof BeamRenderTE){
			if(!worldIn.isRemote){
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
								ModPackets.network.sendTo(new SendElementNBTToClient(nbt), (EntityPlayerMP) playerIn);
							}
							
							playerIn.sendMessage(new TextComponentString(EnumFacing.getFront(i).toString() + ": " + check.toString()));
						}
					}
				}
			}
		}
		
		if(te instanceof CrystalMasterAxisTileEntity){
			playerIn.sendMessage(new TextComponentString("Element: " + ((((CrystalMasterAxisTileEntity) te).getElement() == null) ? "NONE" : ((CrystalMasterAxisTileEntity) te).getElement().toString() + (((CrystalMasterAxisTileEntity) te).isVoid() ? " (VOID), " : ", ") + "Time: " + ((CrystalMasterAxisTileEntity) te).getTime())));
		}
		
		if(te instanceof RatiatorTileEntity){
			playerIn.sendMessage(new TextComponentString("Out: " + ((RatiatorTileEntity) te).getOutput()));
		}

		return EnumActionResult.PASS;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
		tooltip.add("THIS ITEM IS BEING REMOVED!");
		tooltip.add("Destroy any that you have");
	}
}
