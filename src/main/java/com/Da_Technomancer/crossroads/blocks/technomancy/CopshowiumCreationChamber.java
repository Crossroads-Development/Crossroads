package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.technomancy.EntropySavedData;
import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.CopshowiumCreationChamberTileEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class CopshowiumCreationChamber extends BlockContainer{

	public CopshowiumCreationChamber(){
		super(Material.ROCK);
		String name = "copshowium_creation_chamber";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(3);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new CopshowiumCreationChamberTileEntity();
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if(ILinkTE.isLinkTool(heldItem)){
			TileEntity te = worldIn.getTileEntity(pos);
			if(!worldIn.isRemote && te instanceof ILinkTE){
				((ILinkTE) te).wrench(heldItem, playerIn);
			}
		}else{
			playerIn.openGui(Main.instance, GuiHandler.COPSHOWIUM_CHAMBER_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Uses time beams to turn liquids into Molten Copshowium. Liquid will double in volume. Overfilling the chamber will cause it to burst");
		tooltip.add(String.format("Produces %1$.3f%% entropy/ingot produced when using worse liquids", EntropySavedData.getPercentage(CopshowiumCreationChamberTileEntity.FLUX_INGOT)));
		tooltip.add("Can only use worse liquids when entropy is above " + EntropySavedData.Severity.UNSTABLE.getLowerBound() + "%");
	}
}
