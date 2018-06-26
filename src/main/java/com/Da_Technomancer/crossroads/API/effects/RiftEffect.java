package com.Da_Technomancer.crossroads.API.effects;

import net.minecraft.block.BlockSilverfish;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Random;

public class RiftEffect implements IEffect{

	private static final Random rand = new Random();

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		if(worldIn.getBlockState(pos).getBlock() == Blocks.PURPUR_BLOCK){
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
			EntityShulker shulker = new EntityShulker(worldIn);
			shulker.setAttachmentPos(pos);
			shulker.setPosition(pos.getX(), pos.getY(), pos.getZ());
			worldIn.spawnEntity(shulker);
			return;
		}

		if(worldIn.getTileEntity(pos) instanceof TileEntitySkull && ((TileEntitySkull) worldIn.getTileEntity(pos)).getSkullType() == 0){
			int meta = worldIn.getBlockState(pos).getBlock().getMetaFromState(worldIn.getBlockState(pos));
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
			worldIn.setBlockState(pos, Blocks.SKULL.getStateFromMeta(meta), 3);
			((TileEntitySkull) worldIn.getTileEntity(pos)).setType(1);
			return;
		}

		if(BlockSilverfish.canContainSilverfish(worldIn.getBlockState(pos))){
			worldIn.setBlockState(pos, Blocks.MONSTER_EGG.getDefaultState().withProperty(BlockSilverfish.VARIANT, BlockSilverfish.EnumType.forModelBlock(worldIn.getBlockState(pos))), 3);
			return;
		}

		WorldServer worldServ = (WorldServer) worldIn;
		if(worldServ.countEntities(EnumCreatureType.MONSTER, true) <= worldServ.playerEntities.size() * 3 * EnumCreatureType.MONSTER.getMaxNumberOfCreature() && rand.nextInt(64) < mult){
			Biome.SpawnListEntry spawn = worldServ.getSpawnListEntryForTypeAt(EnumCreatureType.MONSTER, pos);
			if(spawn != null){
				try{
					EntityLiving ent = spawn.entityClass.getConstructor(new Class[] {World.class}).newInstance(worldServ);
					ent.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
					Event.Result r = ForgeEventFactory.canEntitySpawn(ent, worldServ, pos.getX(), pos.getY(), pos.getZ(), null);
					if(r == Event.Result.ALLOW || r == Event.Result.DEFAULT){
						worldServ.spawnEntity(ent);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	public static class VoidRiftEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			WorldServer worldServ = (WorldServer) worldIn;
			if(worldServ.countEntities(EnumCreatureType.CREATURE, true) <= worldServ.playerEntities.size() * 10 * EnumCreatureType.CREATURE.getMaxNumberOfCreature() && rand.nextInt(64) < mult){
				Biome.SpawnListEntry spawn = worldServ.getSpawnListEntryForTypeAt(EnumCreatureType.CREATURE, pos);
				if(spawn != null){
					try{
						EntityLiving ent = spawn.entityClass.getConstructor(new Class[] {World.class}).newInstance(worldServ);
						ent.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
						Event.Result r = ForgeEventFactory.canEntitySpawn(ent, worldServ, pos.getX(), pos.getY(), pos.getZ(), null);
						if(r == Event.Result.ALLOW || r == Event.Result.DEFAULT){
							worldServ.spawnEntity(ent);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	}
}
