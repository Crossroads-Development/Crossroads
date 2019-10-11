package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReagentTankTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ReagentTank extends ContainerBlock{

	private final boolean crystal;

	public ReagentTank(boolean crystal){
		super(Material.GLASS);
		this.crystal = crystal;
		String name = (crystal ? "crystal_" : "") + "reagent_tank";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(.5F);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setSoundType(SoundType.GLASS);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new ReagentTankTileEntity(!crystal);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		if(stack.hasTag()){
			double am = 0;
			for(String key : stack.getTag().getKeySet()){
				if(!key.startsWith("qty_")){
					continue;
				}
				int qty = stack.getTag().getInt(key);
				am += qty;
				tooltip.add(new ReagentStack(AlchemyCore.REAGENTS.get(key.substring(4)), qty).toString());
			}

			double temp = MiscUtil.betterRound(stack.getTag().getDouble("he") / am, 3);
			tooltip.add("Temp: " + MiscUtil.betterRound(HeatUtil.toCelcius(temp), 3) + "°C (" + temp + "K)");
		}
	}

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te, ItemStack stackIn){
		if(!(te instanceof ReagentTankTileEntity)){
			super.harvestBlock(worldIn, player, pos, state, te, stackIn);
		}else{
			player.addExhaustion(0.005F);
			ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state));
			stack.put(((ReagentTankTileEntity) te).getContentNBT());
			spawnAsEntity(worldIn, pos, stack);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		if(stack.hasTag()){
			ReagentTankTileEntity te = (ReagentTankTileEntity) world.getTileEntity(pos);
			te.writeContentNBT(stack.getTag());
		}
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos){
		TileEntity te = worldIn.getTileEntity(pos);
		return te instanceof ReagentTankTileEntity ? ((ReagentTankTileEntity) te).getRedstone() : 0;
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state){
		return true;
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof ReagentTankTileEntity){
			if(!worldIn.isRemote){
				playerIn.setHeldItem(hand, ((ReagentTankTileEntity) te).rightClickWithItem(playerIn.getHeldItem(hand), playerIn.isSneaking(), playerIn, hand));
			}
			return true;
		}
		return false;
	}
}
