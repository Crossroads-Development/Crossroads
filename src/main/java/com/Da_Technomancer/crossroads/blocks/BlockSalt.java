package com.Da_Technomancer.crossroads.blocks;

import java.util.Random;

import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockVine;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class BlockSalt extends Block{

	protected BlockSalt(){
		super(Material.SAND);
		setHarvestLevel("shovel", 0);
		String name = "block_salt";
		setUnlocalizedName(name);
		setRegistryName(name);
		this.setCreativeTab(ModItems.tabCrossroads);
		GameRegistry.register(this);
		setHardness(.5F);
		setSoundType(SoundType.SAND);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
		OreDictionary.registerOre(name, this);
		this.setTickRandomly(true);
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn){
		if(entityIn instanceof EntitySlime){
			entityIn.setDead();
			if(!worldIn.isRemote){
				EntityItem item = new EntityItem(worldIn, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(Items.SLIME_BALL));
				worldIn.spawnEntity(item);
			}
		}else if(entityIn instanceof EntityCreeper){
			entityIn.setDead();
			if(!worldIn.isRemote){
				EntityItem item = new EntityItem(worldIn, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(Blocks.DEADBUSH));
				worldIn.spawnEntity(item);
			}
		}

		super.onEntityWalk(worldIn, pos, entityIn);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
		if(worldIn.isRemote){
			return;
		}

		for(int i = 0; i < 10; ++i){
			BlockPos killPos = pos.add(rand.nextInt(5) - 2, rand.nextInt(3) - 1, rand.nextInt(5) - 2);

			if(worldIn.getBlockState(killPos).getMaterial() == Material.PLANTS || ((worldIn.getBlockState(killPos).getMaterial() == Material.VINE && !(worldIn.getBlockState(killPos).getBlock() instanceof BlockVine)))){
				worldIn.setBlockState(killPos, Blocks.DEADBUSH.getDefaultState());
			}else if(worldIn.getBlockState(killPos).getBlock() == Blocks.GRASS){
				worldIn.setBlockState(killPos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT));
			}
		}

	}
}
