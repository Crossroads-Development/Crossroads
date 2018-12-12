package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.TeslaCoilTileEntity;
import com.Da_Technomancer.crossroads.tileentities.alchemy.TeslaCoilTopTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class TeslaCoilTop extends BlockContainer{

	public final TeslaCoilVariants variant;

	public TeslaCoilTop(TeslaCoilVariants variant){
		super(Material.IRON);
		this.variant = variant;
		String name = "tesla_coil_top_" + variant.toString();
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(2);
		setSoundType(SoundType.METAL);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if(EssentialsConfig.isWrench(heldItem, worldIn.isRemote)){
			TileEntity te = worldIn.getTileEntity(pos);
			if(!worldIn.isRemote && te instanceof TeslaCoilTopTileEntity){
				((TeslaCoilTopTileEntity) te).wrench(heldItem, playerIn);
			}
			return true;
		}
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face){
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add("Range: " + variant.range);
		tooltip.add("FE per Jolt: " + variant.joltAmt);
		tooltip.add("Loss: " + (100 - variant.efficiency) + "%");
		if(variant == TeslaCoilVariants.ATTACK){
			tooltip.add("Cannot transfer power. Attacks nearby entities with electric shocks");
		}
		if(variant.joltAmt > TeslaCoilTileEntity.CAPACITY){
			tooltip.add("Requires a Leyden Jar installed in the Tesla Coil");
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new TeslaCoilTopTileEntity();
	}

	public enum TeslaCoilVariants{

		NORMAL(1_000, 8, 95),
		ATTACK(1_000, 8, 0),
		DISTANCE(1_000, 32, 95),
		INTENSITY(10_000, 8, 95),
		EFFICIENCY(1_000, 8, 100);

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
