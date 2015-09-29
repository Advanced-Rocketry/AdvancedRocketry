package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockAlphaTexture extends Block {

	public BlockAlphaTexture(Material mat) {
		super(mat);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}
}
