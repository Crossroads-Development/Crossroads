package com.Da_Technomancer.crossroads.API.alchemy;

import java.util.Random;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.blocks.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractTransmutationBlock extends BlockContainer{

	public AbstractTransmutationBlock(Material materialIn, String name){
		super(materialIn);
		setHardness(0);
		setUnlocalizedName(name);
		setRegistryName(name);
		ModBlocks.toRegister.add(this);
	}

	@Override
	public int quantityDropped(Random random){
		return 0;
	}

	@Override
	public int tickRate(World worldIn){
		return 5;
	}

	/**
	 * @param in
	 * @return The created blockstate, null if this has no effect. 
	 */
	@Nullable
	protected abstract IBlockState createdState(IBlockState in);
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
		if(worldIn.getGameRules().getBoolean("alchemyTransSpread")){
			TileEntity te = worldIn.getTileEntity(pos);
			if(!(te instanceof TransmutationTileEntity)){
				worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
				return;
			}
			TransmutationTileEntity tranTE = (TransmutationTileEntity) te;
			if(tranTE.created == null){
				worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
				return;
			}
			boolean dead = tranTE.life <= 0;
			for(EnumFacing dir : EnumFacing.VALUES){
				IBlockState created = createdState(worldIn.getBlockState(pos.offset(dir)));
				if(created != null){
					if(dead){
						worldIn.setBlockState(pos.offset(dir), created);
					}
					worldIn.setBlockState(pos.offset(dir), getDefaultState());
					TransmutationTileEntity newTE = (TransmutationTileEntity) worldIn.getTileEntity(pos.offset(dir));
					newTE.created = created;
					newTE.life = tranTE.life - 1;
				}
			}
			worldIn.setBlockState(pos, tranTE.created);			
		}
	}

	public static final class TransmutationTileEntity extends TileEntity{

		private int life;
		private IBlockState created;

		@SuppressWarnings("deprecation")
		@Override
		public void readFromNBT(NBTTagCompound nbt){
			super.readFromNBT(nbt);
			life = nbt.getInteger("life");
			Block b = Block.getBlockFromName(nbt.getString("block"));
			if(b == null){
				created = null;
				return;
			}
			created = b.getStateFromMeta(nbt.getInteger("meta"));
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound nbt){
			super.writeToNBT(nbt);
			nbt.setInteger("life", life);
			if(created != null){
				nbt.setString("block", created.getBlock().getRegistryName().toString());
				nbt.setInteger("meta", created.getBlock().getMetaFromState(created));
			}
			return nbt;
		}
	}
}
