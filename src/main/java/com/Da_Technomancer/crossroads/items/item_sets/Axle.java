package com.Da_Technomancer.crossroads.items.item_sets;

import com.Da_Technomancer.crossroads.api.rotary.IMechanism;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.rotary.mechanisms.MechanismTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class Axle extends GearMatItem{

	public Axle(){
		this("axle");
	}

	protected Axle(String name){
		super(name);
	}

	@Override
	protected double shapeFactor(){
		return 1D / 32_000D;
	}

	protected IMechanism mechanismToPlace(){
		return MechanismTileEntity.MECHANISMS.get(1);
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		if(context.getLevel().isClientSide){
			return InteractionResult.SUCCESS;
		}
		GearFactory.GearMaterial type = getMaterial(context.getItemInHand());
		if(type == null){
			return InteractionResult.SUCCESS;
		}

		//Attempt to add this axle to a pre-existing mechanism
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Player playerIn = context.getPlayer();
		Direction side = context.getClickedFace();
		BlockEntity te = world.getBlockEntity(pos);

		if(te instanceof MechanismTileEntity){
			MechanismTileEntity mte = (MechanismTileEntity) te;
			if(mte.members[6] == null){
				RotaryUtil.increaseMasterKey(true);
				mte.setMechanism(6, mechanismToPlace(), type, side.getAxis(), false);
				if(playerIn == null || !playerIn.isCreative()){
					context.getItemInHand().shrink(1);
				}
				return InteractionResult.SUCCESS;
			}
		}

		//Check if offsetting by one would land us in another mechanism
		te = world.getBlockEntity(pos.relative(side));
		if(te instanceof MechanismTileEntity){
			MechanismTileEntity mte = (MechanismTileEntity) te;
			if(mte.members[6] == null){
				RotaryUtil.increaseMasterKey(true);
				mte.setMechanism(6, mechanismToPlace(), type, side.getAxis(), false);
				if(playerIn == null || !playerIn.isCreative()){
					context.getItemInHand().shrink(1);
				}
			}
			return InteractionResult.SUCCESS;
		}

		//Make a new mechanism block
		if(world.getBlockState(pos.relative(side)).canBeReplaced(new BlockPlaceContext(context))){
			if(playerIn == null || !playerIn.isCreative()){
				context.getItemInHand().shrink(1);
			}

			world.setBlock(pos.relative(side), CRBlocks.mechanism.defaultBlockState(), 3);
			te = world.getBlockEntity(pos.relative(side));
			if(te instanceof MechanismTileEntity){
				RotaryUtil.increaseMasterKey(true);
				((MechanismTileEntity) te).setMechanism(6, mechanismToPlace(), type, side.getAxis(), true);
			}
		}

		return InteractionResult.SUCCESS;
	}
}
