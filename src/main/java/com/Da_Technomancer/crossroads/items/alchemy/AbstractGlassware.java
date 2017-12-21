package com.Da_Technomancer.crossroads.items.alchemy;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.alchemy.SolventType;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractGlassware extends Item{

	public abstract double getCapacity();

	/**
	 * Cache the result to minimize calls to this method. 
	 * @param stack
	 * @return The contained reagents, heat, and amount. Modifying the returned array does NOT write through to the ItemStack, use the setReagents method. 
	 */
	@Nonnull
	public Triple<ReagentStack[], Double, Double> getReagants(ItemStack stack){
		ReagentStack[] reagents = new ReagentStack[AlchemyCore.REAGENT_COUNT];
		double heat = 0;
		double totalAmount = 0;
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null){
			boolean hasPolar = nbt.getBoolean("po");
			boolean hasNonPolar = nbt.getBoolean("np");
			boolean hasAquaRegia = nbt.getBoolean("ar");
			heat = nbt.getDouble("he");
			totalAmount = nbt.getDouble("am");
			double temp = totalAmount == 0 ? 0 : (heat / totalAmount) - 273D;
			for(int i = 0; i < AlchemyCore.RESERVED_REAGENT_COUNT + AlchemyCore.DYNAMIC_REAGENT_COUNT ; i++){
				if(nbt.hasKey(i + "_am")){
					reagents[i] = new ReagentStack(AlchemyCore.REAGENTS[i], nbt.getDouble(i + "_am"));
					reagents[i].updatePhase(temp, hasPolar, hasNonPolar, hasAquaRegia);
				}
			}
		}
		return Triple.of(reagents, heat, totalAmount);
	}

	/**
	 * Call this as little as possible. 
	 * @param stack The stack to store the reagents to
	 * @param reagents The reagents to store
	 * @param heat The heat to store to the phial
	 * @param amount The total amount of reagent
	 */
	public void setReagents(ItemStack stack, ReagentStack[] reagents, double heat, double amount){
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		
		double temp = amount == 0 ? 0 : (heat / amount) - 273D;		
		NBTTagCompound nbt = stack.getTagCompound();
		boolean hasPolar = false;
		boolean hasNonPolar = false;
		boolean hasAquaRegia = false;
		for(int i = 0; i < AlchemyCore.RESERVED_REAGENT_COUNT + AlchemyCore.DYNAMIC_REAGENT_COUNT ; i++){
			ReagentStack reag = reagents[i];
			if(reag == null){
				nbt.removeTag(i + "_am");
				continue;
			}

			IReagent type = reag.getType();


			if(i == 11){
				hasAquaRegia = true;
			}
			if(type.getMeltingPoint() <= temp && type.getBoilingPoint() > temp){
				SolventType solv = type.solventType();
				hasPolar |= solv == SolventType.POLAR || solv == SolventType.MIXED_POLAR;
				hasNonPolar |= solv == SolventType.NON_POLAR || solv == SolventType.MIXED_POLAR;
				hasAquaRegia |= solv == SolventType.AQUA_REGIA;
			}

			hasAquaRegia &= hasPolar;

			nbt.setDouble(i + "_am", reag.getAmount());
		}
		nbt.setBoolean("po", hasPolar);
		nbt.setBoolean("np", hasNonPolar);
		nbt.setBoolean("ar", hasAquaRegia);
		nbt.setDouble("he", heat);
		nbt.setDouble("am", amount);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null){
			return;
		}
		double amount = nbt.getDouble("am");
		tooltip.add("Temperature: " + (amount == 0 ? 0 : (nbt.getDouble("he") / amount) - 273D) + "°C");
		
		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			if(nbt.hasKey(i + "_am")){
				tooltip.add(new ReagentStack(AlchemyCore.REAGENTS[i], nbt.getDouble(i + "_am")).toString());
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list){
		if(isInCreativeTab(tab)){
			list.add(new ItemStack(this, 1, 0));
			list.add(new ItemStack(this, 1, 1));
		}
	}
}
