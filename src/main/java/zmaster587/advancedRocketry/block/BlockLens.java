package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/**
 * Yes, this class may seem useless, but setSoundType can't be run in the registry, only by a subclass of Block.
 */
public class BlockLens extends BlockGlass {
    public BlockLens() {
        super(Material.GLASS, true);
        setSoundType(SoundType.GLASS);
    }
}
