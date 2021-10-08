package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import java.util.Random;

public class DirtEffect implements IEffect{

	private final Random rand = new Random();

	@Override
	public void doEffect(Level worldIn, BlockPos pos){

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
				worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.OBSIDIAN).setHoverName(new TextComponent("Dirt LvL 3"))));
				break;
			case 2:
				worldIn.destroyBlock(pos, false);
				Pufferfish pufferFish = EntityType.PUFFERFISH.create(worldIn);
				if(pufferFish != null){
					pufferFish.setPos(pos.getX(), pos.getY(), pos.getZ());
					worldIn.addFreshEntity(pufferFish);
				}
				break;
			case 3:
				worldIn.destroyBlock(pos, false);
				worldIn.explode(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 3, Explosion.BlockInteraction.BREAK);
				break;
			case 4:
				worldIn.destroyBlock(pos, false);
				Squid squid = new Squid(EntityType.SQUID, worldIn);
				squid.setPos(pos.getX(), pos.getY(), pos.getZ());
				squid.setCustomName(new TextComponent("Richard Nixon"));
				squid.setPersistenceRequired();
				worldIn.addFreshEntity(squid);
				break;
			case 5:
				worldIn.setBlock(pos, Blocks.WET_SPONGE.defaultBlockState(), 3);
				break;
			case 6:
				worldIn.destroyBlock(pos, false);
				Villager villager = new Villager(EntityType.VILLAGER, worldIn);
				villager.setPos(pos.getX(), pos.getY(), pos.getZ());
				worldIn.addFreshEntity(villager);
				break;
			case 7:
				worldIn.destroyBlock(pos, false);
				LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(worldIn);
				lightning.moveTo(Vec3.atBottomCenterOf(pos));
				worldIn.addFreshEntity(lightning);
				break;
		}

	}

}
