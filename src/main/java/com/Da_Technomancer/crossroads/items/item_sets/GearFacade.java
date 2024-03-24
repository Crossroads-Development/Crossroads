package com.Da_Technomancer.crossroads.items.item_sets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.rotary.IMechanism;
import com.Da_Technomancer.crossroads.api.rotary.IMechanismProperty;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.rotary.mechanisms.MechanismTileEntity;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public class GearFacade extends Item{

	public GearFacade(){
		super(new Item.Properties());
		String name = "gear_facade";
		CRItems.queueForRegister(name, this);
	}

	protected IMechanism<?> mechanismToPlace(){
		return MechanismTileEntity.MECHANISMS.get(7);
	}

	public FacadeBlock getMaterial(ItemStack stack){
		CompoundTag nbt = stack.getTag();
		if(nbt != null && nbt.contains("facadeBlock")){
			return FacadeBlock.create(new ResourceLocation(nbt.getString("facadeBlock")));
		}
		return FacadeBlock.create(ForgeRegistries.BLOCKS.getKey(Blocks.STONE_BRICKS));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltips, TooltipFlag flag){
		FacadeBlock facade = getMaterial(stack);
		tooltips.add(Component.translatable("tt.crossroads.gear_facade.desc"));
		tooltips.add(Component.translatable("tt.crossroads.gear_facade.setting").append(Component.translatable(facade.getBlockState().getBlock().getDescriptionId())));
	}

	public void setMaterial(ItemStack stack, BlockState state){
		CompoundTag nbt = stack.getOrCreateTag();
		nbt.putString("facadeBlock", ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString());
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();//The position of the block clicked
		Player playerIn = context.getPlayer();

		if(playerIn.isShiftKeyDown()){
			//Sneak-right-clicking sets material type
			BlockState clickedBlock = world.getBlockState(pos);
			if(!clickedBlock.isAir() && clickedBlock.getShape(world, pos) == Shapes.block()){
				//Full block only
				setMaterial(context.getItemInHand(), clickedBlock);
			}
			return InteractionResult.SUCCESS;
		}

		FacadeBlock type = getMaterial(context.getItemInHand());
		if(type == null){
			return InteractionResult.SUCCESS;
		}

		Direction side = context.getClickedFace();
		BlockPos placePos = pos;//Where the gear will be placed
		BlockEntity teAtPlacement = world.getBlockEntity(placePos);

		if(teAtPlacement instanceof MechanismTileEntity){
			//Try to place inside clicked mechanism
			int mechInd = side.get3DDataValue();
			MechanismTileEntity mte = (MechanismTileEntity) teAtPlacement;
			if(mte.members[mechInd] == null){
				//This spot is not already taken
				mte.setMechanism(mechInd, mechanismToPlace(), type, null, false);

				//Consume an item
				if(!world.isClientSide && (playerIn == null || !playerIn.isCreative())){
					context.getItemInHand().shrink(1);
				}
				return InteractionResult.SUCCESS;
			}
		}

		//Try to place in adjacent block
		placePos = pos.relative(side);//Where the gear will be placed
		BlockState stateAtPlacement = world.getBlockState(placePos);
		teAtPlacement = world.getBlockEntity(placePos);
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
		}

		return InteractionResult.SUCCESS;
	}

	private static final HashMap<ResourceLocation, FacadeBlock> NAME_MAP = new HashMap<>(4);

	public static class FacadeBlock implements IMechanismProperty{

		private final ResourceLocation blockRegName;
		private BlockState blockstateCache;

		public static FacadeBlock create(ResourceLocation blockRegName){
			FacadeBlock facade = NAME_MAP.get(blockRegName);
			if(facade == null){
				return new FacadeBlock(blockRegName);
			}
			return facade;
		}

		private FacadeBlock(ResourceLocation blockRegName){
			this.blockRegName = blockRegName;
			NAME_MAP.put(blockRegName, this);
		}

		public BlockState getBlockState(){
			if(blockstateCache == null){
				Block block = ForgeRegistries.BLOCKS.getValue(blockRegName);
				if(block == null){
					block = Blocks.STONE_BRICKS;
				}
				blockstateCache = block.defaultBlockState();
				if(blockstateCache.isAir()){
					blockstateCache = Blocks.STONE_BRICKS.defaultBlockState();
				}
			}
			return blockstateCache;
		}

		@Override
		public void write(CompoundTag nbt){
			nbt.putString("blockRegistryName", blockRegName.toString());
		}

		public static FacadeBlock read(CompoundTag nbt){
			if(nbt.contains("prop_data")){
				//Backwards compat. Will be removed in future version
				String name = nbt.getString("prop_data");
				return switch(name){
					case "cobble" -> create(ForgeRegistries.BLOCKS.getKey(Blocks.COBBLESTONE));
					case "iron" -> create(ForgeRegistries.BLOCKS.getKey(Blocks.IRON_BLOCK));
					case "glass" -> create(ForgeRegistries.BLOCKS.getKey(Blocks.GLASS));
					default -> create(ForgeRegistries.BLOCKS.getKey(Blocks.STONE_BRICKS));
				};
			}

			return create(new ResourceLocation(nbt.getString("blockRegistryName")));
		}
	}
}
