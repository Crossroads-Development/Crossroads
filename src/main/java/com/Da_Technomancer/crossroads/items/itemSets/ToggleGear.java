package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ToggleGear extends GearMatItem{

	private final boolean inverted;

	public ToggleGear(boolean inverted){
		String name = "gear_toggle" + (inverted ? "inv" : "");
		setRegistryName(name);
		this.inverted = inverted;
		CRItems.toRegister.add(this);
	}

	@Override
	protected double shapeFactor(){
		return 0.125D / 8D;
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

		TileEntity te = world.getTileEntity(pos.offset(side));
		if(te instanceof MechanismTileEntity && RotaryUtil.solidToGears(world, pos, side)){
			MechanismTileEntity mte = (MechanismTileEntity) te;
			if(mte.members[side.getOpposite().getIndex()] != null){
				return ActionResultType.SUCCESS;
			}
			if(playerIn == null || !playerIn.isCreative()){
				context.getItem().shrink(1);
			}

			RotaryUtil.increaseMasterKey(true);
			mte.setMechanism(side.getOpposite().getIndex(), MechanismTileEntity.MECHANISMS.get(inverted ? 5 : 4), type, null, false);
		}else if(world.getBlockState(pos.offset(side)).isReplaceable(new BlockItemUseContext(context)) && RotaryUtil.solidToGears(world, pos, side)){
			if(playerIn == null || !playerIn.isCreative()){
				context.getItem().shrink(1);
			}

			world.setBlockState(pos.offset(side), CrossroadsBlocks.sextupleGear.getDefaultState(), 3);
			te = world.getTileEntity(pos.offset(side));
			if(te instanceof MechanismTileEntity){
				RotaryUtil.increaseMasterKey(true);
				((MechanismTileEntity) te).setMechanism(side.getOpposite().getIndex(), MechanismTileEntity.MECHANISMS.get(inverted ? 5 : 4), type, null, true);
			}
		}

		return ActionResultType.SUCCESS;
	}
}
