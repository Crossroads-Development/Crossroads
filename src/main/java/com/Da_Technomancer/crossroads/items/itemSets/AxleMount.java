package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.IMechanism;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class AxleMount extends GearMatItem{

	public AxleMount(){
		this("axle_mount");
	}

	protected AxleMount(String name){
		super();
		setRegistryName(name);
	}

	@Override
	protected double shapeFactor(){
		return 1;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.axle_mount.desc"));
	}

	protected IMechanism mechanismToPlace(){
		return MechanismTileEntity.MECHANISMS.get(6);
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		GearFactory.GearMaterial type = getMaterial(context.getItemInHand());
		if(type == null){
			return InteractionResult.SUCCESS;
		}
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();//The position of the block clicked
		Direction side = context.getClickedFace();
		BlockPos placePos = pos.relative(side);//Where the gear will be placed
		Player playerIn = context.getPlayer();
		BlockState stateAtPlacement = world.getBlockState(placePos);
		BlockEntity teAtPlacement = world.getBlockEntity(placePos);

		//Must be able to place against a solid surface
		if(RotaryUtil.solidToGears(world, pos, side)){
			int mechInd = side.getOpposite().get3DDataValue();//Index this gear would be placed within the mechanism
			if(teAtPlacement instanceof MechanismTileEntity){
				//Existing mechanism TE to expand
				MechanismTileEntity mte = (MechanismTileEntity) teAtPlacement;
				if(mte.members[mechInd] != null){
					//This spot is already taken
					return InteractionResult.SUCCESS;
				}

				mte.setMechanism(mechInd, mechanismToPlace(), type, null, false);

				//Consume an item
				if(!world.isClientSide && (playerIn == null || !playerIn.isCreative())){
					context.getItemInHand().shrink(1);
				}

				RotaryUtil.increaseMasterKey(!world.isClientSide);
			}else if(stateAtPlacement.canBeReplaced(new BlockPlaceContext(context))){
				//No existing mechanism- we will create a new one
				world.setBlock(placePos, CRBlocks.mechanism.defaultBlockState(), 3);

				teAtPlacement = world.getBlockEntity(placePos);
				if(teAtPlacement instanceof MechanismTileEntity){
					((MechanismTileEntity) teAtPlacement).setMechanism(mechInd, mechanismToPlace(), type, null, true);
				}else{
					//Log an error
					Crossroads.logger.error("Mechanism TileEntity did not exist at gear placement; Report to mod author");
				}

				//Consume an item
				if(!world.isClientSide && (playerIn == null || !playerIn.isCreative())){
					context.getItemInHand().shrink(1);
				}

				RotaryUtil.increaseMasterKey(!world.isClientSide);
			}
		}

		return InteractionResult.SUCCESS;
	}
}
