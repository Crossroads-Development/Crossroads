package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatCableTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.List;

public class HeatCable extends ContainerBlock{

	protected static final VoxelShape[] SHAPES = new VoxelShape[64];
	private static final double SIZE = 0.2D;
	private static final Item.Properties itemProp = new Item.Properties().group(CRItems.TAB_HEAT_CABLE);

	protected final HeatInsulators insulator;

	static{
		final double size = 16 * SIZE;
		final double sizeN = 16D - size;
		//There are 64 (2^6) possible states for this block, and each one has a different shape
		//This... is gonna take a while
		VoxelShape core = makeCuboidShape(size, size, size, sizeN, sizeN, sizeN);
		VoxelShape[] pieces = new VoxelShape[6];
		pieces[0] = makeCuboidShape(size, 0, size, sizeN, size, sizeN);
		pieces[1] = makeCuboidShape(size, 16, size, sizeN, sizeN, sizeN);
		pieces[2] = makeCuboidShape(size, size, 0, sizeN, sizeN, size);
		pieces[3] = makeCuboidShape(size, size, 16, sizeN, sizeN, sizeN);
		pieces[4] = makeCuboidShape(0, size, size, size, sizeN, sizeN);
		pieces[5] = makeCuboidShape(16, size, size, sizeN, sizeN, sizeN);
		for(int i = 0; i < 64; i++){
			VoxelShape comp = core;
			for(int j = 0; j < 6; j++){
				if((i & (1 << j)) != 0){
					comp = VoxelShapes.or(comp, pieces[j]);
				}
			}
			SHAPES[i] = comp;
		}
	}

	public HeatCable(HeatInsulators insulator){
		this(insulator, "heat_cable_" + insulator.toString().toLowerCase());
		setDefaultState(getDefaultState().with(CRProperties.CONDUCTOR, Conductors.COPPER));
	}

	protected HeatCable(HeatInsulators insulator, String name){
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(1));
		this.insulator = insulator;
		setRegistryName(name);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this, itemProp);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.CONDUCTOR, CRProperties.DOWN, CRProperties.UP, CRProperties.NORTH, CRProperties.SOUTH, CRProperties.EAST, CRProperties.WEST);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(playerIn != null && hand != null){
			ItemStack held = playerIn.getHeldItem(hand);
			if(held.isEmpty()){
				return false;
			}
			if(EssentialsConfig.isWrench(held)){
				if(!worldIn.isRemote){
					int face;
					if(hit.getHitVec().y < SIZE){
						face = 0;//Down
					}else if(hit.getHitVec().y > 1F - (float) SIZE){
						face = 1;//Up
					}else if(hit.getHitVec().z < (float) SIZE){
						face = 4;//West
					}else if(hit.getHitVec().z > 1F - (float) SIZE){
						face = 5;//East
					}else if(hit.getHitVec().z < (float) SIZE){
						face = 2;//North
					}else if(hit.getHitVec().z > 1F - (float) SIZE){
						face = 3;//South
					}else{
						face = hit.getFace().getIndex();
					}
					TileEntity te = worldIn.getTileEntity(pos);
					if(te instanceof HeatCableTileEntity){
						((HeatCableTileEntity) te).adjust(face);
						Direction dir = Direction.byIndex(face);
						BlockState newState = updatePostPlacement(state, dir, state, worldIn, pos, pos.offset(dir));
						if(newState != state){
							worldIn.setBlockState(pos, newState);
						}
					}
				}
				return true;
			}

			Conductors match = null;
			Item item = held.getItem();
			for(Conductors c : Conductors.values()){
				if(c.tag.contains(item)){
					match = c;
					break;
				}
			}
			if(match != null && state.get(CRProperties.CONDUCTOR) != match){
				if(!worldIn.isRemote){
					worldIn.setBlockState(pos, state.with(CRProperties.CONDUCTOR, match));
				}
//				worldIn.markBlockRangeForRenderUpdate(pos, pos);
				return true;
			}
		}
		return false;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		int index = 0;
		if(state.get(CRProperties.DOWN)){
			index |= 1;
		}
		if(state.get(CRProperties.UP)){
			index |= 1 << 1;
		}
		if(state.get(CRProperties.NORTH)){
			index |= 1 << 2;
		}
		if(state.get(CRProperties.SOUTH)){
			index |= 1 << 3;
		}
		if(state.get(CRProperties.WEST)){
			index |= 1 << 4;
		}
		if(state.get(CRProperties.EAST)){
			index |= 1 << 5;
		}
		return SHAPES[index];
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		boolean[] connect = new boolean[6];
		for(int i = 0; i < 6; i++){
			TileEntity te = context.getWorld().getTileEntity(context.getPos().offset(Direction.byIndex(i)));
			if(te != null && te.getCapability(Capabilities.HEAT_CAPABILITY, Direction.byIndex(i).getOpposite()).isPresent()){
				connect[i] = true;
			}
		}
		return getDefaultState().with(CRProperties.DOWN, connect[0]).with(CRProperties.UP, connect[1]).with(CRProperties.NORTH, connect[2]).with(CRProperties.SOUTH, connect[3]).with(CRProperties.WEST, connect[4]).with(CRProperties.EAST, connect[5]);
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos pos, BlockPos facingPos){
		TileEntity te = worldIn.getTileEntity(facingPos);
		TileEntity thisTE = worldIn.getTileEntity(pos);
		boolean connect = thisTE instanceof HeatCableTileEntity && !((HeatCableTileEntity) thisTE).getLock(facing.getIndex()) && te != null && te.getCapability(Capabilities.HEAT_CAPABILITY, facing.getOpposite()).isPresent();
		BooleanProperty prop;
		switch(facing){
			case DOWN:
				prop = CRProperties.DOWN;
				break;
			case UP:
				prop = CRProperties.UP;
				break;
			case NORTH:
				prop = CRProperties.NORTH;
				break;
			case SOUTH:
				prop = CRProperties.SOUTH;
				break;
			case WEST:
				prop = CRProperties.WEST;
				break;
			case EAST:
			default:
				prop = CRProperties.EAST;
				break;
		}
		return stateIn.with(prop, connect);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new HeatCableTileEntity(insulator);
	}

	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.heat_cable.loss", insulator.getRate()));
		tooltip.add(new TranslationTextComponent("tt.crossroads.heat_cable.melt", insulator.getLimit()));
	}
	
	public enum Conductors implements IStringSerializable{

		COPPER(CRItemTags.INGOTS_COPPER),
		IRON(Tags.Items.INGOTS_IRON),
		QUARTZ(Tags.Items.GEMS_QUARTZ),
		DIAMOND(Tags.Items.GEMS_DIAMOND);

		public final Tag<Item> tag;

		Conductors(Tag<Item> tag){
			this.tag = tag;
		}

		@Override
		public String toString(){
			return super.toString().toLowerCase();
		}

		@Override
		public String getName(){
			return toString();
		}
	}
}
