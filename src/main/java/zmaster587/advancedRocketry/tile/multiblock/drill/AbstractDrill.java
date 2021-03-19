package zmaster587.advancedRocketry.tile.multiblock.drill;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.ARConfiguration;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractDrill {
	protected static List<ItemStack> ores;

	AbstractDrill() {
		//isEmpty check because <init> is called in post init to register for holo projector
		if (ores == null && !ARConfiguration.getCurrentConfig().standardLaserDrillOres.isEmpty()) {
			ores = new ArrayList<>();
			for (int i = 0; i < ARConfiguration.getCurrentConfig().standardLaserDrillOres.size(); i++) {
				String oreDictName = ARConfiguration.getCurrentConfig().standardLaserDrillOres.get(i);

				String[] args = oreDictName.split(":");

				List<ItemStack> ores2 = OreDictionary.getOres(args[0]);

				if (ores2 != null && !ores2.isEmpty()) {
					int amt = 5;
					if (args.length > 1) {
						try {
							amt = Integer.parseInt(args[1]);
						} catch (NumberFormatException ignored) {}
					}
					ores.add(new ItemStack(ores2.get(0).getItem(), amt, ores2.get(0).getItemDamage()));
				} else {
					String[] splitStr = oreDictName.split(":");
					String name;
					try {
						name = splitStr[0] + ":" + splitStr[1];
					} catch (IndexOutOfBoundsException e) {
						AdvancedRocketry.logger.warn("Unexpected ore name: \"" + oreDictName + "\" during laser drill harvesting");
						continue;
					}

					int meta = 0;
					int size = 1;
					//format: "name meta size"
					if (splitStr.length > 2) {
						try {
							meta = Integer.parseInt(splitStr[2]);
						} catch (NumberFormatException ignored) {}
					}
					if (splitStr.length > 3) {
						try {
							size = Integer.parseInt(splitStr[3]);
						} catch (NumberFormatException ignored) {}
					}

					ItemStack stack = ItemStack.EMPTY;
					Block block = Block.getBlockFromName(name);
					if (block == null) {
						Item item = Item.getByNameOrId(name);
						if (item != null)
							stack = new ItemStack(item, size, meta);
					} else
						stack = new ItemStack(block, size, meta);

					if (!stack.isEmpty())
						ores.add(stack);
				}
			}
		}
	}

	abstract ItemStack[] performOperation();

	abstract boolean activate(World world, int x, int z);

	abstract void deactivate();

	abstract boolean isFinished();

	abstract boolean needsRestart();
}
