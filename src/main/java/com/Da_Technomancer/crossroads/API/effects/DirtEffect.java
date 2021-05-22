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

		if(worldIn.isClientSide){
			return;
		}

		int effect = rand.nextInt(8);

		switch(effect){
			case 0:
				worldIn.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
				break;
			case 1:
				worldIn.destroyBlock(pos, false);
				worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.OBSIDIAN).setHoverName(new StringTextComponent("Dirt LvL 3"))));
				break;
			case 2:
				worldIn.destroyBlock(pos, false);
				PufferfishEntity pufferFish = EntityType.PUFFERFISH.create(worldIn);
				if(pufferFish != null){
					pufferFish.setPos(pos.getX(), pos.getY(), pos.getZ());
					worldIn.addFreshEntity(pufferFish);
				}
				break;
			case 3:
				worldIn.destroyBlock(pos, false);
				worldIn.explode(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 3, Explosion.Mode.BREAK);
				break;
			case 4:
				worldIn.destroyBlock(pos, false);
				SquidEntity squid = new SquidEntity(EntityType.SQUID, worldIn);
				squid.setPos(pos.getX(), pos.getY(), pos.getZ());
				squid.setCustomName(new StringTextComponent("Richard Nixon"));
				squid.setPersistenceRequired();
				worldIn.addFreshEntity(squid);
				break;
			case 5:
				worldIn.setBlock(pos, Blocks.WET_SPONGE.defaultBlockState(), 3);
				break;
			case 6:
				worldIn.destroyBlock(pos, false);
				VillagerEntity villager = new VillagerEntity(EntityType.VILLAGER, worldIn);
				villager.setPos(pos.getX(), pos.getY(), pos.getZ());
				worldIn.addFreshEntity(villager);
				break;
			case 7:
				worldIn.destroyBlock(pos, false);
				LightningBoltEntity lightning = EntityType.LIGHTNING_BOLT.create(worldIn);
				lightning.moveTo(Vector3d.atBottomCenterOf(pos));
				worldIn.addFreshEntity(lightning);
				break;
		}

	}

}
