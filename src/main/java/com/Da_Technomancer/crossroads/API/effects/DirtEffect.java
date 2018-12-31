package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.block.BlockSponge;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class DirtEffect implements IEffect{

	private final Random rand = new Random();

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, EnumFacing dir){

		if(worldIn.isRemote){
			return;
		}

		int effect = rand.nextInt(8);

		switch(effect){
			case 0:
				worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState(), 3);
				break;
			case 1:
				worldIn.setBlockToAir(pos);
				worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.OBSIDIAN).setStackDisplayName("Dirt LvL 3")));
				break;
			case 2:
				worldIn.setBlockToAir(pos);
				worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.FISH, 1, 3)));
				break;
			case 3:
				worldIn.setBlockToAir(pos);
				worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 3, true);
				break;
			case 4:
				worldIn.setBlockToAir(pos);
				EntitySquid squid = new EntitySquid(worldIn);
				squid.setPosition(pos.getX(), pos.getY(), pos.getZ());
				squid.setCustomNameTag("Richard Nixon");
				squid.enablePersistence();
				worldIn.spawnEntity(squid);
				break;
			case 5:
				worldIn.setBlockState(pos, Blocks.SPONGE.getDefaultState().withProperty(BlockSponge.WET, true), 3);
				break;
			case 6:
				worldIn.setBlockToAir(pos);
				EntityVillager villager = new EntityVillager(worldIn);
				villager.setPosition(pos.getX(), pos.getY(), pos.getZ());
				worldIn.spawnEntity(villager);
				break;
			case 7:
				worldIn.setBlockToAir(pos);
				worldIn.addWeatherEffect(new EntityLightningBolt(worldIn, pos.getX(), pos.getY(), pos.getZ(), false));
				break;
		}

	}

}
