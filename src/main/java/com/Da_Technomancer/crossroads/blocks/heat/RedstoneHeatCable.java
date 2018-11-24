package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.heat.CableThemes;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypeOwner;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypePort;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypePortTypes;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.render.bakedModel.ConduitBakedModel;
import com.Da_Technomancer.crossroads.render.bakedModel.IConduitModel;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.heat.RedstoneHeatCableTileEntity;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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

public class RedstoneHeatCable extends BlockContainer implements IConduitModel{

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
		setCreativeTab(ModItems.TAB_HEAT_CABLE);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this, false);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face){
		return BlockFaceShape.UNDEFINED;
	}

	@SideOnly(Side.CLIENT)
	public void initModel(){
		StateMapperBase ignoreState = new StateMapperBase(){
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState IBlockState){
				return ConduitBakedModel.BAKED_MODEL;
			}
		};
		ModelLoader.setCustomStateMapper(this, ignoreState);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return BB;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean thingyOfThings){
		addCollisionBoxToList(pos, mask, list, BB);
		IExtendedBlockState exState = (IExtendedBlockState) getExtendedState(state, worldIn, pos);

		if(exState.getValue(Properties.CONNECT)[0]){
			addCollisionBoxToList(pos, mask, list, DOWN);
		}
		if(exState.getValue(Properties.CONNECT)[1]){
			addCollisionBoxToList(pos, mask, list, UP);
		}
		if(exState.getValue(Properties.CONNECT)[2]){
			addCollisionBoxToList(pos, mask, list, NORTH);
		}
		if(exState.getValue(Properties.CONNECT)[3]){
			addCollisionBoxToList(pos, mask, list, SOUTH);
		}
		if(exState.getValue(Properties.CONNECT)[4]){
			addCollisionBoxToList(pos, mask, list, WEST);
		}
		if(exState.getValue(Properties.CONNECT)[5]){
			addCollisionBoxToList(pos, mask, list, EAST);
		}
	}

	@Override
	public ResourceLocation getTexture(){
		return new ResourceLocation(Main.MODID, "blocks/heatcable/" + insulator.name().toLowerCase() + "-copper-redstone");
	}

	@Override
	public ResourceLocation getTexture(IBlockState state){
		return new ResourceLocation(Main.MODID, "blocks/heatcable/" + insulator.name().toLowerCase() + '-' + CableThemes.values()[state.getValue(Properties.TEXTURE_4)].toString() + "-redstone");
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(playerIn != null && hand != null){
			ItemStack held = playerIn.getHeldItem(hand);
			if(held.isEmpty()){
				return false;
			}
			for(int oreDict : OreDictionary.getOreIDs(held)){
				CableThemes match = HeatCable.OREDICT_TO_THEME.get(OreDictionary.getOreName(oreDict));
				if(match != null && state.getValue(Properties.TEXTURE_4) != match.ordinal()){
					if(!worldIn.isRemote){
						worldIn.setBlockState(pos, state.withProperty(Properties.TEXTURE_4, match.ordinal()));
					}
					worldIn.markBlockRangeForRenderUpdate(pos, pos);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand){
		return getDefaultState().withProperty(Properties.TEXTURE_4, 0);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		world.markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
		neighborChanged(state, world, pos, null, null);
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new ExtendedBlockState(this, new IProperty[] {EssentialsProperties.REDSTONE_BOOL, Properties.TEXTURE_4}, new IUnlistedProperty[] {Properties.CONNECT});
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos){
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
		Boolean[] connect = {false, false, false, false, false, false};

		if(state.getValue(EssentialsProperties.REDSTONE_BOOL)){
			for(EnumFacing direction : EnumFacing.values()){
				TileEntity sideTe = world.getTileEntity(pos.offset(direction));
				connect[direction.getIndex()] = sideTe != null && ((sideTe instanceof IPrototypePort && ((IPrototypePort) sideTe).getType() == PrototypePortTypes.HEAT && ((IPrototypePort) sideTe).getSide() == direction.getOpposite()) || (sideTe instanceof IPrototypeOwner && ((IPrototypeOwner) sideTe).getTypes()[direction.getOpposite().getIndex()] == PrototypePortTypes.HEAT) || sideTe.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, direction.getOpposite()));
			}
		}

		extendedBlockState = extendedBlockState.withProperty(Properties.CONNECT, connect);

		return extendedBlockState;
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(EssentialsProperties.REDSTONE_BOOL, (meta & 1) == 1).withProperty(Properties.TEXTURE_4, meta >> 1);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return (state.getValue(EssentialsProperties.REDSTONE_BOOL) ? 1 : 0) + (state.getValue(Properties.TEXTURE_4) << 1);
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos){
		RedstoneHeatCableTileEntity te = (RedstoneHeatCableTileEntity) worldIn.getTileEntity(pos);
		if(!te.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null)){
			return 0;
		}
		double holder = HeatUtil.toKelvin(te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null).getTemp()) / HeatUtil.toKelvin(insulator.getLimit());
		holder *= 16D;

		return Math.min(15, (int) holder);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(worldIn.isBlockPowered(pos)){
			if(!state.getValue(EssentialsProperties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.withProperty(EssentialsProperties.REDSTONE_BOOL, true));
				worldIn.updateComparatorOutputLevel(pos, this);
			}
		}else if(state.getValue(EssentialsProperties.REDSTONE_BOOL)){
			worldIn.setBlockState(pos, state.withProperty(EssentialsProperties.REDSTONE_BOOL, false));
			worldIn.updateComparatorOutputLevel(pos, this);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new RedstoneHeatCableTileEntity(insulator);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
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
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World source, BlockPos pos){
		IExtendedBlockState exState = (IExtendedBlockState) getExtendedState(state, source, pos);
		ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		if(exState.getValue(Properties.CONNECT)[0]){
			list.add(DOWN);
		}
		if(exState.getValue(Properties.CONNECT)[1]){
			list.add(UP);
		}
		if(exState.getValue(Properties.CONNECT)[2]){
			list.add(NORTH);
		}
		if(exState.getValue(Properties.CONNECT)[3]){
			list.add(SOUTH);
		}
		if(exState.getValue(Properties.CONNECT)[4]){
			list.add(WEST);
		}
		if(exState.getValue(Properties.CONNECT)[5]){
			list.add(EAST);
		}
		EntityPlayer play = Minecraft.getMinecraft().player;
		float reDist = Minecraft.getMinecraft().playerController.getBlockReachDistance();
		Vec3d start = play.getPositionEyes(0F).subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		Vec3d end = start.add(play.getLook(0F).x * reDist, play.getLook(0F).y * reDist, play.getLook(0F).z * reDist);
		AxisAlignedBB out = BlockUtil.selectionRaytrace(list, start, end);
		return (out == null ? BB : out).offset(pos);
	}

	@Override
	@Nullable
	public RayTraceResult collisionRayTrace(IBlockState state, World worldIn, BlockPos pos, Vec3d start, Vec3d end){
		IExtendedBlockState exState = (IExtendedBlockState) getExtendedState(state, worldIn, pos);
		ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		list.add(BB);
		if(exState.getValue(Properties.CONNECT)[0]){
			list.add(DOWN);
		}
		if(exState.getValue(Properties.CONNECT)[1]){
			list.add(UP);
		}
		if(exState.getValue(Properties.CONNECT)[2]){
			list.add(NORTH);
		}
		if(exState.getValue(Properties.CONNECT)[3]){
			list.add(SOUTH);
		}
		if(exState.getValue(Properties.CONNECT)[4]){
			list.add(WEST);
		}
		if(exState.getValue(Properties.CONNECT)[5]){
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
