package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReagentTankTileEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ReagentTank extends BlockContainer{

	private final boolean crystal;

	public ReagentTank(boolean crystal){
		super(Material.GLASS);
		this.crystal = crystal;
		String name = (crystal ? "crystal_" : "") + "reagent_tank";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(.5F);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setSoundType(SoundType.GLASS);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new ReagentTankTileEntity(!crystal);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		if(stack.hasTagCompound()){
			double am = 0;
			for(String key : stack.getTagCompound().getKeySet()){
				if(!key.startsWith("qty_")){
					continue;
				}
				int qty = stack.getTagCompound().getInteger(key);
				am += qty;
				tooltip.add(new ReagentStack(AlchemyCore.REAGENTS.get(key.substring(4)), qty).toString());
			}

			double temp = MiscUtil.betterRound(stack.getTagCompound().getDouble("he") / am, 3);
			tooltip.add("Temp: " + MiscUtil.betterRound(HeatUtil.toCelcius(temp), 3) + "°C (" + temp + "K)");
		}
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stackIn){
		if(!(te instanceof ReagentTankTileEntity)){
			super.harvestBlock(worldIn, player, pos, state, te, stackIn);
		}else{
			player.addExhaustion(0.005F);
			ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state));
			stack.setTagCompound(((ReagentTankTileEntity) te).getContentNBT());
			spawnAsEntity(worldIn, pos, stack);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		if(stack.hasTagCompound()){
			ReagentTankTileEntity te = (ReagentTankTileEntity) world.getTileEntity(pos);
			te.writeContentNBT(stack.getTagCompound());
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos){
		TileEntity te = worldIn.getTileEntity(pos);
		return te instanceof ReagentTankTileEntity ? ((ReagentTankTileEntity) te).getRedstone() : 0;
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state){
		return true;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
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
