package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.technomancy.FluxTE;

public class FluxConsumerTileEntity extends FluxTE{

	@Override
	public void update(){
		super.update();

		flux = Math.max(0, flux - 8);
	}

	@Override
	public int addFlux(int fluxIn){
		flux += fluxIn;
		markDirty();
		return flux;
	}

	@Override
	public boolean isFluxEmitter(){
		return false;
	}

	@Override
	public boolean canReceiveFlux(){
		return flux < getCapacity();
	}

	@Override
	public boolean isFluxReceiver(){
		return true;
	}
}
