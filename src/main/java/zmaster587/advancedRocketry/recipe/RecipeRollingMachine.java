package zmaster587.advancedRocketry.recipe;

import zmaster587.advancedRocketry.tile.multiblock.machine.TileRollingMachine;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipeRollingMachine extends RecipeMachineFactory {

    @Override
    public Class getMachine() {
        return TileRollingMachine.class;
    }
}
