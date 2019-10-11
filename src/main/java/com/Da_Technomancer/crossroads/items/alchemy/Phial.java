package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyUtil;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Phial extends AbstractGlassware{

	private final boolean crystal;

	public Phial(boolean crystal){
		this.crystal = crystal;
		String name = "phial_" + (crystal ? "cryst" : "glass");
		maxStackSize = 1;
		hasSubtypes = true;
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		CRItems.toRegister.add(this);
		CRItems.itemAddQue(this);
	}

	@Override
	public int getCapacity(){
		return 20;
	}

	@Override
	public boolean isCrystal(){
		return crystal;
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity playerIn, World worldIn, BlockPos pos, Hand hand, Direction side, BlockRayTraceResult hit){
		ReagentMap contents = getReagants(playerIn.getHeldItem(hand));
		if(contents.getTotalQty() != 0){
			if(!worldIn.isRemote){
				AlchemyUtil.releaseChemical(worldIn, pos, contents);
				if(!playerIn.isCreative()){
					setReagents(playerIn.getHeldItem(hand), new ReagentMap());
				}
			}
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}
}
