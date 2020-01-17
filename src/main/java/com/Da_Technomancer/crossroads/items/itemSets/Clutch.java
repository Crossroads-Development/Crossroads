package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Clutch extends GearMatItem{

	private final boolean inverted;

	public Clutch(boolean inverted){
		super();
		this.inverted = inverted;
		String name = "clutch" + (inverted ? "_inv" : "");
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	protected double shapeFactor(){
		return 1D / 32000D;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		super.addInformation(stack, world, tooltip, advanced);
		tooltip.add(new TranslationTextComponent("tt.crossroads.clutch.redstone"));
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
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof MechanismTileEntity){
			MechanismTileEntity mte = (MechanismTileEntity) te;
			if(mte.members[6] == null){
				RotaryUtil.increaseMasterKey(true);
				mte.setMechanism(6, MechanismTileEntity.MECHANISMS.get(inverted ? 3 : 2), type, context.getFace().getAxis(), false);
				if(playerIn == null || !playerIn.isCreative()){
					context.getItem().shrink(1);
				}
				return ActionResultType.SUCCESS;
			}
		}

		//Check if offsetting by one would land us in another mechanism
		te = world.getTileEntity(pos.offset(context.getFace()));
		if(te instanceof MechanismTileEntity){
			MechanismTileEntity mte = (MechanismTileEntity) te;
			if(mte.members[6] == null){
				RotaryUtil.increaseMasterKey(true);
				mte.setMechanism(6, MechanismTileEntity.MECHANISMS.get(inverted ? 3 : 2), type, context.getFace().getAxis(), false);
				if(playerIn == null || !playerIn.isCreative()){
					context.getItem().shrink(1);
				}
			}
			return ActionResultType.SUCCESS;
		}

		//Make a new mechanism block
		if(world.getBlockState(pos.offset(context.getFace())).isReplaceable(new BlockItemUseContext(context))){
			if(!playerIn.isCreative()){
				playerIn.getHeldItem(context.getHand()).shrink(1);
			}

			world.setBlockState(pos.offset(context.getFace()), CRBlocks.mechanism.getDefaultState(), 3);
			te = world.getTileEntity(pos.offset(context.getFace()));
			if(te instanceof MechanismTileEntity){
				RotaryUtil.increaseMasterKey(true);
				((MechanismTileEntity) te).setMechanism(6, MechanismTileEntity.MECHANISMS.get(inverted ? 3 : 2), type, context.getFace().getAxis(), true);
			}
		}

		return ActionResultType.SUCCESS;
	}
}
