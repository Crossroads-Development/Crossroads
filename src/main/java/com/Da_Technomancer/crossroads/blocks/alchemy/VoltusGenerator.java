package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.VoltusGeneratorTileEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class VoltusGenerator extends BlockContainer{

	public VoltusGenerator(){
		super(Material.IRON);
		String name = "voltus_generator";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(.5F);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setSoundType(SoundType.METAL);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new VoltusGeneratorTileEntity();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Converts Voltus into FE");
		tooltip.add("Consumes: " + ModConfig.getConfigDouble(ModConfig.voltusUsage, true) + " Voltus per 1000FE");
	}
}
