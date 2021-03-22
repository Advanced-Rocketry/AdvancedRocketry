package zmaster587.advancedRocketry.tile.multiblock.drill;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

abstract class AbstractDrill {

	abstract ItemStack[] performOperation();

	abstract boolean activate(World world, int x, int z);

	abstract void deactivate();

	abstract boolean isFinished();

	abstract boolean needsRestart();
}
