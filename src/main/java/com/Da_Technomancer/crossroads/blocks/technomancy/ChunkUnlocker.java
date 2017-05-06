package com.Da_Technomancer.crossroads.blocks.technomancy;

import java.util.Random;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.ChunkUnlockerTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ChunkUnlocker extends BlockContainer{

	public ChunkUnlocker(){
		super(Material.IRON);
		String name = "chunk_unlocker";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
		setCreativeTab(ModItems.tabCrossroads);
		setHardness(3);
		setSoundType(SoundType.METAL);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new ChunkUnlockerTileEntity();
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
		FieldWorldSavedData data = FieldWorldSavedData.get(worldIn);
		data.fieldNodes.put(MiscOp.getLongFromChunkPos(new ChunkPos(pos)), FieldWorldSavedData.getDefaultChunkFlux());
		data.nodeForces.put(MiscOp.getLongFromChunkPos(new ChunkPos(pos)), FieldWorldSavedData.getDefaultChunkForce());
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		FieldWorldSavedData.get(world).fieldNodes.remove(MiscOp.getLongFromChunkPos(new ChunkPos(pos)));
		super.breakBlock(world, pos, state);
	}
}
