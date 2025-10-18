package com.Da_Technomancer.crossroads.api.crafting;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class is a bare-bones block version of net.minecraft.item.crafting.Ingredient
 * In the likely event that either vanilla or Forge adds a standard way to put blocks in recipe inputs, we will switch to that immediately
 */
public class BlockIngredient implements Predicate<BlockState>{

	public static final BlockIngredient EMPTY = new BlockIngredient(List.of());

	private final List<IBlockList> keys;
	private boolean cacheValid = false;//Currently nothing invalidates the cache
	private final Collection<Block> matched = new HashSet<>();

	/**
	 * Everything in matched should be either a block tag, a block, an IBlockList, or a blockstate
	 * @param matched Everything this ingredient should match
	 */
	public static BlockIngredient of(Object... matched){
		if(matched.length == 1 && matched[0].getClass().isArray()){
			//Because of the unusually vague parameters for the constructor, it's easy to accidentally pass an array of values as an array of the array (due to it being a varArgs)
			//This detects that case, and corrects it rather than throwing an error
			matched = (Object[]) matched[0];
		}

		ArrayList<IBlockList> keys = new ArrayList<>(matched.length);
		for(Object key : matched){
			switch(key){
				case IBlockList iBlockList -> keys.add(iBlockList);
				case TagKey<?> tagKey -> {
					try{
						keys.add(new TagList((TagKey<Block>) tagKey));
					}catch(ClassCastException e){
						Crossroads.logger.error("An illegal tag type was added to a BlockIngredient. Report to mod author!", e);
						throw e;
					}
				}
				case Block block -> keys.add(new BlockList(List.of(block)));
				case BlockState blockState -> keys.add(new BlockList(List.of(blockState.getBlock())));
				case null, default -> {
					JsonParseException e = new JsonParseException("Illegal type added to BlockIngredient; Type: " + key.getClass() + "; Value: " + key.toString());
					Crossroads.logger.error("An illegal value was added to a BlockIngredient. Report to mod author!", e);
					throw e;
				}
			}
		}
		return new BlockIngredient(keys);
	}

	private BlockIngredient(List<IBlockList> matched){
		this.keys = matched;
	}

	/**
	 * Checks if this was defined as an empty ingredient
	 * This does not load contained tags, making this method safe for lazy-loading
	 * Note that if this ingredient was defined as containing only tags which are empty, it will return false
	 * @return Whether this ingredient was defined as being totally empty
	 */
	public boolean isStrictlyEmpty(){
		return this == EMPTY || keys.isEmpty() || keys.stream().allMatch(IBlockList::isEmpty);
	}

	/**
	 * Creates a list of the item forms of every matched block
	 * @deprecated This is not reliable as many blocks don't map to items cleanly. Use alternatives wherever possible
	 * @return The item form of every matched block, if an item form exists. Will contain no duplicates, may be empty
	 */
	@Deprecated
	public List<ItemStack> getMatchedItemForm(){
		updateCache();
		return matched.parallelStream().unordered().map(ItemStack::new).distinct().filter(s -> s.getItem() != Items.AIR).collect(Collectors.toList());
	}

	private void updateCache(){
		if(!cacheValid){
			matched.clear();
			keys.forEach(key -> matched.addAll(key.getMatched()));
			cacheValid = true;
		}
	}

	@Override
	public boolean test(BlockState blockState){
		updateCache();
		Block b = blockState.getBlock();
		return matched.contains(b);
	}


	public static final Codec<BlockIngredient> CODEC = CraftingUtil.singleOrListCodec(IBlockList.CODEC, 1, Integer.MAX_VALUE).xmap(BlockIngredient::new, blockIngredient -> blockIngredient.keys);

	public static final StreamCodec<RegistryFriendlyByteBuf, BlockIngredient> STREAM_CODEC = StreamCodec.of(new StreamEncoder<RegistryFriendlyByteBuf, BlockIngredient>(){
		@Override
		public void encode(RegistryFriendlyByteBuf buf, BlockIngredient ingr){
			ingr.updateCache();
			buf.writeVarInt(ingr.matched.size());//Write how many Blocks this matches
			for(Block b : ingr.matched){
				buf.writeResourceLocation(MiscUtil.getRegistryName(b, BuiltInRegistries.BLOCK));//Write the registry name of every matched block.
			}
		}
	}, new StreamDecoder<RegistryFriendlyByteBuf, BlockIngredient>(){
		@Override
		public BlockIngredient decode(RegistryFriendlyByteBuf buf){
			int count = buf.readVarInt();
			if(count <= 0){
				return BlockIngredient.EMPTY;
			}
			List<Block> matched = new ArrayList<>(count);
			for(int i = 0; i < count; i++){
				matched.add(BuiltInRegistries.BLOCK.get(buf.readResourceLocation()));
			}
			//Create a block ingredient with one large IBlockList that matches every fluid. Note this doesn't preserve Tag associations of the original definition
			return new BlockIngredient(List.of(new BlockList(matched)));
		}
	});


	private interface IBlockList{

		static final MapCodec<IBlockList> MAP_CODEC = NeoForgeExtraCodecs.xor(BlockList.MAP_CODEC, TagList.MAP_CODEC)
				.xmap(either -> either.map(blockList -> blockList, tagList -> tagList), iBlockList -> {
					if(iBlockList instanceof TagList tagList){
						return Either.right(tagList);
					}else if(iBlockList instanceof BlockList fluidList){
						return Either.left(fluidList);
					}else{
						throw new UnsupportedOperationException("This is neither a block value nor a tag value.");
					}
				});
		static final Codec<IBlockList> CODEC = MAP_CODEC.codec();

		Collection<Block> getMatched();

		boolean isEmpty();

	}

	private static class TagList implements IBlockList{

		private static final MapCodec<TagList> MAP_CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(tagList -> tagList.tag))
						.apply(instance, TagList::new)
		);

		private final TagKey<Block> tag;

		public TagList(TagKey<Block> matched){
			tag = matched;
			if(tag == null){
				throw new JsonParseException("No defined tag in BlockIngredient");
			}
		}

		@Override
		public Collection<Block> getMatched(){
			return CraftingUtil.getTagContents(tag);
		}

		@Override
		public boolean isEmpty(){
			return false;//We do not check the tag contents, to enable lazyloading
		}
	}

	private static class BlockList implements IBlockList{

		//This codec is a bit over-engineered. Technically, it allows the block tag in JSON to have a list of block IDs instead of just a single block ID, but this is only to allow Codec re-encoding of BlockList with multiple entries, which only occurs for a BlockIngredient which has been de-serialized by the StreamCodec
		private static final MapCodec<BlockList> MAP_CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(CraftingUtil.singleOrListCodec(BuiltInRegistries.BLOCK.byNameCodec(), 1, Integer.MAX_VALUE).fieldOf("block").forGetter(blockList -> blockList.blocks))
						.apply(instance, BlockList::new)
		);

		private final List<Block> blocks;

		public BlockList(List<Block> matched){
			blocks = matched;
			if(matched == null){
				throw new JsonParseException("No defined block in BlockIngredient");
			}
		}

		@Override
		public Collection<Block> getMatched(){
			return blocks;
		}

		@Override
		public boolean isEmpty(){
			return blocks.isEmpty();
		}
	}
}
