package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.api.alchemy.IReagent;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public abstract class AbstractGlassware extends Item{

	private static final String TAG_NAME = "reagents";

	protected final GlasswareTypes type;
	protected final boolean isCrystal;

	protected AbstractGlassware(GlasswareTypes type, boolean isCrystal){
		super(new Properties().stacksTo(1));
		this.type = type;
		this.isCrystal = isCrystal;
	}

	public int getCapacity(){
		return type.capacity;
	}

	public boolean isCrystal(){
		return isCrystal;
	}

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

		for(IReagent reag : reags.keySetReag()){
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
		return stack.hasTag() ? ReagentMap.readFromNBT(stack.getTag().getCompound(TAG_NAME)) : new ReagentMap();
	}

	/**
	 * Call this as little as possible. 
	 * @param stack The stack to store the reagents to
	 * @param reagents The reagents to store
	 */
	public void setReagents(ItemStack stack, ReagentMap reagents){
		if(!stack.hasTag()){
			stack.setTag(new CompoundTag());
		}

		CompoundTag nbt = new CompoundTag();
		stack.getTag().put(TAG_NAME, nbt);

		reagents.write(nbt);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.boilerplate.alchemy_capacity", getCapacity()));
		if(!stack.hasTag()){
			return;
		}
		ReagentMap stored = getReagants(stack);

		double temp = stored.getTempC();

		if(stored.getTotalQty() == 0){
			tooltip.add(Component.translatable("tt.crossroads.boilerplate.alchemy_empty"));
		}else{
			HeatUtil.addHeatInfo(tooltip, temp, Short.MIN_VALUE);
			int total = 0;
			for(IReagent type : stored.keySetReag()){
				int qty = stored.getQty(type);
				if(qty > 0){
					total++;
					if(total <= 4 || flagIn != TooltipFlag.Default.NORMAL){
						tooltip.add(Component.translatable("tt.crossroads.boilerplate.alchemy_content", type.getName(), qty));
					}else{
						break;
					}
				}
			}
			if(total > 4 && flagIn == TooltipFlag.Default.NORMAL){
				tooltip.add(Component.translatable("tt.crossroads.boilerplate.alchemy_excess", total - 4));
			}
		}
	}

	public GlasswareTypes containerType(){
		return type;
	}

	public enum GlasswareTypes implements StringRepresentable{

		NONE(0, false),
		PHIAL(20, false),
		FLORENCE(100, true),
		SHELL(20, false);

		public final int capacity;
		public final boolean connectToCable;

		private GlasswareTypes(int capacity, boolean cableHeat){
			this.capacity = capacity;
			this.connectToCable = cableHeat;
		}

		@Override
		public String getSerializedName(){
			return name().toLowerCase(Locale.US);
		}
	}
}
