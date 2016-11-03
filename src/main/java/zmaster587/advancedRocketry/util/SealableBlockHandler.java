package zmaster587.advancedRocketry.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.IFluidBlock;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.atmosphere.IAtmosphereSealHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler for checking if blocks can be used to deal a room.
 * <p/>
 * Created by Dark(DarkGuardsman, Robert) on 1/6/2016.
 */
public final class SealableBlockHandler implements IAtmosphereSealHandler
{
	/** List of blocks not allowed. */
	private List<Block> blockBanList = new ArrayList();
	/** List of blocks that are allowed regardless of properties. */
	private List<Block> blockAllowList = new ArrayList();
	/** List of block materials not allowed. */
	private List<Material> materialBanList = new ArrayList();
	/** List of block materials that are allowed regardless of properties. */
	private List<Material> materialAllowList = new ArrayList();
	//TODO add meta support
	//TODO add complex logic support threw API interface
	//TODO add complex logic handler for integration support

	/** INSTANCE */
	public static final SealableBlockHandler INSTANCE = new SealableBlockHandler();

	private SealableBlockHandler() {}

	/**
	 * Checks to see if the block at the location can be sealed
	 * @param world
	 * @param pos
	 * @return
	 */
	@Override
	public boolean isBlockSealed(World world, BlockPos pos)
	{
		//Ensure we are not checking outside of the map
		if(pos.getY() >= 0 && pos.getY() <= 256)
		{
			//Prevents orphan chunk loading - DarkGuardsman
			//ChunkExists
			if(world instanceof WorldServer && !((WorldServer) world).getChunkProvider().chunkExists(pos.getX() >> 4, pos.getZ() >> 4))
			{
				return false;
			}

			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			Material material = state.getMaterial();

			//Always allow list
			if (blockAllowList.contains(block) || materialAllowList.contains(material))
			{
				return true;
			}
			//Always block list
			else if (blockBanList.contains(block) || materialBanList.contains(material))
			{
				return false;
			}
			else if (material.isLiquid() || !material.isSolid())
			{
				return false;
			}
			else if (world.isAirBlock(pos) || block instanceof IFluidBlock)
			{
				return false;
			}
			//TODO replace with seal logic handler
			else if (block == AdvancedRocketryBlocks.blockAirLock)
			{
				return checkDoorIsSealed(world, pos, state);
			}
			//TODO add is side solid check, which will require forge direction or side check. Eg more complex logic...
			return isFulBlock(world, pos);
		}
		return false;
	}

	@Override
	public void addUnsealableBlock(Block block)
	{
		if (!blockBanList.contains(block))
		{
			blockBanList.add(block);
		}
		if (blockAllowList.contains(block))
		{
			blockAllowList.remove(block);
		}
	}

	@Override
	public void addSealableBlock(Block block)
	{
		if (!blockAllowList.contains(block))
		{
			blockAllowList.add(block);
		}
		if (blockBanList.contains(block))
		{
			blockBanList.remove(block);
		}
	}

	/**
	 * Checks if a block is full sized based off of block bounds. This
	 * is not a perfect check as mods may have a full size. However,
	 * have a 3D model that doesn't look a full block in size. There
	 * is no way around this other than to make a black list check.
	 *
	 * @param world - world
	 * @param pos   - location
	 * @return true if full block
	 */
	public static boolean isFulBlock(World world, BlockPos pos)
	{
		return isFullBlock(world, pos, world.getBlockState(pos));
	}

	/**
	 * Checks if a block is full sized based off of block bounds. This
	 * is not a perfect check as mods may have a full size. However,
	 * have a 3D model that doesn't look a full block in size. There
	 * is no way around this other than to make a black list check.
	 *
	 * @param block - block to compare
	 * @return true if full block
	 */
	public static boolean isFullBlock(IBlockAccess world, BlockPos pos, IBlockState state)
	{
		AxisAlignedBB bb = state.getCollisionBoundingBox((World) world, pos);
		
        int minX = (int) (bb.minX * 100);
        int minY = (int) (bb.minY * 100);
        int minZ = (int) (bb.minZ * 100);
        int maxX = (int) (bb.maxX * 100);
        int maxY = (int) (bb.maxY * 100);
        int maxZ = (int) (bb.maxZ * 100);

        return minX == 0 && minY == 0 && minZ == 0 && maxX == 100 && maxY == 100 && maxZ == 100;
	}

	//TODO unit test, document, cleanup
	private boolean checkDoorIsSealed(World world, BlockPos pos, IBlockState state)
	{
		IBlockState state2 = state;
		//For some reason the actual direction is stored in the bottom block of a door, so get that, but use the current block to determine openness due to order of update
		if(state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER)
			state2 = world.getBlockState(pos.down());
		
		if(state.getValue(BlockDoor.OPEN))
			return isBlockSealed(world, pos.offset(state2.getValue(BlockDoor.FACING))) && isBlockSealed(world, pos.offset(state2.getValue(BlockDoor.FACING).rotateYCCW().rotateYCCW())); 
		//state.getValue(BlockDoor.FACING)
		return isBlockSealed(world, pos.offset(state2.getValue(BlockDoor.FACING).rotateY())) && isBlockSealed(world, pos.offset(state2.getValue(BlockDoor.FACING).rotateYCCW())); 
	}

	/**
	 * Checks if the block is banned from being a seal
	 *
	 * @param block - checked block
	 * @return true if it is banned, based on ban list only.
	 */
	public boolean isBlockBanned(Block block)
	{
		return blockBanList.contains(block);
	}

	/**
	 * Checks if the material is banned from being a seal
	 *
	 * @param mat - material being checked
	 * @return true if it is banned, based on ban list only.
	 */
	public boolean isMaterialBanned(Material mat)
	{
		return materialBanList.contains(mat);
	}

	/**
	 * Loads defaults..
	 */
	public void loadDefaultData()
	{
		materialBanList.add(Material.AIR);
		materialBanList.add(Material.CACTUS);
		materialBanList.add(Material.CRAFTED_SNOW);
		materialBanList.add(Material.FIRE);
		materialBanList.add(Material.LEAVES);
		materialBanList.add(Material.PORTAL);
		materialBanList.add(Material.VINE);
		materialBanList.add(Material.PLANTS);
		materialBanList.add(Material.CORAL);
		materialBanList.add(Material.WEB);
		materialBanList.add(Material.SPONGE);
		materialBanList.add(Material.SAND);

		//TODO check each vanilla block
	}
}
