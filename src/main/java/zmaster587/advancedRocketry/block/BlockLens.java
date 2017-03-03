package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockLens extends BlockGlass {
	public BlockLens() {
		super(Material.GLASS, true);
		setSoundType(SoundType.GLASS);
	}
}
