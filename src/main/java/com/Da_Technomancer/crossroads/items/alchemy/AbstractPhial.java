package com.Da_Technomancer.crossroads.items.alchemy;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCraftingManager;
import com.Da_Technomancer.crossroads.API.alchemy.IReagentType;
import com.Da_Technomancer.crossroads.API.alchemy.Reagent;
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

public abstract class AbstractPhial extends Item{

	public abstract double getCapacity();

	/**
	 * Cache the result to minimize calls to this method. 
	 * @param stack
	 * @return The contained reagents. Modifying the returned array does NOT write through to the ItemStack, use the setReagents method. 
	 */
	@Nonnull
	public Reagent[] getReagants(ItemStack stack){
		Reagent[] reagents = new Reagent[AlchemyCraftingManager.RESERVED_REAGENT_COUNT + AlchemyCraftingManager.DYNAMIC_REAGENT_COUNT];
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt != null){
			boolean hasPolar = nbt.getBoolean("po");
			boolean hasNonPolar = nbt.getBoolean("np");
			boolean hasAquaRegia = nbt.getBoolean("ar");
			for(int i = 0; i < AlchemyCraftingManager.RESERVED_REAGENT_COUNT + AlchemyCraftingManager.DYNAMIC_REAGENT_COUNT ; i++){
				if(nbt.hasKey(i + "_am")){
					reagents[i] = new Reagent(AlchemyCraftingManager.REAGENTS[i], nbt.getDouble(i + "_am"), nbt.getDouble(i + "te"));
					reagents[i].updatePhase(hasPolar, hasNonPolar, hasAquaRegia);
				}
			}
		}
		return reagents;
	}

	/**
	 * Call this as little as possible. 
	 * @param stack The stack to store the reagents to
	 * @param reagents The reagents to store
	 */
	public void setReagents(ItemStack stack, Reagent[] reagents){
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound nbt = stack.getTagCompound();
		boolean hasPolar = false;
		boolean hasNonPolar = false;
		boolean hasAquaRegia = false;
		for(int i = 0; i < AlchemyCraftingManager.RESERVED_REAGENT_COUNT + AlchemyCraftingManager.DYNAMIC_REAGENT_COUNT ; i++){
			Reagent reag = reagents[i];
			if(reag == null){
				continue;
			}

			IReagentType type = reag.getType();


			if(i == 11){
				hasAquaRegia = true;
			}
			if(type.getMeltingPoint() <= reag.getTemp() && type.getBoilingPoint() > reag.getTemp()){
				SolventType solv = type.solventType();
				hasPolar |= solv == SolventType.POLAR || solv == SolventType.MIXED_POLAR;
				hasNonPolar |= solv == SolventType.NON_POLAR || solv == SolventType.MIXED_POLAR;
				hasAquaRegia |= solv == SolventType.AQUA_REGIA;
			}

			hasAquaRegia &= hasPolar;

			nbt.setDouble(i + "_am", reag.getAmount());
			nbt.setDouble(i + "_te", reag.getTemp());
		}
		nbt.setBoolean("po", hasPolar);
		nbt.setBoolean("np", hasNonPolar);
		nbt.setBoolean("ar", hasAquaRegia);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null){
			return;
		}

		for(int i = 0; i < AlchemyCraftingManager.RESERVED_REAGENT_COUNT + AlchemyCraftingManager.DYNAMIC_REAGENT_COUNT ; i++){
			if(nbt.hasKey(i + "_am")){
				tooltip.add(new Reagent(AlchemyCraftingManager.REAGENTS[i], nbt.getDouble(i + "_am"), nbt.getDouble(i + "te")).toString());
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list){
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
	}
}
