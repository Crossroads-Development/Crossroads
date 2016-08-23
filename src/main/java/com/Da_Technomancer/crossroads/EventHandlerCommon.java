package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.BrazierTileEntity;

import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class EventHandlerCommon{

	@SubscribeEvent
	public void cancelWitchSpawns(LivingSpawnEvent e){
		if(e.getEntity() instanceof EntityWitch && BrazierTileEntity.blockSpawning(e.getWorld(), e.getX(), e.getY(), e.getZ())){
			e.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void addSpecialItems(EntityJoinWorldEvent event){
		if(!event.getEntity().worldObj.isRemote && event.getEntity() instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) event.getEntity();

			NBTTagCompound tag = getPlayerTag(player);
			if(!tag.hasKey("starter")){
				switch(player.getGameProfile().getName()){
					case "Da_Technomancer":
						player.inventory.addItemStackToInventory(new ItemStack(ModItems.debugReader, 1));
						break;
					default:
						break;
				}

				tag.setBoolean("starter", true);
			}
		}
	}
	
	private static NBTTagCompound getPlayerTag(EntityPlayer playerIn){
		NBTTagCompound tag = playerIn.getEntityData();
		if(!tag.hasKey(EntityPlayer.PERSISTED_NBT_TAG)){
			tag.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
		}
		tag = tag.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);

		if(!tag.hasKey(Main.MODID)){
			tag.setTag(Main.MODID, new NBTTagCompound());
		}

		return tag.getCompoundTag(Main.MODID);
	}
}
