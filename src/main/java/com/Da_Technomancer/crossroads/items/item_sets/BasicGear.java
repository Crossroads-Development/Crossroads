package com.Da_Technomancer.crossroads.items.item_sets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.CRMaterialLibrary;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.rotary.IMechanism;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.rotary.mechanisms.MechanismTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

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
		BlockPos clickedPos = context.getClickedPos();//The position of the block clicked
		Direction clickedSide = context.getClickedFace();

		//Place along an axle by clicking the side of the axle (not the tip, which places against the axle)
		Direction.Axis existingAxleAxis;
		if(world.getBlockEntity(clickedPos) instanceof MechanismTileEntity mte && mte.members[6] != null && (existingAxleAxis = mte.getAxleAxis()) != null && existingAxleAxis != clickedSide.getAxis()){
			//Confirm that it was actually the axle that was clicked on
			VoxelShape axleBB = mte.members[6].getBoundingBox(null, existingAxleAxis);
			Vec3 clickLocation = context.getClickLocation().subtract(clickedPos.getX(), clickedPos.getY(), clickedPos.getZ());//Adjust to be relative to blockspace
			Optional<Vec3> closestPoint = axleBB.closestPointTo(clickLocation);
			if(closestPoint.isPresent() && closestPoint.get().distanceToSqr(clickLocation) <= 0.0001F){//If closestPoint exactly equals clickLocation, it was a click on the axle. Added a small error margin.
				//Which of the two ends was the click closer to?
				Direction placementSide;
				if(existingAxleAxis.choose(clickLocation.x, clickLocation.y, clickLocation.z) < 0.5){
					placementSide = Direction.fromAxisAndDirection(existingAxleAxis, Direction.AxisDirection.NEGATIVE);
				}else{
					placementSide = Direction.fromAxisAndDirection(existingAxleAxis, Direction.AxisDirection.POSITIVE);
				}
				if(tryPlacement(world, clickedPos, placementSide, type, context)){
					return InteractionResult.SUCCESS;
				}
			}
		}

		//Place against a solid surface
		if(RotaryUtil.solidToGears(world, clickedPos, clickedSide)){
			if(tryPlacement(world, clickedPos.relative(clickedSide), clickedSide.getOpposite(), type, context)){
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.SUCCESS;
	}

	protected boolean tryPlacement(Level world, BlockPos pos, Direction side, CRMaterialLibrary.GearMaterial type, UseOnContext context){
		int mechInd = side.get3DDataValue();//Index this gear would be placed within the mechanism
		IMechanism<?> mechanism = mechanismToPlace();
		if(world.getBlockEntity(pos) instanceof MechanismTileEntity mte){
			//Existing mechanism TE to expand
			if(mte.members[mechInd] != null){
				//This spot is already taken
				return false;
			}
			mte.setMechanism(mechInd, mechanism, type, null, false);
			RotaryUtil.increaseMasterKey(!world.isClientSide, world);
			//Consume an item
			if(!world.isClientSide && (context.getPlayer() == null || !context.getPlayer().isCreative())){
				context.getItemInHand().shrink(1);
			}
			return true;
		}else if(world.getBlockState(pos).canBeReplaced(new BlockPlaceContext(context))){
			//No existing mechanism- we will create a new one
			world.setBlock(pos, CRBlocks.mechanism.defaultBlockState(), MiscUtil.BLOCK_FLAGS_NORMAL);
			BlockEntity teAtPlacement = world.getBlockEntity(pos);
			if(teAtPlacement instanceof MechanismTileEntity mte){
				mte.setMechanism(mechInd, mechanism, type, null, true);
			}else{
				//Log an error
				Crossroads.logger.error("Mechanism BlockEntity did not exist at gear placement; Report to mod author");
			}
			RotaryUtil.increaseMasterKey(!world.isClientSide, world);
			//Consume an item
			if(!world.isClientSide && (context.getPlayer() == null || !context.getPlayer().isCreative())){
				context.getItemInHand().shrink(1);
			}
			return true;
		}
		return false;
	}
}
