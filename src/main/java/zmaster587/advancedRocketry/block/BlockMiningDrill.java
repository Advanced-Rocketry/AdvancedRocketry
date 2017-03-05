package zmaster587.advancedRocketry.block;

import zmaster587.advancedRocketry.api.IMiningDrill;
import zmaster587.advancedRocketry.tile.TileDrill;
import zmaster587.libVulpes.block.BlockTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockMiningDrill extends BlockTile implements IMiningDrill {

	public BlockMiningDrill() {
		super(TileDrill.class, zmaster587.libVulpes.inventory.GuiHandler.guiId.MODULAR.ordinal());
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return false;
	}
	@Override
	public float getMiningSpeed(World world, int x, int y, int z) {
		return 0.01f;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		return false;//super.onBlockActivated(world, x, y, z, player, par6, par7, par8, par9);
	}
	
	@Override
	public int powerConsumption() {
		return 0;
	}

}
