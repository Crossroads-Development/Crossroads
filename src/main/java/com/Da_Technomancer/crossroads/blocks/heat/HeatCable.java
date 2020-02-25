package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.API.templates.ConduitBlock;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatCableTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.List;

public class HeatCable extends ConduitBlock<EnumTransferMode>{

	private static final double SIZE = 0.2D;
	protected static final VoxelShape[] SHAPES = generateShapes(SIZE);
	private static final Item.Properties itemProp = new Item.Properties().group(CRItems.TAB_HEAT_CABLE);

	protected final HeatInsulators insulator;

	public HeatCable(HeatInsulators insulator){
		this(insulator, "heat_cable_" + insulator.toString().toLowerCase());
	}

	protected HeatCable(HeatInsulators insulator, String name){
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(1));
		this.insulator = insulator;
		setRegistryName(name);
		CRBlocks.blockAddQue(this, itemProp);
		setDefaultState(getDefaultState().with(CRProperties.CONDUCTOR, Conductors.COPPER));
	}

	@Override
	protected double getSize(){
		return SIZE;
	}

	@Override
	protected EnumTransferMode getDefaultValue(){
		return EnumTransferMode.NONE;
	}

	@Override
	protected EnumTransferMode getValueForPlacement(World world, BlockPos pos, Direction side, @Nullable TileEntity neighTE){
		return EnumTransferMode.BOTH;
	}

	@Override
	protected Property<EnumTransferMode>[] getSideProp(){
		return CRProperties.CONDUIT_SIDES_BASE;
	}

	@Override
	protected VoxelShape[] getShapes(){
		return SHAPES;
	}

	@Override
	protected boolean evaluate(EnumTransferMode value, BlockState state, @Nullable IConduitTE<EnumTransferMode> te){
		return value.isConnection();
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		super.fillStateContainer(builder);
		builder.add(CRProperties.CONDUCTOR);
	}

	@Override
	protected EnumTransferMode cycleMode(EnumTransferMode prev){
		return prev.isConnection() ? EnumTransferMode.NONE : EnumTransferMode.BOTH;
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(playerIn != null && hand != null){
			if(!super.onBlockActivated(state, worldIn, pos, playerIn, hand, hit)){
				Conductors match = null;
				Item item = playerIn.getHeldItem(hand).getItem();
				for(Conductors c : Conductors.values()){
					if(c.tag.contains(item)){
						match = c;
						break;
					}
				}
				if(match != null && state.get(CRProperties.CONDUCTOR) != match){
					if(!worldIn.isRemote){
						worldIn.setBlockState(pos, state.with(CRProperties.CONDUCTOR, match), 2);
					}
					return true;
				}
			}else{
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onAdjusted(World world, BlockPos pos, BlockState newState, Direction facing, EnumTransferMode newVal, @Nullable IConduitTE<EnumTransferMode> te){
		super.onAdjusted(world, pos, newState, facing, newVal, te);

		//(un)lock the neighboring heat cable with this once, if applicable
		TileEntity neighTE = world.getTileEntity(pos.offset(facing));
		if(neighTE instanceof HeatCableTileEntity){
			//Adjust the neighboring pipe alongside this one to have the same data
			((HeatCableTileEntity) neighTE).setData(facing.getOpposite().getIndex(), newVal.isConnection(), newVal);
		}
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new HeatCableTileEntity(insulator);
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
