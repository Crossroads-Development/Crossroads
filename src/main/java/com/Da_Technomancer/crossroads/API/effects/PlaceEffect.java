package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.util.List;

public class PlaceEffect extends BeamEffect{

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, World worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			if(voi){
				if(!CRConfig.isProtected(worldIn, pos, worldIn.getBlockState(pos))){
					worldIn.destroyBlock(pos, true);
				}
			}else{
				List<ItemEntity> items = worldIn.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos.add(-power, -power, -power), pos.add(power, power, power)), EntityPredicates.IS_ALIVE);
				if(items.size() != 0){
					FakePlayer placer = FakePlayerFactory.get((ServerWorld) worldIn, new GameProfile(null, Crossroads.MODID + "-place_effect-" + worldIn.getDimension().getType().getId()));
					for(ItemEntity ent : items){
						ItemStack stack = ent.getItem();
						if(!stack.isEmpty() && stack.getItem() instanceof BlockItem){
							BlockItemUseContext context = new BlockItemUseContext(new ItemUseContext(placer, Hand.MAIN_HAND, new BlockRayTraceResult(new Vec3d(ent.posX, ent.posY, ent.posZ), Direction.DOWN, ent.getPosition(), false)));
							BlockState state = ((BlockItem) stack.getItem()).getBlock().getStateForPlacement(context);
							if(state.isValidPosition(worldIn, ent.getPosition())){
								worldIn.setBlockState(ent.getPosition(), state);
								state.getBlock().onBlockPlacedBy(worldIn, ent.getPosition(), worldIn.getBlockState(ent.getPosition()), placer, stack);
								SoundType soundtype = state.getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, placer);
								worldIn.playSound(null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
								stack.shrink(1);
								if(stack.getCount() <= 0){
									ent.remove();
								}
							}
						}
					}
				}
			}
		}
	}
}