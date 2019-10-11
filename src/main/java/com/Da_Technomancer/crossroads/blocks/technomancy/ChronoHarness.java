package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.technomancy.EntropySavedData;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.ChronoHarnessTileEntity;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ChronoHarness extends ContainerBlock{

	public ChronoHarness(){
		super(Material.IRON);
		String name = "chrono_harness";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(2);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().with(EssentialsProperties.REDSTONE_BOOL, false));
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new ChronoHarnessTileEntity();
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, EssentialsProperties.REDSTONE_BOOL);
	}


	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(EssentialsProperties.REDSTONE_BOOL, meta == 1);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(worldIn.isBlockPowered(pos)){
			if(!state.get(EssentialsProperties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.with(EssentialsProperties.REDSTONE_BOOL, true), 2);
			}
		}else if(state.get(EssentialsProperties.REDSTONE_BOOL)){
			worldIn.setBlockState(pos, state.with(EssentialsProperties.REDSTONE_BOOL, false), 2);
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, worldIn, pos, this, pos);
	}

	@Override
	public int getMetaFromState(BlockState state){
		return state.get(EssentialsProperties.REDSTONE_BOOL) ? 1 : 0;
	}
	
	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}
	
	@Override
	public boolean isFullCube(BlockState state){
		return false;
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
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Produces FE for free, but creates entropy as a byproduct");
		tooltip.add(String.format("Produces %1$dFE/t and %2$.3f%% entropy/tick", ChronoHarnessTileEntity.POWER, EntropySavedData.getPercentage(ChronoHarnessTileEntity.POWER / FluxUtil.getFePerFlux(true))));
		tooltip.add("Disabled by a redstone signal");
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		//TODO
		return BlockFaceShape.UNDEFINED;
	}
}
