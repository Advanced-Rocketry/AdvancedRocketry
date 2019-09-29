package zmaster587.advancedRocketry.api.atmosphere;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Interface used to talk to the internal seal handler for advanced rocketry.
 *
 * Keep in mind most blocks are already check for seal vs not seal. So you
 * only need to register blocks that do not fit the logic correctly. For
 * example 3D machines that are a full block. However, are not sealed
 * for any number of reasons including their shape.
 *
 * Created by Dark(DarkGuardsman, Robert) on 1/6/2016.
 */
public interface IAtmosphereSealHandler
{
    /**
     * Checks if a block can be used to seal a room for the blob
     *
     * @param world - world
     * @return true if it can be used to seal the blob
     */
    boolean isBlockSealed(World world, BlockPos pos);

    /**
     * Simplified way to ban an entire block set, including all metadata.
     *
     * @param block - block to ban, will remove off of allow list if present.
     */
    void addUnsealableBlock(Block block);

    /**
     * Simplified way to allow a non-solid block from being used as a seal.
     *
     * @param block - block to allow, will remove off of disable list if present.
     */
    void addSealableBlock(Block block);
    
    /**
     * Returns a list of blocks that can have been forced to be sealable
     */
    List<Block> getOverridenSealableBlocks();
}
