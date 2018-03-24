package com.Da_Technomancer.crossroads.entity;

import java.awt.Color;
import java.util.ArrayList;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.EnumSolventType;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.packets.INbtReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.NbtToEntityClient;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityFlameCore extends Entity implements INbtReceiver{

	private int maxRadius;
	/**
	 * In order to avoid iterating over a large ReagentStack[] that is mostly empty several times a tick, this list is created to store all non-null reagent stacks
	 */
	private ArrayList<ReagentStack> reagList = new ArrayList<ReagentStack>();
	protected Color col;

	public EntityFlameCore(World worldIn){
		super(worldIn);
		setSize(1F, 1F);
		setNoGravity(true);
		noClip = true;
		ignoreFrustumCheck = true;
	}

	public void setInitialValues(ReagentStack[] reags, double temp, int radius){
		this.reags = reags == null ? new ReagentStack[AlchemyCore.REAGENT_COUNT] : reags;
		this.temp = temp;
		reagList.clear();
		double r = 0;
		double g = 0;
		double b = 0;
		double a = 0;
		double amount = 0;
		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			if(reags[i] != null){
				Color color = reags[i].getType().getColor(reags[i].getPhase(temp));
				r += reags[i].getAmount() * (double) color.getRed();
				g += reags[i].getAmount() * (double) color.getGreen();
				b += reags[i].getAmount() * (double) color.getBlue();
				a += reags[i].getAmount() * (double) color.getAlpha();
				amount += reags[i].getAmount();

				if(i != 0 && i != 37){//Skips i=0 (phelostigen) & i=37 (ignus infernum)
					reagList.add(reags[i]);
				}
			}
		}
		maxRadius = radius;
		NBTTagCompound nbt = new NBTTagCompound();
		col = new Color((int) (r / amount), (int) (g / amount), (int) (b / amount), (int) (a / amount));
		nbt.setInteger("col", col.getRGB());
		ModPackets.network.sendToAllAround(new NbtToEntityClient(getUniqueID(), nbt), new TargetPoint(world.provider.getDimension(), posX, posY, posZ, 512));
	}

	private ReagentStack[] reags = null;
	private double temp = 0;

	@Override
	protected void entityInit(){

	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRender3d(double x, double y, double z){
		return true;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		reags = new ReagentStack[AlchemyCore.REAGENT_COUNT];
		temp = nbt.getDouble("temp");
		maxRadius = nbt.getInteger("rad");
		boolean[] solvents = new boolean[EnumSolventType.values().length];

		for(int i = 0; i < solvents.length; i++){
			solvents[i] = nbt.getBoolean(i + "_solv");
		}

		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			if(nbt.hasKey(i + "_am")){
				reags[i] = new ReagentStack(AlchemyCore.REAGENTS[i], nbt.getDouble(i + "_am"));
				reags[i].updatePhase(temp, solvents);
			}
		}

		ticksExisted = nbt.getInteger("life");
		setInitialValues(reags, temp, maxRadius);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		boolean[] solvents = new boolean[EnumSolventType.values().length];

		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			ReagentStack reag = reags[i];
			if(reag == null){
				nbt.removeTag(i + "_am");
			}else{
				nbt.setDouble(i + "_am", reag.getAmount());

				IReagent type = reag.getType();
				solvents[EnumSolventType.AQUA_REGIA.ordinal()] |= i == 11;//Aqua regia is a special case where it works no matter the phase, but ONLY works at all if a polar solvent is present. 

				if(type.getMeltingPoint() <= temp && type.getBoilingPoint() > temp && type.solventType() != null){
					solvents[type.solventType().ordinal()] = true;
				}
			}
		}

		solvents[EnumSolventType.AQUA_REGIA.ordinal()] &= solvents[EnumSolventType.POLAR.ordinal()];

		for(int i = 0; i < solvents.length; i++){
			nbt.setBoolean(i + "_solv", solvents[i]);
		}

		nbt.setDouble("temp", temp);
		nbt.setInteger("rad", maxRadius);
		nbt.setInteger("life", ticksExisted);
	}

	protected int ticksExisted = -1;
	protected static final float FLAME_VEL = 0.1F;//Flame interface speed, blocks per tick

	@Override
	public void onUpdate(){
		if(col == null){
			return;
		}

		super.onUpdate();

		ticksExisted++;

		if(!world.isRemote && ticksExisted % 8 == 0){
			int radius = (int) Math.round(FLAME_VEL * (float) ticksExisted);
			BlockPos pos = new BlockPos(posX, posY, posZ);


			for(int i = 0; i <= radius; i++){
				for(int j = 0; j <= radius; j++){
					//x-z plane
					act(reagList, reags, temp, world, pos.add(i, -radius, j));
					act(reagList, reags, temp, world, pos.add(i - radius, -radius, j));
					act(reagList, reags, temp, world, pos.add(i, -radius, j - radius));
					act(reagList, reags, temp, world, pos.add(i - radius, -radius, j - radius));
					act(reagList, reags, temp, world, pos.add(i, radius, j));
					act(reagList, reags, temp, world, pos.add(i - radius, radius, j));
					act(reagList, reags, temp, world, pos.add(i, radius, j - radius));
					act(reagList, reags, temp, world, pos.add(i - radius, radius, j - radius));
					//x-y plane
					act(reagList, reags, temp, world, pos.add(i, j, -radius));
					act(reagList, reags, temp, world, pos.add(i - radius, j, -radius));
					act(reagList, reags, temp, world, pos.add(i, j - radius, -radius));
					act(reagList, reags, temp, world, pos.add(i - radius, j - radius, -radius));
					act(reagList, reags, temp, world, pos.add(i, j, radius));
					act(reagList, reags, temp, world, pos.add(i - radius, j, radius));
					act(reagList, reags, temp, world, pos.add(i, j - radius, radius));
					act(reagList, reags, temp, world, pos.add(i - radius, j - radius, radius));
					//y-z plane
					act(reagList, reags, temp, world, pos.add(-radius, i, j));
					act(reagList, reags, temp, world, pos.add(-radius, i - radius, j));
					act(reagList, reags, temp, world, pos.add(-radius, i, j - radius));
					act(reagList, reags, temp, world, pos.add(-radius, i - radius, j - radius));
					act(reagList, reags, temp, world, pos.add(radius, i, j));
					act(reagList, reags, temp, world, pos.add(radius, i - radius, j));
					act(reagList, reags, temp, world, pos.add(radius, i, j - radius));
					act(reagList, reags, temp, world, pos.add(radius, i - radius, j - radius));
				}
			}

			if(maxRadius <= radius){
				setDead();
			}
		}
	}

	private static void act(ArrayList<ReagentStack> reagList, ReagentStack[] reags, double temp, World world, BlockPos pos){
		if(reags[16] == null){
			float hardness = world.getBlockState(pos).getBlockHardness(world, pos);

			if(hardness >= 0 && hardness <= 3F){
				world.setBlockState(pos, Blocks.FIRE.getDefaultState());
			}
		}

		for(ReagentStack r : reagList){
			if(r != null){//Should never contain null
				r.getType().onRelease(world, pos, r.getAmount(), temp, r.getPhase(temp), reags);
			}
		}
	}

	@Override
	public boolean canTriggerWalking(){
		return false;
	}

	@Override
	public void receiveNBT(NBTTagCompound nbt){
		int colorCode = nbt.getInteger("col");
		col = Color.decode(Integer.toString(colorCode & 0xFFFFFF));
		col = new Color(col.getRed(), col.getGreen(), col.getBlue(), colorCode >>> 24);
	}
}
