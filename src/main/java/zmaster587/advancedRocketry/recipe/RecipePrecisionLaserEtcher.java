package zmaster587.advancedRocketry.recipe;

import zmaster587.advancedRocketry.tile.multiblock.machine.TilePrecisionLaserEtcher;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipePrecisionLaserEtcher extends RecipeMachineFactory {

    @Override
    public Class getMachine() {
        return TilePrecisionLaserEtcher.class;
    }
}
