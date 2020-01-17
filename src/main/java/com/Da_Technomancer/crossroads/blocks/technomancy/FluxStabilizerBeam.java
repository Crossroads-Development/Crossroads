package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.technomancy.EntropySavedData;
import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.AbstractStabilizerTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.StabilizerBeamTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class FluxStabilizerBeam extends ContainerBlock{

	private final boolean crystal;

	public FluxStabilizerBeam(boolean crystal){
		super(Material.IRON);
		this.crystal = crystal;
		String name = crystal ? "flux_stabilizer_crystal_beam" : "flux_stabilizer_beam";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setHardness(3);
		setSoundType(SoundType.METAL);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if(ILinkTE.isLinkTool(heldItem)){
			TileEntity te = worldIn.getTileEntity(pos);
			if(!worldIn.isRemote && te instanceof ILinkTE){
				((ILinkTE) te).wrench(heldItem, playerIn);
			}
			return true;
		}
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new StabilizerBeamTileEntity().setCrystal(crystal);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add(String.format("Destroys up to %1$.3f%% entropy/tick with stability", EntropySavedData.getPercentage(AbstractStabilizerTileEntity.DRAIN_CAP)));
		tooltip.add(String.format("-%1$.3f%% entropy per stability", EntropySavedData.getPercentage(1)));
		int limit = crystal ? EntropySavedData.Severity.DESTRUCTIVE.getLowerBound() : EntropySavedData.Severity.HARMFUL.getLowerBound();
		tooltip.add(String.format("Destroyed when entropy reaches %1$d%%", limit));
	}
}
