package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.ambient.particles.CRParticles;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.alchemy.FlameCoresSavedData;
import com.Da_Technomancer.crossroads.entity.EntityFlameCore;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class DampingPowder extends Item{

	private static final int RANGE = 10;

	private static final DispenseItemBehavior DAMPING_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior(){

		/**
		 * Dispense the specified stack, play the dispense sound and spawn particles.
		 */
		@Override
		public ItemStack execute(BlockSource source, ItemStack stack){
			stack.shrink(1);
			Vec3 partPos = Vec3.atCenterOf(source.getPos());
			if(source.getBlockState().hasProperty(DispenserBlock.FACING)){
				partPos = partPos.add(Vec3.atLowerCornerOf(source.getBlockState().getValue(DispenserBlock.FACING).getNormal()));
			}
			performDamping(source.getLevel(), new BlockPos(partPos), true);
			return stack;
		}

		/**
		 * Play the dispense sound from the specified block.
		 */
		@Override
		protected void playSound(BlockSource source){
			source.getLevel().levelEvent(1000, source.getPos(), 0);
		}
	};

	public DampingPowder(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS));
		String name = "damping_powder";
		CRItems.toRegister.put(name, this);
		DispenserBlock.registerBehavior(this, DAMPING_DISPENSER_BEHAVIOR);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn){
		ItemStack held = playerIn.getItemInHand(handIn);
		if(!playerIn.isCreative()){
			held.shrink(1);
		}
		if(worldIn instanceof ServerLevel level){
			performDamping(level, new BlockPos(playerIn.getEyePosition()).relative(playerIn.getDirection()), false);
		}
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, held);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.damp_powder.desc"));
		tooltip.add(Component.translatable("tt.crossroads.damp_powder.quip").setStyle(MiscUtil.TT_QUIP));
	}

	private static boolean performDamping(ServerLevel world, BlockPos pos, boolean blockSource){
		List<EntityFlameCore> cores = FlameCoresSavedData.getFlameCores(world);
		cores.removeIf(core -> {
			//This uses Chebyshev distance from the center of the flame cloud, with range being cloud radius + RANGE
			//If we're within this cube, we're in range
			int range = core.getRadius() + RANGE;
			BlockPos corePos = core.blockPosition();
			corePos = corePos.subtract(pos);
			return range < Math.abs(corePos.getX()) || range < Math.abs(corePos.getY()) || range < Math.abs(corePos.getZ());
		});
		for(EntityFlameCore ent : cores){
			ent.remove(Entity.RemovalReason.KILLED);
		}
		if(!cores.isEmpty()){
			world.playSound(null, pos, SoundEvents.TOTEM_USE, blockSource ? SoundSource.BLOCKS : SoundSource.PLAYERS, 0.5F, 0);
		}
		CRParticles.summonParticlesFromServer(world, ParticleTypes.END_ROD, 3, pos.getX(), pos.getY(), pos.getZ(), 0.5, 0.3, 0.5, 0, 0, 0, 0.1, 0.05, 0.1, false);
		return !cores.isEmpty();
	}
}
