package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.tileentities.beams.BeamExtractorTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BeamExtractor extends BeamBlock{

	public BeamExtractor(){
		super("beam_extractor");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new BeamExtractorTileEntity();
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof BeamExtractorTileEntity){
			InventoryHelper.dropInventoryItems(worldIn, pos, (BeamExtractorTileEntity) te);
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ) && !worldIn.isRemote){
			playerIn.openGui(Main.instance, GuiHandler.BEAM_EXTRACTOR_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Produces beams from various items");
		tooltip.add("Who knew salt had magic powers?");
	}
}
