package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class FluxNodeTileEntity extends IFluxLink.FluxHelper{

	@ObjectHolder("flux_node")
	public static BlockEntityType<FluxNodeTileEntity> TYPE = null;

	private static final float SPIN_RATE = 3.6F;//For rendering

	private int entropyClient;//records what was last send to the client. Current value on the client side
	private float angle;//for rendering

	public FluxNodeTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state, null, Behaviour.NODE);
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

	@Override
	public boolean renderFluxWarning(){
		return entropyClient + CRConfig.fluxNodeGain.get() * 4 >= getMaxFlux();
	}

	@Override
	public void clientTick(){
		super.clientTick();
		angle += entropyClient * SPIN_RATE / 20F;
	}

	@Override
	public void serverTick(){
		super.serverTick();
		if(lastTick != level.getGameTime() && level.getGameTime() % FluxUtil.FLUX_TIME == 0 && !isShutDown()){
			if(flux > 0){
				flux += CRConfig.fluxNodeGain.get();
			}
		}
		super.tick();
		syncFlux();
		setChanged();
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
		save(nbt);//We need the ability to getReadingFlux() on the client when loading
//		nbt.putFloat("angle", angle);
		return nbt;
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
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
