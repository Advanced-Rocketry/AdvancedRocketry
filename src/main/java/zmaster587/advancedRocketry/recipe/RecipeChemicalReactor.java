package zmaster587.advancedRocketry.recipe;

import zmaster587.advancedRocketry.tile.multiblock.machine.TileChemicalReactor;
import zmaster587.libVulpes.recipe.RecipeMachineFactory;

public class RecipeChemicalReactor extends RecipeMachineFactory {

    @Override
    public Class getMachine() {
        return TileChemicalReactor.class;
    }
}
