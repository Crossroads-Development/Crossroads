package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.mojang.authlib.GameProfile;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.util.List;

public class PlaceEffect extends BeamEffect{

	public static FakePlayer getBlockFakePlayer(ServerLevel world){
		GameProfile fakePlayerProfile = new GameProfile(null, Crossroads.MODID + "-block-fake-player-" + MiscUtil.getDimensionName(world));
		return FakePlayerFactory.get(world, fakePlayerProfile);
	}

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, Level worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			if(voi){
				if(!CRConfig.isProtected(worldIn, pos, worldIn.getBlockState(pos))){
					worldIn.destroyBlock(pos, true);
				}
			}else{
				double range = Math.sqrt(power) / 2D;
				List<ItemEntity> items = worldIn.getEntitiesOfClass(ItemEntity.class, new AABB(Vec3.atCenterOf(pos).add(-range, -range, -range), Vec3.atCenterOf(pos).add(range, range, range)), EntitySelector.ENTITY_STILL_ALIVE);
				if(items.size() != 0){
					FakePlayer placer = getBlockFakePlayer((ServerLevel) worldIn);
					for(ItemEntity ent : items){
						ItemStack stack = ent.getItem();
						if(!stack.isEmpty() && stack.getItem() instanceof BlockItem){
							BlockPlaceContext context = new BlockPlaceContext(new UseOnContext(placer, InteractionHand.MAIN_HAND, new BlockHitResult(new Vec3(ent.getX(), ent.getY(), ent.getZ()), Direction.DOWN, ent.blockPosition(), false)));
							BlockState state = ((BlockItem) stack.getItem()).getBlock().getStateForPlacement(context);
							BlockState worldState = worldIn.getBlockState(ent.blockPosition());
							if(worldState.canBeReplaced(context) && state.canSurvive(worldIn, ent.blockPosition())){
								worldIn.setBlockAndUpdate(ent.blockPosition(), state);
								state.getBlock().setPlacedBy(worldIn, ent.blockPosition(), worldIn.getBlockState(ent.blockPosition()), placer, stack);
								SoundType soundtype = state.getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, placer);
								worldIn.playSound(null, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
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