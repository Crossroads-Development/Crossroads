package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.API.redstone.RedstoneUtil;
import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.tileentities.RedstoneTransmitterTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class RedstoneTransmitter extends ContainerBlock{

	protected RedstoneTransmitter(){
		super(Material.ROCK);
		String name = "redstone_transmitter";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setHardness(.5F);
		setSoundType(SoundType.STONE);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().with(Properties.COLOR, DyeColor.WHITE));
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(!worldIn.isBlockTickPending(pos, this)){
			int i = -1;

			if(RedstoneDiodeBlock.isDiode(worldIn.getBlockState(fromPos))){
				i = -3;
			}
			worldIn.updateBlockTick(pos, this, 2, i);
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, worldIn, pos, this, pos);
	}

	@Override
	public void tick(BlockState state, World worldIn, BlockPos pos, Random rand){
		TileEntity rawTE = worldIn.getTileEntity(pos);
		if(!(rawTE instanceof RedstoneTransmitterTileEntity)){
			return;
		}
		((RedstoneTransmitterTileEntity) rawTE).setOutput(RedstoneUtil.getPowerAtPos(worldIn, pos));
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		ItemStack heldItem = playerIn.getHeldItem(hand);
		TileEntity te = worldIn.getTileEntity(pos);
		if(ILinkTE.isLinkTool(heldItem) && te instanceof RedstoneTransmitterTileEntity){
			if(!worldIn.isRemote){
				((RedstoneTransmitterTileEntity) te).wrench(heldItem, playerIn);
			}
			return true;
		}else if(heldItem.getItem() == Items.DYE && te instanceof RedstoneTransmitterTileEntity){
			if(!worldIn.isRemote){
				((RedstoneTransmitterTileEntity) te).dye(DyeColor.byDyeDamage(heldItem.getMetadata()));
			}
			return true;
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Transmits redstone signals wirelessly to nearby linked Redstone Receivers");
		tooltip.add("Link to Receivers with a wrench. Use it on a Transmitter first then a Receiver");
		tooltip.add("Can be color coded with dyes");
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new RedstoneTransmitterTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.COLOR);
	}

	@Override
	public int getMetaFromState(BlockState state){
		return state.get(Properties.COLOR).getMetadata();
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(Properties.COLOR, DyeColor.byMetadata(meta));
	}
}
