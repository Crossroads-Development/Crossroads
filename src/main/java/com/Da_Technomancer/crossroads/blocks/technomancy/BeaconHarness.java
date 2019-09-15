package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.technomancy.EntropySavedData;
import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.tileentities.technomancy.BeaconHarnessTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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

public class BeaconHarness extends BeamBlock{

	public BeaconHarness(){
		super("beacon_harness", Material.GLASS);
		setHardness(0.5F);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new BeaconHarnessTileEntity();
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(worldIn.isBlockPowered(pos) && worldIn.getTileEntity(pos) instanceof BeaconHarnessTileEntity){
			((BeaconHarnessTileEntity) worldIn.getTileEntity(pos)).trigger();
		}
	}
	@Override
	public int getLightOpacity(BlockState state){
		return 15;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Produces massive beams when stabilized with a beam of the primary color missing from the output");
		tooltip.add(String.format("Produces %1$.3f%% entropy/tick while running", EntropySavedData.getPercentage(BeaconHarnessTileEntity.FLUX)));
		tooltip.add("It's balanced because it requires nether stars.");
	}

	@Override
	public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction blockFaceClickedOn, BlockRayTraceResult hit, int meta, LivingEntity placer){
		return getDefaultState();
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

	//The following three methods are indeed needed as they override the overrides in BeamBlock
	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState();
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this);
	}

	@Override
	public int getMetaFromState(BlockState state){
		return 0;
	}
}
