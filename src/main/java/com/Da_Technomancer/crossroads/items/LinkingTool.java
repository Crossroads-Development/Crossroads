package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
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
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setMaxStackSize(1);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.IBlockAccess world, BlockPos pos, EntityPlayer player){
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey(ILinkTE.POS_NBT)){
			BlockPos linked = BlockPos.fromLong(stack.getTagCompound().getLong(ILinkTE.POS_NBT));
			int dim = stack.getTagCompound().getInteger(ILinkTE.DIM_NBT);
			tooltip.add("Linking from (" + linked.getX() + ", " + linked.getY() + ", " + linked.getZ() + "); Dim " + dim);
		}else{
			tooltip.add("Not linking");
		}
		tooltip.add("Links machines with a right click, in order from->to. Clear links with a shift-right click");
	}
}
