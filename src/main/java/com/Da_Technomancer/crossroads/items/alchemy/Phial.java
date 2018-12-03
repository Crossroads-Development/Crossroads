package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
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
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		ReagentMap contents = getReagants(playerIn.getHeldItem(hand));
		if(contents.getTotalQty() != 0){
			if(!worldIn.isRemote){
				double temp = contents.getTempC();
				for(IReagent type : contents.keySet()){
					int qty = contents.getQty(type);
					if(qty != 0){
						type.onRelease(worldIn, pos, qty, temp, type.getPhase(temp), contents);
					}
				}
				if(!playerIn.isCreative()){
					setReagents(playerIn.getHeldItem(hand), new ReagentMap());
				}
			}
			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}
}
