package com.Da_Technomancer.crossroads.blocks.magic;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.magic.ArcaneReflectorTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ArcaneReflector extends BlockContainer{

	public ArcaneReflector(){
		super(Material.ROCK);
		String name = "arcaneReflector";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
		this.setCreativeTab(ModItems.tabCrossroads);
		this.setHardness(3);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new ArcaneReflectorTileEntity();
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return this.getDefaultState().withProperty(Properties.FACING, (placer == null) ? EnumFacing.NORTH : BlockPistonBase.getFacingFromEntity(pos, placer));
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn){
		if(worldIn.isBlockPowered(pos)){
			if(!state.getValue(Properties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.withProperty(Properties.REDSTONE_BOOL, true));
			}
		}else{
			if(state.getValue(Properties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.withProperty(Properties.REDSTONE_BOOL, false));
			}
		}
	}

	@Override
	public int damageDropped(IBlockState state){
		return 0;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {Properties.FACING, Properties.REDSTONE_BOOL});
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return this.getDefaultState().withProperty(Properties.FACING, EnumFacing.getFront(meta & 7)).withProperty(Properties.REDSTONE_BOOL, (meta & 8) == 8);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		int facingbits = state.getValue(Properties.FACING).getIndex();
		facingbits += state.getValue(Properties.REDSTONE_BOOL) ? 8 : 0;
		return facingbits;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
}
