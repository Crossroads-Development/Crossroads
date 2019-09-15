package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.technomancy.EntropySavedData;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.AbstractStabilizerTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.StabilizerElectricTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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

public class FluxStabilizerElectric extends ContainerBlock{

	private final boolean crystal;

	public FluxStabilizerElectric(boolean crystal){
		super(Material.IRON);
		this.crystal = crystal;
		String name = crystal ? "flux_stabilizer_crystal_electric" : "flux_stabilizer_electric";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setHardness(3);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
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
		return new StabilizerElectricTileEntity().setCrystal(crystal);
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
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add(String.format("Destroys up to %1$.3f%% entropy/tick with FE", EntropySavedData.getPercentage(AbstractStabilizerTileEntity.DRAIN_CAP)));
		tooltip.add(String.format("-%1$.3f%% entropy per %2$dFE", EntropySavedData.getPercentage(1), FluxUtil.getFePerFlux(true)));
		tooltip.add("Drains energy at " + AbstractStabilizerTileEntity.DRAIN_CAP * FluxUtil.getFePerFlux(true) + "FE/t regardless of work");
		int limit = crystal ? EntropySavedData.Severity.DESTRUCTIVE.getLowerBound() : EntropySavedData.Severity.HARMFUL.getLowerBound();
		tooltip.add(String.format("Destroyed when entropy reaches %1$d%%", limit));
	}
}
