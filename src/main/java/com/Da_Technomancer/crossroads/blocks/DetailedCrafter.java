package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This is in the normal blocks package instead of the blocks.technomancy package as this will eventually be used for alchemy and witchcraft.  *
 */
public class DetailedCrafter extends Block{

	public DetailedCrafter(){
		super(Material.IRON);
		String name = "detailed_crafter";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.tabCrossroads);
		setHardness(3);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			StoreNBTToClient.syncNBTToClient((EntityPlayerMP) playerIn, false);
			playerIn.openGui(Main.instance, GuiHandler.CRAFTER_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}
}
