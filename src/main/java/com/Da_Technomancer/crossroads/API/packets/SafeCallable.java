package com.Da_Technomancer.crossroads.API.packets;

import java.util.ArrayList;

import com.Da_Technomancer.crossroads.API.technomancy.LooseBeamRenderable;
import com.Da_Technomancer.crossroads.tileentities.SlottedChestTileEntity;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**Certain packets need to call or store code in client side only stuff, and can not do so without crashing due to (for example) WorldClient not existing on the server side.
 * In those cases, the packets should call methods in this class
 * This class is for CLIENT SIDE CODE ONLY
 */	
public class SafeCallable{
	
	public static final ArrayList<LooseBeamRenderable> beamsToRender = new ArrayList<LooseBeamRenderable>();
	
	protected static void summonLightning(WorldClient client, BlockPos pos){
		client.spawnEntity(new EntityLightningBolt(client, pos.getX(), pos.getY(), pos.getZ(), true));
	}
	
	protected static void chestLock(World worldClient, NBTTagCompound nbt, BlockPos pos){
		TileEntity te = worldClient.getTileEntity(pos);

		if(te instanceof SlottedChestTileEntity){
			SlottedChestTileEntity chest = ((SlottedChestTileEntity) te);
			for(int i = 0; i < 54; i++){
				if(nbt.hasKey("lock" + i)){
					chest.lockedInv[i] = new ItemStack(nbt.getCompoundTag("lock" + i));
				}else{
					chest.lockedInv[i] = ItemStack.EMPTY;
				}
			}
		}
	}
}
