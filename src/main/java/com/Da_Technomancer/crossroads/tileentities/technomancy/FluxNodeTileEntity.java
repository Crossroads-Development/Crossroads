package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@ObjectHolder(Crossroads.MODID)
public class FluxNodeTileEntity extends TileEntity implements ITickableTileEntity, IFluxLink{

	@ObjectHolder("flux_node")
	private static TileEntityType<FluxNodeTileEntity> type = null;

	private static final float SPIN_RATE = 3.6F;//For rendering

	private HashSet<BlockPos> links = new HashSet<>(16);
	private int entropy;//On the client side, this is only occasionally updated
	private int entropyClient;//records what was last send to the client. 0 on the client side
	private float angle;//for rendering
	private int fluxToTrans = 0;

	public FluxNodeTileEntity(){
		super(type);
	}

	private void syncFlux(){
		if(entropyClient == 0 ^ entropy == 0 || Math.abs(entropyClient - entropy) >= 5){
			entropyClient = entropy;
			CRPackets.sendPacketAround(world, pos, new SendLongToClient((byte) 0, entropy, pos));
		}
	}

	/**
	 * For rendering
	 * @param partialTicks Partial ticks (for intermediate frames)
	 * @return The angle to render
	 */
	public float getRenderAngle(float partialTicks){
		return angle + partialTicks * entropy * SPIN_RATE / 20F;
	}

	/**
	 * For rendering
	 * @return Whether this node should render effects for being near the failure point
	 */
	private boolean overSafeLimit(){
		return entropy * 1.5F >= getMaxFlux();
	}

	@Override
	public void tick(){
		if(world.isRemote){
			angle += entropy * SPIN_RATE / 20F;
			//This 5 is distinct from FluxUtil.FLUX_TIME (despite being the same value) in that this is the lifetime of the render
			if(world.getGameTime() % 5 == 0 && overSafeLimit()){
				CRRenderUtil.addArc(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, pos.getX() + 1.5F, pos.getY() + 1.5F, pos.getZ() + 1.5F, 3, 1F, FluxUtil.COLOR_CODES[(int) (world.getGameTime() % 3)]);
			}
		}else if(world.getGameTime() % FluxUtil.FLUX_TIME == 0){
			fluxToTrans += entropy;//Save flux to a separate variable so tick order doesn't interfere with the amount transferred next tick
			entropy = 0;
			markDirty();
		}else if(world.getGameTime() % FluxUtil.FLUX_TIME == 1){
			//Perform transfer
			fluxToTrans -= FluxUtil.performTransfer(this, links, fluxToTrans);
			entropy += fluxToTrans;
			fluxToTrans = 0;
			FluxUtil.checkFluxOverload(this);
			markDirty();
			syncFlux();
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putFloat("angle", angle);
		nbt.putInt("entropy", entropy);
		nbt.putInt("flux_trans", fluxToTrans);
		int count = 0;
		for(BlockPos relPos : links){
			nbt.putLong("link_" + count++, relPos.toLong());
		}
		return nbt;
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putInt("entropy", entropy);
		nbt.putFloat("angle", angle);
		int count = 0;
		for(BlockPos relPos : links){
			nbt.putLong("link_" + count++, relPos.toLong());
		}
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		angle = nbt.getFloat("angle");
		entropy = nbt.getInt("entropy");
		entropyClient = entropy;
		fluxToTrans = nbt.getInt("flux_trans");
		int count = 0;
		while(nbt.contains("link_" + count)){
			links.add(BlockPos.fromLong(nbt.getLong("link_" + count)));
			count++;
		}
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity serverPlayerEntity){
		if(identifier == 0){
			entropy = (int) message;
		}
		if(identifier == LINK_PACKET_ID){
			links.add(BlockPos.fromLong(message));
			markDirty();
		}else if(identifier == CLEAR_PACKET_ID){
			links.clear();
			markDirty();
		}
	}

	public int getReadout(){
		return Math.max(entropy, fluxToTrans);
	}

	@Override
	public int getFlux(){
		return entropy;
	}

	@Override
	public void addFlux(int deltaFlux){
		entropy += deltaFlux;
		markDirty();
	}

	@Override
	public void setFlux(int newFlux){
		entropy = newFlux;
		markDirty();
	}

	@Override
	public Set<BlockPos> getLinks(){
		return links;
	}

	@Override
	public Behaviour getBehaviour(){
		return Behaviour.NODE;
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		FluxUtil.addFluxInfo(chat, this, -1);
		FluxUtil.addLinkInfo(chat, this);
	}
}
