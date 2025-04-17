package com.Da_Technomancer.crossroads.blocks.alchemy;


import com.Da_Technomancer.crossroads.api.alchemy.*;
import com.Da_Technomancer.crossroads.api.electric.IEnergyCapable;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.heat.IHeatCapable;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.render.CRRenderUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.blocks.electric.TeslaCoilTopTileEntity;
import com.Da_Technomancer.crossroads.crafting.AlchemyRec;
import com.Da_Technomancer.essentials.api.IItemCapable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class ReactionChamberTileEntity extends ReagentHolderTE implements IHeatCapable, IItemCapable, IEnergyCapable{

	public static final BlockEntityType<ReactionChamberTileEntity> TYPE = CRTileEntity.createType(ReactionChamberTileEntity::new, CRBlocks.reactionChamberGlass, CRBlocks.reactionChamberCrystal);

	private int energy = 0;
	private final ReactionChamberImpl reactionChamber = new ReactionChamberImpl(() -> energy >= DRAIN);
	private static final int ENERGY_CAPACITY = 20;
	public static final int DRAIN = 10;
	public static final int CAPACITY = 256;

	private static final Pair<Vector3f, Vector3f>[] RENDER_SHAPE = new Pair[] {Pair.of(new Vector3f(0.02F, 0.02F, 0.02F), new Vector3f(0.98F, 0.98F, 0.98F))};

	private final IHeatHandler heatHandler = new HeatHandler();
	private final IItemHandler itemHandler = new ItemHandler();
	private final IEnergyStorage energyHandler = new EnergyHandler();

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

	public void setMap(ReagentMap map){
		contents = map;
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

		for(RecipeHolder<AlchemyRec> react : ReagentManager.getReactions(level)){
			if(react.value().performReaction(reactionChamber)){
				correctReag();
				break;
			}
		}
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
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		energy = nbt.getInt("ener");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putInt("ener", energy);
	}

	@Nullable
	@Override
	public IEnergyStorage getEnergyHandler(Direction dir){
		if(dir == null || dir.getAxis() != Direction.Axis.Y){
			return energyHandler;
		}
		return null;
	}

	@Override
	@Nullable
	public IChemicalHandler getChemicalHandler(Direction dir){
		return chemHandler;
	}

	@Override
	@Nullable
	public IHeatHandler getHeatHandler(Direction dir){
		return heatHandler;
	}

	@Override
	@Nullable
	public IItemHandler getItemHandler(Direction direction){
		return itemHandler;
	}

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
