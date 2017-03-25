package com.Da_Technomancer.crossroads.API.packets;

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

	public String sContext;
	public int message;
	public BlockPos pos;
	public int dim;
	
	public SendIntToServer(String context, int message, BlockPos pos, int dim){
		this.sContext = context;
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
				processMessage(DimensionManager.getWorld(dim), sContext, message, pos);
			}
		});

		return null;
	}

	public void processMessage(World world, String context, int message, BlockPos pos){
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof IIntReceiver){
			((IIntReceiver) te).receiveInt(context, message);
		}
	}
}
