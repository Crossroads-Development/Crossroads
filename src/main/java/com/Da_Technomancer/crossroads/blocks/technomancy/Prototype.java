package com.Da_Technomancer.crossroads.blocks.technomancy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.enums.PrototypePortTypes;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendNBTToClient;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.client.bakedModel.PrototypeBakedModel;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypeTileEntity;

import net.minecraft.block.Block;
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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
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
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
		worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos){
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
		Integer[] sides = new Integer[6];
		PrototypePortTypes[] ports = ((PrototypeTileEntity) world.getTileEntity(pos)).getTypes();
		for(int i = 0; i < 6; i++){
			sides[i] = ports[i] == null ? null : ports[i].ordinal();
		}
		extendedBlockState = extendedBlockState.withProperty(Properties.PORT_TYPE, sides);

		return extendedBlockState;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
		if(stack.hasTagCompound()){
			tooltip.add("Name: " + stack.getTagCompound().getString("name"));
		}
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, boolean canHarvest){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof PrototypeTileEntity){
			if(worldIn.isRemote){
				return false;
			}
			spawnAsEntity(worldIn, pos, getDrops(worldIn, pos, state, 0).get(0));
			worldIn.destroyBlock(pos, false);
			return false;
		}else{
			return super.removedByPlayer(state, worldIn, pos, player, canHarvest);
		}
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof PrototypeTileEntity){
			ItemStack drop = new ItemStack(Item.getItemFromBlock(this), 1, 0);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("index", ((PrototypeTileEntity) te).getIndex());
			nbt.setString("name", ((PrototypeTileEntity) te).name);
			drop.setTagCompound(nbt);
			drops.add(drop);
		}
		return drops;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
		//Otherwise it returns an invalid prototype/duplicate.
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos){
		return ModConfig.allowPrototype.getInt() != -1 && ModConfig.allowPrototype.getInt() != 2 && super.canPlaceBlockAt(worldIn, pos);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		if(!world.isRemote){
			if(stack.hasTagCompound()){
				PrototypeTileEntity te = (PrototypeTileEntity) world.getTileEntity(pos);
				if(PrototypeWorldSavedData.get(false).prototypes.size() > stack.getTagCompound().getInteger("index")){
					te.setIndex(stack.getTagCompound().getInteger("index"));
					te.name = stack.getTagCompound().getString("name");
					//onLoad is normally called before onBlockPlacedBy, AKA before the index is set. It gets called again here so it can run with the index.
					te.onLoad();
					te.markDirty();
					ModPackets.network.sendToAllAround(new SendNBTToClient(te.getUpdateTag(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					
					for(EnumFacing side : EnumFacing.HORIZONTALS){
						onNeighborChange(world, pos, pos.offset(side));//Updates the redstone-in ports with initial values. 
					}
				}else{
					world.setBlockState(pos, Blocks.AIR.getDefaultState());
				}
			}else{
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
			}
			if(placer instanceof EntityPlayer && ((EntityPlayer) placer).isCreative()){
				//Prevent prototype duplication in creative.
				placer.setHeldItem(placer.getActiveHand(), ItemStack.EMPTY);
			}
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state){
		worldIn.getTileEntity(pos).onChunkUnload();
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor){
		if(pos.getY() == neighbor.getY() && world instanceof World){
			neighborChanged(world.getBlockState(pos), (World) world, pos, world.getBlockState(neighbor).getBlock(), neighbor);
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(worldIn.isRemote){
			return;
		}
		TileEntity te = worldIn.getTileEntity(pos);
		if(!(te instanceof PrototypeTileEntity)){
			return;
		}
		PrototypeTileEntity prTe = (PrototypeTileEntity) te;
		BlockPos dirPos = fromPos.subtract(pos);
		EnumFacing dir = EnumFacing.getFacingFromVector(dirPos.getX(), dirPos.getY(), dirPos.getZ());
		if(prTe.getIndex() == -1){
			return;
		}
		PrototypeInfo info = PrototypeWorldSavedData.get(true).prototypes.get(prTe.getIndex());
		WorldServer worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
		if(info != null && info.ports[dir.getIndex()] != null && info.ports[dir.getIndex()] == PrototypePortTypes.REDSTONE_IN){
			BlockPos relPos = info.portPos[dir.getIndex()].offset(dir);
			relPos = info.chunk.getBlock(relPos.getX(), relPos.getY(), relPos.getZ());
			worldDim.getBlockState(relPos).neighborChanged(worldDim, relPos, blockIn, relPos.offset(dir.getOpposite()));
		}
	}
}