package zmaster587.advancedRocketry.block;

import zmaster587.advancedRocketry.api.IMiningDrill;
import zmaster587.advancedRocketry.tile.TileDrill;
import net.minecraft.world.World;

public class BlockMiningDrill extends BlockTile implements IMiningDrill {

	public BlockMiningDrill() {
		super(TileDrill.class, zmaster587.advancedRocketry.inventory.GuiHandler.guiId.MODULAR.ordinal());
	}

	@Override
	public float getMiningSpeed(World world, int x, int y, int z) {
		return world.isAirBlock(x, y + 1, z) && world.isAirBlock(x, y + 2, z) ? 0.01f : 0f;
	}

	@Override
	public int powerConsumption() {
		return 0;
	}

}
