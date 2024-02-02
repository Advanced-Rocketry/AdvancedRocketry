package zmaster587.advancedRocketry.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockAdvancedBipropellantRocketMotor extends BlockBipropellantRocketMotor {

    public BlockAdvancedBipropellantRocketMotor(Material mat) {
        super(mat);
    }

    @Override
    public int getThrust(World world, BlockPos pos) {
        return 50;
    }

    @Override
    public int getFuelConsumptionRate(World world, int x, int y, int z) {
        return 3;
    }
}
