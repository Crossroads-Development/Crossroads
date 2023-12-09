package com.Da_Technomancer.crossroads.effects.beam_effects;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.beams.BeamHit;
import com.Da_Technomancer.crossroads.api.beams.EnumBeamAlignments;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.List;

public class PlaceEffect extends BeamEffect{

	public static FakePlayer getBlockFakePlayer(ServerLevel world){
		GameProfile fakePlayerProfile = new GameProfile(null, Crossroads.MODID + "-block-fake-player-" + MiscUtil.getDimensionName(world));
		return FakePlayerFactory.get(world, fakePlayerProfile);
	}

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, BeamHit beamHit){
		if(!performTransmute(align, voi, power, beamHit)){
			if(voi){
				if(!CRConfig.isProtected(beamHit.getWorld(), beamHit.getPos(), beamHit.getEndState())){
					beamHit.getWorld().destroyBlock(beamHit.getPos(), true);
				}
			}else{
				double range = Math.sqrt(power);
				List<ItemEntity> items = beamHit.getNearbyEntities(ItemEntity.class, range, null);
				if(items.size() != 0){
					FakePlayer placer = getBlockFakePlayer(beamHit.getWorld());
					for(ItemEntity ent : items){
						ItemStack stack = ent.getItem();
						if(!stack.isEmpty() && stack.getItem() instanceof BlockItem){
							BlockPlaceContext context = new BlockPlaceContext(new UseOnContext(placer, InteractionHand.MAIN_HAND, new BlockHitResult(new Vec3(ent.getX(), ent.getY(), ent.getZ()), Direction.DOWN, ent.blockPosition(), false)));
							BlockState state = ((BlockItem) stack.getItem()).getBlock().getStateForPlacement(context);

							if(state != null){
								BlockPos pos = ent.blockPosition();
								BlockState worldState = beamHit.getWorld().getBlockState(pos);
								if(worldState.canBeReplaced(context) && state.canSurvive(beamHit.getWorld(), pos)){
									tryPlace(state, beamHit.getWorld(), pos, worldState, placer, stack, ent);
								}else{
									pos = pos.above();
									worldState = beamHit.getWorld().getBlockState(pos);
									if(worldState.canBeReplaced(context) && state.canSurvive(beamHit.getWorld(), pos)){
										tryPlace(state, beamHit.getWorld(), pos, worldState, placer, stack, ent);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void tryPlace(BlockState state, Level world, BlockPos pos, BlockState existingState, FakePlayer placer, ItemStack stack, ItemEntity ent){
		world.setBlockAndUpdate(pos, state);
		state.getBlock().setPlacedBy(world, pos, existingState, placer, stack);
		SoundType soundtype = state.getBlock().getSoundType(state, world, pos, placer);
		world.playSound(null, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
		stack.shrink(1);
		if(stack.getCount() <= 0){
			ent.remove(Entity.RemovalReason.DISCARDED);
		}
	}
}