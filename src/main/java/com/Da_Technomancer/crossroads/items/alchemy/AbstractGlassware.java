package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
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

	private static final String TAG_NAME = "reagents";

	public abstract int getCapacity();

	public static int getColorRGB(ItemStack stack){
		if(!(stack.getItem() instanceof AbstractGlassware)){
			return -1;
		}
		
		Triple<ReagentMap, Double, Integer> info = ((AbstractGlassware) stack.getItem()).getReagants(stack);
		
		int r = 0;
		int g = 0;
		int b = 0;
		int a = 0;
		int amount = info.getRight();
		
		if(amount <= 0){
			return stack.getMetadata() == 1 ? 0xFFD0D0FF : 0xFFD0D0D0;
		}
		
		ReagentMap reags = info.getLeft();
		double temp = HeatUtil.toCelcius(info.getMiddle() / amount);
		
		for(IReagent reag : reags.keySet()){
			int qty = reags.getQty(reag);
			if(qty != 0){
				Color color = reag.getColor(reag.getPhase(temp));
				r += qty * color.getRed();
				g += qty * color.getGreen();
				b += qty * color.getBlue();
				a += qty * color.getAlpha();
			}
		}
		return new Color(r / amount, g / amount, b / amount, a / amount).getRGB();
	}
	
	/**
	 * Cache the result to minimize calls to this method. 
	 * @param stack
	 * @return The contained reagents, heat, and amount. Modifying the returned array does NOT write through to the ItemStack, use the setReagents method. 
	 */
	@Nonnull
	public Triple<ReagentMap, Double, Integer> getReagants(ItemStack stack){
		ReagentMap reagents = new ReagentMap();
		double heat = 0;
		int totalAmount = 0;
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_NAME)){
			NBTTagCompound nbt = stack.getTagCompound().getCompoundTag(TAG_NAME);
			heat = nbt.getDouble("he");

			for(String key : nbt.getKeySet()){
				if(!key.startsWith("qty_")){
					continue;
				}
				String id = key.substring(4);//Remove the qty_ header
				reagents.addReagent(id, nbt.getInteger(key));
				totalAmount += reagents.getQty(id);
			}
		}
		return Triple.of(reagents, heat, totalAmount);
	}

	/**
	 * Call this as little as possible. 
	 * @param stack The stack to store the reagents to
	 * @param reagents The reagents to store
	 * @param heat The heat to store to the phial
	 */
	public void setReagents(ItemStack stack, ReagentMap reagents, double heat){
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}

		NBTTagCompound nbt = new NBTTagCompound();
		stack.getTagCompound().setTag(TAG_NAME, nbt);

		reagents.writeToNBT(nbt);

		nbt.setDouble("he", heat);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		if(!stack.hasTagCompound()){
			return;
		}
		Triple<ReagentMap, Double, Integer> stored = getReagants(stack);

		tooltip.add("Temperature: " + MiscUtil.betterRound(stored.getRight() == 0 ? 0 : HeatUtil.toCelcius(stored.getMiddle() / stored.getRight()), 3) + "°C");

		for(IReagent type : stored.getLeft().keySet()){
			int qty = stored.getLeft().getQty(type);
			if(qty > 0){
				tooltip.add(new ReagentStack(type, qty).toString());
			}
		}

		if(advanced == ITooltipFlag.TooltipFlags.ADVANCED){
			tooltip.add("Debug: Heat: " + stored.getMiddle() + "; Qty: " + stored.getRight());
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
