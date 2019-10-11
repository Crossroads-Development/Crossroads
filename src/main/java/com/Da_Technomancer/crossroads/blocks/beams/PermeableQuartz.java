package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.beams.IBeamTransparent;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class PermeableQuartz extends Block implements IBeamTransparent{

	public PermeableQuartz(){
		super(Material.ROCK);
		String name = "permeable_quartz";
		setRegistryName(name);
		setTranslationKey(name);
		setHardness(4F);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Permeable to beams");
		tooltip.add("Safe for decoration");
	}
}
