package zmaster587.advancedRocketry.block;

import zmaster587.libVulpes.tile.TileModelRender;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockAdvRocketMotor extends BlockRocketMotor {

	public BlockAdvRocketMotor(Material mat) {
		super(mat,TileModelRender.models.ADVROCKET.ordinal());
	}
	
	@Override
	public int getThrust(World world, int x, int y, int z) {
		return 50;
	}
	
	@Override
	public int getFuelConsumptionRate(World world, int x, int y, int z) {
		return 2;
	}
}
