package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.TemporalAcceleratorTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class TemporalAccelerator extends ContainerBlock{

	public TemporalAccelerator(){
		super(Properties.create(Material.IRON).hardnessAndResistance(3).sound(SoundType.METAL));
		String name = "temporal_accelerator";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().with(CRProperties.ACCELERATOR_TARGET, Mode.BOTH));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING, CRProperties.ACCELERATOR_TARGET);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return getDefaultState().with(ESProperties.FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		ItemStack held = playerIn.getHeldItem(hand);
		//Linking with a linking tool
		if(FluxUtil.handleFluxLinking(worldIn, pos, held, playerIn)){
			return true;
		}else if(ESConfig.isWrench(held)){
			if(playerIn.isSneaking()){
				//Sneak clicking- change mode
				TileEntity te = worldIn.getTileEntity(pos);
				state = state.cycle(CRProperties.ACCELERATOR_TARGET);
				worldIn.setBlockState(pos, state);
				if(te instanceof TemporalAcceleratorTileEntity){
					((TemporalAcceleratorTileEntity) te).resetCache();
				}
				if(worldIn.isRemote){
					Mode newMode = state.get(CRProperties.ACCELERATOR_TARGET);
					playerIn.sendMessage(new TranslationTextComponent("tt.crossroads.time_accel.new_mode", MiscUtil.localize(newMode.getLocalizationName())));
					if(!CRConfig.teTimeAccel.get() && newMode.accelerateTileEntities){
						playerIn.sendMessage(new TranslationTextComponent("tt.crossroads.time_accel.config").setStyle(new Style().setColor(TextFormatting.RED)));
					}
				}
			}else{
				//Rotate this machine
				TileEntity te = worldIn.getTileEntity(pos);
				worldIn.setBlockState(pos, state.cycle(ESProperties.FACING));
				if(te instanceof TemporalAcceleratorTileEntity){
					((TemporalAcceleratorTileEntity) te).resetCache();
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new TemporalAcceleratorTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.time_accel.desc", TemporalAcceleratorTileEntity.SIZE));
		tooltip.add(new TranslationTextComponent("tt.crossroads.time_accel.beam"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.time_accel.wrench"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.time_accel.flux"));
	}

	public enum Mode implements IStringSerializable{

		ENTITIES(true, false, false),
		BLOCKS(false, true, true),
		BOTH(true, true, true);

		public final boolean accelerateEntities;
		public final boolean accelerateTileEntities;
		public final boolean accelerateBlockTicks;

		Mode(boolean entity, boolean te, boolean blockTicks){
			accelerateEntities = entity;
			accelerateTileEntities = te;
			accelerateBlockTicks = blockTicks;
		}

		@Override
		public String toString(){
			return name().toLowerCase(Locale.US);
		}

		@Override
		public String getName(){
			return toString();
		}

		public String getLocalizationName(){
			return "tt.crossroads.time_accel.mode." + getName();
		}
	}
}
