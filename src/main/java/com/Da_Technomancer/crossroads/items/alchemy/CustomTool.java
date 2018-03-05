package com.Da_Technomancer.crossroads.items.alchemy;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.items.ModItems;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CustomTool extends Item{

	public static final HashMap<String, CustomTool> TOOL_TYPES = new HashMap<String, CustomTool>();
	
	private final String toolClass;
	
	public CustomTool(String toolClass){
		this.toolClass = toolClass;
		String name = "custom_tool_" + toolClass;
		setUnlocalizedName(name);
		setRegistryName(name);
		setMaxStackSize(1);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
		setMaxDamage(1);
		TOOL_TYPES.put(toolClass, this);
	}

	public static ItemStack craftCustomTool(String toolClass, List<ItemStack> crystals){
		NBTTagCompound nbt = new NBTTagCompound();
		int sulf = 0;
		int qsil = 0;
		int salt = 0;
		int phel = 0;
		int aeth = 0;
		int adam = 0;

		for(ItemStack stack : crystals){
			NBTTagCompound st_nbt = stack.getTagCompound();
			if(st_nbt == null){
				return ItemStack.EMPTY;
			}
			sulf += st_nbt.getInteger("sulf");
			qsil += st_nbt.getInteger("qsil");
			salt += st_nbt.getInteger("salt");
			phel += st_nbt.getInteger("phel");
			aeth += st_nbt.getInteger("aeth");
			adam += st_nbt.getInteger("adam");
		}
		sulf /= crystals.size();
		qsil /= crystals.size();
		salt /= crystals.size();
		phel /= crystals.size();
		aeth /= crystals.size();
		adam /= crystals.size();

		nbt.setFloat("attack_dmg", sulf / 6);//0-16
		nbt.setInteger("mining_lvl", toolClass.equals("sword") ? 0 : sulf / 10);//0-10
		nbt.setFloat("mining_spd", toolClass.equals("sword") ? 0 : Math.max(1F, qsil / 5));//1-20
		nbt.setFloat("attack_spd", toolClass.equals("pickaxe") ? -2.8F : toolClass.equals("shovel") || toolClass.equals("axe") ? -3F : -5.6F + qsil / 10F);//On swords: -5.6-4.4
		nbt.setInteger("durability", salt * 52);//0-5200
		nbt.setInteger("heat", phel / 10);//0-10

		ItemStack output = new ItemStack(TOOL_TYPES.get(toolClass), 1);
		output.setTagCompound(nbt);

		if(aeth >= 20){
			output.addEnchantment(Enchantment.getEnchantmentByID(70), 0);
		}
		if(adam >= 5){
			output.addEnchantment(Enchantment.getEnchantmentByID(19), (adam / 5) - 1);
		}

		return output;
	}

	@Override
	public int getMaxDamage(ItemStack stack){
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null){
			return 0;
		}
		return nbt.getInteger("durability");
	}
	
	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState){
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null){
			return -1;
		}

		return toolClass.equals(this.toolClass) ? nbt.getInteger("mining_lvl") : -1;

	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state){
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null){
			return 1F;
		}

		if(state.getBlock().isToolEffective(toolClass, state)){
			return nbt.getFloat("mining_spd");
		}

		return 1.0F;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack){
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		if(slot == EntityEquipmentSlot.MAINHAND){
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt == null){
				return multimap;
			}
			
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", nbt.getFloat("attack_dmg"), 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", nbt.getFloat("attack_spd"), 0));
		}

		return multimap;
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker){
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null){
			return false;
		}
		
		stack.damageItem("sword".equals(toolClass) ? 1 : 2, attacker);
		return true;
	}

	/**
	 * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
	 */
	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving){
		if(!worldIn.isRemote && state.getBlockHardness(worldIn, pos) != 0.0D){
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt == null){
				return false;
			}
			
			stack.damageItem("sword".equals(toolClass) ? 2 : 1, entityLiving);
		}

		return true;
	}

	@Override
	public Set<String> getToolClasses(ItemStack stack){
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null){
			return super.getToolClasses(stack);
		}
		return ImmutableSet.of(toolClass);
	}
	
	/**
	 * Returns True is the item is renderer in full 3D when hold.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D(){
		return true;
	}
}
