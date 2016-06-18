package zmaster587.advancedRocketry.block;

import zmaster587.advancedRocketry.api.IMiningDrill;
import zmaster587.advancedRocketry.tile.TileDrill;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockMiningDrill extends BlockTile implements IMiningDrill {

	public BlockMiningDrill() {
		super(TileDrill.class, zmaster587.advancedRocketry.inventory.GuiHandler.guiId.MODULAR.ordinal());
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return false;
	}
	@Override
	public float getMiningSpeed(World world, int x, int y, int z) {
		return world.isAirBlock(x, y + 1, z) && world.isAirBlock(x, y + 2, z) ? 0.01f : 0f;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		return true;//super.onBlockActivated(world, x, y, z, player, par6, par7, par8, par9);
	}
	
	@Override
	public int powerConsumption() {
		return 0;
	}

}
