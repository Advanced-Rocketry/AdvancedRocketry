package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class BlockPlanetSoil extends Block {

	MapColor extraMapColor;
	
	public BlockPlanetSoil() {
		super(Material.ground);
		setHarvestLevel("shovel", 0);
		setStepSound(soundTypeSand);
	}
	
	public Block setMapColor(MapColor color) {
		extraMapColor = color;
		return this;
	}
	
	@Override
	public MapColor getMapColor(int meta) {
		return extraMapColor == null ? super.getMapColor(meta) : extraMapColor;
	}
}
