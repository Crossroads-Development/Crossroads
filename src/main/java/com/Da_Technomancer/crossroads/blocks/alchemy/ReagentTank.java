package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReagentTankTileEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;

public class ReagentTank extends BlockContainer{

	public ReagentTank(){
		super(Material.GLASS);
		String name = "reagent_tank";
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(.5F);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setSoundType(SoundType.GLASS);
		ModBlocks.toRegister.add(this);
		Item item = new ItemBlock(this){
			@Override
			public String getUnlocalizedName(ItemStack stack){
				return stack.getMetadata() == 1 ? "tile." + name + "_cryst" : "tile." + name + "_glass";
			}
			
			@Override
			public int getMetadata(int damage){
				return damage;
			}
		}.setMaxDamage(0).setHasSubtypes(true);
		item.setRegistryName(name);
		ModItems.toRegister.add(item);
		ModItems.toClientRegister.put(Pair.of(item, 0), new ModelResourceLocation(Main.MODID + ':' + name + "_glass", "inventory"));
		ModItems.toClientRegister.put(Pair.of(item, 1), new ModelResourceLocation(Main.MODID + ':' + name + "_cryst", "inventory"));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new ReagentTankTileEntity((meta & 1) == 0);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer(){
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return (state.getValue(Properties.CRYSTAL) ? 1 : 0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.CRYSTAL, (meta & 1) == 1);
	}

	@Override
	public int damageDropped(IBlockState state){
		return state.getValue(Properties.CRYSTAL) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.CRYSTAL);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		if(stack.hasTagCompound()){
			double am = 0;
			for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
				if(stack.getTagCompound().hasKey(i + "_am")){
					double amount = stack.getTagCompound().getDouble(i + "_am");
					am += amount;
					tooltip.add(new ReagentStack(AlchemyCore.REAGENTS[i], amount).toString());
				}
			}

			tooltip.add("Temp: " + MiscOp.betterRound(stack.getTagCompound().getDouble("heat") / am - 273D, 3));
		}
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stackIn){
		if(!(te instanceof ReagentTankTileEntity) || ((ReagentTankTileEntity) te).getAmount() <= AlchemyCore.MIN_QUANTITY){
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
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getStateFromMeta(meta);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof ReagentTankTileEntity){
			if(!worldIn.isRemote){
				playerIn.setHeldItem(hand, ((ReagentTankTileEntity) te).rightClickWithItem(playerIn.getHeldItem(hand), playerIn.isSneaking()));
			}
			return true;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list){
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
	}
}
