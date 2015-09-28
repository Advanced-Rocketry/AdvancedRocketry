package zmaster587.advancedRocketry.recipe.NEI;

import static codechicken.lib.gui.GuiDraw.changeTexture;
import static codechicken.lib.gui.GuiDraw.drawTexturedModalRect;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.advancedRocketry.recipe.RecipesBlastFurnace;
import zmaster587.advancedRocketry.recipe.RecipesBlastFurnace.RecipeBlastFurnace;
import zmaster587.advancedRocketry.tile.multiblock.TileElectricArcFurnace;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class BlastFurnaceNEI extends TemplateNEI {
	@Override
	public String getRecipeName() {
		return "Blast Furnace";
	}
	
    
	@Override
	public int recipiesPerPage() {
		return 1;
	}


	@Override
	protected Class getMachine() {
		return TileElectricArcFurnace.class;
	}


	@Override
	protected ProgressBarImage getProgressBar() {
		return TextureResources.arcFurnaceProgressBar;
	}
}
