package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxLink;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.render.CRRenderUtil;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import com.Da_Technomancer.essentials.tileentities.ILinkTE;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Set;

@ObjectHolder(Crossroads.MODID)
public class FluxNodeTileEntity extends TileEntity implements ITickableTileEntity, IFluxLink{

	@ObjectHolder("flux_node")
	public static TileEntityType<FluxNodeTileEntity> type = null;

	private static final float SPIN_RATE = 3.6F;//For rendering

	private final IFluxLink.FluxHelper fluxHelper = new FluxHelper(this, Behaviour.NODE);
	private int entropyClient;//records what was last send to the client. Current value on the client side
	private float angle;//for rendering

	public FluxNodeTileEntity(){
		super(type);
	}

	private void syncFlux(){
		if((entropyClient == 0) ^ (fluxHelper.getReadingFlux() == 0) || Math.abs(entropyClient - fluxHelper.getReadingFlux()) >= 4){
			entropyClient = fluxHelper.getReadingFlux();
			CRPackets.sendPacketAround(world, pos, new SendLongToClient((byte) 0, fluxHelper.getReadingFlux(), pos));
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
	public AxisAlignedBB getRenderBoundingBox(){
		//Increase render BB to include links
		return new AxisAlignedBB(pos).grow(getRange());
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
		if(world.isRemote){
			angle += entropyClient * SPIN_RATE / 20F;
			//This 5 is the lifetime of the render
			if(world.getGameTime() % 5 == 0 && overSafeLimit()){
				CRRenderUtil.addArc(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, pos.getX() + 1.5F, pos.getY() + 1.5F, pos.getZ() + 1.5F, 3, 1F, FluxUtil.COLOR_CODES[(int) (world.getGameTime() % 3)]);
			}
		}else{
			fluxHelper.tick();
			syncFlux();
			markDirty();
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putFloat("angle", angle);
		fluxHelper.write(nbt);
		return nbt;
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putFloat("angle", angle);
		fluxHelper.write(nbt);
		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);
		angle = nbt.getFloat("angle");
		fluxHelper.read(nbt);
		entropyClient = fluxHelper.getReadingFlux();
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity serverPlayerEntity){
		fluxHelper.receiveLong(identifier, message, serverPlayerEntity);
		if(identifier == 0){
			entropyClient = (int) message;
		}
	}

	@Override
	public int getReadingFlux(){
		return fluxHelper.getReadingFlux();
	}

	@Override
	public int getFlux(){
		return fluxHelper.getFlux();
	}

	@Override
	public void addFlux(int deltaFlux){
		fluxHelper.addFlux(deltaFlux);
	}

	@Override
	public boolean canBeginLinking(){
		return fluxHelper.canBeginLinking();
	}

	@Override
	public boolean canLink(ILinkTE otherTE){
		return fluxHelper.canLink(otherTE);
	}

	@Override
	public Set<BlockPos> getLinks(){
		return fluxHelper.getLinks();
	}

	@Override
	public boolean createLinkSource(ILinkTE endpoint, @Nullable PlayerEntity player){
		return fluxHelper.createLinkSource(endpoint, player);
	}

	@Override
	public void removeLinkSource(BlockPos end){
		fluxHelper.removeLinkSource(end);
	}

	@Override
	public boolean allowAccepting(){
		//We accept flux as long as we don't have a redstone signal
		return RedstoneUtil.getRedstoneAtPos(world, pos) == 0;
	}

	@Override
	public boolean canAcceptLinks(){
		return fluxHelper.canAcceptLinks();
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		FluxUtil.addFluxInfo(chat, this, -1);
		fluxHelper.addInfo(chat, player, hit);
	}
}
