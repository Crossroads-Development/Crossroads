package com.Da_Technomancer.crossroads.items;

import java.util.ArrayList;

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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidGauge extends Item implements IInfoDevice{

	public FluidGauge(){
		String name = "fluid_gauge";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.tabCrossroads);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	/**
	 * Value chosen at random.
	 */
	private static final int CHAT_ID = 823035;
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		TileEntity te = worldIn.getTileEntity(pos);
		
		if(!worldIn.isRemote){
			if(te != null){
				ArrayList<String> chat = new ArrayList<String>();
				if(te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)){
					IFluidHandler pipe = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

					chat.add(pipe.getTankProperties().length + " internal tank" + (pipe.getTankProperties().length == 1 ? "." : "s."));
					for(IFluidTankProperties tank : pipe.getTankProperties()){
						chat.add("% full: " + (tank.getContents() == null ? 0 : tank.getContents().amount) * 100 / tank.getCapacity());
					}
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
