package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.entity.EntityGhostMarker;
import net.minecraft.block.Blocks;
import net.minecraft.block.SilverfishBlock;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Random;

public class RiftEffect implements IEffect{

	private static final Random rand = new Random();

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
		if(worldIn.getBlockState(pos).getBlock() == Blocks.PURPUR_BLOCK){
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
			ShulkerEntity shulker = new ShulkerEntity(worldIn);
			shulker.setAttachmentPos(pos);
			shulker.setPosition(pos.getX(), pos.getY(), pos.getZ());
			worldIn.spawnEntity(shulker);
			return;
		}

		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof SkullTileEntity && ((SkullTileEntity) te).getSkullType() == 0){
			int meta = worldIn.getBlockState(pos).getBlock().getMetaFromState(worldIn.getBlockState(pos));
			//Annoying vanilla bug where setting from one type of skull to another won't update the texture
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
			worldIn.setBlockState(pos, Blocks.SKULL.getStateFromMeta(meta), 3);
			te = worldIn.getTileEntity(pos);
			if(te instanceof SkullTileEntity){
				((SkullTileEntity) te).setType(1);
			}
			return;
		}

		if(SilverfishBlock.canContainSilverfish(worldIn.getBlockState(pos))){
			worldIn.setBlockState(pos, Blocks.MONSTER_EGG.getDefaultState().with(SilverfishBlock.VARIANT, SilverfishBlock.EnumType.forModelBlock(worldIn.getBlockState(pos))), 3);
			return;
		}

		ServerWorld worldServ = (ServerWorld) worldIn;
		if(worldServ.countEntities(EntityClassification.MONSTER, true) <= worldServ.playerEntities.size() * 3 * EntityClassification.MONSTER.getMaxNumberOfCreature() && rand.nextInt(256) < mult){
			Biome.SpawnListEntry spawn = worldServ.getSpawnListEntryForTypeAt(EntityClassification.MONSTER, pos);
			if(spawn != null){
				try{
					MobEntity ent = spawn.entityClass.getConstructor(new Class[] {World.class}).newInstance(worldServ);
					ent.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
					Event.Result r = ForgeEventFactory.canEntitySpawn(ent, worldServ, pos.getX(), pos.getY(), pos.getZ(), null);
					if(r == Event.Result.ALLOW || r == Event.Result.DEFAULT){
						ent.onInitialSpawn(worldServ.getDifficultyForLocation(pos), null);//Gives mobs weapons/armor, makes slimes not have glitched health, and other essential things
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
		public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
			EntityGhostMarker marker = new EntityGhostMarker(worldIn, EntityGhostMarker.EnumMarkerType.BLOCK_SPAWNING);
			marker.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			CompoundNBT rangeData = new CompoundNBT();
			rangeData.setInteger("range", mult);
			marker.data = rangeData;
			worldIn.spawnEntity(marker);
		}
	}
}
