package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

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

	@Deprecated
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		return this.extraMapColor;
	}

}
