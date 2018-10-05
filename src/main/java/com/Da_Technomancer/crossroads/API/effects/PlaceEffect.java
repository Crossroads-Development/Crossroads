package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.ModConfig;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.ArrayList;

public class PlaceEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		ArrayList<EntityItem> items = (ArrayList<EntityItem>) worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.add(-mult, -mult, -mult), pos.add(mult, mult, mult)), EntitySelectors.IS_ALIVE);
		if(items.size() != 0){
			FakePlayer placer = FakePlayerFactory.get((WorldServer) worldIn, new GameProfile(null, Main.MODID + "-place_effect-" + worldIn.provider.getDimension()));
			for(EntityItem ent : items){
				if(!ent.getItem().isEmpty() && ent.getItem().getItem() instanceof ItemBlock && ((ItemBlock) ent.getItem().getItem()).getBlock().canPlaceBlockAt(worldIn, ent.getPosition())){
					worldIn.setBlockState(ent.getPosition(), ((ItemBlock) ent.getItem().getItem()).getBlock().getStateForPlacement(worldIn, ent.getPosition(), EnumFacing.DOWN, 0, 0, 0, ent.getItem().getMetadata(), placer, EnumHand.MAIN_HAND));
					Block block = ((ItemBlock) ent.getItem().getItem()).getBlock();
					block.onBlockPlacedBy(worldIn, ent.getPosition(), worldIn.getBlockState(ent.getPosition()), placer, ent.getItem());
					SoundType soundtype = block.getSoundType(worldIn.getBlockState(pos), worldIn, pos, null);
					worldIn.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
					ent.getItem().shrink(1);
					if(ent.getItem().getCount() <= 0){
						ent.setDead();
					}
				}
			}
		}
	}

	public static class BreakEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			String[] bannedBlocks = ModConfig.getConfigStringList(ModConfig.destroyBlacklist, false);
			String id = worldIn.getBlockState(pos).getBlock().getRegistryName().toString();
			for(String s : bannedBlocks){
				if(s.equals(id)){
					return;
				}
			}
			worldIn.destroyBlock(pos, true);
		}
	}
}