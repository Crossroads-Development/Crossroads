package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearSlaveTileEntity;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
	public ActionResultType useOn(ItemUseContext context){
//		if(context.getWorld().isRemote){
//			return ActionResultType.SUCCESS;
//		}
		GearFactory.GearMaterial type = getMaterial(context.getItemInHand());
		if(type == null){
			return ActionResultType.SUCCESS;
		}
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		PlayerEntity playerIn = context.getPlayer();
		Direction side = context.getClickedFace();
		pos = pos.relative(side);
		BlockItemUseContext blockContext = new BlockItemUseContext(context);

		//Check we have space to place this
		if(!world.getBlockState(pos).canBeReplaced(blockContext)){
			return ActionResultType.FAIL;
		}

		for(BlockPos cPos : relSlavePos[side.getAxis().ordinal()]){
			if(!world.getBlockState(pos.offset(cPos)).canBeReplaced(blockContext)){
				return ActionResultType.FAIL;
			}
		}

		//Consume the item
		if(!world.isClientSide && (playerIn == null || !playerIn.isCreative())){
			context.getItemInHand().shrink(1);
		}

		//Place the gear
		world.setBlock(pos, CRBlocks.largeGearMaster.defaultBlockState().setValue(ESProperties.FACING, side.getOpposite()), 3);
		TileEntity te = world.getBlockEntity(pos);
		if(te instanceof LargeGearMasterTileEntity){
			((LargeGearMasterTileEntity) te).initSetup(type);
		}

		for(BlockPos cPos : relSlavePos[side.getAxis().ordinal()]){
			world.setBlock(pos.offset(cPos), CRBlocks.largeGearSlave.defaultBlockState().setValue(ESProperties.FACING, side.getOpposite()), 3);
			TileEntity relTE = world.getBlockEntity(pos.offset(cPos));
			if(relTE instanceof LargeGearSlaveTileEntity){
				((LargeGearSlaveTileEntity) relTE).setInitial(BlockPos.ZERO.subtract(cPos));
			}
		}

		//Notify the rotary system of a change
		RotaryUtil.increaseMasterKey(false);

		return ActionResultType.SUCCESS;
	}
}
