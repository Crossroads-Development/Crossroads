package com.Da_Technomancer.crossroads.items.item_sets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.CRMaterialLibrary;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.rotary.IMechanism;
import com.Da_Technomancer.crossroads.api.rotary.IMechanismProperty;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.rotary.mechanisms.MechanismLargeGearCore;
import com.Da_Technomancer.crossroads.blocks.rotary.mechanisms.MechanismLargeGearEdge;
import com.Da_Technomancer.crossroads.blocks.rotary.mechanisms.MechanismTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class LargeGear extends BasicGear{

	public LargeGear(){
		super("gear_base_large");
	}

	@Override
	protected double shapeFactor(){
		return 9D * 1.125D / 8D;
	}

	@Override
	protected IMechanism<?> mechanismToPlace(){
		return MechanismLargeGearCore.INSTANCE;
	}

	/**
	 * Every position relative to the core to place an edge. Each inner array is for the axis alignment with the same ordinal as the index
	 */
	public static final MechanismLargeGearEdge.CorePosOffset[][] EDGE_OFFSET_POSITIONS = new MechanismLargeGearEdge.CorePosOffset[3][8];
	static{
		for(Direction.Axis axis : Direction.Axis.values()){
			int index = 0;
			for(int i = -1; i < 2; i++){
				for(int j = -1; j < 2; j++){
					if(i == 0 && j == 0){
						continue;//Don't include center
					}
					EDGE_OFFSET_POSITIONS[axis.ordinal()][index] = new MechanismLargeGearEdge.CorePosOffset(new BlockPos(axis == Direction.Axis.X ? 0 : i, axis == Direction.Axis.Y ? 0 : j, axis == Direction.Axis.X ? i : (axis == Direction.Axis.Y ? j : 0)));
					index++;
				}
			}
		}
	}

	@Override
	protected boolean tryPlacement(Level world, BlockPos pos, Direction side, CRMaterialLibrary.GearMaterial type, UseOnContext context){
		if(!canPlaceMechanismSlot(world, pos, side)){
			return false;
		}
		for(MechanismLargeGearEdge.CorePosOffset edgeOffset : EDGE_OFFSET_POSITIONS[side.getAxis().ordinal()]){
			if(!canPlaceMechanismSlot(world, edgeOffset.edgePos(pos), side)){
				return false;
			}
		}

		forcePlaceMechanismSlot(world, pos, side, mechanismToPlace(), type);
		for(MechanismLargeGearEdge.CorePosOffset edgeOffset : EDGE_OFFSET_POSITIONS[side.getAxis().ordinal()]){
			forcePlaceMechanismSlot(world, edgeOffset.edgePos(pos), side, MechanismLargeGearEdge.INSTANCE, edgeOffset);
		}
		RotaryUtil.increaseMasterKey(!world.isClientSide, world);
		//Consume an item
		if(!world.isClientSide && (context.getPlayer() == null || !context.getPlayer().isCreative())){
			context.getItemInHand().shrink(1);
		}
		return false;
	}

	private static boolean canPlaceMechanismSlot(Level world, BlockPos pos, Direction side){
		if(!RotaryUtil.couldMechanismExistAtLocation(world, pos, side, null, MechanismLargeGearEdge.INSTANCE)){
			return false;
		}
		if(world.getBlockEntity(pos) instanceof MechanismTileEntity mechanismTE){
			return mechanismTE.members[side.ordinal()] == null;
		}
		return world.getBlockState(pos).canBeReplaced();
	}

	private static void forcePlaceMechanismSlot(Level world, BlockPos pos, Direction side, IMechanism<?> mechanism, IMechanismProperty mechanismProperty){
		int mechInd = side.get3DDataValue();
		if(world.getBlockEntity(pos) instanceof MechanismTileEntity mte){
			//Existing mechanism TE to expand
			mte.setMechanism(mechInd, mechanism, mechanismProperty, null, false);
		}else{
			//No existing mechanism- we will create a new one
			world.setBlock(pos, CRBlocks.mechanism.defaultBlockState(), MiscUtil.BLOCK_FLAGS_NORMAL);
			if(world.getBlockEntity(pos) instanceof MechanismTileEntity mte){
				mte.setMechanism(mechInd, mechanism, mechanismProperty, null, true);
			}else{
				//Log an error
				Crossroads.logger.error("Mechanism BlockEntity did not exist at gear placement; Report to mod author");
			}
		}
	}
}
