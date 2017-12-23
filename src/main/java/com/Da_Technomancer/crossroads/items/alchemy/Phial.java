package com.Da_Technomancer.crossroads.items.alchemy;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
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

public class Phial extends AbstractGlassware{

	private static final ModelResourceLocation LOCAT_GLASS = new ModelResourceLocation(Main.MODID + ":phial_glass", "inventory");
	private static final ModelResourceLocation LOCAT_CRYSTAL = new ModelResourceLocation(Main.MODID + ":phial_crystal", "inventory");
	
	public Phial(){
		String name = "phial";
		maxStackSize = 1;
		hasSubtypes = true;
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.tabCrossroads);
		ModItems.toRegister.add(this);
		ModItems.toClientRegister.put(Pair.of((Item) this, 0), LOCAT_GLASS);
		ModItems.toClientRegister.put(Pair.of((Item) this, 1), LOCAT_CRYSTAL);
	}

	@Override
	public double getCapacity(){
		return 25D;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack){
		return stack.getMetadata() == 1 ? "item.phial_cryst" : "item.phial_glass";
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		Triple<ReagentStack[], Double, Double> contents = getReagants(playerIn.getHeldItem(hand));
		if(contents.getRight() > 0){
			if(!worldIn.isRemote){
				double temp = (contents.getMiddle() / contents.getRight()) + 273D;
				for(ReagentStack r : contents.getLeft()){
					if(r != null){
						r.getType().onRelease(worldIn, pos, r.getAmount(), r.getPhase(temp));
					}
				}
				if(!playerIn.isCreative()){
					setReagents(playerIn.getHeldItem(hand), new ReagentStack[AlchemyCore.REAGENT_COUNT], 0, 0);
				}
			}
			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}
}
