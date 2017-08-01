package com.Da_Technomancer.crossroads.items;

import java.util.ArrayList;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoDevice;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Thermometer extends Item implements IInfoDevice{

	public Thermometer(){
		String name = "thermometer";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.tabCrossroads);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	/**
	 * Value chosen at random.
	 */
	private static final int CHAT_ID = 823028;
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te != null){
				ArrayList<String> chat = new ArrayList<String>();
				if(te.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null)){
					chat.add("Temp: " + te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null).getTemp() + "°C");
				}
				if(te instanceof IInfoTE){
					((IInfoTE) te).addInfo(chat, this, playerIn, facing);
				}
				if(!chat.isEmpty()){
					String out = "";
					for(String line : chat){
						if(!out.equals("")){
							out += "\n";
						}
						out += line;
					}
					ModPackets.network.sendTo(new SendChatToClient(out, CHAT_ID), (EntityPlayerMP) playerIn);
					return EnumActionResult.SUCCESS;
				}
			}
		}
		
		return EnumActionResult.PASS;
	}
}
