package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypePortTypes;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.render.bakedModel.PrototypeBakedModel;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypePortTileEntity;

import net.minecraft.block.*;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
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

public class PrototypePort extends ContainerBlock{

	public PrototypePort(){
		super(Material.IRON);
		String name = "prototype_port";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setHardness(3);
		setSoundType(SoundType.METAL);
		setResistance(2000);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new PrototypePortTileEntity();

	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		PrototypePortTileEntity te = ((PrototypePortTileEntity) worldIn.getTileEntity(pos));
		if(!worldIn.isRemote && !te.isActive()){
			ModPackets.network.sendTo(new SendIntToClient((byte) 0, te.getSide().getIndex() + (te.getType().ordinal() << 3), pos), (ServerPlayerEntity) playerIn);
			playerIn.openGui(Crossroads.instance, GuiHandler.PROTOTYPE_PORT_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@OnlyIn(Dist.CLIENT)
	public void initModel(){
		StateMapperBase ignoreState = new StateMapperBase(){
			@Override
			protected ModelResourceLocation getModelResourceLocation(BlockState IBlockState){
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
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		if(!worldIn.isRemote){
			PrototypePortTileEntity te = ((PrototypePortTileEntity) worldIn.getTileEntity(pos));
			if(!te.isActive()){
				ModPackets.network.sendToAllAround(new SendIntToClient((byte) 0, te.getSide().getIndex() + (te.getType().ordinal() << 3), pos), new TargetPoint(worldIn.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
		}
	}
	
	@Override
	public BlockState getExtendedState(BlockState state, IBlockAccess world, BlockPos pos){
		PrototypePortTileEntity te = ((PrototypePortTileEntity) world.getTileEntity(pos));
		Integer[] sides = new Integer[6];
		sides[te.getSide().getIndex()] = te.getType().ordinal();
		return ((IExtendedBlockState) state).with(Properties.PORT_TYPE, sides);
	}

	@Override
	public PushReaction getPushReaction(BlockState state){
		//Tile entities shouldn't be pushable anyway, but there are enough mods in existence that allow moving tile entities to warrant extra precautions. 
		return PushReaction.BLOCK;
	}
	
	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor){
		if(pos.getY() == neighbor.getY() && world instanceof World){
			neighborChanged(world.getBlockState(pos), (World) world, pos, world.getBlockState(neighbor).getBlock(), neighbor);
		}
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(worldIn.isRemote || worldIn != DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID)){
			return;
		}
		TileEntity te = worldIn.getTileEntity(pos);
		if(!(te instanceof PrototypePortTileEntity)){
			return;
		}
		PrototypePortTileEntity prTe = (PrototypePortTileEntity) te;
		BlockPos dirPos = fromPos.subtract(pos);
		Direction dir = Direction.getFacingFromVector(dirPos.getX(), dirPos.getY(), dirPos.getZ());
		if(prTe.getIndex() == -1 || prTe.getType() != PrototypePortTypes.REDSTONE_OUT || prTe.getSide() != dir){
			return;
		}
		PrototypeInfo info = PrototypeWorldSavedData.get(false).prototypes.get(prTe.getIndex());
		if(info != null && info.owner != null && info.owner.get() != null){
			info.owner.get().neighborChanged(dir, CrossroadsBlocks.prototypePort);
		}
	}
}