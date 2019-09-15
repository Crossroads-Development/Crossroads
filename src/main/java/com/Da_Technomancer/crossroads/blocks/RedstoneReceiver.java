package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.tileentities.RedstoneReceiverTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class RedstoneReceiver extends ContainerBlock{

	protected RedstoneReceiver(){
		super(Material.ROCK);
		String name = "redstone_receiver";
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
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		ItemStack heldItem = playerIn.getHeldItem(hand);
		TileEntity te = worldIn.getTileEntity(pos);
		if(ILinkTE.isLinkTool(heldItem) && te instanceof RedstoneReceiverTileEntity){
			if(!worldIn.isRemote){
				((RedstoneReceiverTileEntity) te).wrench(heldItem, playerIn);
			}
			return true;
		}else if(heldItem.getItem() == Items.DYE && te instanceof RedstoneReceiverTileEntity){
			if(!worldIn.isRemote){
				((RedstoneReceiverTileEntity) te).dye(DyeColor.byDyeDamage(heldItem.getMetadata()));
			}
			return true;
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Receives redstone signals wirelessly from a nearby linked Redstone Transmitter");
		tooltip.add("Link to a Transmitter with a wrench. Use it on a Transmitter first then a Receiver");
		tooltip.add("Can be color coded with dyes");
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new RedstoneReceiverTileEntity();
	}

	@Override
	public int getWeakPower(BlockState blockState, IBlockAccess blockAccess, BlockPos pos, Direction side){
		TileEntity te = blockAccess.getTileEntity(pos);
		if(te instanceof RedstoneReceiverTileEntity){
			return (int) Math.round(((RedstoneReceiverTileEntity) te).getStrength());
		}

		return super.getWeakPower(blockState, blockAccess, pos, side);
	}

	@Override
	public boolean canProvidePower(BlockState state){
		return true;
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
