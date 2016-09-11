package com.Da_Technomancer.crossroads.blocks;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.SlottedChestTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SlottedChest extends BlockContainer{

	public SlottedChest(){
		super(Material.WOOD);
		String name = "slottedChest";
		setSoundType(SoundType.WOOD);
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(2);
		this.setCreativeTab(ModItems.tabCrossroads);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new SlottedChestTileEntity();
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState blockstate){
		SlottedChestTileEntity te = (SlottedChestTileEntity) world.getTileEntity(pos);
		InventoryHelper.dropInventoryItems(world, pos, te.iInv);
		super.breakBlock(world, pos, blockstate);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			playerIn.openGui(Main.instance, GuiHandler.SLOTTEDCHEST_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}
}
