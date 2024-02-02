package zmaster587.advancedRocketry.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockNuclearRocketMotor extends BlockRocketMotor {

    public BlockNuclearRocketMotor(Material mat) {
        super(mat);
    }

    @Override
    public int getThrust(World world, BlockPos pos) {
        return 35;
    }

    @Override
    public int getFuelConsumptionRate(World world, int x, int y, int z) {
        return 1;
    }
}
