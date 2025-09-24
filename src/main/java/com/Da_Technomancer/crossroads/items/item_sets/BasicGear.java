package com.Da_Technomancer.crossroads.items.item_sets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.CRMaterialLibrary;
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
import net.minecraft.world.level.block.state.BlockState;

public class BasicGear extends GearMatItem{

	public BasicGear(){
		this("gear_base");
	}

	protected BasicGear(String name){
		super(name);

	}

	@Override
	protected double shapeFactor(){
		return 0.125D / 8D;
	}

	protected IMechanism<?> mechanismToPlace(){
		return MechanismTileEntity.MECHANISMS.get(0);
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		CRMaterialLibrary.GearMaterial type = getMaterial(context.getItemInHand());
		if(type == null){
			return InteractionResult.SUCCESS;
		}
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();//The position of the block clicked
		Direction side = context.getClickedFace();
		BlockPos placePos = pos.relative(side);//Where the gear will be placed
		Player playerIn = context.getPlayer();
		BlockState stateAtPlacement = world.getBlockState(placePos);
		BlockEntity teAtPlacement = world.getBlockEntity(placePos);

		//Must be able to place against a solid surface
		if(RotaryUtil.solidToGears(world, pos, side)){
			int mechInd = side.getOpposite().get3DDataValue();//Index this gear would be placed within the mechanism
			if(teAtPlacement instanceof MechanismTileEntity mte){
				//Existing mechanism TE to expand
				if(mte.members[mechInd] != null){
					//This spot is already taken
					return InteractionResult.SUCCESS;
				}

				mte.setMechanism(mechInd, mechanismToPlace(), type, null, false);

				//Consume an item
				if(!world.isClientSide && (playerIn == null || !playerIn.isCreative())){
					context.getItemInHand().shrink(1);
				}

				RotaryUtil.increaseMasterKey(!world.isClientSide);
			}else if(stateAtPlacement.canBeReplaced(new BlockPlaceContext(context))){
				//No existing mechanism- we will create a new one
				world.setBlock(placePos, CRBlocks.mechanism.defaultBlockState(), 3);

				teAtPlacement = world.getBlockEntity(placePos);
				if(teAtPlacement instanceof MechanismTileEntity){
					((MechanismTileEntity) teAtPlacement).setMechanism(mechInd, mechanismToPlace(), type, null, true);
				}else{
					//Log an error
					Crossroads.logger.error("Mechanism TileEntity did not exist at gear placement; Report to mod author");
				}

				//Consume an item
				if(!world.isClientSide && (playerIn == null || !playerIn.isCreative())){
					context.getItemInHand().shrink(1);
				}

				RotaryUtil.increaseMasterKey(!world.isClientSide);
			}
		}

		return InteractionResult.SUCCESS;
	}
}
