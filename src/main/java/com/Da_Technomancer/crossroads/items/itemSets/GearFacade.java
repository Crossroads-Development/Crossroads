package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.rotary.IMechanismProperty;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.IMechanism;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class GearFacade extends Item{

	private final FacadeBlock block;

	public GearFacade(FacadeBlock block){
		super(new Item.Properties().group(CRItems.TAB_CROSSROADS));
		this.block = block;
		setRegistryName("gear_facade_" + block.getSaveName());
		CRItems.toRegister.add(this);
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
	public ActionResultType onItemUse(ItemUseContext context){
		FacadeBlock type = getMaterial();
		if(type == null){
			return ActionResultType.SUCCESS;
		}
		World world = context.getWorld();
		BlockPos pos = context.getPos();//The position of the block clicked
		Direction side = context.getFace();
		PlayerEntity playerIn = context.getPlayer();
		BlockPos placePos = pos;//Where the gear will be placed
		TileEntity teAtPlacement = world.getTileEntity(placePos);

		if(teAtPlacement instanceof MechanismTileEntity){
			//Try to place inside clicked mechanism
			int mechInd = side.getIndex();
			MechanismTileEntity mte = (MechanismTileEntity) teAtPlacement;
			if(mte.members[mechInd] == null){
				//This spot is not already taken
				mte.setMechanism(mechInd, mechanismToPlace(), type, null, false);

				//Consume an item
				if(!world.isRemote && (playerIn == null || !playerIn.isCreative())){
					context.getItem().shrink(1);
				}
				return ActionResultType.SUCCESS;
			}
		}

		//Try to place in adjacent block
		placePos = pos.offset(side);//Where the gear will be placed
		BlockState stateAtPlacement = world.getBlockState(placePos);
		teAtPlacement = world.getTileEntity(placePos);
		int mechInd = side.getOpposite().getIndex();//Index this gear would be placed within the mechanism
		if(teAtPlacement instanceof MechanismTileEntity){
			//Existing mechanism TE to expand
			MechanismTileEntity mte = (MechanismTileEntity) teAtPlacement;
			if(mte.members[mechInd] != null){
				//This spot is already taken
				return ActionResultType.SUCCESS;
			}

			mte.setMechanism(mechInd, mechanismToPlace(), type, null, false);

			//Consume an item
			if(!world.isRemote && (playerIn == null || !playerIn.isCreative())){
				context.getItem().shrink(1);
			}
		}else if(stateAtPlacement.isReplaceable(new BlockItemUseContext(context))){
			//No existing mechanism- we will create a new one
			world.setBlockState(placePos, CRBlocks.mechanism.getDefaultState(), 3);

			teAtPlacement = world.getTileEntity(placePos);
			if(teAtPlacement instanceof MechanismTileEntity){
				((MechanismTileEntity) teAtPlacement).setMechanism(mechInd, mechanismToPlace(), type, null, true);
			}else{
				//Log an error
				Crossroads.logger.error("Mechanism TileEntity did not exist at gear placement; Report to mod author");
			}

			//Consume an item
			if(!world.isRemote && (playerIn == null || !playerIn.isCreative())){
				context.getItem().shrink(1);
			}
		}

		return ActionResultType.SUCCESS;
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
