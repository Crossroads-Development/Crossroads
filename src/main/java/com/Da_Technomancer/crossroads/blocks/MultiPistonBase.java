package com.Da_Technomancer.crossroads.blocks;

import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.WorldBuffer;
import com.Da_Technomancer.crossroads.items.ModItems;

import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

/**Notable differences from a normal piston include:
 * 15 block head range, distance controlled by signal strength,
 * No quasi-connectivity,
 * Redstone can be placed on top of the piston,
 * Hit box does not change when extended,
 * Piston extension and retraction is instant, no 2-tick delay or rendering of block movement.
 * Can move up to 64 blocks at a time instead of 12
 */
public class MultiPistonBase extends Block{

	private final boolean sticky;

	protected MultiPistonBase(boolean sticky){
		super(Material.PISTON);
		String name = "multi_piston" + (sticky ? "_sticky" : "");
		setUnlocalizedName(name);
		setRegistryName(name);
		this.sticky = sticky;
		setHardness(0.5F);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setDefaultState(getDefaultState().withProperty(Properties.FACING, EnumFacing.NORTH).withProperty(Properties.REDSTONE_BOOL, false));
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getDefaultState().withProperty(Properties.FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote) && getExtension(worldIn, pos, state.getValue(Properties.FACING)) == 0){
			if(!worldIn.isRemote){
				IBlockState endState = state.cycleProperty(Properties.FACING);
				worldIn.setBlockState(pos, endState);
				checkRedstone(worldIn, pos, endState.getValue(Properties.FACING));
			}
			return true;
		}
		return false;
	}

	protected void safeBreak(World worldIn, BlockPos pos){
		if(safeToBreak){
			worldIn.destroyBlock(pos, true);
		}
	}

	private boolean safeToBreak = true;

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
		checkRedstone(worldIn, pos, state.getValue(Properties.FACING));
	}

	private void checkRedstone(World worldIn, BlockPos pos, EnumFacing dir){
		int i = Math.max(worldIn.getRedstonePower(pos.down(), EnumFacing.DOWN), Math.max(worldIn.getRedstonePower(pos.up(), EnumFacing.UP), Math.max(worldIn.getRedstonePower(pos.east(), EnumFacing.EAST), Math.max(worldIn.getRedstonePower(pos.west(), EnumFacing.WEST), Math.max(worldIn.getRedstonePower(pos.north(), EnumFacing.NORTH), worldIn.getRedstonePower(pos.south(), EnumFacing.SOUTH))))));
		if(i > 0){
			if(!worldIn.getBlockState(pos).getValue(Properties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(Properties.REDSTONE_BOOL, true));
			}
		}else{
			if(worldIn.getBlockState(pos).getValue(Properties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(Properties.REDSTONE_BOOL, false));
			}
		}

		int prev = getExtension(worldIn, pos, dir);
		if(prev != i && prev != -1){
			safeToBreak = false;
			setExtension(worldIn, pos, dir, i, prev);
			safeToBreak = true;
			if(!worldIn.isBlockTickPending(pos, this)){
				worldIn.updateBlockTick(pos, this, 1, -1);
			}
		}
	}

	private int getExtension(World worldIn, BlockPos pos, EnumFacing dir){
		if(!safeToBreak){
			return -1;
		}
		final Block GOAL = sticky ? ModBlocks.multiPistonExtendSticky : ModBlocks.multiPistonExtend;
		for(int i = 1; i <= 15; i++){
			if(worldIn.getBlockState(pos.offset(dir, i)).getBlock() != GOAL || worldIn.getBlockState(pos.offset(dir, i)).getValue(Properties.FACING) != dir){
				return i - 1;
			}
		}
		return 15;
	}

	private void setExtension(World worldIn, BlockPos pos, EnumFacing dir, int distance, int prev){
		if(prev == distance){
			return;
		}

		final WorldBuffer world = new WorldBuffer(worldIn);
		final Block GOAL = sticky ? ModBlocks.multiPistonExtendSticky : ModBlocks.multiPistonExtend;
		for(int i = 1; i <= prev; i++){
			if(world.getBlockState(pos.offset(dir, i)).getBlock() == GOAL && world.getBlockState(pos.offset(dir, i)).getValue(Properties.FACING) == dir){
				world.addChange(pos.offset(dir, i), Blocks.AIR.getDefaultState());
			}
		}

		if(sticky && prev > distance){
			for(int i = prev + 1; i > distance + 1; i--){
				ArrayList<BlockPos> list = new ArrayList<BlockPos>();

				if(canPush(world.getBlockState(pos.offset(dir, i)), false)){
					if(propogate(list, world, pos.offset(dir, i), dir.getOpposite(), null)){
						break;
					}else{
						for(int index = list.size() - 1; index >= 0; --index){
							BlockPos moving = list.get(index);

							if(world.getBlockState(moving.offset(dir.getOpposite())).getMobilityFlag() == EnumPushReaction.DESTROY){
								world.getBlockState(moving.offset(dir.getOpposite())).getBlock().dropBlockAsItem(worldIn, moving.offset(dir.getOpposite()), world.getBlockState(moving.offset(dir.getOpposite())), 0);
							}
							world.addChange(moving.offset(dir.getOpposite()), world.getBlockState(moving));
							world.addChange(moving, Blocks.AIR.getDefaultState());
						}
					}
				}
			}
		}

		if(distance == 0){
			if(world.hasChanges()){
				worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, .5F, (worldIn.rand.nextFloat() * .15F) + .6F);
			}
			world.doChanges();
			return;
		}

		for(int i = 1; i <= distance; i++){
			ArrayList<BlockPos> list = new ArrayList<BlockPos>();

			if(canPush(world.getBlockState(pos.offset(dir, i)), false)){
				if(propogate(list, world, pos.offset(dir, i), dir, null)){
					if(world.hasChanges()){
						worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, .5F, (worldIn.rand.nextFloat() * .25F) + .6F);
					}
					world.doChanges();
					return;
				}
			}else if(!canPush(world.getBlockState(pos.offset(dir, i)), true)){
				if(world.hasChanges()){
					worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, .5F, (worldIn.rand.nextFloat() * .25F) + .6F);
				}
				world.doChanges();
				return;
			}

			if(list.isEmpty()){
				for(Entity ent : getEntitiesMultiChunk(FULL_BLOCK_AABB.offset(pos.offset(dir, i)), worldIn)){
					if(ent.getPushReaction() != EnumPushReaction.IGNORE){
						ent.setPositionAndUpdate(ent.posX + (double) dir.getFrontOffsetX(), ent.posY + (double) dir.getFrontOffsetY(), ent.posZ + (double) dir.getFrontOffsetZ());
						if(sticky){
							ent.addVelocity(dir.getFrontOffsetX(), dir.getFrontOffsetY(), dir.getFrontOffsetZ());
							ent.velocityChanged = true;
						}
					}
				}
			}else{
				for(int index = list.size() - 1; index >= 0; --index){
					BlockPos moving = list.get(index);

					if(world.getBlockState(moving.offset(dir)).getMobilityFlag() == EnumPushReaction.DESTROY){
						worldIn.destroyBlock(moving.offset(dir), true);
					}
					world.addChange(moving.offset(dir), world.getBlockState(moving));
					world.addChange(moving, Blocks.AIR.getDefaultState());
					AxisAlignedBB box;
					//Due to the fact that the block isn't actually at that position (WorldBuffer), exceptions have to be caught.
					try{
						box = world.getBlockState(moving.offset(dir)).getCollisionBoundingBox(worldIn, pos);
					}catch(Exception e){
						box = FULL_BLOCK_AABB;
					}
					box = box.offset(moving.offset(dir));
					for(Entity ent : getEntitiesMultiChunk(box, worldIn)){
						if(ent.getPushReaction() != EnumPushReaction.IGNORE){
							ent.setPositionAndUpdate(ent.posX + (double) dir.getFrontOffsetX(), ent.posY + (double) dir.getFrontOffsetY(), ent.posZ + (double) dir.getFrontOffsetZ());
							if(world.getBlockState(moving.offset(dir)).getBlock() == Blocks.SLIME_BLOCK){
								ent.addVelocity(dir.getFrontOffsetX(), dir.getFrontOffsetY(), dir.getFrontOffsetZ());
								ent.velocityChanged = true;
							}
						}
					}
				}
			}

			for(int j = i; j >= 1; j--){
				if(world.getBlockState(pos.offset(dir, j)).getMobilityFlag() == EnumPushReaction.DESTROY){
					worldIn.destroyBlock(pos.offset(dir, j), true);
				}
				world.addChange(pos.offset(dir, j), GOAL.getDefaultState().withProperty(Properties.FACING, dir).withProperty(Properties.HEAD, i == j));
			}
		}
		if(world.hasChanges()){
			worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, .5F, (worldIn.rand.nextFloat() * .25F) + .6F);
		}
		world.doChanges();
	}

	private static boolean canPush(IBlockState state, boolean blocking){
		if(blocking){
			return (state.getBlock() == Blocks.PISTON || state.getBlock() == Blocks.STICKY_PISTON) ? !state.getValue(BlockPistonBase.EXTENDED) : state.getMobilityFlag() != EnumPushReaction.BLOCK && !state.getBlock().hasTileEntity(state) && state.getBlock() != Blocks.OBSIDIAN && state.getBlockHardness(null, null) >= 0;
		}else{
			return (state.getBlock() == Blocks.PISTON || state.getBlock() == Blocks.STICKY_PISTON) ? !state.getValue(BlockPistonBase.EXTENDED) : state.getMobilityFlag() == EnumPushReaction.NORMAL && state.getMaterial() != Material.AIR && !state.getBlock().hasTileEntity(state) && state.getBlock() != Blocks.OBSIDIAN && state.getBlockHardness(null, null) >= 0;
		}
	}

	private static final int PUSH_LIMIT = 64;

	/**
	 * Used recursively to fill a list with the blocks to be moved. Returns true if there is a problem that stops the movement.
	 */
	private static boolean propogate(ArrayList<BlockPos> list, WorldBuffer buf, BlockPos pos, EnumFacing dir, @Nullable BlockPos forward){
		if(list.contains(pos)){
			return false;
		}
		if(!canPush(buf.getBlockState(pos.offset(dir)), true)){
			return true;
		}
		if(forward == null){
			list.add(pos);
		}else{
			list.add(list.indexOf(forward), pos);
		}

		if(buf.getBlockState(pos).getBlock() == Blocks.SLIME_BLOCK){
			//The back has to be checked before the sides or the list ordering gets messed up.
			//Likewise, the sides have to be sent before the front
			if(canPush(buf.getBlockState(pos.offset(dir.getOpposite())), false)){
				if(list.size() > PUSH_LIMIT || propogate(list, buf, pos.offset(dir.getOpposite()), dir, pos)){
					return true;
				}
			}

			for(EnumFacing checkDir : EnumFacing.VALUES){
				if(checkDir != dir && checkDir != dir.getOpposite()){
					if(canPush(buf.getBlockState(pos.offset(checkDir)), false)){
						if(list.size() > PUSH_LIMIT || propogate(list, buf, pos.offset(checkDir), dir, pos)){
							return true;
						}
					}
				}
			}
		}

		if(canPush(buf.getBlockState(pos.offset(dir)), false)){
			if(list.size() > PUSH_LIMIT || propogate(list, buf, pos.offset(dir), dir, null)){
				return true;
			}
		}

		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		setExtension(world, pos, state.getValue(Properties.FACING), 0, getExtension(world, pos, state.getValue(Properties.FACING)));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		neighborChanged(world.getBlockState(pos), world, pos, null, null);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(worldIn.isRemote){
			return;
		}
		checkRedstone(worldIn, pos, state.getValue(Properties.FACING));
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty[] {Properties.FACING, Properties.REDSTONE_BOOL});
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return this.getDefaultState().withProperty(Properties.FACING, EnumFacing.getFront(meta & 7)).withProperty(Properties.REDSTONE_BOOL, (meta & 8) == 8);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.FACING).getIndex() + (state.getValue(Properties.REDSTONE_BOOL) ? 8 : 0);
	}

	@Override
	public EnumPushReaction getMobilityFlag(IBlockState state){
		return state.getValue(Properties.REDSTONE_BOOL) ? EnumPushReaction.BLOCK : EnumPushReaction.NORMAL;
	}


	/**
	 * An alternate version of World#getEntitiesWithinAABBExcludingEntity that checks a 3x3x3 cube of mini chunks (16x16x16 cubes within chunks) for entities.
	 * This is less efficient than the standard method, but necessary to fix a bug.
	 */
	private static ArrayList<Entity> getEntitiesMultiChunk(AxisAlignedBB checkBox, World worldIn){
		ArrayList<Entity> found = new ArrayList<Entity>();

		int i = MathHelper.floor((checkBox.minX - World.MAX_ENTITY_RADIUS) / 16.0D) - 1;
		int j = MathHelper.floor((checkBox.maxX + World.MAX_ENTITY_RADIUS) / 16.0D) + 1;
		int k = MathHelper.floor((checkBox.minZ - World.MAX_ENTITY_RADIUS) / 16.0D) - 1;
		int l = MathHelper.floor((checkBox.maxZ + World.MAX_ENTITY_RADIUS) / 16.0D) + 1;

		int yMin = MathHelper.clamp(MathHelper.floor((checkBox.minY - World.MAX_ENTITY_RADIUS) / 16.0D) - 1, 0, 15);
		int yMax = MathHelper.clamp(MathHelper.floor((checkBox.maxY + World.MAX_ENTITY_RADIUS) / 16.0D) + 1, 0, 15);

		for(int iLoop = i; iLoop <= j; ++iLoop){
			for(int kLoop = k; kLoop <= l; ++kLoop){
				if(((ChunkProviderServer) worldIn.getChunkProvider()).chunkExists(iLoop, kLoop)){
					Chunk chunk = worldIn.getChunkFromChunkCoords(iLoop, kLoop);
					for(int yLoop = yMin; yLoop <= yMax; ++yLoop){
						if(!chunk.getEntityLists()[yLoop].isEmpty()){
							for(Entity entity : chunk.getEntityLists()[yLoop]){
								if(entity.getEntityBoundingBox().intersects(checkBox)){
									found.add(entity);
								}
							}
						}
					}
				}
			}
		}

		return found;
	}
}