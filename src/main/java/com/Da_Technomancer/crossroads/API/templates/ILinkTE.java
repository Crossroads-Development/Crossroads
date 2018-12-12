package com.Da_Technomancer.crossroads.API.templates;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;

/**
 * A helper class to be placed on TileEntities to enable basic linking behaviour
 */
public interface ILinkTE{

	public static final String POS_NBT = "c_link";
	public static final String DIM_NBT = "c_link_dim";

	/**
	 * @return This TE
	 */
	public TileEntity getTE();

	public default int getRange(){
		return 16;
	}

	/**
	 *
	 * @param otherTE The ILinkTE to attempt linking to
	 * @return Whether this TE is allowed to link to the otherTE. The source TE controls whether the link is allowed, the target is not checked. Do not check range
	 */
	public boolean canLink(ILinkTE otherTE);

	/**
	 * A mutable list of linked relative block positions.
	 * @return The list of links
	 */
	public ArrayList<BlockPos> getLinks();

	/**
	 * @return The maximum number of linked devices. The source controls this
	 */
	public default int getMaxLinks(){
		return 3;
	}

	/**
	 *
	 * @param endpoint The endpoint that this is being linked to
	 * @param player The calling player, for sending chat messages
	 * @return Whether the operation succeeded
	 */
	public default boolean link(ILinkTE endpoint, EntityPlayer player){
		ArrayList<BlockPos> links = getLinks();
		BlockPos linkPos = endpoint.getTE().getPos().subtract(getTE().getPos());
		if(links.contains(linkPos)){
			player.sendMessage(new TextComponentString("Device already linked; Canceling linking"));
		}else if(links.size() < getMaxLinks()){
			links.add(linkPos);
			player.sendMessage(new TextComponentString("Linked device at " + getTE().getPos() + " to send to " + endpoint.getTE().getPos()));
			return true;
		}else{
			player.sendMessage(new TextComponentString("All " + getMaxLinks() + " links already occupied; Canceling linking"));
		}
		return false;
	}

	/**
	 * Must be called from the block (server side only) to perform linking
	 * @param wrench The held wrench itemstack
	 * @param player The current player   
	 * @return The possibly modified wrench
	 */
	public default ItemStack wrench(ItemStack wrench, EntityPlayer player){
		if(player.isSneaking()){
			player.sendMessage(new TextComponentString("Clearing links"));
			clearLinks();
		}else if(wrench.hasTagCompound() && wrench.getTagCompound().hasKey(POS_NBT) && wrench.getTagCompound().getInteger(DIM_NBT) == player.world.provider.getDimension()){
			BlockPos prev = BlockPos.fromLong(wrench.getTagCompound().getLong(POS_NBT));

			TileEntity te = player.world.getTileEntity(prev);
			if(te instanceof ILinkTE && ((ILinkTE) te).canLink(this) && te != this){
				if(prev.distanceSq(getTE().getPos()) <= ((ILinkTE) te).getRange() * ((ILinkTE) te).getRange()){
					((ILinkTE) te).link(this, player);
				}else{
					player.sendMessage(new TextComponentString("Out of range; Canceling linking"));
				}
			}else{
				player.sendMessage(new TextComponentString("Invalid pair; Canceling linking"));
			}
		}else{
			if(!wrench.hasTagCompound()){
				wrench.setTagCompound(new NBTTagCompound());
			}

			wrench.getTagCompound().setLong(POS_NBT, getTE().getPos().toLong());
			wrench.getTagCompound().setInteger(DIM_NBT, getTE().getWorld().provider.getDimension());
			player.sendMessage(new TextComponentString("Beginning linking"));
			return wrench;
		}

		wrench.getTagCompound().removeTag(POS_NBT);
		wrench.getTagCompound().removeTag(DIM_NBT);
		return wrench;
	}

	public default void clearLinks(){
		getLinks().clear();
	}
}
