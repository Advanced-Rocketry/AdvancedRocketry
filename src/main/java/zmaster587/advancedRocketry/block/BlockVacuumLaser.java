package zmaster587.advancedRocketry.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.tile.TileForceFieldProjector;
import zmaster587.libVulpes.block.BlockFullyRotatable;

public class BlockVacuumLaser extends BlockFullyRotatable {

	public BlockVacuumLaser(Material material) {
		super(material);
	}

}
