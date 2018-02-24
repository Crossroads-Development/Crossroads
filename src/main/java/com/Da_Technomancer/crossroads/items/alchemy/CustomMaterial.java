package com.Da_Technomancer.crossroads.items.alchemy;

import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CustomMaterial extends Item{
	

	public CustomMaterial(){
		String name = "custom_material";
		maxStackSize = 1;
		hasSubtypes = true;
		setUnlocalizedName(name);
		setRegistryName(name);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}
	
	public static ItemStack createCrystal(ReagentStack[] reagents, double amount){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("sulf", reagents[3] == null ? 0 : (int) Math.round(100D * reagents[3].getAmount() / amount));
		nbt.setInteger("qsil", reagents[13] == null ? 0 : (int) Math.round(100D * reagents[13].getAmount() / amount));
		nbt.setInteger("salt", reagents[16] == null ? 0 : (int) Math.round(100D * reagents[16].getAmount() / amount));
		nbt.setInteger("phel", reagents[0] == null ? 0 : (int) Math.round(100D * reagents[0].getAmount() / amount));
		nbt.setInteger("aeth", reagents[1] == null ? 0 : (int) Math.round(100D * reagents[1].getAmount() / amount));
		nbt.setInteger("adam", reagents[2] == null ? 0 : (int) Math.round(100D * reagents[2].getAmount() / amount));
		ItemStack stack = new ItemStack(ModItems.customMaterial, 1);
		stack.setTagCompound(nbt);
		return stack;
	}
	

	/**
	 * This is not in a creative tab due to creative giving a version that has no NBT
	 */
	@Override
	protected boolean isInCreativeTab(CreativeTabs targetTab){
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		if(stack.hasTagCompound()){
			NBTTagCompound nbt = stack.getTagCompound();
			tooltip.add("Sulfur Content: " + nbt.getInteger("sulf") + "%");
			tooltip.add("Quicksilver Content: " + nbt.getInteger("qsil") + "%");
			tooltip.add("Salt Content: " + nbt.getInteger("salt") + "%");
			tooltip.add("Phelostogen Content: " + nbt.getInteger("phel") + "%");
			tooltip.add("Aether Content: " + nbt.getInteger("aeth") + "%");
			tooltip.add("Adamant Content: " + nbt.getInteger("adam") + "%");
		}else{
			tooltip.add("Sample Crystal");
		}
	}
}
