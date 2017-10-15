package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypePortTypes;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.client.bakedModel.PrototypeBakedModel;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypePortTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PrototypePort extends BlockContainer{

	public PrototypePort(){
		super(Material.IRON);
		String name = "prototype_port";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.tabCrossroads);
		setHardness(3);
		setSoundType(SoundType.METAL);
		setResistance(2000);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new PrototypePortTileEntity();

	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		PrototypePortTileEntity te = ((PrototypePortTileEntity) worldIn.getTileEntity(pos));
		if(!worldIn.isRemote && !te.isActive()){
			ModPackets.network.sendTo(new SendIntToClient(0, te.getSide().getIndex() + (te.getType().ordinal() << 3), pos), (EntityPlayerMP) playerIn);
			playerIn.openGui(Main.instance, GuiHandler.PROTOTYPE_PORT_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
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
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		if(!worldIn.isRemote){
			PrototypePortTileEntity te = ((PrototypePortTileEntity) worldIn.getTileEntity(pos));
			if(!te.isActive()){
				ModPackets.network.sendToAllAround(new SendIntToClient(0, te.getSide().getIndex() + (te.getType().ordinal() << 3), pos), new TargetPoint(worldIn.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
		}
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos){
		PrototypePortTileEntity te = ((PrototypePortTileEntity) world.getTileEntity(pos));
		Integer[] sides = new Integer[6];
		sides[te.getSide().getIndex()] = te.getType().ordinal();
		return ((IExtendedBlockState) state).withProperty(Properties.PORT_TYPE, sides);
	}

	@Override
	public EnumPushReaction getMobilityFlag(IBlockState state){
		//Tile entities shouldn't be pushable anyway, but there are enough mods in existence that allow moving tile entities to warrant extra precautions. 
		return EnumPushReaction.BLOCK;
	}
	
	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor){
		if(pos.getY() == neighbor.getY() && world instanceof World){
			neighborChanged(world.getBlockState(pos), (World) world, pos, world.getBlockState(neighbor).getBlock(), neighbor);
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(worldIn.isRemote || worldIn != DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID)){
			return;
		}
		TileEntity te = worldIn.getTileEntity(pos);
		if(!(te instanceof PrototypePortTileEntity)){
			return;
		}
		PrototypePortTileEntity prTe = (PrototypePortTileEntity) te;
		BlockPos dirPos = fromPos.subtract(pos);
		EnumFacing dir = EnumFacing.getFacingFromVector(dirPos.getX(), dirPos.getY(), dirPos.getZ());
		if(prTe.getIndex() == -1 || prTe.getType() != PrototypePortTypes.REDSTONE_OUT || prTe.getSide() != dir){
			return;
		}
		PrototypeInfo info = PrototypeWorldSavedData.get(false).prototypes.get(prTe.getIndex());
		if(info != null && info.owner != null && info.owner.get() != null){
			info.owner.get().neighborChanged(dir, ModBlocks.prototypePort);
		}
	}
}