package zmaster587.advancedRocketry.block;

import zmaster587.advancedRocketry.api.IMiningDrill;
import zmaster587.advancedRocketry.tile.TileDrill;
import zmaster587.libVulpes.block.BlockTile;
import zmaster587.libVulpes.block.BlockFullyRotatable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockMiningDrill extends BlockFullyRotatable implements IMiningDrill {

	public BlockMiningDrill() {
		super(Material.ROCK);
		//super(TileDrill.class, zmaster587.libVulpes.inventory.GuiHandler.guiId.MODULAR.ordinal());
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return false;
	}

	@Override
	public float getMiningSpeed(World world, BlockPos pos) {
		return world.isAirBlock(pos.add(0,1,0)) && world.isAirBlock(pos.add(0,2,0)) ? 0.02f : 0.01f;
	}

	@Override
	public int powerConsumption() {
		return 0;
	}

}
