package com.Da_Technomancer.crossroads.blocks.electric;

import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTileEntity;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTopTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class TeslaCoilTop extends ContainerBlock{

	public final TeslaCoilVariants variant;

	public TeslaCoilTop(TeslaCoilVariants variant){
		super(Material.IRON);
		this.variant = variant;
		String name = "tesla_coil_top_" + variant.toString();
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setHardness(2);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if(ILinkTE.isLinkTool(heldItem)){
			TileEntity te = worldIn.getTileEntity(pos);
			if(!worldIn.isRemote && te instanceof TeslaCoilTopTileEntity){
				((TeslaCoilTopTileEntity) te).wrench(heldItem, playerIn);
			}
			return true;
		}
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add("Range: " + variant.range);
		tooltip.add("FE per Jolt: " + variant.joltAmt);
		tooltip.add("Loss: " + (100 - variant.efficiency) + "%");
		if(variant == TeslaCoilVariants.ATTACK){
			tooltip.add("Cannot transfer power. Attacks nearby entities with electric shocks");
		}else if(variant == TeslaCoilVariants.DECORATIVE){
			tooltip.add("Cannot transfer power. Shoots decorative arcs");
		}
		if(variant.joltAmt > TeslaCoilTileEntity.CAPACITY){
			tooltip.add("Requires a Leyden Jar installed in the Tesla Coil");
		}
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new TeslaCoilTopTileEntity();
	}

	public enum TeslaCoilVariants{

		NORMAL(1_000, 8, 95),
		ATTACK(1_000, 6, 0),
		DISTANCE(1_000, 32, 95),
		INTENSITY(10_000, 8, 95),
		EFFICIENCY(1_000, 8, 100),
		DECORATIVE(100, 0, 0);

		public final int joltAmt;
		public final int range;
		public final int efficiency;

		TeslaCoilVariants(int joltAmt, int range, int efficiency){
			this.joltAmt = joltAmt;
			this.range = range;
			this.efficiency = efficiency;
		}

		@Override
		public String toString(){
			return name().toLowerCase();
		}
	}
}
