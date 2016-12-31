package com.Da_Technomancer.crossroads.API.effects;

import java.util.Random;

import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLightningToClient;

import net.minecraft.block.BlockSponge;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class DirtEffect implements IEffect{

	private final Random rand = new Random();
	
	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){

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
				worldIn.spawnEntityInWorld(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.OBSIDIAN).setStackDisplayName("Dirt LvL 3")));
				break;
			case 2:
				worldIn.setBlockToAir(pos);
				worldIn.spawnEntityInWorld(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.FISH, 1, 3)));
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
				worldIn.spawnEntityInWorld(squid);
				break;
			case 5:
				worldIn.setBlockState(pos, Blocks.SPONGE.getDefaultState().withProperty(BlockSponge.WET, true), 3);
				break;
			case 6:
				worldIn.setBlockToAir(pos);
				EntityVillager villager = new EntityVillager(worldIn);
				villager.setPosition(pos.getX(), pos.getY(), pos.getZ());
				worldIn.spawnEntityInWorld(villager);
				break;
			case 7:
				worldIn.setBlockToAir(pos);
				worldIn.spawnEntityInWorld(new EntityLightningBolt(worldIn, pos.getX(), pos.getY(), pos.getZ(), false));
				ModPackets.network.sendToAllAround(new SendLightningToClient(pos), new TargetPoint(worldIn.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				break;
		}

	}

}
