package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.IConduitModel;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.client.bakedModel.ConduitBakedModel;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.fluid.FluidTubeTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FluidTube extends BlockContainer implements IConduitModel{

	public FluidTube() {
		super(Material.IRON);
		setUnlocalizedName("fluidTube");
	    setRegistryName("fluidTube");
	    GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this).setRegistryName("fluidTube"));
	    this.setCreativeTab(ModItems.tabCrossroads);
		this.setHardness(3);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new FluidTubeTileEntity();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
        // To make sure that our ISBM model is chosen for all states we use this custom state mapper:
        StateMapperBase ignoreState = new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState IBlockState){
                return ConduitBakedModel.BAKED_MODEL;
            }
        };
        ModelLoader.setCustomStateMapper(this, ignoreState);
    }
	
	@Override
	public ResourceLocation getTexture(){
		return new ResourceLocation(Main.MODID + ":blocks/blockBronze");
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
		return new ItemStack(Item.getByNameOrId(Main.MODID + ":fluidTube"), 1, 0);
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
	}
	
	@Override
	public boolean isBlockNormalCube(IBlockState state){
		return false;
	}
	
	@Override
	protected BlockStateContainer createBlockState(){
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {Properties.CONNECT});
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos){
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
		Boolean[] connect = {false, false, false, false, false, false};
		
		for(EnumFacing direction: EnumFacing.values()){
			if(world.getTileEntity(pos.offset(direction)) != null && world.getTileEntity(pos.offset(direction)).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite())){
				connect[direction.getIndex()] = true;
			}
		}
		
		extendedBlockState = extendedBlockState.withProperty(Properties.CONNECT, connect);
		
		return extendedBlockState;
	}
	
	@Override
    public boolean isOpaqueCube(IBlockState state){
        return false;
    }

	@Override
	public double getSize() {
		return .3125D;
	}
}
