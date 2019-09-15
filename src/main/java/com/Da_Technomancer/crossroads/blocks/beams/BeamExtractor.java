package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.tileentities.beams.BeamExtractorTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
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
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new BeamExtractorTileEntity();
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, BlockState state){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof BeamExtractorTileEntity){
			InventoryHelper.dropInventoryItems(worldIn, pos, (BeamExtractorTileEntity) te);
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ) && !worldIn.isRemote){
			playerIn.openGui(Crossroads.instance, GuiHandler.BEAM_EXTRACTOR_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}


	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Produces beams from various items");
		tooltip.add("Who knew salt had magic powers?");
	}
}
