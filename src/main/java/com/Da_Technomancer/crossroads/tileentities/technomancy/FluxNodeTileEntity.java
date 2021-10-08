package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink.Behaviour;

@ObjectHolder(Crossroads.MODID)
public class FluxNodeTileEntity extends IFluxLink.FluxHelper{

	@ObjectHolder("flux_node")
	public static BlockEntityType<FluxNodeTileEntity> type = null;

	private static final float SPIN_RATE = 3.6F;//For rendering

	private int entropyClient;//records what was last send to the client. Current value on the client side
	private float angle;//for rendering

	public FluxNodeTileEntity(){
		super(type, null, Behaviour.NODE);
	}

	private void syncFlux(){
		if((entropyClient == 0) ^ (getReadingFlux() == 0) || Math.abs(entropyClient - getReadingFlux()) >= 4){
			entropyClient = getReadingFlux();
			CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient((byte) 0, getReadingFlux(), worldPosition));
		}
	}

	/**
	 * For rendering
	 * @param partialTicks Partial ticks (for intermediate frames)
	 * @return The angle to render
	 */
	public float getRenderAngle(float partialTicks){
		return angle + partialTicks * entropyClient * SPIN_RATE / 20F;
	}

	@Override
	public AABB getRenderBoundingBox(){
		//Increase render BB to include links
		return new AABB(worldPosition).inflate(getRange());
	}

	/**
	 * For rendering
	 * @return Whether this node should render effects for being near the failure point
	 */
	private boolean overSafeLimit(){
		return entropyClient * 1.5F >= getMaxFlux();
	}

	@Override
	public void tick(){
		if(level.isClientSide){
			super.tick();
			angle += entropyClient * SPIN_RATE / 20F;
			//This 5 is the lifetime of the render
			if(level.getGameTime() % 5 == 0 && overSafeLimit()){
				CRRenderUtil.addArc(level, worldPosition.getX() + 0.5F, worldPosition.getY() + 0.5F, worldPosition.getZ() + 0.5F, worldPosition.getX() + 0.5F + (float) Math.random(), worldPosition.getY() + 0.5F + (float) Math.random(), worldPosition.getZ() + 0.5F + (float) Math.random(), 3, 1F, FluxUtil.COLOR_CODES[(int) (level.getGameTime() % 3)]);
			}
		}else{
			if(lastTick != level.getGameTime() && level.getGameTime() % FluxUtil.FLUX_TIME == 0 && !isShutDown()){
				if(flux > 0){
					flux += CRConfig.fluxNodeGain.get();
				}
			}
			super.tick();
			syncFlux();
			setChanged();
		}
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putFloat("angle", angle);
		return nbt;
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		nbt.putFloat("angle", angle);
		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundTag nbt){
		super.load(state, nbt);
		angle = nbt.getFloat("angle");
		entropyClient = getReadingFlux();
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer serverPlayerEntity){
		super.receiveLong(identifier, message, serverPlayerEntity);
		if(identifier == 0){
			entropyClient = (int) message;
		}
	}

	@Override
	public boolean allowAccepting(){
		//We accept flux as long as we don't have a redstone signal and we aren't shut down
		return RedstoneUtil.getRedstoneAtPos(level, worldPosition) == 0 && !isShutDown();
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		FluxUtil.addFluxInfo(chat, this, -1);
		super.addInfo(chat, player, hit);
	}
}
