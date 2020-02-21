package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.IMechanism;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Axle extends GearMatItem{

	public Axle(){
		this("axle");
	}

	protected Axle(String name){
		super();
		setRegistryName(name);
	}

	@Override
	protected double shapeFactor(){
		return 1D / 32_000D;
	}

	protected IMechanism mechanismToPlace(){
		return MechanismTileEntity.MECHANISMS.get(1);
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

		//Attempt to add this axle to a pre-existing mechanism
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		PlayerEntity playerIn = context.getPlayer();
		Direction side = context.getFace();
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof MechanismTileEntity){
			MechanismTileEntity mte = (MechanismTileEntity) te;
			if(mte.members[6] == null){
				RotaryUtil.increaseMasterKey(true);
				mte.setMechanism(6, mechanismToPlace(), type, side.getAxis(), false);
				if(playerIn == null || !playerIn.isCreative()){
					context.getItem().shrink(1);
				}
				return ActionResultType.SUCCESS;
			}
		}

		//Check if offsetting by one would land us in another mechanism
		te = world.getTileEntity(pos.offset(side));
		if(te instanceof MechanismTileEntity){
			MechanismTileEntity mte = (MechanismTileEntity) te;
			if(mte.members[6] == null){
				RotaryUtil.increaseMasterKey(true);
				mte.setMechanism(6, mechanismToPlace(), type, side.getAxis(), false);
				if(playerIn == null || !playerIn.isCreative()){
					context.getItem().shrink(1);
				}
			}
			return ActionResultType.SUCCESS;
		}

		//Make a new mechanism block
		if(world.getBlockState(pos.offset(side)).isReplaceable(new BlockItemUseContext(context))){
			if(playerIn == null || !playerIn.isCreative()){
				context.getItem().shrink(1);
			}

			world.setBlockState(pos.offset(side), CRBlocks.mechanism.getDefaultState(), 3);
			te = world.getTileEntity(pos.offset(side));
			if(te instanceof MechanismTileEntity){
				RotaryUtil.increaseMasterKey(true);
				((MechanismTileEntity) te).setMechanism(6, mechanismToPlace(), type, side.getAxis(), true);
			}
		}

		return ActionResultType.SUCCESS;
	}
}
