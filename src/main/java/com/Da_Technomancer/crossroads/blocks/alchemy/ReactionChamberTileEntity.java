package com.Da_Technomancer.crossroads.blocks.alchemy;


import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.alchemy.AlchemyReactorTE;
import com.Da_Technomancer.crossroads.api.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.blocks.electric.TeslaCoilTopTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3f;

public class ReactionChamberTileEntity extends AlchemyReactorTE{

	public static final BlockEntityType<ReactionChamberTileEntity> TYPE = CRTileEntity.createType(ReactionChamberTileEntity::new, CRBlocks.reactionChamberGlass, CRBlocks.reactionChamberCrystal);

	private int energy = 0;
	private static final int ENERGY_CAPACITY = 20;
	public static final int DRAIN = 10;
	public static final int CAPACITY = 256;

	private static final Pair<Vector3f, Vector3f>[] RENDER_SHAPE = new Pair[] {Pair.of(new Vector3f(0.02F, 0.02F, 0.02F), new Vector3f(0.98F, 0.98F, 0.98F))};

	public ReactionChamberTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	public ReactionChamberTileEntity(BlockPos pos, BlockState state, boolean glass){
		super(TYPE, pos, state, glass);
	}

	@Override
	protected void initHeat(){
		if(!init){
			init = true;
			cableTemp = getBiomeTemp();
		}
	}

	@Override
	public Pair<Vector3f, Vector3f>[] getRenderVolumes(){
		return RENDER_SHAPE;
	}

	public ReagentMap getMap(){
		return contents;
	}

	public void writeContentNBT(ItemStack stack){
		contents = ReactionChamber.getReagents(stack);
		dirtyReag = true;
	}

	public float getReds(){
		return Math.min(CAPACITY, contents.getTotalQty());
	}

	@Override
	public void serverTick(){
		if(energy >= DRAIN){
			energy -= DRAIN;
			//Spawn random electric arcs between the walls of the block
			if(level.random.nextInt(10) == 0){
				Vec3[] arcPos = new Vec3[2];
				for(int i = 0; i < 2; i++){
					float u = level.random.nextFloat() * 0.8F + 0.1F;
					float v = level.random.nextFloat() * 0.8F + 0.1F;
					switch(level.random.nextInt(6)){
						case 0:
							arcPos[i] = new Vec3(u, v, 0.1);
							break;
						case 1:
							arcPos[i] = new Vec3(u, v, 0.9);
							break;
						case 2:
							arcPos[i] = new Vec3(u, 0.1, v);
							break;
						case 3:
							arcPos[i] = new Vec3(u, 0.9, v);
							break;
						case 4:
							arcPos[i] = new Vec3(0.1, u, v);
							break;
						case 5:
							arcPos[i] = new Vec3(0.9, u, v);
							break;
					}
				}
				CRRenderUtil.addArc(level, arcPos[0].add(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()), arcPos[1].add(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()), 1, 0F, TeslaCoilTopTileEntity.COLOR_CODES[(int) (level.getGameTime() % 3)]);
			}
		}

		super.serverTick();
	}

	@Override
	public boolean isCharged(){
		return energy >= 10 || super.isCharged();
	}

	@Override
	public int transferCapacity(){
		return CAPACITY;
	}

	@Override
	protected boolean useCableHeat(){
		return true;
	}

	@Override
	protected EnumTransferMode[] getModes(){
		return new EnumTransferMode[] {EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH};
	}

	@Override
	protected void performTransfer(){
		vesselTransfer(this);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		energy = nbt.getInt("ener");
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("ener", energy);
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		heatOpt.invalidate();
		itemOpt.invalidate();
		energyOpt.invalidate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY){
			return (LazyOptional<T>) chemOpt;
		}
		if(cap == Capabilities.HEAT_CAPABILITY){
			return (LazyOptional<T>) heatOpt;
		}
		if(cap == ForgeCapabilities.ITEM_HANDLER){
			return (LazyOptional<T>) itemOpt;
		}
		if(cap == ForgeCapabilities.ENERGY && (side == null || side.getAxis() != Direction.Axis.Y)){
			return (LazyOptional<T>) energyOpt;
		}
		return super.getCapability(cap, side);
	}

	private final LazyOptional<IHeatHandler> heatOpt = LazyOptional.of(HeatHandler::new);
	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);
	private final LazyOptional<IEnergyStorage> energyOpt  = LazyOptional.of(EnergyHandler::new);

	private class EnergyHandler implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			int toMove = Math.min(ENERGY_CAPACITY - energy, maxReceive);

			if(!simulate && toMove > 0){
				energy += toMove;
				setChanged();
			}

			return toMove;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			return 0;
		}

		@Override
		public int getEnergyStored(){
			return energy;
		}

		@Override
		public int getMaxEnergyStored(){
			return ENERGY_CAPACITY;
		}

		@Override
		public boolean canExtract(){
			return false;
		}

		@Override
		public boolean canReceive(){
			return true;
		}
	}

	private class HeatHandler implements IHeatHandler{

		@Override
		public double getTemp(){
			initHeat();
			return cableTemp;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			cableTemp = Math.max(HeatUtil.ABSOLUTE_ZERO, tempIn);
			dirtyReag = true;
			setChanged();
		}

		@Override
		public void addHeat(double tempChange){
			initHeat();
			cableTemp = Math.max(HeatUtil.ABSOLUTE_ZERO, cableTemp + tempChange);
			dirtyReag = true;
			setChanged();
		}
	}
}
