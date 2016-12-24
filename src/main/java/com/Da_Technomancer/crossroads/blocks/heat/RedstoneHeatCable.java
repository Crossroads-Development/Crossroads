package com.Da_Technomancer.crossroads.blocks.heat;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IBlockCompare;
import com.Da_Technomancer.crossroads.API.IConduitModel;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.enums.HeatConductors;
import com.Da_Technomancer.crossroads.API.enums.HeatInsulators;
import com.Da_Technomancer.crossroads.client.bakedModel.ConduitBakedModel;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.heat.RedstoneHeatCableTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RedstoneHeatCable extends BlockContainer implements IConduitModel, IBlockCompare{

	private HeatConductors conductor;
	private HeatInsulators insulator;
	private static final double size = .2D;
	private static final AxisAlignedBB BB = new AxisAlignedBB(size, size, size, 1 - size, 1 - size, 1 - size);
	private static final AxisAlignedBB DOWN = new AxisAlignedBB(size, 0, size, 1 - size, size, 1 - size);
	private static final AxisAlignedBB UP = new AxisAlignedBB(size, 1, size, 1 - size, 1 - size, 1 - size);
	private static final AxisAlignedBB NORTH = new AxisAlignedBB(size, size, 0, 1 - size, 1 - size, size);
	private static final AxisAlignedBB SOUTH = new AxisAlignedBB(size, size, 1, 1 - size, 1 - size, 1 - size);
	private static final AxisAlignedBB WEST = new AxisAlignedBB(0, size, size, size, 1 - size, 1 - size);
	private static final AxisAlignedBB EAST = new AxisAlignedBB(1, size, size, 1 - size, 1 - size, 1 - size);
	
	public RedstoneHeatCable(HeatConductors conductor, HeatInsulators insulator){
		super(Material.IRON);
		this.conductor = conductor;
		this.insulator = insulator;
		String name = "redstoneHeatCable" + conductor.toString() + insulator.toString();
		setUnlocalizedName(name);
		setRegistryName(name);
		this.setHardness(1);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(name));
		setCreativeTab(ModItems.tabCrossroads);
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
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity){
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
		return new ResourceLocation(Main.MODID, "blocks/heatcable/" + insulator.name().toLowerCase() + '-' + conductor.name().toLowerCase() + "-redstone");
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		world.markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new ExtendedBlockState(this, new IProperty[] {Properties.REDSTONE_BOOL}, new IUnlistedProperty[] {Properties.CONNECT});
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos){
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
		Boolean[] connect = {false, false, false, false, false, false};

		if(state.getValue(Properties.REDSTONE_BOOL)){
			for(EnumFacing direction : EnumFacing.values()){
				if(world.getTileEntity(pos.offset(direction)) != null && world.getTileEntity(pos.offset(direction)).hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, direction.getOpposite())){
					connect[direction.getIndex()] = true;
				}
			}
		}

		extendedBlockState = extendedBlockState.withProperty(Properties.CONNECT, connect);

		return extendedBlockState;
	}
	
	@Override
	public int damageDropped(IBlockState state){
		return 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return this.getDefaultState().withProperty(Properties.REDSTONE_BOOL, meta == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.REDSTONE_BOOL) ? 1 : 0;
	}
	
	@Override
	public boolean hasComparatorInputOverride(IBlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos){
		RedstoneHeatCableTileEntity te = (RedstoneHeatCableTileEntity) worldIn.getTileEntity(pos);
		if(!te.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null) || te.getInsulator() == null){
			return 0;
		}
		double holder = (te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null).getTemp() + 273) / (te.getInsulator().getLimit() + 273);
		holder *= 15D;
		
		return (int) holder;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn){
		if(worldIn.isBlockPowered(pos)){
			if(!state.getValue(Properties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.withProperty(Properties.REDSTONE_BOOL, true));
			}
		}else{
			if(state.getValue(Properties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.withProperty(Properties.REDSTONE_BOOL, false));
			}
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new RedstoneHeatCableTileEntity(conductor, insulator);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
		tooltip.add("Transfer Rate: " + conductor.getRate());
		tooltip.add("Loss Rate: " + insulator.getRate());
		tooltip.add("Melting Point: " + insulator.getLimit() + "*C");
	}

	@Override
	public double getSize(){
		return size;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side){
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
		EntityPlayer play = Minecraft.getMinecraft().thePlayer;
		float reDist = Minecraft.getMinecraft().playerController.getBlockReachDistance();
		Vec3d start = play.getPositionEyes(0F).subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		Vec3d end = start.addVector(play.getLook(0F).xCoord * reDist, play.getLook(0F).yCoord * reDist, play.getLook(0F).zCoord * reDist);
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

	@Override
	public double getOutput(World worldIn, BlockPos pos){
		RedstoneHeatCableTileEntity te = (RedstoneHeatCableTileEntity) worldIn.getTileEntity(pos);
		if(!te.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null) || te.getInsulator() == null){
			return 0;
		}
		double holder = (te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null).getTemp() + 273) / (te.getInsulator().getLimit() + 273);
		holder *= 15D;
		
		return holder;
	}
}
