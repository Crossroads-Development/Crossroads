package com.Da_Technomancer.crossroads.API.packets;

import com.Da_Technomancer.essentials.packets.Message;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("serial")
public class SendIntToServer extends Message<SendIntToServer>{

	public SendIntToServer(){
		
	}

	public int identifier;
	public int message;
	public BlockPos pos;
	public int dim;
	
	public SendIntToServer(int identifier, int message, BlockPos pos, int dim){
		this.identifier = identifier;
		this.message = message;
		this.pos = pos;
		this.dim = dim;
	}

	@Override
	public IMessage handleMessage(MessageContext context){
		if(context.side != Side.SERVER){
			System.err.println("MessageToServer received on wrong side:" + context.side);
			return null;
		}

		DimensionManager.getWorld(dim).addScheduledTask(new Runnable(){
			@Override
			public void run(){
				processMessage(DimensionManager.getWorld(dim), identifier, message, pos, context.getServerHandler().player);
			}
		});

		return null;
	}

	public void processMessage(World world, int identifier, int message, BlockPos pos, EntityPlayerMP sendingPlayer){
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof IIntReceiver){
			((IIntReceiver) te).receiveInt(identifier, message, sendingPlayer);
		}
	}
}
