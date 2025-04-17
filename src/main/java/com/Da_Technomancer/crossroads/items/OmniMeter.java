package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.api.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.api.templates.IInfoTE;
import com.Da_Technomancer.essentials.api.redstone.IRedstoneHandler;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;

public class OmniMeter extends Item{

	protected OmniMeter(){
		super(new Item.Properties().stacksTo(1));
		String name = "omnimeter";
		CRItems.queueForRegister(name, this);
	}

	public static final int CHAT_ID = 279478;//Value chosen at random

	/**
	 * For calling on the server side only
	 * @param chat The list to be populated with information
	 * @param player The player
	 * @param world The world
	 * @param pos The position aimed at
	 * @param facing The side clicked on
	 * @param hit Detailed target info
	 */
	public static void measure(ArrayList<Component> chat, Player player, Level world, BlockPos pos, Direction facing, BlockHitResult hit){
		BlockEntity te = world.getBlockEntity(pos);
		if(te != null){
			IFluidHandler pipeFluidHandler;
			if((pipeFluidHandler = world.getCapability(Capabilities.FluidHandler.BLOCK, te.getBlockPos(), null)) != null){

				int tanks = pipeFluidHandler.getTanks();
				if(tanks == 1){
					chat.add(Component.translatable("tt.crossroads.meter.fluid_tank.single"));
				}else{
					chat.add(Component.translatable("tt.crossroads.meter.fluid_tank.plural", tanks));
				}

				for(int tank = 0; tank < tanks; tank++){
					//Hi future me,
					//If you're (me're?) looking at this, someone wrote a translation lang file for CR and subsequently discovered that the fluid printout isn't localized properly
					//It's a straightforward fix- send the fluid registry name in the packet and localize on the client- it's just kind of weird
					chat.add(Component.translatable("tt.crossroads.meter.fluid_tank.info", pipeFluidHandler.getTankCapacity(tank), MiscUtil.getLocalizedFluidName(pipeFluidHandler.getFluidInTank(tank).getFluidType().getDescriptionId()), pipeFluidHandler.getFluidInTank(tank).getAmount()));
				}
			}

			IAxisHandler axisHandler;
			if((axisHandler = world.getCapability(CRCapabilities.AXIS_CAPABILITY, te.getBlockPos(), null)) != null){
				chat.add(Component.translatable("tt.crossroads.meter.axis.current", CRConfig.formatVal(axisHandler.getTotalEnergy()), CRConfig.formatVal(axisHandler.getBaseSpeed())));
				chat.add(Component.translatable("tt.crossroads.meter.axis.change", CRConfig.formatVal(axisHandler.getEnergyChange()), CRConfig.formatVal(axisHandler.getEnergyLost())));
			}

			IEnergyStorage battEnergyHandler;
			if((battEnergyHandler = world.getCapability(Capabilities.EnergyStorage.BLOCK, te.getBlockPos(), null)) != null){
				chat.add(Component.translatable("tt.crossroads.meter.fe", battEnergyHandler.getEnergyStored(), battEnergyHandler.getMaxEnergyStored()));
			}

			//Read circuit output
			IRedstoneHandler redstoneHandler;
			if((redstoneHandler = world.getCapability(RedstoneUtil.REDSTONE_CAPABILITY, te.getBlockPos(), null)) != null){
				chat.add(Component.translatable("tt.crossroads.meter.circuit", CRConfig.formatVal(redstoneHandler.getOutput())));
			}

			if(te instanceof IInfoTE infoTE){
				infoTE.addInfo(chat, player, hit);
			}
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected){
		if(!worldIn.isClientSide && entityIn instanceof Player player && (isSelected || player.getOffhandItem().getItem() == this)){
			BlockHitResult ray = MiscUtil.rayTrace(player, 8);
			if(ray == null){
				return;
			}
			ArrayList<Component> chat = new ArrayList<>();
			OmniMeter.measure(chat, player, player.level(), ray.getBlockPos(), ray.getDirection(), ray);
			if(!chat.isEmpty()){
				CRPackets.sendPacketToPlayer((ServerPlayer) player, new SendChatToClient(chat, OmniMeter.CHAT_ID, ray.getBlockPos()));
			}
		}
	}
}
