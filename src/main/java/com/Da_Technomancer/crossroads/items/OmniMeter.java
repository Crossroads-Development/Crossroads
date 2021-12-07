package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.essentials.blocks.redstone.IRedstoneHandler;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;

public class OmniMeter extends Item{

	protected OmniMeter(){
		super(new Item.Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
		String name = "omnimeter";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	private static final int CHAT_ID = 279478;//Value chosen at random

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
			LazyOptional<IFluidHandler> fluidOpt;
			if((fluidOpt = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)).isPresent()){
				IFluidHandler pipe = fluidOpt.orElseThrow(NullPointerException::new);

				int tanks = pipe.getTanks();
				if(tanks == 1){
					chat.add(new TranslatableComponent("tt.crossroads.meter.fluid_tank.single"));
				}else{
					chat.add(new TranslatableComponent("tt.crossroads.meter.fluid_tank.plural", tanks));
				}

				for(int tank = 0; tank < tanks; tank++){
					//Hi future me,
					//If you're (me're?) looking at this, someone wrote a translation lang file for CR and subsequently discovered that the fluid printout isn't localized properly
					//It's a straightforward fix- send the fluid registry name in the packet and localize on the client- it's just kind of weird
					chat.add(new TranslatableComponent("tt.crossroads.meter.fluid_tank.info", pipe.getTankCapacity(tank), MiscUtil.getLocalizedFluidName(pipe.getFluidInTank(tank).getTranslationKey()), pipe.getFluidInTank(tank).getAmount()));
				}
			}

			LazyOptional<IAxisHandler> axisOpt;
			if((axisOpt = te.getCapability(Capabilities.AXIS_CAPABILITY, null)).isPresent()){
				IAxisHandler axisHandler = axisOpt.orElseThrow(NullPointerException::new);
				chat.add(new TranslatableComponent("tt.crossroads.meter.axis.current", CRConfig.formatVal(axisHandler.getTotalEnergy()), CRConfig.formatVal(axisHandler.getBaseSpeed())));
				chat.add(new TranslatableComponent("tt.crossroads.meter.axis.change", CRConfig.formatVal(axisHandler.getEnergyChange()), CRConfig.formatVal(axisHandler.getEnergyLost())));
			}

			LazyOptional<IEnergyStorage> engOpt;
			if((engOpt = te.getCapability(CapabilityEnergy.ENERGY, null)).isPresent()){
				IEnergyStorage batt = engOpt.orElseThrow(NullPointerException::new);
				chat.add(new TranslatableComponent("tt.crossroads.meter.fe", batt.getEnergyStored(), batt.getMaxEnergyStored()));
			}

			//Read circuit output
			LazyOptional<IRedstoneHandler> redsOpt;
			if((redsOpt = te.getCapability(RedstoneUtil.REDSTONE_CAPABILITY, null)).isPresent()){
				IRedstoneHandler redstoneHandler = redsOpt.orElseThrow(NullPointerException::new);
				chat.add(new TranslatableComponent("tt.crossroads.meter.circuit", CRConfig.formatVal(redstoneHandler.getOutput())));
			}
		}

//		if(te instanceof IBeamRenderTE){
//			BeamUnit[] mag = ((IBeamRenderTE) te).getLastSent();
//			boolean output = false;
//			if(mag != null){
//				for(int i = 0; i < mag.length; i++){
//					BeamUnit check = mag[i];
//					if(!check.isEmpty()){
//						output = true;
//						EnumBeamAlignments.getAlignment(check).discover(player, true);
//						String dir = Direction.byIndex(i).toString();
//						dir = Character.toUpperCase(dir.charAt(0)) + dir.substring(1);
//						chat.add(new TranslationTextComponent("tt.crossroads.meter.beam", dir, check.toString()));
//					}
//				}
//			}
//			if(!output){
//				//Generic message so it doesn't output nothing to the user
//				chat.add(new TranslationTextComponent("tt.crossroads.meter.beam.none"));
//			}
//		}

		if(te instanceof IInfoTE){
			((IInfoTE) te).addInfo(chat, player, hit);
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		if(!context.getLevel().isClientSide){
			ArrayList<Component> chat = new ArrayList<>();

			BlockHitResult result = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), false);
			measure(chat, context.getPlayer(), context.getLevel(), context.getClickedPos(), context.getClickedFace(), result);

			if(!chat.isEmpty()){
				CRPackets.sendPacketToPlayer((ServerPlayer) context.getPlayer(), new SendChatToClient(chat, CHAT_ID));
			}
		}

		return InteractionResult.SUCCESS;
	}
}
