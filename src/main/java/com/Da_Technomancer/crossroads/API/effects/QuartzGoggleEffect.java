package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.tileentities.RatiatorTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.CrystalMasterAxisTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class QuartzGoggleEffect implements IGoggleEffect{

	/**
	 * Initial value chosen at random
	 */
	private static final int CHAT_ID = 254857;

	@Override
	public void armorTick(World world, EntityPlayer player){
		String out = "";
		RayTraceResult ray = MiscOp.rayTrace(player, 8);
		if(ray == null){
			return;
		}
		TileEntity te = world.getTileEntity(ray.getBlockPos());
		if(te instanceof BeamRenderTE){
			MagicUnit[] mag = ((BeamRenderTE) te).getLastFullSent();
			if(mag != null){
				NBTTagCompound nbt = MiscOp.getPlayerTag(player);
				if(!nbt.hasKey("elements")){
					nbt.setTag("elements", new NBTTagCompound());
				}
				nbt = nbt.getCompoundTag("elements");
				for(int i = 0; i < mag.length; i++){
					MagicUnit check = mag[i];
					if(check != null){
						if(!nbt.hasKey(MagicElements.getElement(check).name())){
							nbt.setBoolean(MagicElements.getElement(check).name(), true);
							//Doesn't use deletion-chat as the element discovery notification shouldn't be wiped away in 1 tick.
							player.addChatComponentMessage(new TextComponentString(TextFormatting.BOLD.toString() + "New Element Discovered: " + MagicElements.getElement(check).toString() + TextFormatting.RESET.toString()));
						}

						if(!out.equals("")){
							out += "\n";
						}
						out += EnumFacing.getFront(i).toString() + ": " + check.toString();
					}
				}
			}
		}

		if(te instanceof CrystalMasterAxisTileEntity){
			if(!out.equals("")){
				out += "\n";
			}
			out += "Element: " + ((((CrystalMasterAxisTileEntity) te).getElement() == null) ? "NONE" : (((CrystalMasterAxisTileEntity) te).getElement().toString() + (((CrystalMasterAxisTileEntity) te).isVoid() ? " (VOID), " : ", ") + "Time: " + ((CrystalMasterAxisTileEntity) te).getTime()));
		}

		if(te instanceof RatiatorTileEntity){
			if(!out.equals("")){
				out += "\n";
			}
			out += "Output Signal: " + ((RatiatorTileEntity) te).getOutput();
		}


		if(!out.equals("")){
			ModPackets.network.sendTo(new SendChatToClient(out, CHAT_ID), (EntityPlayerMP) player);
		}
	}
}