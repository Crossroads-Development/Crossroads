package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.technomancy.BeaconHarnessTileEntity;
import com.Da_Technomancer.essentials.tileentities.ILinkTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class BeaconHarness extends BeamBlock{

	public BeaconHarness(){
		super("beacon_harness", Properties.create(Material.GLASS).hardnessAndResistance(.5F).lightValue(15));
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new BeaconHarnessTileEntity();
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		TileEntity te;
		if(worldIn.isBlockPowered(pos) && (te = worldIn.getTileEntity(pos)) instanceof BeaconHarnessTileEntity){
			((BeaconHarnessTileEntity) te).trigger();
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Produces massive beams when stabilized with a beam of the primary color missing from the output");
		tooltip.add(String.format("Produces %1$.3f%% entropy/tick while running", EntropySavedData.getPercentage(BeaconHarnessTileEntity.FLUX)));
		tooltip.add("It's balanced because it requires nether stars.");
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


	//The following methods are indeed needed as they override the overrides in BeamBlock
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){

	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return getDefaultState();
	}
}
