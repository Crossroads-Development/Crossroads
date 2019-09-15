package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public abstract class AbstractGlassware extends Item{

	private static final String TAG_NAME = "reagents";

	public abstract int getCapacity();

	public abstract boolean isCrystal();

	public static int getColorRGB(ItemStack stack){
		if(!(stack.getItem() instanceof AbstractGlassware)){
			return -1;
		}
		
		ReagentMap reags = ((AbstractGlassware) stack.getItem()).getReagants(stack);
		
		int r = 0;
		int g = 0;
		int b = 0;
		int a = 0;
		int amount = reags.getTotalQty();
		
		if(amount <= 0){
			return ((AbstractGlassware) stack.getItem()).isCrystal() ? 0xFFD0D0FF : 0xFFD0D0D0;
		}

		double temp = reags.getTempC();
		
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
	 * @param stack The glassware itemstack
	 * @return The contained reagents. Modifying the returned array does NOT write through to the ItemStack, use the setReagents method.
	 */
	@Nonnull
	public ReagentMap getReagants(ItemStack stack){
		return stack.hasTagCompound() ? ReagentMap.readFromNBT(stack.getTagCompound().getCompoundTag(TAG_NAME)) : new ReagentMap();
	}

	/**
	 * Call this as little as possible. 
	 * @param stack The stack to store the reagents to
	 * @param reagents The reagents to store
	 */
	public void setReagents(ItemStack stack, ReagentMap reagents){
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new CompoundNBT());
		}

		CompoundNBT nbt = new CompoundNBT();
		stack.getTagCompound().setTag(TAG_NAME, nbt);

		reagents.writeToNBT(nbt);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		if(!stack.hasTagCompound()){
			return;
		}
		ReagentMap stored = getReagants(stack);

		double temp = stored.getTempC();

		tooltip.add("Temperature: " + (stored.getTotalQty() == 0 ? "N/A" : MiscUtil.betterRound(temp, 3) + "°C (" + MiscUtil.betterRound(HeatUtil.toKelvin(temp), 3) + "K)"));

		for(IReagent type : stored.keySet()){
			int qty = stored.getQty(type);
			if(qty > 0){
				tooltip.add(new ReagentStack(type, qty).toString());
			}
		}

		if(advanced == ITooltipFlag.TooltipFlags.ADVANCED){
			tooltip.add("Debug: Heat: " + stored.getHeat() + "; Qty: " + stored.getTotalQty());
		}
	}
}
