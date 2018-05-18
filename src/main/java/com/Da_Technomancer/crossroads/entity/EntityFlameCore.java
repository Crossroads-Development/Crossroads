package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.packets.INbtReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.NbtToEntityClient;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.ArrayList;

import static net.minecraft.tileentity.TileEntity.INFINITE_EXTENT_AABB;

public class EntityFlameCore extends Entity implements INbtReceiver{

	private int maxRadius;
	/**
	 * In order to avoid iterating over a large ReagentStack[] that is mostly empty several times a tick, this list is created to store all non-null reagent stacks
	 */
	private ArrayList<ReagentStack> reagList = new ArrayList<>();
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
		maxRadius = radius;
	}

	private ReagentStack[] reags = null;
	private double temp = 0;

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
	}

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

		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			if(nbt.hasKey(i + "_am")){
				reags[i] = new ReagentStack(AlchemyCore.REAGENTS[i], nbt.getDouble(i + "_am"));
				reags[i].updatePhase(temp);
			}
		}

		ticksExisted = nbt.getInteger("life");
		setInitialValues(reags, temp, maxRadius);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){

		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			ReagentStack reag = reags[i];
			if(reag == null){
				nbt.removeTag(i + "_am");
			}else{
				nbt.setDouble(i + "_am", reag.getAmount());
			}
		}

		nbt.setDouble("temp", temp);
		nbt.setInteger("rad", maxRadius);
		nbt.setInteger("life", ticksExisted);
	}

	protected int ticksExisted = -1;
	protected static final float FLAME_VEL = 0.1F;//Flame interface speed, blocks per tick

	@Override
	public void onUpdate(){
		ticksExisted++;
		super.onUpdate();

		if(world.isRemote){
			return;
		}

		if(col == null){
			reagList.clear();
			double r = 0;
			double g = 0;
			double b = 0;
			double a = 0;
			double amount = 0;
			for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
				if(this.reags[i] != null){
					Color color = this.reags[i].getType().getColor(this.reags[i].getPhase(temp));
					r += this.reags[i].getAmount() * (double) color.getRed();
					g += this.reags[i].getAmount() * (double) color.getGreen();
					b += this.reags[i].getAmount() * (double) color.getBlue();
					a += this.reags[i].getAmount() * (double) color.getAlpha();
					amount += this.reags[i].getAmount();

					if(i != 0 && i != 37){//Skips i=0 (phelostigen) & i=37 (ignus infernum)
						reagList.add(this.reags[i]);
					}
				}
			}
			NBTTagCompound nbt = new NBTTagCompound();
			col = new Color((int) (r / amount), (int) (g / amount), (int) (b / amount), (int) (a / amount));
			nbt.setInteger("col", col.getRGB());
			nbt.setInteger("life", ticksExisted);
			ModPackets.network.sendToAllAround(new NbtToEntityClient(getUniqueID(), nbt), new TargetPoint(world.provider.getDimension(), posX, posY, posZ, 512));
		}

		if(ticksExisted % 8 == 0){
			int radius = (int) Math.round(FLAME_VEL * (float) ticksExisted);
			BlockPos pos = new BlockPos(posX, posY, posZ);
			boolean lastAction = maxRadius <= radius;

			for(int i = 0; i <= radius; i++){
				for(int j = 0; j <= radius; j++){
					//x-z plane
					act(reagList, reags, temp, world, pos.add(i, -radius, j), lastAction);
					act(reagList, reags, temp, world, pos.add(i - radius, -radius, j), lastAction);
					act(reagList, reags, temp, world, pos.add(i, -radius, j - radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i - radius, -radius, j - radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i, radius, j), lastAction);
					act(reagList, reags, temp, world, pos.add(i - radius, radius, j), lastAction);
					act(reagList, reags, temp, world, pos.add(i, radius, j - radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i - radius, radius, j - radius), lastAction);
					//x-y plane
					act(reagList, reags, temp, world, pos.add(i, j, -radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i - radius, j, -radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i, j - radius, -radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i - radius, j - radius, -radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i, j, radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i - radius, j, radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i, j - radius, radius), lastAction);
					act(reagList, reags, temp, world, pos.add(i - radius, j - radius, radius), lastAction);
					//y-z plane
					act(reagList, reags, temp, world, pos.add(-radius, i, j), lastAction);
					act(reagList, reags, temp, world, pos.add(-radius, i - radius, j), lastAction);
					act(reagList, reags, temp, world, pos.add(-radius, i, j - radius), lastAction);
					act(reagList, reags, temp, world, pos.add(-radius, i - radius, j - radius), lastAction);
					act(reagList, reags, temp, world, pos.add(radius, i, j), lastAction);
					act(reagList, reags, temp, world, pos.add(radius, i - radius, j), lastAction);
					act(reagList, reags, temp, world, pos.add(radius, i, j - radius), lastAction);
					act(reagList, reags, temp, world, pos.add(radius, i - radius, j - radius), lastAction);
				}
			}

			if(lastAction){
				setDead();
			}
		}
	}

	private static void act(ArrayList<ReagentStack> reagList, ReagentStack[] reags, double temp, World world, BlockPos pos, boolean lastAction){
		if(reags[16] == null){
			IBlockState state = world.getBlockState(pos);

			if(state.getBlockHardness(world, pos) >= 0){
				world.setBlockState(pos, lastAction && Math.random() > 0.75D && Blocks.FIRE.canPlaceBlockAt(world, pos) ? Blocks.FIRE.getDefaultState() : Blocks.AIR.getDefaultState(), lastAction ? 3 : 18);
			}
		}

		for(ReagentStack r : reagList){
			if(r != null){//Should never contain null
				r.getType().onRelease(world, pos, r.getAmount(), temp, EnumMatterPhase.FLAME, reags);
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
		ticksExisted = nbt.getInteger("life");
	}
}
