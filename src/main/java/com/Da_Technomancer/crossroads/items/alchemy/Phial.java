package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

public class Phial extends AbstractGlassware{

	private static final ModelResourceLocation LOCAT_GLASS = new ModelResourceLocation(Main.MODID + ":phial_glass", "inventory");
	private static final ModelResourceLocation LOCAT_CRYSTAL = new ModelResourceLocation(Main.MODID + ":phial_crystal", "inventory");
	
	public Phial(){
		String name = "phial";
		maxStackSize = 1;
		hasSubtypes = true;
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.toClientRegister.put(Pair.of((Item) this, 0), LOCAT_GLASS);
		ModItems.toClientRegister.put(Pair.of((Item) this, 1), LOCAT_CRYSTAL);
	}

	@Override
	public int getCapacity(){
		return 25;
	}
	
	@Override
	public String getTranslationKey(ItemStack stack){
		return stack.getMetadata() == 1 ? "item.phial_cryst" : "item.phial_glass";
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		Triple<ReagentMap, Double, Integer> contents = getReagants(playerIn.getHeldItem(hand));
		if(contents.getRight() > 0){
			if(!worldIn.isRemote){
				double temp = (contents.getMiddle() / contents.getRight()) + 273D;
				for(IReagent type : contents.getLeft().keySet()){
					int qty = contents.getLeft().getQty(type);
					if(qty > 0){
						type.onRelease(worldIn, pos, qty, temp, type.getPhase(temp), contents.getLeft());
					}
				}
				if(!playerIn.isCreative()){
					setReagents(playerIn.getHeldItem(hand), new ReagentMap(), 0);
				}
			}
			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}
}
