package com.Da_Technomancer.crossroads.API.technomancy;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyUtil;
import com.Da_Technomancer.crossroads.API.alchemy.EnumReagents;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.render.RenderUtil;
import com.Da_Technomancer.essentials.tileentities.ILinkTE;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FluxUtil{

	/**
	 * transfer is a 2 stage process:
	 * time % FLUX_TIME == 0: prep to receive
	 * time % FLUX_TIME == 1: send flux
	 * other times: nothing happens
	 *
	 * This allows flux transfer to tick-order independent
	 */
	public static final int FLUX_TIME = BeamManager.BEAM_TIME;
	private static final int[] COLOR_CODES = new int[] {0xFFF000, 0xFFFA00, 0xFFA000};//color codes for flux rendering

	public static void performTransfer(IFluxLink src, BlockPos linkPos){
		World world = src.getTE().getWorld();
		BlockPos endPos = src.getTE().getPos().add(linkPos);
		TileEntity te = world.getTileEntity(endPos);
		if(te instanceof IFluxLink){
			int srcFlux = src.getFlux();
			((IFluxLink) te).addFlux(srcFlux);
			src.setFlux(0);
			renderFlux(world, src.getTE().getPos(), endPos, srcFlux);
		}
	}

	/**
	 * Transfer a given amount of flux from the src, and return any remainder
	 * @param src The source to transfer from
	 * @param links The linked relative positions to transfer to (will be checked)
	 * @param toTransfer The qty of flux to attempt to transfer
	 * @return The amount of untransfered flux
	 */
	public static int performTransfer(IFluxLink src, Set<BlockPos> links, int toTransfer){
		World world = src.getTE().getWorld();
		BlockPos pos = src.getTE().getPos();
		//Run through each link and collect all the valid IFluxLink links
		//Each object is a TileEntity extending IFluxLink
		Object[] dests = links.stream().map((linkPos) -> world.getTileEntity(pos.add(linkPos))).filter((te) -> te instanceof IFluxLink).toArray();
		int toTransPer = toTransfer / dests.length;//Due to integer division, the source may have a small amount of flux remaining at the end
		for(Object dest : dests){
			IFluxLink linked = (IFluxLink) dest;
			linked.addFlux(toTransPer);
			src.addFlux(-toTransPer);
			renderFlux(world, pos, linked.getTE().getPos(), toTransPer);
		}
		return toTransfer - toTransPer * dests.length;
	}

	public static void renderFlux(World world, BlockPos src, BlockPos dest, int qty){
		//This is basically a carbon copy of the tesla coil render code- this should probably be tweaked to make it more thematic
		RenderUtil.addArc(world, src.getX() + 0.5F, src.getY() + 0.5F, src.getZ() + 0.5F, dest.getX() + 0.5F, dest.getY() + 0.5F, dest.getZ() + 0.5F, 3, 0.3F, COLOR_CODES[(int) (world.getGameTime() % 3)]);
	}

	public static Set<BlockPos> makeLinkSet(BlockPos link){
		HashSet<BlockPos> out = new HashSet<>(1);
		if(link != null){
			out.add(link);
		}
		return out;
	}

	public static void addLinkInfo(List<ITextComponent> tooltip, ILinkTE te){
		Set<BlockPos> links = te.getLinks();
		if(links.size() == 0){
			tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.link.none"));
		}else{
			BlockPos tePos = te.getTE().getPos();
			int totalLinked = links.size();
			if(totalLinked <= 4){
				for(BlockPos relPos : links){
					BlockPos linkPos = tePos.add(relPos);
					tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.link", linkPos.getX(), linkPos.getY(), linkPos.getZ()));
				}
			}else{
				int printed = 0;
				for(BlockPos relPos : links){
					BlockPos linkPos = tePos.add(relPos);
					tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.link", linkPos.getX(), linkPos.getY(), linkPos.getZ()));
					if(++printed == 4){
						break;
					}
				}
				tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.link.excess", totalLinked + 1 - printed));
			}
		}
	}

	/**
	 * Checks whether flux is over the limit, and if so performs a flux event and destroys this block
	 * @param te The machine to check
	 * @return Whether this machine was destroyed (flux > fluxLimit)
	 */
	public static boolean checkFluxOverload(IFluxLink te){
		if(te.getFlux() > te.getMaxFlux()){
			World world = te.getTE().getWorld();
			BlockPos pos = te.getTE().getPos();
			world.destroyBlock(pos, CRConfig.entropyDropBlock.get());
			fluxEvent(world, pos);
			return true;
		}
		return false;
	}

	public static boolean handleFluxLinking(World world, BlockPos pos, ItemStack stack, PlayerEntity player){
		if(ILinkTE.isLinkTool(stack)){
			TileEntity te = world.getTileEntity(pos);
			if(!world.isRemote && te instanceof ILinkTE){
				((ILinkTE) te).wrench(stack, player);
			}
			return true;
		}
		return false;
	}

	public static void fluxEvent(World worldIn, BlockPos pos){
		if(!CRConfig.fluxEvent.get()){
			//small explode (everything else disabled in config)
			//equivalent to TNT explosion
			worldIn.createExplosion(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 4, Explosion.Mode.BREAK);
		}
		//Create a random bad effect
		int selector = (int) (Math.random() * 100);
		if(selector < 50){
			//Explode
			//Equivalent to charged creeper explosion
			worldIn.createExplosion(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 5, CRConfig.entropyDropBlock.get() ? Explosion.Mode.BREAK : Explosion.Mode.DESTROY);
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
	}
}
