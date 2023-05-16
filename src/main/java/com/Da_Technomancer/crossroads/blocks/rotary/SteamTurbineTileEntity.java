package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.ambient.particles.CRParticles;
import com.Da_Technomancer.crossroads.ambient.particles.ColorParticleData;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.templates.InventoryTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.gui.container.SteamTurbineContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.awt.*;

public class SteamTurbineTileEntity extends InventoryTE{

	public static final BlockEntityType<SteamTurbineTileEntity> TYPE = CRTileEntity.createType(SteamTurbineTileEntity::new, CRBlocks.steamTurbine);

	public static final double INERTIA = 80D;
	private static final int CAPACITY = 10_000;
	public static final int LIMIT = 5;

	public SteamTurbineTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, 0);
		fluidProps[0] = new TankProperty(CAPACITY, false, true);
		fluidProps[1] = new TankProperty(CAPACITY, true, false, f -> CraftingUtil.tagContains(CRFluids.STEAM, f));
		initFluidManagers();
	}

	@Override
	protected int fluidTanks(){
		return 2;
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	protected double getMoInertia(){
		return INERTIA;
	}

	private static ColorParticleData steamParticle;

	@Override
	public void serverTick(){
		super.serverTick();
		int limit = 0;
		if(!fluids[1].isEmpty()){
			limit = fluids[1].getAmount() / 100;
			limit = Math.min(limit, (CAPACITY - fluids[0].getAmount()) / 100);
			limit = Math.min(limit, LIMIT);
			if(limit != 0){
				fluids[1].shrink(limit * 100);
				if(fluids[0].isEmpty()){
					fluids[0] = new FluidStack(CRFluids.distilledWater.getStill(), 100 * limit);
				}else{
					fluids[0].grow(100 * limit);
				}
				if(axleHandler.axis != null){
					axleHandler.addEnergy(((double) limit) * .1D * (double) CRConfig.steamWorth.get() * CRConfig.jouleWorth.get(), true);
				}
			}
		}

		//Update blockstate
		if(getBlockState().getValue(CRProperties.ACTIVE) != (limit > 0)){
			level.setBlock(worldPosition, getBlockState().setValue(CRProperties.ACTIVE, limit > 0), MiscUtil.BLOCK_FLAGS_VISUAL);
		}
	}

	@Override
	public void clientTick(){
		super.clientTick();

		//Particles
		if(level.getGameTime() % 4 == 0 && getBlockState().getValue(CRProperties.ACTIVE)){
			if(steamParticle == null){
				steamParticle = new ColorParticleData(CRParticles.COLOR_SOLID, Color.LIGHT_GRAY);
			}
			CRParticles.summonParticlesFromClient(level, steamParticle, 2, worldPosition.getX() + 0.5, worldPosition.getY() + 4F / 16, worldPosition.getZ() + 0.5, 0.1, 0, 0.1, 0, 0.06, 0, 0.01, 0.02, 0.01, true);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == ForgeCapabilities.FLUID_HANDLER){
			return (LazyOptional<T>) globalFluidOpt;
		}
		if(capability == Capabilities.AXLE_CAPABILITY && facing == Direction.UP){
			return (LazyOptional<T>) axleOpt;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction){
		return false;
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.steam_turbine");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new SteamTurbineContainer(id, playerInv, createContainerBuf());
	}
}
