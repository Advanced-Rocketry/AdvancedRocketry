package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class BlockPlanetSoil extends Block {

	MapColor extraMapColor;
	
	public BlockPlanetSoil() {
		super(Material.GROUND);
		setHarvestLevel("shovel", 0);
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
