package com.Da_Technomancer.crossroads.tileentities.magic;

import java.awt.Color;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.effects.IEffect;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class LensHolderTileEntity extends BeamRenderTE implements ITickable, IIntReceiver{
	
	private Color col;
	private int reach;
	private int size;
	private boolean up;
	private int timer;
	
	@SuppressWarnings("unchecked")
	@Override
	public Triple<Color, Integer, Integer>[] getBeam(){
		Triple<Color, Integer, Integer>[] out = new Triple[6];
		out[EnumFacing.getFacingFromAxis(up ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, worldObj.getBlockState(pos).getValue(Properties.ORIENT) ? Axis.X : Axis.Z).getIndex()] = col == null ? null : Triple.of(col, reach, size);
		return out;
	}

	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}

		//TODO CORRECT
		if(timer-- <= 0){
			wipeBeam();
		}
	}
	
	private void wipeBeam(){
		if(col != null || reach != 0 || size != 0){
			col = null;
			reach = 0;
			size = 0;
			ModPackets.network.sendToAllAround(new SendIntToClient("beam", 0, pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
	}
	
	@Override
	public void receiveInt(String context, int message){
		if(context.equals("beam")){
			up = message > 0;
			message = Math.abs(message);
			int i = message & 16777215;
			col = Color.decode(Integer.toString(i));
			reach = ((message & 251658240) >> 24) + 1;
			size = ((message - reach) >> 28) + 1;
		}
	}
	
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(col != null){
			nbt.setInteger("col", col.getRGB() & 16777215);
		}
		nbt.setBoolean("up", up);
		nbt.setInteger("reach", reach);
		nbt.setInteger("size", size);
		
		return nbt;
	}
	
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		col = nbt.hasKey("col") ? Color.decode(Integer.toString(nbt.getInteger("col"))) : null;
		up = nbt.getBoolean("up");
		reach = nbt.getInteger("reach");
		size = nbt.getInteger("size");
	}
	
	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		if(col != null){
			nbt.setInteger("col", col.getRGB() & 16777215);
		}
		nbt.setInteger("reach", reach);
		nbt.setInteger("size", size + 1);
		nbt.setBoolean("up", up);
		return nbt;
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return (oldState.getBlock() != newState.getBlock());
	}
	
	/** NON STANDARD COPY OF THIS METHOD
	 */
	private void emit(MagicUnit mag, AxisDirection way){
		if(mag == null || mag.getRGB() == null){
			return;
		}
		EnumFacing dir = EnumFacing.getFacingFromAxis(way, worldObj.getBlockState(pos).getValue(Properties.ORIENT) ? Axis.X : Axis.Z);
		timer = 2;
		for(int i = 1; i <= IMagicHandler.MAX_DISTANCE; i++){
			if(worldObj.getTileEntity(pos.offset(dir, i)) != null && worldObj.getTileEntity(pos.offset(dir, i)).hasCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite())){
				int siz = Math.min((int) Math.sqrt(mag.getPower()) - 1, 7);
				if(col == null || mag.getRGB().getRGB() != col.getRGB() || siz != size || i != reach){
					ModPackets.network.sendToAllAround(new SendIntToClient("beam", (((i - 1) << 24) + (mag.getRGB().getRGB() & 16777215) + (siz << 28)) * (way == AxisDirection.POSITIVE ? 1 : -1), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					size = siz;
					col = mag.getRGB();
					reach = i;
				}
				worldObj.getTileEntity(pos.offset(dir, i)).getCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite()).setMagic(mag);
				return;
			}

			if(i == IMagicHandler.MAX_DISTANCE || (worldObj.getBlockState(pos.offset(dir, i)) != null && !worldObj.getBlockState(pos.offset(dir, i)).getBlock().isAir(worldObj.getBlockState(pos.offset(dir, i)), worldObj, pos.offset(dir, i)))){
				int siz = Math.min((int) Math.sqrt(mag.getPower()) - 1, 7);
				if(col == null || mag.getRGB().getRGB() != col.getRGB() || siz != size || i != reach){
					ModPackets.network.sendToAllAround(new SendIntToClient("beam", (((i - 1) << 24) + (mag.getRGB().getRGB() & 16777215) + (siz << 28)) * (way == AxisDirection.POSITIVE ? 1 : -1), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					size = siz;
					col = mag.getRGB();
					reach = i;
				}
				IEffect e = MagicElements.getElement(mag).getMixEffect(mag.getRGB());
				if(e != null){
					e.doEffect(worldObj, pos.offset(dir, i));
				}
				return;
			}
		}
	}
	
	private final IMagicHandler magicHandler = new MagicHandler(AxisDirection.NEGATIVE);
	private final IMagicHandler magicHandlerNeg = new MagicHandler(AxisDirection.POSITIVE);
	private final IItemHandler lensHandler = new LensHandler();
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && (side == null || (side.getAxis() == Axis.X) == worldObj.getBlockState(pos).getValue(Properties.ORIENT))){
			return true;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return true;
		}
		
		
		return super.hasCapability(cap, side);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && (side == null || (side.getAxis() == Axis.X) == worldObj.getBlockState(pos).getValue(Properties.ORIENT))){
			return side.getAxisDirection() == AxisDirection.NEGATIVE ? (T) magicHandlerNeg : (T) magicHandler;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (T) lensHandler;
		}
		
		return super.getCapability(cap, side);
	}
	
	private class MagicHandler implements IMagicHandler{

		private final AxisDirection dir;
		
		private MagicHandler(AxisDirection dir){
			this.dir = dir;
		}

		@Override
		public void setMagic(MagicUnit mag){
			if(mag.getVoid() != 0){
				worldObj.setBlockState(pos, ModBlocks.lensHolder.getDefaultState().withProperty(Properties.ORIENT, worldObj.getBlockState(pos).getValue(Properties.ORIENT)).withProperty(Properties.TUXTURE_6, 0));
				emit(mag, dir);
				return;
			}
			
			switch(worldObj.getBlockState(pos).getValue(Properties.TUXTURE_6)){
				case 0:
					emit(mag, dir);
					break;
				case 1:
					if(mag.getEnergy() != 0){
						emit(new MagicUnit(mag.getEnergy(), 0, 0, 0), dir);
					}
					break;
				case 2:
					if(mag.getPotential() != 0){
						emit(new MagicUnit(0, mag.getPotential(), 0, 0), dir);
					}
					break;
				case 3:
					if(mag.getStability() != 0){
						emit(new MagicUnit(0, 0, mag.getStability(), 0), dir);
					}
					break;
				case 4:
					if(MagicElements.getElement(mag) == MagicElements.LIGHT){
						worldObj.setBlockState(pos, ModBlocks.lensHolder.getDefaultState().withProperty(Properties.ORIENT, worldObj.getBlockState(pos).getValue(Properties.ORIENT)).withProperty(Properties.TUXTURE_6, 5));
					}
					emit(mag, dir);
					break;
				case 5:
					emit(mag, dir);
			}
		}
	}
	
	private class LensHandler implements IItemHandler{

		private ItemStack getLens(){
			switch(worldObj.getBlockState(pos).getValue(Properties.TUXTURE_6)){
				case 0:
					return null;
				case 1:
					return new ItemStack(Item.getByNameOrId(Main.MODID + ":gemRuby"), 1);
				case 2:
					return new ItemStack(Items.EMERALD, 1);
				case 3:
					return new ItemStack(Items.DIAMOND, 1);
				case 4:
					return new ItemStack(ModItems.pureQuartz, 1);
				case 5:
					return new ItemStack(ModItems.luminescentQuartz, 1);
				default: 
					return null;
			}
		}
		
		@Override
		public int getSlots(){
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			return slot == 0 ? getLens() : null;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(slot != 0 || getLens() != null || stack == null || (stack.getItem() != Items.DIAMOND && stack.getItem() != Items.EMERALD && stack.getItem() != ModItems.pureQuartz && stack.getItem() != Item.getByNameOrId(Main.MODID + ":gemRuby"))){
				return stack;
			}
			
			if(!simulate){
				worldObj.setBlockState(pos, ModBlocks.lensHolder.getDefaultState().withProperty(Properties.ORIENT, worldObj.getBlockState(pos).getValue(Properties.ORIENT)).withProperty(Properties.TUXTURE_6, stack.getItem() == ModItems.pureQuartz ? 4 : stack.getItem() == Items.EMERALD ? 2 : stack.getItem() == Items.DIAMOND ? 3 : 1));
			}
			
			return stack.stackSize - 1 <= 0 ? null : new ItemStack(stack.getItem(), stack.stackSize - 1);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			if(slot != 0 || amount < 1 || getLens() == null){
				return null;
			}
			ItemStack holder = getLens();
			if(!simulate){
				worldObj.setBlockState(pos, ModBlocks.lensHolder.getDefaultState().withProperty(Properties.ORIENT, worldObj.getBlockState(pos).getValue(Properties.ORIENT)).withProperty(Properties.TUXTURE_6, 0));
			}
			return holder;
		}
	}
} 
