package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.effects.alchemy.IAlchEffect;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.NbtToEntityClient;
import com.Da_Technomancer.crossroads.CRConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
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

	public void setInitialValues(ReagentMap reags, int radius){
		this.reags = reags == null ? new ReagentMap() : reags;
		maxRadius = radius;
	}

	private ReagentMap reags = null;

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
	}

	@Override
	protected void entityInit(){

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isInRangeToRender3d(double x, double y, double z){
		return true;
	}

	@Override
	public void readEntityFromNBT(CompoundNBT nbt){
		reags = new ReagentMap();
		maxRadius = nbt.getInt("rad");
		reags = ReagentMap.readFromNBT(nbt);
		ticksExisted = nbt.getInt("life");
		setInitialValues(reags, maxRadius);
	}

	@Override
	public void writeEntityToNBT(CompoundNBT nbt){
		if(reags != null){
			reags.writeToNBT(nbt);
		}

		nbt.putInt("rad", maxRadius);
		nbt.putInt("life", ticksExisted);
	}

	protected int ticksExisted = -1;
	protected static final float FLAME_VEL = 0.1F;//Flame interface speed, blocks per tick

	@Override
	public void onUpdate(){
		ticksExisted++;
		super.onUpdate();

		if(world.isRemote || reags == null){
			return;
		}

		if(col == null){
			reagList.clear();
			int r = 0;
			int g = 0;
			int b = 0;
			int a = 0;
			int amount = 0;
			double temp = reags.getTempC();
			for(IReagent type : reags.keySet()){
				int qty = reags.getQty(type);
				if(qty > 0){
					Color color = type.getColor(type.getPhase(temp));
					r += qty * color.getRed();
					g += qty * color.getGreen();
					b += qty * color.getBlue();
					a += qty * color.getAlpha();
					amount += qty;

					if(!type.getId().equals(EnumReagents.PHELOSTOGEN.id()) && !type.getId().equals(EnumReagents.HELLFIRE.id())){
						reagList.add(reags.getStack(type));
					}
				}
			}
			if(amount <= 0){
				setDead();
				return;
			}

			CompoundNBT nbt = new CompoundNBT();
			col = new Color(r / amount, g / amount, b / amount, a / amount);
			nbt.putInt("col", col.getRGB());
			nbt.putInt("life", ticksExisted);
			CrossroadsPackets.network.sendToAllAround(new NbtToEntityClient(getUniqueID(), nbt), new TargetPoint(world.provider.getDimension(), posX, posY, posZ, 512));
		}

		if(ticksExisted % 8 == 0){
			int radius = (int) Math.round(FLAME_VEL * (float) ticksExisted);
			BlockPos pos = new BlockPos(posX, posY, posZ);
			boolean lastAction = maxRadius <= radius;

			double temp = reags.getTempC();

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

	private static void act(ArrayList<ReagentStack> reagList, ReagentMap reags, double temp, World world, BlockPos pos, boolean lastAction){
		//Block destruction is disabled by alchemical salt
		if(reags.getQty(EnumReagents.ALCHEMICAL_SALT.id()) == 0){
			BlockState state = world.getBlockState(pos);

			if(!CRConfig.isProtected(world, pos, state) && state.getBlockHardness(world, pos) >= 0){
				world.setBlockState(pos, lastAction && Math.random() > 0.75D && Blocks.FIRE.canPlaceBlockAt(world, pos) ? Blocks.FIRE.getDefaultState() : Blocks.AIR.getDefaultState(), lastAction ? 3 : 18);
			}
		}

		for(ReagentStack r : reagList){
			IAlchEffect effect;
			//reagList should never contain null
			if(r != null && (effect = r.getType().getEffect(r.getType().getPhase(temp))) != null){
				effect.doEffect(world, pos, r.getAmount(), EnumMatterPhase.FLAME, reags);
			}
		}
	}

	@Override
	public boolean canTriggerWalking(){
		return false;
	}

	@Override
	public void receiveNBT(CompoundNBT nbt){
		int colorCode = nbt.getInt("col");
		col = Color.decode(Integer.toString(colorCode & 0xFFFFFF));
		col = new Color(col.getRed(), col.getGreen(), col.getBlue(), colorCode >>> 24);
		ticksExisted = nbt.getInt("life");
	}
}
