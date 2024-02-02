package zmaster587.advancedRocketry.recipe;

import zmaster587.advancedRocketry.tile.multiblock.machine.TileCrystallizer;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipeCrystallizer extends RecipeMachineFactory {

    @Override
    public Class getMachine() {
        return TileCrystallizer.class;
    }
}
