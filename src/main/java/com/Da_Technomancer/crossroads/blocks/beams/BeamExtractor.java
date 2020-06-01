package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.beams.BeamExtractorTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

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
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		if(newState.getBlock() != this){
			InventoryHelper.dropInventoryItems(world, pos, (IInventory) world.getTileEntity(pos));
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!super.onBlockActivated(state, worldIn, pos, playerIn, hand, hit).isSuccess() && !worldIn.isRemote){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof INamedContainerProvider){
				NetworkHooks.openGui((ServerPlayerEntity) playerIn, (INamedContainerProvider) te, pos);
			}
		}
		return ActionResultType.SUCCESS;
	}


	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_extractor.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.beam_extractor.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
