package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.fish.PufferfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.Random;

public class DirtEffect implements IEffect{

	private final Random rand = new Random();

	@Override
	public void doEffect(World worldIn, BlockPos pos){

		if(worldIn.isRemote){
			return;
		}

		int effect = rand.nextInt(8);

		switch(effect){
			case 0:
				worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState(), 3);
				break;
			case 1:
				worldIn.destroyBlock(pos, false);
				worldIn.addEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.OBSIDIAN).setDisplayName(new StringTextComponent("Dirt LvL 3"))));
				break;
			case 2:
				worldIn.destroyBlock(pos, false);
				PufferfishEntity pufferFish = EntityType.PUFFERFISH.create(worldIn);
				if(pufferFish != null){
					pufferFish.setPosition(pos.getX(), pos.getY(), pos.getZ());
					worldIn.addEntity(pufferFish);
				}
				break;
			case 3:
				worldIn.destroyBlock(pos, false);
				worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 3, Explosion.Mode.BREAK);
				break;
			case 4:
				worldIn.destroyBlock(pos, false);
				SquidEntity squid = new SquidEntity(EntityType.SQUID, worldIn);
				squid.setPosition(pos.getX(), pos.getY(), pos.getZ());
				squid.setCustomName(new StringTextComponent("Richard Nixon"));
				squid.enablePersistence();
				worldIn.addEntity(squid);
				break;
			case 5:
				worldIn.setBlockState(pos, Blocks.WET_SPONGE.getDefaultState(), 3);
				break;
			case 6:
				worldIn.destroyBlock(pos, false);
				VillagerEntity villager = new VillagerEntity(EntityType.VILLAGER, worldIn);
				villager.setPosition(pos.getX(), pos.getY(), pos.getZ());
				worldIn.addEntity(villager);
				break;
			case 7:
				worldIn.destroyBlock(pos, false);
				LightningBoltEntity lightning = EntityType.LIGHTNING_BOLT.create(worldIn);
				lightning.moveForced(Vector3d.copyCenteredHorizontally(pos));
				worldIn.addEntity(lightning);
				break;
		}

	}

}
