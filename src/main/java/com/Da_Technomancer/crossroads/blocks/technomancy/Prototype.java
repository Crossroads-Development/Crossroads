package com.Da_Technomancer.crossroads.blocks.technomancy;

import java.util.ArrayList;
import java.util.List;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.client.bakedModel.PrototypeBakedModel;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypeTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Prototype extends BlockContainer{

	public Prototype(){
		super(Material.IRON);
		String name = "prototype";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name).setMaxStackSize(1));
		setHardness(3);
		setSoundType(SoundType.METAL);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new PrototypeTileEntity();

	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	public void initModel(){
		StateMapperBase ignoreState = new StateMapperBase(){
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState IBlockState){
				return PrototypeBakedModel.BAKED_MODEL;
			}
		};
		ModelLoader.setCustomStateMapper(this, ignoreState);
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {Properties.PORT_TYPE});
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos){
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
		Integer[] sides = new Integer[6];
		PrototypeInfo info = PrototypeWorldSavedData.get(DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID)).prototypeInfo.get(((PrototypeTileEntity) world.getTileEntity(pos)).getChunk());
		for(int i = 0; i < 6; i++){
			sides[i] = info.ports[i] == null ? null : info.ports[i].ordinal();
		}
		extendedBlockState = extendedBlockState.withProperty(Properties.PORT_TYPE, sides);

		return extendedBlockState;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
		if(stack.hasTagCompound()){
			tooltip.add("Name: " + stack.getTagCompound().getString("name"));
			PrototypeInfo info = PrototypeWorldSavedData.get(DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID)).prototypeInfo.get(stack.getTagCompound().getLong("chunk"));
			if(info == null){
				return;
			}
			for(EnumFacing side : EnumFacing.values()){
				tooltip.add(side.toString() + ": " + (info.ports[side.getIndex()] == null ? "NONE" : info.ports[side.getIndex()].toString()));
			}
		}
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof PrototypeTileEntity){
			ItemStack drop = new ItemStack(Item.getItemFromBlock(this), 1, 0);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setLong("chunk", ((PrototypeTileEntity) te).getChunk());
			nbt.setString("name", ((PrototypeTileEntity) te).name);
			drop.setTagCompound(nbt);
			drops.add(drop);
		}
		return drops;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		if(!world.isRemote){
			if(stack.hasTagCompound()){
				PrototypeTileEntity te = (PrototypeTileEntity) world.getTileEntity(pos);
				if(PrototypeWorldSavedData.get(DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID)).prototypeInfo.containsKey(stack.getTagCompound().getLong("chunk"))){
					te.setChunk(stack.getTagCompound().getLong("chunk"));
					te.name = stack.getTagCompound().getString("name");
				}else{
					world.setBlockState(pos, Blocks.AIR.getDefaultState());
				}
			}else{
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
			}
		}
	}
}