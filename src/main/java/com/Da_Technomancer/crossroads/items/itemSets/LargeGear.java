package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearSlaveTileEntity;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LargeGear extends GearMatItem{

	public LargeGear(){
		super();
		String name = "gear_base_large";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	protected double shapeFactor(){
		return 9D * 1.125D / 8D;
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context){
		if(context.getWorld().isRemote){
			return ActionResultType.SUCCESS;
		}
		GearFactory.GearMaterial type = getMaterial(context.getItem());
		if(type == null){
			return ActionResultType.SUCCESS;
		}
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		PlayerEntity playerIn = context.getPlayer();
		Direction side = context.getFace();
		pos = pos.offset(side);
		
		BlockPos[] spaces = new BlockPos[9];
		for(int i = -1; i < 2; i++){
			for(int j = -1; j < 2; j++){
				spaces[i * 3 + j + 4] = new BlockPos(side.getXOffset() == 0 ? i : 0, side.getYOffset() == 0 ? j : 0, side.getXOffset() == 0 ? side.getYOffset() == 0 ? 0 : j : i);
			}
		}

		for(BlockPos cPos : spaces){
			if(!world.getBlockState(pos.add(cPos)).isReplaceable(new BlockItemUseContext(context))){
				return ActionResultType.FAIL;
			}
		}

		if(playerIn == null || !playerIn.isCreative()){
			context.getItem().shrink(1);
		}

		for(BlockPos cPos : spaces){
			if(cPos.distanceSq(BlockPos.ZERO) == 0){
				world.setBlockState(pos, CRBlocks.largeGearMaster.getDefaultState().with(EssentialsProperties.FACING, side.getOpposite()), 3);
				((LargeGearMasterTileEntity) world.getTileEntity(pos)).initSetup(type);
			}else{
				world.setBlockState(pos.add(cPos), CRBlocks.largeGearSlave.getDefaultState().with(EssentialsProperties.FACING, side.getOpposite()), 3);
				((LargeGearSlaveTileEntity) world.getTileEntity(pos.add(cPos))).setInitial(BlockPos.ZERO.subtract(cPos));
			}
		}
		RotaryUtil.increaseMasterKey(false);

		return ActionResultType.SUCCESS;
	}
}
