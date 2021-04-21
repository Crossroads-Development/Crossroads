package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyUtil;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

public class Phial extends AbstractGlassware{

	public Phial(boolean crystal){
		super(GlasswareTypes.PHIAL, crystal);
		String name = "phial_" + (crystal ? "cryst" : "glass");
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context){
		ReagentMap contents = getReagants(context.getItemInHand());
		if(contents.getTotalQty() != 0){
			if(!context.getLevel().isClientSide){
				AlchemyUtil.releaseChemical(context.getLevel(), context.getClickedPos(), contents);
				if(context.getPlayer() == null || !context.getPlayer().isCreative()){
					setReagents(context.getItemInHand(), new ReagentMap());
				}
			}
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}
}
