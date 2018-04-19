package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.EnumSolventType;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public abstract class AbstractGlassware extends Item{

	public abstract double getCapacity();

	public static int getColorRGB(ItemStack stack){
		if(!(stack.getItem() instanceof AbstractGlassware)){
			return -1;
		}
		
		Triple<ReagentStack[], Double, Double> info = ((AbstractGlassware) stack.getItem()).getReagants(stack);
		
		double r = 0;
		double g = 0;
		double b = 0;
		double a = 0;
		double amount = info.getRight();
		
		if(amount <= AlchemyCore.MIN_QUANTITY){
			return stack.getMetadata() == 1 ? 0xFFD0D0FF : 0xFFD0D0D0;
		}
		
		ReagentStack[] reags = info.getLeft();
		double temp = info.getMiddle() / amount - 273D;
		
		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			if(reags[i] != null){
				Color color = reags[i].getType().getColor(reags[i].getPhase(temp));
				r += reags[i].getAmount() * (double) color.getRed();
				g += reags[i].getAmount() * (double) color.getGreen();
				b += reags[i].getAmount() * (double) color.getBlue();
				a += reags[i].getAmount() * (double) color.getAlpha();
			}
		}
		return new Color((int) (r / amount), (int) (g / amount), (int) (b / amount), (int) (a / amount)).getRGB();
	}
	
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
			heat = nbt.getDouble("he");
			totalAmount = nbt.getDouble("am");
			double temp = totalAmount == 0 ? 0 : (heat / totalAmount) - 273D;
			boolean[] solvents = new boolean[EnumSolventType.values().length];

			for(int i = 0; i < solvents.length; i++){
				solvents[i] = nbt.getBoolean(i + "_solv");
			}

			for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
				if(nbt.hasKey(i + "_am")){
					reagents[i] = new ReagentStack(AlchemyCore.REAGENTS[i], nbt.getDouble(i + "_am"));
					reagents[i].updatePhase(temp);
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

		boolean[] solvents = new boolean[EnumSolventType.values().length];

		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			ReagentStack reag = reagents[i];
			if(reag == null){
				nbt.removeTag(i + "_am");
			}else{
				nbt.setDouble(i + "_am", reag.getAmount());

				IReagent type = reag.getType();
				solvents[EnumSolventType.AQUA_REGIA.ordinal()] |= i == 11;//Aqua regia is a special case where it works no matter the phase, but ONLY works at all if a polar solvent is present. 

				if(type.getMeltingPoint() <= temp && type.getBoilingPoint() > temp && type.solventType() != null){
					solvents[type.solventType().ordinal()] = true;
				}
			}
		}

		solvents[EnumSolventType.AQUA_REGIA.ordinal()] &= solvents[EnumSolventType.POLAR.ordinal()];

		for(int i = 0; i < solvents.length; i++){
			nbt.setBoolean(i + "_solv", solvents[i]);
		}

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
