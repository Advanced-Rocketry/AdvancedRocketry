package zmaster587.advancedRocketry.tile.multiblock.drill;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

/**
 * This drill is used if the laserDrillPlanet config option is disabled. It simply conjures ores from nowhere
 */
class VoidDrill extends AbstractDrill {

	private final Random random;

	VoidDrill() {
		super();
		this.random = new Random();
	}

	/**
	 * Performs a single drilling operation
	 *
	 * @return The ItemStacks produced by this tick of drilling
	 */
	ItemStack[] performOperation() {
		ArrayList<ItemStack> items = new ArrayList<>();
		if (random.nextInt(10) == 0) {
			ItemStack item = ores.get(random.nextInt(ores.size()));
			ItemStack newStack = item.copy();
			items.add(newStack);
		} else
			items.add(new ItemStack(Blocks.COBBLESTONE, 5));

		ItemStack[] stacks = new ItemStack[items.size()];

		stacks = items.toArray(stacks);

		return stacks;
	}

	boolean activate(World world, int x, int z) {
		return true;
	}

	void deactivate() {}

	boolean isFinished() {
		return false;
	}

	boolean needsRestart() {
		return false;
	}
}
