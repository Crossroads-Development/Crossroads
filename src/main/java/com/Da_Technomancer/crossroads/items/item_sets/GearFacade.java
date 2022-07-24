package com.Da_Technomancer.crossroads.items.item_sets;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.rotary.IMechanism;
import com.Da_Technomancer.crossroads.api.rotary.IMechanismProperty;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.rotary.mechanisms.MechanismTileEntity;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;

public class GearFacade extends Item{

	private final FacadeBlock block;

	public GearFacade(FacadeBlock block){
		super(new Item.Properties().tab(CRItems.TAB_CROSSROADS));
		this.block = block;
		String name = "gear_facade_" + block.getSaveName();
		CRItems.toRegister.put(name, this);
	}

	protected IMechanism<?> mechanismToPlace(){
		return MechanismTileEntity.MECHANISMS.get(7);
	}

	public FacadeBlock getMaterial(){
		return block;
	}

	public static ItemStack withMaterial(FacadeBlock mat, int count){
		if(mat == null){
			mat = FacadeBlock.STONE_BRICK;
		}
		GearFacade item;
		switch(mat){
			default:
			case STONE_BRICK:
				item = CRItems.gearFacadeStoneBrick;
				break;
			case COBBLE:
				item = CRItems.gearFacadeCobble;
				break;
			case IRON:
				item = CRItems.gearFacadeIron;
				break;
			case GLASS:
				item = CRItems.gearFacadeGlass;
				break;
		}
		return new ItemStack(item, count);
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		FacadeBlock type = getMaterial();
		if(type == null){
			return InteractionResult.SUCCESS;
		}
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();//The position of the block clicked
		Direction side = context.getClickedFace();
		Player playerIn = context.getPlayer();
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

	private static final HashMap<String, FacadeBlock> NAME_MAP = new HashMap<>(4);

	public enum FacadeBlock implements IMechanismProperty{

		STONE_BRICK("stone_brick", new ResourceLocation("block/stone_bricks")),
		COBBLE("cobble", new ResourceLocation("block/cobblestone")),
		IRON("iron", new ResourceLocation("block/iron_block")),
		GLASS("glass", new ResourceLocation("block/glass"));

		private final String name;
		private final ResourceLocation texture;

		FacadeBlock(String name, ResourceLocation texture){
			this.name = name;
			this.texture = texture;
			NAME_MAP.put(name, this);
		}

		public ResourceLocation getTexture(){
			return texture;
		}

		@Override
		public int serialize(){
			return ordinal();
		}

		@Override
		public String getSaveName(){
			return name;
		}

		public static FacadeBlock deserialize(int serial){
			if(serial < 0 || serial >= values().length){
				return STONE_BRICK;
			}
			return values()[serial];
		}

		public static FacadeBlock loadProperty(String name){
			return NAME_MAP.getOrDefault(name, STONE_BRICK);
		}

		public String getName(){
			return MiscUtil.localize("facade_block." + name);
		}
	}
}
