package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.CrossroadsConfig;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.StirlingEngineTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class StirlingEngine extends ContainerBlock{

	public StirlingEngine(){
		super(Material.IRON);
		String name = "stirling_engine";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setHardness(3);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new StirlingEngineTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("I: 200");
		tooltip.add("Moves 5°C/t for every 100°C of temperature difference");
		tooltip.add("Produces " + ((ForgeConfigSpec.DoubleValue) CrossroadsConfig.stirlingMultiplier).get() + "J for every °C moved per 100°C of temperature difference");
		tooltip.add("Stops producing power once it reaches " + ((ForgeConfigSpec.DoubleValue) CrossroadsConfig.stirlingSpeedLimit).get() + "rad/s");
	}
}
