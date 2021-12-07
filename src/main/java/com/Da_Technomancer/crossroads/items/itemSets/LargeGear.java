package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearSlaveTileEntity;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LargeGear extends GearMatItem{

	public LargeGear(){
		super();
		String name = "gear_base_large";
		setRegistryName(name);
	}

	@Override
	protected double shapeFactor(){
		return 9D * 1.125D / 8D;
	}

	/**
	 * Every position relative to the master to place a slave. Each inner array is for the axis alignment with the same ordinal as the index
	 */
	private static final BlockPos[][] relSlavePos = new BlockPos[3][8];

	static{
		for(Direction.Axis axis : Direction.Axis.values()){
			int index = 0;
			for(int i = -1; i < 2; i++){
				for(int j = -1; j < 2; j++){
					if(i == 0 && j == 0){
						continue;//Don't include center
					}
					relSlavePos[axis.ordinal()][index] = new BlockPos(axis == Direction.Axis.X ? 0 : i, axis == Direction.Axis.Y ? 0 : j, axis == Direction.Axis.X ? i : (axis == Direction.Axis.Y ? j : 0));
					index++;
				}
			}
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
//		if(context.getWorld().isRemote){
//			return ActionResultType.SUCCESS;
//		}
		GearFactory.GearMaterial type = getMaterial(context.getItemInHand());
		if(type == null){
			return InteractionResult.SUCCESS;
		}
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Player playerIn = context.getPlayer();
		Direction side = context.getClickedFace();
		pos = pos.relative(side);
		BlockPlaceContext blockContext = new BlockPlaceContext(context);

		//Check we have space to place this
		if(!world.getBlockState(pos).canBeReplaced(blockContext)){
			return InteractionResult.FAIL;
		}

		for(BlockPos cPos : relSlavePos[side.getAxis().ordinal()]){
			if(!world.getBlockState(pos.offset(cPos)).canBeReplaced(blockContext)){
				return InteractionResult.FAIL;
			}
		}

		//Consume the item
		if(!world.isClientSide && (playerIn == null || !playerIn.isCreative())){
			context.getItemInHand().shrink(1);
		}

		//Place the gear
		world.setBlock(pos, CRBlocks.largeGearMaster.defaultBlockState().setValue(ESProperties.FACING, side.getOpposite()), 3);
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof LargeGearMasterTileEntity){
			((LargeGearMasterTileEntity) te).initSetup(type);
		}

		for(BlockPos cPos : relSlavePos[side.getAxis().ordinal()]){
			world.setBlock(pos.offset(cPos), CRBlocks.largeGearSlave.defaultBlockState().setValue(ESProperties.FACING, side.getOpposite()), 3);
			BlockEntity relTE = world.getBlockEntity(pos.offset(cPos));
			if(relTE instanceof LargeGearSlaveTileEntity){
				((LargeGearSlaveTileEntity) relTE).setInitial(BlockPos.ZERO.subtract(cPos));
			}
		}

		//Notify the rotary system of a change
		RotaryUtil.increaseMasterKey(false);

		return InteractionResult.SUCCESS;
	}
}
