package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class DetailedCrafter extends Block{

	public DetailedCrafter(){
		super(Properties.create(Material.IRON).hardnessAndResistance(3));
		String name = "detailed_crafter";
		setRegistryName(name);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!worldIn.isRemote){
			StoreNBTToClient.syncNBTToClient((ServerPlayerEntity) playerIn);
			playerIn.openGui(Crossroads.instance, GuiHandler.CRAFTER_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}
}
