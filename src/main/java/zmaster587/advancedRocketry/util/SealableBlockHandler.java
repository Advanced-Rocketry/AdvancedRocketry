package zmaster587.advancedRocketry.util;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.atmosphere.IAtmosphereSealHandler;
import zmaster587.libVulpes.util.BlockPosition;

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

    private SealableBlockHandler(){}

    @Override
    public boolean isBlockSealed(World world, int x, int y, int z)
    {
        return isBlockSealed(world, new BlockPosition(x, y, z));
    }

    public boolean isBlockSealed(World world, BlockPosition pos)
    {
        Block block = world.getBlock(pos.x, pos.y, pos.z);
        int meta = world.getBlockMetadata(pos.x, pos.y, pos.z);
        Material material = block.getMaterial();

        //Always allow list
        if(blockAllowList.contains(block) || materialAllowList.contains(material))
        {
            return true;
        }
        //Always block list
        else if(blockBanList.contains(block) || materialBanList.contains(material))
        {
            return false;
        }
        else if (material.isLiquid() || !material.isSolid())
        {
            return false;
        }
        else if (world.isAirBlock(pos.x, pos.y, pos.z) || block instanceof IFluidBlock)
        {
            return false;
        }
        //TODO replace with seal logic handler
        else if (block == AdvancedRocketryBlocks.blockAirLock)
        {
            return checkDoorIsSealed(world, pos, meta);
        }
        //TODO add is side solid check, which will require forge direction or side check. Eg more complex logic...
        return isFulBlock(world, pos);
    }

    @Override
    public void addUnsealableBlock(Block block)
    {
        if(!blockBanList.contains(block))
        {
            blockBanList.add(block);
        }
        if(blockAllowList.contains(block))
        {
            blockAllowList.remove(block);
        }
    }

    @Override
    public void addSealableBlock(Block block)
    {
        if(!blockAllowList.contains(block))
        {
            blockAllowList.add(block);
        }
        if(blockBanList.contains(block))
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
    public static boolean isFulBlock(World world, BlockPosition pos)
    {
        Block block = world.getBlock(pos.x, pos.y, pos.z);
        //size * 100 to correct rounding errors
        int minX = (int) (block.getBlockBoundsMinX() * 100);
        int minY = (int) (block.getBlockBoundsMinY() * 100);
        int minZ = (int) (block.getBlockBoundsMinZ() * 100);
        int maxX = (int) (block.getBlockBoundsMaxX() * 100);
        int maxY = (int) (block.getBlockBoundsMaxY() * 100);
        int maxZ = (int) (block.getBlockBoundsMaxZ() * 100);

        return minX == 0 && minY == 0 && minZ == 0 && maxX == 100 && maxY == 100 && maxZ == 100;
    }

    //TODO unit test, document, cleanup
    private boolean checkDoorIsSealed(World world, BlockPosition pos, int meta)
    {
        //TODO: door corners
        return ((meta & 8) == 8
                ||
                ((meta & 4) >> 2 == (meta & 1) && checkDoorSeal(world, pos.getPositionAtOffset(0, 0, 1), meta)
                        && checkDoorSeal(world, pos.getPositionAtOffset(0, 0, -1), meta))
                ||
                (meta & 4) >> 2 != (meta & 1) && checkDoorSeal(world, pos.getPositionAtOffset(1, 0, 0), meta)
                        && checkDoorSeal(world, pos.getPositionAtOffset(-1, 0, 0), meta));
    }

    //TODO unit test, document, cleanup
    private boolean checkDoorSeal(World world, BlockPosition pos, int meta)
    {
        Block otherBlock = world.getBlock(pos.x, pos.y, pos.z);
        int otherMeta = world.getBlockMetadata(pos.x, pos.y, pos.z);

        return (otherBlock == AdvancedRocketryBlocks.blockAirLock && (otherMeta & 1) == (meta & 1)) ||
                (otherBlock != AdvancedRocketryBlocks.blockAirLock && isBlockSealed(world, pos));
    }

    /**
     * Loads defaults..
     */
    public void loadDefaultData()
    {
        materialBanList.add(Material.air);
        materialBanList.add(Material.cactus);
        materialBanList.add(Material.craftedSnow);
        materialBanList.add(Material.fire);
        materialBanList.add(Material.leaves);
        materialBanList.add(Material.portal);
        materialBanList.add(Material.vine);
        materialBanList.add(Material.plants);
        materialBanList.add(Material.coral);
        materialBanList.add(Material.web);
        materialBanList.add(Material.sponge);
        materialBanList.add(Material.sand);

        //TODO check each vanilla block
    }
}
