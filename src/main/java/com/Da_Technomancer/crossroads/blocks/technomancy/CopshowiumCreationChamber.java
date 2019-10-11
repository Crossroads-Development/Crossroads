package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.technomancy.EntropySavedData;
import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.CopshowiumCreationChamberTileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class CopshowiumCreationChamber extends ContainerBlock{

	public CopshowiumCreationChamber(){
		super(Material.ROCK);
		String name = "copshowium_creation_chamber";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setHardness(3);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new CopshowiumCreationChamberTileEntity();
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if(ILinkTE.isLinkTool(heldItem)){
			TileEntity te = worldIn.getTileEntity(pos);
			if(!worldIn.isRemote && te instanceof ILinkTE){
				((ILinkTE) te).wrench(heldItem, playerIn);
			}
		}else{
			playerIn.openGui(Crossroads.instance, GuiHandler.COPSHOWIUM_CHAMBER_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Uses time beams to turn liquids into Molten Copshowium. Liquid will double in volume. Overfilling the chamber will cause it to burst");
		tooltip.add(String.format("Produces %1$.3f%% entropy/ingot produced when using worse liquids", EntropySavedData.getPercentage(CopshowiumCreationChamberTileEntity.FLUX_INGOT)));
		tooltip.add("Can only use worse liquids when entropy is above " + EntropySavedData.Severity.UNSTABLE.getLowerBound() + "%");
	}
}
