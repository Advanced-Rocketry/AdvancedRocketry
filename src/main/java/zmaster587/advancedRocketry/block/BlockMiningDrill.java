package zmaster587.advancedRocketry.block;

import zmaster587.advancedRocketry.api.IMiningDrill;
import zmaster587.advancedRocketry.tile.TileDrill;
import net.minecraft.world.IWorldAccess;

public class BlockMiningDrill extends BlockTile implements IMiningDrill {

	public BlockMiningDrill() {
		super(TileDrill.class, zmaster587.advancedRocketry.inventory.GuiHandler.guiId.MODULAR.ordinal());
	}

	@Override
	public float getMiningSpeed(IWorldAccess world, int x, int y, int z) {
		return 0.01f;
	}

	@Override
	public int powerConsumption() {
		return 0;
	}

}
