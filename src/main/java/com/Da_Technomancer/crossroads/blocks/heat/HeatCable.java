package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.API.templates.ConduitBlock;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatCableTileEntity;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.Tag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.List;

public class HeatCable extends ConduitBlock<EnumTransferMode>{

	private static final double SIZE = 0.2D;
	protected static final VoxelShape[] SHAPES = generateShapes(SIZE);
	private static final Item.Properties itemProp = new Item.Properties().tab(CRItems.TAB_HEAT_CABLE);

	public final HeatInsulators insulator;

	public HeatCable(HeatInsulators insulator){
		this(insulator, "heat_cable_" + insulator.toString().toLowerCase());
	}

	protected HeatCable(HeatInsulators insulator, String name){
		super(CRBlocks.getMetalProperty());
		this.insulator = insulator;
		setRegistryName(name);
		CRBlocks.blockAddQue(this, itemProp);
		registerDefaultState(defaultBlockState().setValue(CRProperties.CONDUCTOR, Conductors.COPPER));
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
	protected EnumTransferMode getValueForPlacement(Level world, BlockPos pos, Direction side, @Nullable BlockEntity neighTE){
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
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		super.createBlockStateDefinition(builder);
		builder.add(CRProperties.CONDUCTOR);
	}

	@Override
	protected EnumTransferMode cycleMode(EnumTransferMode prev){
		return prev.isConnection() ? EnumTransferMode.NONE : EnumTransferMode.BOTH;
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(!super.use(state, worldIn, pos, playerIn, hand, hit).shouldSwing()){
			Conductors match = null;
			Item item = playerIn.getItemInHand(hand).getItem();
			for(Conductors c : Conductors.values()){
				if(c.tag.contains(item)){
					match = c;
					break;
				}
			}
			if(match != null && state.getValue(CRProperties.CONDUCTOR) != match){
				if(!worldIn.isClientSide){
					worldIn.setBlock(pos, state.setValue(CRProperties.CONDUCTOR, match), 2);
				}
				return InteractionResult.SUCCESS;
			}
		}else{
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	protected void onAdjusted(Level world, BlockPos pos, BlockState newState, Direction facing, EnumTransferMode newVal, @Nullable IConduitTE<EnumTransferMode> te){
		super.onAdjusted(world, pos, newState, facing, newVal, te);

		//(un)lock the neighboring heat cable with this once, if applicable
		BlockEntity neighTE = world.getBlockEntity(pos.relative(facing));
		if(neighTE instanceof HeatCableTileEntity){
			//Adjust the neighboring pipe alongside this one to have the same data
			((HeatCableTileEntity) neighTE).setData(facing.getOpposite().get3DDataValue(), newVal.isConnection(), newVal);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new HeatCableTileEntity(pos, state, insulator);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, HeatCableTileEntity.TYPE);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.heat_cable.loss", insulator.getRate()));
		tooltip.add(new TranslatableComponent("tt.crossroads.heat_cable.melt", insulator.getLimit()));
	}

	public enum Conductors implements StringRepresentable{

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
		public String getSerializedName(){
			return toString();
		}
	}
}
