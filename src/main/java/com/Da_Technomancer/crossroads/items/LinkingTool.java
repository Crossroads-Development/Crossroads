package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class LinkingTool extends Item{

	public LinkingTool(){
		String name = "linking_tool";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setMaxStackSize(1);
		CrossroadsItems.toRegister.add(this);
		CrossroadsItems.itemAddQue(this);
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.IBlockAccess world, BlockPos pos, PlayerEntity player){
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		if(stack.hasTag() && stack.getTag().contains(ILinkTE.POS_NBT)){
			BlockPos linked = BlockPos.fromLong(stack.getTag().getLong(ILinkTE.POS_NBT));
			int dim = stack.getTag().getInt(ILinkTE.DIM_NBT);
			tooltip.add("Linking from (" + linked.getX() + ", " + linked.getY() + ", " + linked.getZ() + "); Dim " + dim);
		}else{
			tooltip.add("Not linking");
		}
		tooltip.add("Links machines with a right click, in order from->to. Clear links with a shift-right click");
	}
}
