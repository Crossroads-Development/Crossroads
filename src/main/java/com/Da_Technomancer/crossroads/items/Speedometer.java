package com.Da_Technomancer.crossroads.items;

import java.util.ArrayList;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoDevice;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Speedometer extends Item implements IInfoDevice{

	public Speedometer(){
		String name = "speedometer";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.tabCrossroads);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	/**
	 * Value chosen at random.
	 */
	private static final int CHAT_ID = 823485;


	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		TileEntity te = worldIn.getTileEntity(pos);

		if(!worldIn.isRemote){
			if(te != null){
				ArrayList<String> chat = new ArrayList<String>();
				if(te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite())){
					IAxleHandler gear = te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite());
					chat.add("Speed: " + MiscOp.betterRound(gear.getMotionData()[0], 3) + ", Energy: " + MiscOp.betterRound(gear.getMotionData()[1], 3));
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
				}
			}
		}

		return te instanceof IInfoTE ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
	}
}
