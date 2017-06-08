package com.Da_Technomancer.crossroads.API.effects.goggles;

import java.util.ArrayList;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.GoggleLenses;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.tileentities.RatiatorTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class QuartzGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(World world, EntityPlayer player, ArrayList<String> chat, RayTraceResult ray){
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
							player.sendMessage(new TextComponentString(TextFormatting.BOLD.toString() + "New Element Discovered: " + MagicElements.getElement(check).toString() + TextFormatting.RESET.toString()));
						}
						chat.add(EnumFacing.getFront(i).toString() + ": " + check.toString());
					}
				}
			}
		}

		if(te instanceof IGoggleInfoTE){
			((IGoggleInfoTE) te).addInfo(chat, GoggleLenses.QUARTZ, player, ray.sideHit);
		}
		if(te instanceof RatiatorTileEntity){
			chat.add("Output Signal: " + ((RatiatorTileEntity) te).getOutput());
		}
	}
}