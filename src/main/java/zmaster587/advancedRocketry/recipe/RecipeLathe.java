package zmaster587.advancedRocketry.recipe;

import zmaster587.advancedRocketry.tile.multiblock.machine.TileLathe;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipeLathe extends RecipeMachineFactory {

    @Override
    public Class getMachine() {
        return TileLathe.class;
    }
}
