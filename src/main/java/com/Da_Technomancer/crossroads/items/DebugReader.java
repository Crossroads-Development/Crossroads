package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.rotary.ITileMasterAxis;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatCableTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.SidedGearHolderTileEntity;

import net.minecraft.entity.player.EntityPlayer;
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

public class DebugReader extends Item{

	public DebugReader(){
		setUnlocalizedName("debugReader");
		setRegistryName("debugReader");
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(worldIn.isRemote && worldIn.getTileEntity(pos) instanceof SidedGearHolderTileEntity){
			for(int i = 0; i < 6; i++){
				SidedGearHolderTileEntity gear = (SidedGearHolderTileEntity) worldIn.getTileEntity(pos);
				playerIn.addChatComponentMessage(new TextComponentString("Angle=" + (gear.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.getFront(i)) ? gear.getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.getFront(i)).getAngle() : "NONE")));
			}
		}

		if(worldIn.isRemote){
			return EnumActionResult.PASS;
		}

		TileEntity te = worldIn.getTileEntity(pos);

		for(int i = 0; i < 6; i++){
			if(te != null && te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.getFront(i))){
				double[] gear = te.getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.getFront(i)).getMotionData();
				playerIn.addChatComponentMessage(new TextComponentString("w=" + gear[0] + ", E=" + gear[1] + ", P=" + gear[2] + ", lastE=" + gear[3] + " Type " + te.getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.getFront(i)).getMember().toString()));

			}
		}

		if(te instanceof ITileMasterAxis){
			playerIn.addChatComponentMessage(new TextComponentString(Boolean.toString(((ITileMasterAxis) te).isLocked())));
			playerIn.addChatComponentMessage(new TextComponentString(((ITileMasterAxis) te).getTotalEnergy() + " Energy Total"));
		}else{
			playerIn.addChatComponentMessage(new TextComponentString(Integer.toString(CommonProxy.masterKey)));
		}

		if(te != null && te.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null)){
			IHeatHandler cable = te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null);
			playerIn.addChatComponentMessage(new TextComponentString("Temp = " + cable.getTemp()));

			if(te instanceof HeatCableTileEntity){
				HeatCableTileEntity heatCable = (HeatCableTileEntity) te;
				playerIn.addChatComponentMessage(new TextComponentString("Insul = " + heatCable.getInsulator() + ", Cond = " + heatCable.getConductor()));
			}
		}

		if(te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)){
			IFluidHandler pipe = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

			for(IFluidTankProperties tank : pipe.getTankProperties()){
				playerIn.addChatComponentMessage(new TextComponentString("Amount = " + (tank.getContents() == null ? 0 : tank.getContents().amount) + " Type = " + (tank.getContents() == null ? "None" : tank.getContents().getFluid().getUnlocalizedName()) + " Capacity = " + tank.getCapacity()));
			}
		}
		
		if(te instanceof BeamRenderTE){
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
							}
							
							playerIn.addChatComponentMessage(new TextComponentString(check.toString()));
						}
					}
				}
			}
		}

		return EnumActionResult.PASS;
	}
}
