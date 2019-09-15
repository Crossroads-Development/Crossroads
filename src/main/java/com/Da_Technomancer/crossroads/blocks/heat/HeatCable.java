package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.API.heat.CableThemes;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.render.bakedModel.ConduitBakedModel;
import com.Da_Technomancer.crossroads.render.bakedModel.IConduitModel;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatCableTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.block.BlockRenderType;
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
import java.util.HashMap;
import java.util.List;

public class HeatCable extends ContainerBlock implements IConduitModel{

	public static final HashMap<String, CableThemes> OREDICT_TO_THEME = new HashMap<String, CableThemes>();

	private final HeatInsulators insulator;
	private static final double SIZE = .2D;
	private static final AxisAlignedBB BB = new AxisAlignedBB(SIZE, SIZE, SIZE, 1 - SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB DOWN = new AxisAlignedBB(SIZE, 0, SIZE, 1 - SIZE, SIZE, 1 - SIZE);
	private static final AxisAlignedBB UP = new AxisAlignedBB(SIZE, 1, SIZE, 1 - SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB NORTH = new AxisAlignedBB(SIZE, SIZE, 0, 1 - SIZE, 1 - SIZE, SIZE);
	private static final AxisAlignedBB SOUTH = new AxisAlignedBB(SIZE, SIZE, 1, 1 - SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB WEST = new AxisAlignedBB(0, SIZE, SIZE, SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB EAST = new AxisAlignedBB(1, SIZE, SIZE, 1 - SIZE, 1 - SIZE, 1 - SIZE);

	public HeatCable(HeatInsulators insulator){
		super(Material.IRON);
		this.insulator = insulator;
		String name = "heat_cable_" + insulator.toString().toLowerCase();
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(1);
		setCreativeTab(CrossroadsItems.TAB_HEAT_CABLE);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this, false);
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
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean pleaseDontBeRelevantToAnythingOrIWillBeSad){
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
		return new ResourceLocation(Crossroads.MODID, "blocks/heatcable/" + insulator.name().toLowerCase() + "-copper");
	}

	@Override
	public ResourceLocation getTexture(BlockState state){
		return new ResourceLocation(Crossroads.MODID, "blocks/heatcable/" + insulator.name().toLowerCase() + '-' + CableThemes.values()[state.get(Properties.TEXTURE_4)].toString());
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
					if(te instanceof HeatCableTileEntity){
						((HeatCableTileEntity) te).adjust(face);
					}
				}
				return true;
			}

			for(int oreDict : OreDictionary.getOreIDs(held)){
				CableThemes match = OREDICT_TO_THEME.get(OreDictionary.getOreName(oreDict));
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
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(Properties.TEXTURE_4, meta);
	}

	@Override
	public int getMetaFromState(BlockState state){
		return state.get(Properties.TEXTURE_4);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		world.markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new ExtendedBlockState(this, new IProperty[] {Properties.TEXTURE_4}, new IUnlistedProperty[] {Properties.CONNECT});
	}

	@Override
	public BlockState getExtendedState(BlockState state, IBlockAccess world, BlockPos pos){
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
		TileEntity te = world.getTileEntity(pos);

		Boolean[] connect = te instanceof HeatCableTileEntity ? ((HeatCableTileEntity) te).getMatches() : new Boolean[] {false, false, false, false, false, false};

//		TODO relevant for prototypes
//		for(EnumFacing direction : EnumFacing.values()){
//			TileEntity sideTe = world.getTileEntity(pos.offset(direction));
//			connect[direction.getIndex()] = sideTe != null && ((sideTe instanceof IPrototypePort && ((IPrototypePort) sideTe).getType() == PrototypePortTypes.HEAT && ((IPrototypePort) sideTe).getSide() == direction.getOpposite()) || (sideTe instanceof IPrototypeOwner && ((IPrototypeOwner) sideTe).getTypes()[direction.getOpposite().getIndex()] == PrototypePortTypes.HEAT) || sideTe.hasCapability(Capabilities.HEAT_CAPABILITY, direction.getOpposite()));
//		}

		extendedBlockState = extendedBlockState.with(Properties.CONNECT, connect);

		return extendedBlockState;
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
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
		tooltip.add("Loss Rate: -" + insulator.getRate() + "°C/t");
		tooltip.add("Melting Point: " + insulator.getLimit() + "°C");
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
		Vec3d start = play.getPositionEyes(0F).subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
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
