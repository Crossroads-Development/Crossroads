package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.GameProfileNonPicky;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayFrameTileEntity;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class GatewayFrame extends BlockContainer{

	public GatewayFrame(){
		super(Material.IRON);
		String name = "gateway_frame";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(3);
		setSoundType(SoundType.METAL);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		EnumFacing facing = (placer == null) ? EnumFacing.NORTH : EnumFacing.getDirectionFromEntityLiving(pos, placer);
		if(facing == EnumFacing.UP){
			facing = EnumFacing.DOWN;
		}
		return getDefaultState().withProperty(EssentialsProperties.FACING, placer instanceof FakePlayer ? EnumFacing.NORTH : facing);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		if(!worldIn.isRemote){
			((GatewayFrameTileEntity) worldIn.getTileEntity(pos)).setOwner(!(placer instanceof EntityPlayer) ? null : new GameProfileNonPicky(((EntityPlayer) placer).getGameProfile()));
		}
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {EssentialsProperties.FACING});
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		EnumFacing facing = EnumFacing.getFront(meta);
		return getDefaultState().withProperty(EssentialsProperties.FACING, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(EssentialsProperties.FACING).getIndex();
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new GatewayFrameTileEntity();

	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot){
		return state.withProperty(EssentialsProperties.FACING, rot.rotate(state.getValue(EssentialsProperties.FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn){
		return state.withRotation(mirrorIn.toRotation(state.getValue(EssentialsProperties.FACING)));
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
}
