package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.CableThemes;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.render.bakedModel.ConduitBakedModel;
import com.Da_Technomancer.crossroads.render.bakedModel.IConduitModel;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatCableTileEntity;
import com.Da_Technomancer.crossroads.tileentities.heat.RedstoneHeatCableTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class RedstoneHeatCable extends ContainerBlock implements IConduitModel{

	private final HeatInsulators insulator;
	private static final double SIZE = .2D;
	private static final AxisAlignedBB BB = new AxisAlignedBB(SIZE, SIZE, SIZE, 1 - SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB DOWN = new AxisAlignedBB(SIZE, 0, SIZE, 1 - SIZE, SIZE, 1 - SIZE);
	private static final AxisAlignedBB UP = new AxisAlignedBB(SIZE, 1, SIZE, 1 - SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB NORTH = new AxisAlignedBB(SIZE, SIZE, 0, 1 - SIZE, 1 - SIZE, SIZE);
	private static final AxisAlignedBB SOUTH = new AxisAlignedBB(SIZE, SIZE, 1, 1 - SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB WEST = new AxisAlignedBB(0, SIZE, SIZE, SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB EAST = new AxisAlignedBB(1, SIZE, SIZE, 1 - SIZE, 1 - SIZE, 1 - SIZE);

	public RedstoneHeatCable(HeatInsulators insulator){
		super(Material.IRON);
		this.insulator = insulator;
		String name = "redstone_heat_cable_" + insulator.toString().toLowerCase();
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(1);
		setCreativeTab(CrossroadsItems.TAB_HEAT_CABLE);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this, false);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return BlockFaceShape.UNDEFINED;
	}

	@OnlyIn(Dist.CLIENT)
	public void initModel(){
		StateMapperBase ignoreState = new StateMapperBase(){
			@Override
			protected ModelResourceLocation getModelResourceLocation(BlockState IBlockState){
				return ConduitBakedModel.BAKED_MODEL;
			}
		};
		ModelLoader.setCustomStateMapper(this, ignoreState);
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos){
		return BB;
	}

	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean thingyOfThings){
		addCollisionBoxToList(pos, mask, list, BB);
		IExtendedBlockState exState = (IExtendedBlockState) getExtendedState(state, worldIn, pos);

		if(exState.get(Properties.CONNECT)[0]){
			addCollisionBoxToList(pos, mask, list, DOWN);
		}
		if(exState.get(Properties.CONNECT)[1]){
			addCollisionBoxToList(pos, mask, list, UP);
		}
		if(exState.get(Properties.CONNECT)[2]){
			addCollisionBoxToList(pos, mask, list, NORTH);
		}
		if(exState.get(Properties.CONNECT)[3]){
			addCollisionBoxToList(pos, mask, list, SOUTH);
		}
		if(exState.get(Properties.CONNECT)[4]){
			addCollisionBoxToList(pos, mask, list, WEST);
		}
		if(exState.get(Properties.CONNECT)[5]){
			addCollisionBoxToList(pos, mask, list, EAST);
		}
	}

	@Override
	public ResourceLocation getTexture(){
		return new ResourceLocation(Crossroads.MODID, "blocks/heatcable/" + insulator.name().toLowerCase() + "-copper-redstone");
	}

	@Override
	public ResourceLocation getTexture(BlockState state){
		return new ResourceLocation(Crossroads.MODID, "blocks/heatcable/" + insulator.name().toLowerCase() + '-' + CableThemes.values()[state.get(Properties.TEXTURE_4)].toString() + "-redstone");
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(playerIn != null && hand != null){
			ItemStack held = playerIn.getHeldItem(hand);
			if(held.isEmpty()){
				return false;
			}
			if(EssentialsConfig.isWrench(held, worldIn.isRemote)){
				if(!worldIn.isRemote){
					int face;
					if(hitY < SIZE){
						face = 0;//Down
					}else if(hitY > 1F - (float) SIZE){
						face = 1;//Up
					}else if(hitX < (float) SIZE){
						face = 4;//West
					}else if(hitX > 1F - (float) SIZE){
						face = 5;//East
					}else if(hitZ < (float) SIZE){
						face = 2;//North
					}else if(hitZ > 1F - (float) SIZE){
						face = 3;//South
					}else{
						face = side.getIndex();
					}
					TileEntity te = worldIn.getTileEntity(pos);
					if(te instanceof RedstoneHeatCableTileEntity){
						((RedstoneHeatCableTileEntity) te).adjust(face);
					}
				}
				return true;
			}
			for(int oreDict : OreDictionary.getOreIDs(held)){
				CableThemes match = HeatCable.OREDICT_TO_THEME.get(OreDictionary.getOreName(oreDict));
				if(match != null && state.get(Properties.TEXTURE_4) != match.ordinal()){
					if(!worldIn.isRemote){
						worldIn.setBlockState(pos, state.with(Properties.TEXTURE_4, match.ordinal()));
					}
					worldIn.markBlockRangeForRenderUpdate(pos, pos);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, BlockRayTraceResult hit, int meta, LivingEntity placer, Hand hand){
		return getDefaultState().with(Properties.TEXTURE_4, 0);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		world.markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
		neighborChanged(state, world, pos, null, null);
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new ExtendedBlockState(this, new IProperty[] {EssentialsProperties.REDSTONE_BOOL, Properties.TEXTURE_4}, new IUnlistedProperty[] {Properties.CONNECT});
	}

	@Override
	public BlockState getExtendedState(BlockState state, IBlockAccess world, BlockPos pos){
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
		TileEntity te = world.getTileEntity(pos);
		Boolean[] connect = te instanceof HeatCableTileEntity ? ((HeatCableTileEntity) te).getMatches() : new Boolean[] {false, false, false, false, false, false};
		extendedBlockState = extendedBlockState.with(Properties.CONNECT, connect);

		return extendedBlockState;
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(EssentialsProperties.REDSTONE_BOOL, (meta & 1) == 1).with(Properties.TEXTURE_4, meta >> 1);
	}

	@Override
	public int getMetaFromState(BlockState state){
		return (state.get(EssentialsProperties.REDSTONE_BOOL) ? 1 : 0) + (state.get(Properties.TEXTURE_4) << 1);
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos){
		RedstoneHeatCableTileEntity te = (RedstoneHeatCableTileEntity) worldIn.getTileEntity(pos);
		if(!te.hasCapability(Capabilities.HEAT_CAPABILITY, null)){
			return 0;
		}
		double holder = HeatUtil.toKelvin(te.getCapability(Capabilities.HEAT_CAPABILITY, null).getTemp()) / HeatUtil.toKelvin(insulator.getLimit());
		holder *= 16D;

		return Math.min(15, (int) holder);
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(worldIn.isBlockPowered(pos)){
			if(!state.get(EssentialsProperties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.with(EssentialsProperties.REDSTONE_BOOL, true));
				worldIn.updateComparatorOutputLevel(pos, this);
			}
		}else if(state.get(EssentialsProperties.REDSTONE_BOOL)){
			worldIn.setBlockState(pos, state.with(EssentialsProperties.REDSTONE_BOOL, false));
			worldIn.updateComparatorOutputLevel(pos, this);
		}
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new RedstoneHeatCableTileEntity(insulator);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Loss Rate: -" + insulator.getRate() + "°C/t");
		tooltip.add("Melting Point: " + insulator.getLimit() + "°C");
		tooltip.add("");

		tooltip.add("Comparators read: ");
		for(int i = 0; i < 5; i++){
			StringBuilder line = new StringBuilder(50);
			for(int j = 3*i + 1; j <= 3*i + 3; j++){
				line.append("  ").append(j).append(" above ").append(Integer.toString((int) Math.round(HeatUtil.toCelcius(j * HeatUtil.toKelvin(insulator.getLimit()) / 16D)))).append("°C ");
			}
			tooltip.add(line.toString());
		}
	}

	@Override
	public double getSize(){
		return SIZE;
	}

	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(BlockState state, World source, BlockPos pos){
		IExtendedBlockState exState = (IExtendedBlockState) getExtendedState(state, source, pos);
		ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		if(exState.get(Properties.CONNECT)[0]){
			list.add(DOWN);
		}
		if(exState.get(Properties.CONNECT)[1]){
			list.add(UP);
		}
		if(exState.get(Properties.CONNECT)[2]){
			list.add(NORTH);
		}
		if(exState.get(Properties.CONNECT)[3]){
			list.add(SOUTH);
		}
		if(exState.get(Properties.CONNECT)[4]){
			list.add(WEST);
		}
		if(exState.get(Properties.CONNECT)[5]){
			list.add(EAST);
		}
		PlayerEntity play = Minecraft.getInstance().player;
		float reDist = Minecraft.getInstance().playerController.getBlockReachDistance();
		Vec3d start = play.getEyePosition(0F).subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		Vec3d end = start.add(play.getLook(0F).x * reDist, play.getLook(0F).y * reDist, play.getLook(0F).z * reDist);
		AxisAlignedBB out = BlockUtil.selectionRaytrace(list, start, end);
		return (out == null ? BB : out).offset(pos);
	}

	@Override
	@Nullable
	public RayTraceResult collisionRayTrace(BlockState state, World worldIn, BlockPos pos, Vec3d start, Vec3d end){
		IExtendedBlockState exState = (IExtendedBlockState) getExtendedState(state, worldIn, pos);
		ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		list.add(BB);
		if(exState.get(Properties.CONNECT)[0]){
			list.add(DOWN);
		}
		if(exState.get(Properties.CONNECT)[1]){
			list.add(UP);
		}
		if(exState.get(Properties.CONNECT)[2]){
			list.add(NORTH);
		}
		if(exState.get(Properties.CONNECT)[3]){
			list.add(SOUTH);
		}
		if(exState.get(Properties.CONNECT)[4]){
			list.add(WEST);
		}
		if(exState.get(Properties.CONNECT)[5]){
			list.add(EAST);
		}

		start = start.subtract(pos.getX(), pos.getY(), pos.getZ());
		end = end.subtract(pos.getX(), pos.getY(), pos.getZ());
		AxisAlignedBB out = BlockUtil.selectionRaytrace(list, start, end);
		if(out == null){
			return null;
		}else{
			RayTraceResult untransformed = out.calculateIntercept(start, end);
			return new RayTraceResult(untransformed.hitVec.add((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()), untransformed.sideHit, pos);
		}
	}
}
