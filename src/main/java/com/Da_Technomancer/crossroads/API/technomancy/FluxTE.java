package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;

public abstract class FluxTE extends ModuleTE implements ILinkTE, IFluxHandler{

	protected int flux;
	protected ArrayList<BlockPos> links = new ArrayList<>(getMaxLinks());

	@Override
	public void update(){
		super.update();

		if(world.isRemote || flux == 0 || world.getTotalWorldTime() % FluxUtil.FLUX_TIME != 0){
			return;
		}

		IFluxHandler[] targets = new IFluxHandler[getMaxLinks()];
		int targetCount = 0;
		int moved = 0;

		for(BlockPos link : links){
			BlockPos endPos = pos.add(link);
			TileEntity te = world.getTileEntity(endPos);
			if(te instanceof IFluxHandler && ((IFluxHandler) te).isFluxReceiver() && ((IFluxHandler) te).canReceiveFlux()){
				targets[targetCount] = (IFluxHandler) te;
				targetCount++;
			}
		}

		for(int i = 0; i < targetCount; i++){
			moved += flux / targetCount;
			targets[i].addFlux(flux / targetCount);
			FluxUtil.renderFlux(world, pos, ((TileEntity) targets[i]).getPos(), flux / targetCount);
		}
		flux -= moved;
		markDirty();
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		super.addInfo(chat, player, side, hitX, hitY, hitZ);
		for(BlockPos link : links){
			chat.add("Linked Position: X=" + (pos.getX() + link.getX()) + " Y=" + (pos.getY() + link.getY()) + " Z=" + (pos.getZ() + link.getZ()));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("flux", flux);
		for(int i = 0; i < links.size(); i++){
			nbt.setLong("link" + i, links.get(i).toLong());
		}

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		flux = nbt.getInteger("flux");
		for(int i = 0; i < getMaxLinks(); i++){
			if(nbt.hasKey("link" + i)){
				links.add(BlockPos.fromLong(nbt.getLong("link" + i)));
			}
		}
	}

	@Override
	public TileEntity getTE(){
		return this;
	}

	@Override
	public boolean canLink(ILinkTE otherTE){
		return isFluxEmitter() && otherTE instanceof IFluxHandler && ((IFluxHandler) otherTE).isFluxReceiver();
	}

	@Override
	public ArrayList<BlockPos> getLinks(){
		return links;
	}

	@Override
	public boolean canReceiveFlux(){
		return isFluxReceiver();
	}

	@Override
	public int getFlux(){
		return flux;
	}

	@Override
	public int getMaxLinks(){
		return 4;
	}

	@Override
	public int getCapacity(){
		return 64;
	}

	@Override
	public int addFlux(int fluxIn){
		flux += fluxIn;
		markDirty();
		if(flux > getCapacity()){
			world.destroyBlock(pos, false);
			world.createExplosion(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0, false);
			FluxUtil.fluxEvent(world, pos, 64);
		}
		return flux;
	}
}
