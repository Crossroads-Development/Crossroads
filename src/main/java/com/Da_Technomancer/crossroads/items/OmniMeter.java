package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTEBase;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;
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
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import java.util.ArrayList;

public class OmniMeter extends Item{

	public OmniMeter(){
		String name = "omnimeter";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setMaxStackSize(1);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	private static final int CHAT_ID = 279478;//Value chosen at random

	/**
	 * For calling on the server side only
	 * @param chat The list to be populated with information
	 * @param player The player
	 * @param world The world
	 * @param pos The position aimed at
	 * @param facing The side clicked on
	 * @param hitX The hitX
	 * @param hitY The hitY
	 * @param hitZ The hitZ
	 */
	public static void measure(ArrayList<String> chat, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ){
		TileEntity te = world.getTileEntity(pos);
		if(te != null){
			if(te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)){
				IFluidHandler pipe = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
				chat.add(pipe.getTankProperties().length + " internal tank" + (pipe.getTankProperties().length == 1 ? "." : "s."));
				for(IFluidTankProperties tank : pipe.getTankProperties()){
					chat.add("Amount: " + (tank.getContents() == null ? 0 : tank.getContents().amount) + ", Type: " + (tank.getContents() == null ? "None" : tank.getContents().getLocalizedName()) + ", Capacity: " + tank.getCapacity());
				}
			}

			if(te.hasCapability(Capabilities.AXIS_CAPABILITY, null)){
				chat.add("Total Energy: " + MiscUtil.betterRound(te.getCapability(Capabilities.AXIS_CAPABILITY, null).getTotalEnergy(), 3));
			}

			if(te.hasCapability(CapabilityEnergy.ENERGY, null)){
				IEnergyStorage batt = te.getCapability(CapabilityEnergy.ENERGY, null);
				chat.add("Charge: " + batt.getEnergyStored() + "/" + batt.getMaxEnergyStored() + "FE");
			}
		}

		if(te instanceof BeamRenderTEBase){
			BeamUnit[] mag = ((BeamRenderTEBase) te).getLastSent();
			if(mag != null){
				NBTTagCompound nbt = MiscUtil.getPlayerTag(player);
				if(!nbt.hasKey("elements")){
					nbt.setTag("elements", new NBTTagCompound());
				}
				nbt = nbt.getCompoundTag("elements");
				for(int i = 0; i < mag.length; i++){
					BeamUnit check = mag[i];
					if(check != null){
						if(!nbt.hasKey(EnumBeamAlignments.getAlignment(check).name())){
							nbt.setBoolean(EnumBeamAlignments.getAlignment(check).name(), true);
							//Doesn't use deletion-chat as the element discovery notification shouldn't be wiped away in 1 tick.
							player.sendMessage(new TextComponentString(TextFormatting.BOLD.toString() + "New Element Discovered: " + EnumBeamAlignments.getAlignment(check).toString() + TextFormatting.RESET.toString()));
							StoreNBTToClient.syncNBTToClient((EntityPlayerMP) player, false);
						}
						chat.add(EnumFacing.byIndex(i).toString() + ": " + check.toString());
					}
				}
			}
		}

		if(te instanceof IInfoTE){
			((IInfoTE) te).addInfo(chat, player, facing, hitX, hitY, hitZ);
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){

			ArrayList<String> chat = new ArrayList<>();

			measure(chat, playerIn, worldIn, pos, facing, hitX, hitY, hitZ);

			if(!chat.isEmpty()){
				StringBuilder out = new StringBuilder();
				for(String line : chat){
					if(out.length() != 0){
						out.append("\n");
					}
					out.append(line);
				}
				ModPackets.network.sendTo(new SendChatToClient(out.toString(), CHAT_ID), (EntityPlayerMP) playerIn);
			}
		}

		return EnumActionResult.SUCCESS;
	}
}
