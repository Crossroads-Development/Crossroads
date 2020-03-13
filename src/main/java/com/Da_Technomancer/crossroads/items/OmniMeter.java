package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.templates.IBeamRenderTE;
import com.Da_Technomancer.crossroads.CRConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;

public class OmniMeter extends Item{

	protected OmniMeter(){
		super(new Item.Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1));
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
	public static void measure(ArrayList<ITextComponent> chat, PlayerEntity player, World world, BlockPos pos, Direction facing, BlockRayTraceResult hit){
		TileEntity te = world.getTileEntity(pos);
		if(te != null){
			LazyOptional<IFluidHandler> fluidOpt;
			if((fluidOpt = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)).isPresent()){
				IFluidHandler pipe = fluidOpt.orElseThrow(NullPointerException::new);

				int tanks = pipe.getTanks();
				if(tanks == 1){
					chat.add(new TranslationTextComponent("tt.crossroads.meter.fluid_tank.single"));
				}else{
					chat.add(new TranslationTextComponent("tt.crossroads.meter.fluid_tank.plural", tanks));
				}

				for(int tank = 0; tank < tanks; tank++){
					//Hi future me,
					//If you're (me're?) looking at this, someone wrote a translation lang file for CR and subsequently discovered that the fluid printout isn't localized properly
					//It's a straightforward fix- send the fluid registry name in the packet and localize on the client- it's just kind of weird
					chat.add(new TranslationTextComponent("tt.crossroads.meter.fluid_tank.info", pipe.getTankCapacity(tank), MiscUtil.getLocalizedFluidName(pipe.getFluidInTank(tank).getTranslationKey()), pipe.getFluidInTank(tank).getAmount()));
				}
			}

			LazyOptional<IAxisHandler> axisOpt;
			if((axisOpt = te.getCapability(Capabilities.AXIS_CAPABILITY, null)).isPresent()){
				chat.add(new TranslationTextComponent("tt.crossroads.meter.axis", CRConfig.formatVal(axisOpt.orElseThrow(NullPointerException::new).getTotalEnergy())));
			}

			LazyOptional<IEnergyStorage> engOpt;
			if((engOpt = te.getCapability(CapabilityEnergy.ENERGY, null)).isPresent()){
				IEnergyStorage batt = engOpt.orElseThrow(NullPointerException::new);
				chat.add(new TranslationTextComponent("tt.crossroads.meter.fe", batt.getEnergyStored(), batt.getMaxEnergyStored()));
			}
		}

		if(te instanceof IBeamRenderTE){
			BeamUnit[] mag = ((IBeamRenderTE) te).getLastSent();
			boolean output = false;
			if(mag != null){
				for(int i = 0; i < mag.length; i++){
					BeamUnit check = mag[i];
					if(!check.isEmpty()){
						output = true;
						EnumBeamAlignments.getAlignment(check).discover(player, true);
						String dir = Direction.byIndex(i).toString();
						dir = Character.toUpperCase(dir.charAt(0)) + dir.substring(1);
						chat.add(new TranslationTextComponent("tt.crossroads.meter.beam", dir, check.toString()));
					}
				}
			}
			if(!output){
				//Generic message so it doesn't output nothing to the user
				chat.add(new TranslationTextComponent("tt.crossroads.meter.beam.none"));
			}
		}

		if(te instanceof IInfoTE){
			((IInfoTE) te).addInfo(chat, player, hit);
		}
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context){
		if(!context.getWorld().isRemote){
			ArrayList<ITextComponent> chat = new ArrayList<>();

			BlockRayTraceResult result = new BlockRayTraceResult(context.getHitVec(), context.getFace(), context.getPos(), false);
			measure(chat, context.getPlayer(), context.getWorld(), context.getPos(), context.getFace(), result);

			if(!chat.isEmpty()){
				CRPackets.sendPacketToPlayer((ServerPlayerEntity) context.getPlayer(), new SendChatToClient(chat, CHAT_ID));
			}
		}

		return ActionResultType.SUCCESS;
	}
}
