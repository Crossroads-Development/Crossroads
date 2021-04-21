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
	public ActionResultType useOn(ItemUseContext context){
		if(context.getLevel().isClientSide){
			return ActionResultType.SUCCESS;
		}
		GearFactory.GearMaterial type = getMaterial(context.getItemInHand());
		if(type == null){
			return ActionResultType.SUCCESS;
		}

		//Attempt to add this axle to a pre-existing mechanism
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		PlayerEntity playerIn = context.getPlayer();
		Direction side = context.getClickedFace();
		TileEntity te = world.getBlockEntity(pos);

		if(te instanceof MechanismTileEntity){
			MechanismTileEntity mte = (MechanismTileEntity) te;
			if(mte.members[6] == null){
				RotaryUtil.increaseMasterKey(true);
				mte.setMechanism(6, mechanismToPlace(), type, side.getAxis(), false);
				if(playerIn == null || !playerIn.isCreative()){
					context.getItemInHand().shrink(1);
				}
				return ActionResultType.SUCCESS;
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
			return ActionResultType.SUCCESS;
		}

		//Make a new mechanism block
		if(world.getBlockState(pos.relative(side)).canBeReplaced(new BlockItemUseContext(context))){
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

		return ActionResultType.SUCCESS;
	}
}
