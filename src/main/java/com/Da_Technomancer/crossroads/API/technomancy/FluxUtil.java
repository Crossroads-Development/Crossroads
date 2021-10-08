package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyUtil;
import com.Da_Technomancer.crossroads.API.alchemy.EnumReagents;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.API.beams.BeamUtil;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.essentials.tileentities.ILinkTE;
import com.Da_Technomancer.essentials.tileentities.LinkHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class FluxUtil{

	/**
	 * transfer is a 2 stage process:
	 * time % FLUX_TIME == 0: prep to receive
	 * time % FLUX_TIME == 1: send flux
	 * other times: nothing happens
	 *
	 * This allows flux transfer to be tick-order independent
	 */
	public static final int FLUX_TIME = BeamUtil.BEAM_TIME;
	//	public static final int[] COLOR_CODES = new int[] {new Color(67, 0, 49).getRGB(), new Color(255, 68, 0).getRGB(), new Color(220, 64, 0).getRGB()};//color codes for flux rendering
	public static final int[] COLOR_CODES = new int[] {new Color(0, 0, 0).getRGB(), new Color(42, 0, 51).getRGB(), new Color(212, 192, 220).getRGB()};//color codes for flux rendering

	/**
	 * Transfer a given amount of flux from the src, and return any remainder
	 * Does not modify the source in any way; the source is responsible for removing successfully transferred flux
	 * @param src The source to transfer from
	 * @param links The linked relative positions to transfer to (will be checked)
	 * @param toTransfer The qty of flux to attempt to transfer
	 * @return A pair containing: The amount of untransferred flux, the rendered entropy array for syncing to the client
	 */
	public static Pair<Integer, int[]> performTransfer(IFluxLink src, Set<BlockPos> links, int toTransfer){
		if(toTransfer <= 0){
			return Pair.of(0, new int[0]);
		}
		Level world = src.getTE().getLevel();
		BlockPos pos = src.getTE().getBlockPos();
		//Run through each link and collect all the valid IFluxLink links
		//We have special handling for outputs in unloaded chunks- we send flux in that direction, but delete the flux rather than actually transfer it or loading the chunk
		//This is to prevent functional systems from having flux build up in unloaded chunk borders
		//The left entry is the relative position of this output
		//Each right entry is a TileEntity extending IFluxLink, or Boolean.TRUE to represent an output TE that is unloaded
		List<Pair<BlockPos, IFluxLink>> dests = links.stream().map((BlockPos linkPos) -> {
			BlockPos absPos = pos.offset(linkPos);
			if(world.hasChunkAt(absPos)){
				BlockEntity te = world.getBlockEntity(absPos);
				if(te instanceof IFluxLink && ((IFluxLink) te).allowAccepting()){
					return Pair.of(linkPos, (IFluxLink) te);
				}else{
					return null;//Invalid output, will be removed by the filter
				}
			}else{
				//Known issue: Because we don't load the output, we can't verified that the output position is actually a valid accepting IFluxLink te
				return Pair.of(linkPos, (IFluxLink) null);
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());

		if(dests.isEmpty()){
			return Pair.of(toTransfer, new int[0]);//no recipients
		}

		int toTransPer = toTransfer / dests.size();//Due to integer division, the source may have a small amount of flux remaining at the end
		int[] renderOutput = new int[dests.size()];
		for(int i = 0; i < dests.size(); i++){
			Pair<BlockPos, ?> dest = dests.get(i);
			//Skip transfer to unloaded outputs
			if(dest.getRight() != null){
				IFluxLink linked = (IFluxLink) dest.getRight();
				linked.addFlux(toTransPer);
//				src.addFlux(-toTransPer);
//				renderFlux(world, pos, linked.getTE().getPos(), toTransPer);
			}

			BlockPos relPos = dest.getLeft();
			renderOutput[i] = (relPos.getX() & 0xFF) | ((relPos.getY() & 0xFF) << 8) | ((relPos.getZ() & 0xFF) << 16);
		}
		return Pair.of(toTransfer - toTransPer * dests.size(), renderOutput);
	}

//	@Deprecated
//	public static void renderFlux(World world, BlockPos src, BlockPos dest, int qty){
//		if(qty > 0){
//			CRRenderUtil.addEntropyBeam(world, src.getX() + 0.5F, src.getY() + 0.5F, src.getZ() + 0.5F, dest.getX() + 0.5F, dest.getY() + 0.5F, dest.getZ() + 0.5F, qty, (byte) (FLUX_TIME + 1), true);
//		}
//	}

	/**
	 * Adds information about the flux of this TE
	 * @param tooltip The output list (will be modified)
	 * @param te The TE to provide info about
	 * @param fluxPerCycle Flux production. -1 to not display info about flux production
	 */
	public static void addFluxInfo(List<Component> tooltip, IFluxLink te, int fluxPerCycle){
		if(fluxPerCycle < 0){
			tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.flux_simple", te.getReadingFlux(), te.getMaxFlux(), CRConfig.formatVal(100F * te.getReadingFlux() / te.getMaxFlux())));
		}else{
			tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.flux", te.getReadingFlux(), te.getMaxFlux(), CRConfig.formatVal(100F * te.getReadingFlux() / te.getMaxFlux()), CRConfig.formatVal(fluxPerCycle)));
		}
	}

	public static void addLinkInfo(List<Component> tooltip, ILinkTE te){
		Set<BlockPos> links = te.getLinks();
		if(links.size() == 0){
			tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.link.none"));
		}else{
			BlockPos tePos = te.getTE().getBlockPos();
			int totalLinked = links.size();
			if(totalLinked <= 4){
				for(BlockPos relPos : links){
					BlockPos linkPos = tePos.offset(relPos);
					tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.link", linkPos.getX(), linkPos.getY(), linkPos.getZ()));
				}
			}else{
				int printed = 0;
				for(BlockPos relPos : links){
					BlockPos linkPos = tePos.offset(relPos);
					tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.link", linkPos.getX(), linkPos.getY(), linkPos.getZ()));
					if(++printed == 4){
						break;
					}
				}
				tooltip.add(new TranslatableComponent("tt.crossroads.boilerplate.link.excess", totalLinked + 1 - printed));
			}
		}
	}

	/**
	 * Checks whether flux is over the limit, and if so performs a flux event and destroys this block
	 * @param te The machine to check
	 * @return Whether this machine should shut down (flux > fluxLimit && safe mode enabled)
	 */
	public static boolean checkFluxOverload(IFluxLink te){
		if(te.getFlux() > te.getMaxFlux()){
			if(CRConfig.fluxSafeMode.get()){
				return true;
			}
			BlockEntity tileEntity = te.getTE();
			Level world = tileEntity.getLevel();
			BlockPos pos = tileEntity.getBlockPos();
			world.destroyBlock(pos, CRConfig.entropyDropBlock.get());
			fluxEvent(world, pos);
		}
		return false;
	}

	public static InteractionResult handleFluxLinking(Level world, BlockPos pos, ItemStack stack, Player player){
		if(LinkHelper.isLinkTool(stack)){
			BlockEntity te = world.getBlockEntity(pos);
			if(!world.isClientSide && te instanceof ILinkTE){
				LinkHelper.wrench((ILinkTE) te, stack, player);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	public static void fluxEvent(Level worldIn, BlockPos pos){
		if(CRConfig.fluxEvent.get()){
			//Create a random bad effect
			int selector = (int) (Math.random() * 100);
			if(selector < 50){
				//Explode
				//Equivalent to charged creeper explosion
				worldIn.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 5, CRConfig.entropyDropBlock.get() ? Explosion.BlockInteraction.BREAK : Explosion.BlockInteraction.DESTROY);
			}else if(selector < 65){
				//Alchemy phelostogen/voltus/salt cloud
				ReagentMap map = new ReagentMap();
				map.addReagent(EnumReagents.PHELOSTOGEN.id(), 32, 100);
				map.addReagent(EnumReagents.ALCHEMICAL_SALT.id(), 10, 100);
				map.addReagent(EnumReagents.ELEM_CHARGE.id(), 10, 100);
				AlchemyUtil.releaseChemical(worldIn, pos, map);
			}else if(selector < 72){
				//Alchemy phelostogen/aether/salt cloud
				ReagentMap map = new ReagentMap();
				map.addReagent(EnumReagents.PHELOSTOGEN.id(), 32, 100);
				map.addReagent(EnumReagents.ALCHEMICAL_SALT.id(), 10, 100);
				map.addReagent(EnumReagents.AETHER.id(), 10, 100);
				AlchemyUtil.releaseChemical(worldIn, pos, map);
			}else if(selector < 75){
				//Alchemy pure-phelostogen cloud
				ReagentMap map = new ReagentMap();
				map.addReagent(EnumReagents.PHELOSTOGEN.id(), 32, 100);
				AlchemyUtil.releaseChemical(worldIn, pos, map);
			}else{
				//potential-void area of effect
				EnumBeamAlignments.POTENTIAL.getEffect().doBeamEffect(EnumBeamAlignments.POTENTIAL, true, 64, worldIn, pos, null);
			}
		}else{
			//small explode (everything else disabled in config)
			//equivalent to TNT explosion
			worldIn.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 4, Explosion.BlockInteraction.BREAK);
		}
	}
}
