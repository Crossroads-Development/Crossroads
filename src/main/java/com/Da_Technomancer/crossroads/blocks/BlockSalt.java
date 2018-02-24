package com.Da_Technomancer.crossroads.blocks;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.crafting.ModCrafting;

import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockVine;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSalt extends BlockFalling{

	protected BlockSalt(){
		super(Material.SAND);
		setHarvestLevel("shovel", 0);
		String name = "block_salt";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(.5F);
		setSoundType(SoundType.SAND);
		setTickRandomly(true);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
		ModCrafting.toRegisterOreDict.add(Pair.of(this, new String[] {"blockSalt"}));
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn){
		if(entityIn instanceof EntitySlime){
			entityIn.setDead();
			if(!worldIn.isRemote){
				worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(Items.SLIME_BALL, ((EntitySlime) entityIn).getSlimeSize() + 1)));
			}
		}else if(entityIn instanceof EntityCreeper){
			entityIn.setDead();
			if(!worldIn.isRemote){
				worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(Blocks.DEADBUSH)));
			}
		}

		super.onEntityWalk(worldIn, pos, entityIn);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
		if(worldIn.isRemote){
			return;
		}
		super.updateTick(worldIn, pos, state, rand);
		
		for(int i = 0; i < 10; ++i){
			BlockPos killPos = pos.add(rand.nextInt(5) - 2, rand.nextInt(3) - 1, rand.nextInt(5) - 2);

			if(worldIn.getBlockState(killPos).getMaterial() == Material.PLANTS || ((worldIn.getBlockState(killPos).getMaterial() == Material.VINE && !(worldIn.getBlockState(killPos).getBlock() instanceof BlockVine)))){
				worldIn.setBlockState(killPos, Blocks.DEADBUSH.getDefaultState());
			}else if(worldIn.getBlockState(killPos).getBlock() == Blocks.GRASS){
				worldIn.setBlockState(killPos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getDustColor(IBlockState state){
		return Color.WHITE.getRGB();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Plants are overrated.");
	}
}
