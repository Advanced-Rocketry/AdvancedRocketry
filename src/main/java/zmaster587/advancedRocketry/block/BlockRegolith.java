package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class BlockRegolith extends Block {

	MapColor extraMapColor;
	
	public BlockRegolith() {
		super(Material.GROUND);
		setHarvestLevel("shovel", 0);
		setSoundType(SoundType.SAND);
	}
	
	public Block setMapColor(MapColor color) {
		extraMapColor = color;
		return this;
	}
	
//	@Override//TODO colour for soil?
//	public MapColor getMapColor(IBlockState state) {
//		return extraMapColor == null ? super.getMapColor(state) : extraMapColor;
//	}
}
