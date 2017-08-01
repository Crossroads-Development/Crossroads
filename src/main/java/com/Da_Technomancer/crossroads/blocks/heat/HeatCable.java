package com.Da_Technomancer.crossroads.blocks.heat;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.enums.CableThemes;
import com.Da_Technomancer.crossroads.API.enums.HeatInsulators;
import com.Da_Technomancer.crossroads.API.enums.PrototypePortTypes;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypeOwner;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypePort;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.client.bakedModel.ConduitBakedModel;
import com.Da_Technomancer.crossroads.client.bakedModel.IConduitModel;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.heat.HeatCableTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
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

public class HeatCable extends BlockContainer implements IConduitModel{

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
		String name = "heat_cable_copper_" + insulator.toString().toLowerCase();
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(1);
		setCreativeTab(ModItems.tabHeatCable);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this, false);	
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
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean pleaseDontBeRelevantToAnythingOrIWillBeSad){
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
		return new ResourceLocation(Main.MODID, "blocks/heatcable/" + insulator.name().toLowerCase() + "-copper");
	}

	@Override
	public ResourceLocation getTexture(IBlockState state){
		return new ResourceLocation(Main.MODID, "blocks/heatcable/" + insulator.name().toLowerCase() + '-' + CableThemes.values()[state.getValue(Properties.TEXTURE_4)].toString());
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(playerIn != null && hand != null){
			ItemStack held = playerIn.getHeldItem(hand);
			if(held.isEmpty()){
				return false;
			}
			for(int oreDict : OreDictionary.getOreIDs(held)){
				CableThemes match = IHeatHandler.OREDICT_TO_THEME.get(OreDictionary.getOreName(oreDict));
				if(match != null && state.getValue(Properties.TEXTURE_4) != match.ordinal()){
					if(!worldIn.isRemote){
						worldIn.setBlockState(pos, state.withProperty(Properties.TEXTURE_4, match.ordinal()));
						worldIn.markBlockRangeForRenderUpdate(pos, pos);
					}
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
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.TEXTURE_4, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.TEXTURE_4);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		world.markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new ExtendedBlockState(this, new IProperty[] {Properties.TEXTURE_4}, new IUnlistedProperty[] {Properties.CONNECT});
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos){
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
		Boolean[] connect = {false, false, false, false, false, false};

		for(EnumFacing direction : EnumFacing.values()){
			TileEntity sideTe = world.getTileEntity(pos.offset(direction));
			if(sideTe != null && ((sideTe instanceof IPrototypePort && ((IPrototypePort) sideTe).getType() == PrototypePortTypes.HEAT && ((IPrototypePort) sideTe).getSide() == direction.getOpposite()) || (sideTe instanceof IPrototypeOwner && ((IPrototypeOwner) sideTe).getTypes()[direction.getOpposite().getIndex()] == PrototypePortTypes.HEAT) || sideTe.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, direction.getOpposite()))){
				connect[direction.getIndex()] = true;
			}
		}

		extendedBlockState = extendedBlockState.withProperty(Properties.CONNECT, connect);

		return extendedBlockState;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new HeatCableTileEntity(insulator);
	}

	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Loss Rate: -" + insulator.getRate() + "°C/t");
		tooltip.add("Melting Point: " + insulator.getLimit() + "°C");
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
		Vec3d end = start.addVector(play.getLook(0F).x * reDist, play.getLook(0F).y * reDist, play.getLook(0F).z * reDist);
		AxisAlignedBB out = MiscOp.rayTraceMulti(list, start, end);
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
		AxisAlignedBB out = MiscOp.rayTraceMulti(list, start, end);
		if(out == null){
			return null;
		}else{
			RayTraceResult untransformed = out.calculateIntercept(start, end);
			return new RayTraceResult(untransformed.hitVec.addVector((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()), untransformed.sideHit, pos);
		}
	}
}
