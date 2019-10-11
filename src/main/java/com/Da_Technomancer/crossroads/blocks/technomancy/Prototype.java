package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypePortTypes;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.render.bakedModel.PrototypeBakedModel;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.tileentities.technomancy.PrototypeTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.*;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Prototype extends ContainerBlock{

	public Prototype(){
		super(Material.IRON);
		String name = "prototype";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(3);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new PrototypeTileEntity();

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
	public void tick(BlockState state, World worldIn, BlockPos pos, Random rand){
		worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
	}


	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof PrototypeTileEntity){
					((PrototypeTileEntity) te).rotate();
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public BlockState getExtendedState(BlockState state, IBlockAccess world, BlockPos pos){
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
		Integer[] sides = new Integer[6];
		PrototypePortTypes[] ports = ((PrototypeTileEntity) world.getTileEntity(pos)).getTypes();
		for(int i = 0; i < 6; i++){
			sides[i] = ports[i] == null ? null : ports[i].ordinal();
		}
		extendedBlockState = extendedBlockState.with(Properties.PORT_TYPE, sides);

		return extendedBlockState;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		if(stack.hasTag()){
			tooltip.add("Name: " + stack.getTag().getString("name"));
			for(int i = 0; i < 6; i++){
				if(stack.getTag().contains("ttip" + i)){
					tooltip.add(Direction.byIndex(i).name().charAt(0) + Direction.byIndex(i).toString().substring(1) + ": " + stack.getTag().getString("ttip" + i));
				}
			}
			if(advanced == ITooltipFlag.TooltipFlags.ADVANCED){
				tooltip.add("Index: " + stack.getTag().getInt("index"));
			}
		}
	}

	@Override
	public boolean removedByPlayer(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid){
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
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, BlockState state, int fortune){
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof PrototypeTileEntity){
			ItemStack drop = new ItemStack(Item.getItemFromBlock(this), 1, 0);
			CompoundNBT nbt = new CompoundNBT();
			nbt.putInt("index", ((PrototypeTileEntity) te).getIndex());
			nbt.putString("name", ((PrototypeTileEntity) te).name);
			for(int i = 0; i < 6; i++){
				if(((PrototypeTileEntity) te).tooltips[i] != null){
					nbt.putString("ttip" + i, ((PrototypeTileEntity) te).tooltips[i]);
				}
			}
			drop.put(nbt);
			drops.add(drop);
		}
		return drops;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, World world, BlockPos pos, PlayerEntity player){
		//Otherwise it returns an invalid prototype/duplicate.
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos){
		return CRConfig.allowPrototype.getInt() != -1 && CRConfig.allowPrototype.getInt() != 2 && super.canPlaceBlockAt(worldIn, pos);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		if(!world.isRemote){
			if(stack.hasTag()){
				PrototypeTileEntity te = (PrototypeTileEntity) world.getTileEntity(pos);
				if(PrototypeWorldSavedData.get(false).prototypes.size() > stack.getTag().getInt("index")){
					te.setIndex(stack.getTag().getInt("index"));
					te.name = stack.getTag().getString("name");
					for(int i = 0; i < 6; i++){
						if(stack.getTag().contains("ttip" + i)){
							te.tooltips[i] = stack.getTag().getString("ttip" + i);
						}
					}
					//onLoad is normally called before onBlockPlacedBy, AKA before the index is set. It gets called again here so it can run with the index.
					te.onLoad();
					te.markDirty();
					CrossroadsPackets.network.sendToAllAround(new SendNBTToClient(te.getUpdateTag(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));

					for(Direction side : Direction.HORIZONTALS){
						onNeighborChange(world, pos, pos.offset(side));//Updates the redstone-in ports with initial values. 
					}
				}else{
					world.setBlockState(pos, Blocks.AIR.getDefaultState());
				}
			}else{
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
			}
			if(placer instanceof PlayerEntity && ((PlayerEntity) placer).isCreative()){
				//Prevent prototype duplication in creative.
				placer.setHeldItem(placer.getActiveHand(), ItemStack.EMPTY);
			}
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, BlockState state){
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
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(worldIn.isRemote){
			return;
		}
		TileEntity te = worldIn.getTileEntity(pos);
		if(!(te instanceof PrototypeTileEntity)){
			return;
		}
		PrototypeTileEntity prTe = (PrototypeTileEntity) te;
		BlockPos dirPos = fromPos.subtract(pos);
		Direction dir = prTe.adjustSide(Direction.getFacingFromVector(dirPos.getX(), dirPos.getY(), dirPos.getZ()), true);
		if(prTe.getIndex() == -1){
			return;
		}
		PrototypeInfo info = PrototypeWorldSavedData.get(true).prototypes.get(prTe.getIndex());
		ServerWorld worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
		if(info != null && info.ports[dir.getIndex()] != null && info.ports[dir.getIndex()] == PrototypePortTypes.REDSTONE_IN){
			BlockPos relPos = info.portPos[dir.getIndex()].offset(dir);
			relPos = info.chunk.getBlock(relPos.getX(), relPos.getY(), relPos.getZ());
			worldDim.getBlockState(relPos).neighborChanged(worldDim, relPos, blockIn, relPos.offset(dir.getOpposite()));
		}
	}
}