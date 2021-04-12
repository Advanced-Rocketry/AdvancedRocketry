package zmaster587.advancedRocketry.tile.multiblock.orbitallaserdrill;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * This drill is used if the laserDrillPlanet config option is disabled. It simply conjures ores from nowhere
 */
class VoidDrill extends AbstractDrill {

	private final Random random;
	private List<ItemStack> ores;
	private boolean planetOresInitialized;

	VoidDrill() {
		this.random = new Random();
		this.planetOresInitialized = false;
		loadGlobalOres();
	}

	private void loadGlobalOres() {
		//isEmpty check because <init> is called in post init to register for holo projector
		if (ores == null && !ARConfiguration.getCurrentConfig().standardLaserDrillOres.isEmpty()) {
			ores = new ArrayList<>();

			for (int i = 0; i < ARConfiguration.getCurrentConfig().standardLaserDrillOres.size(); i++) {
				String oreDictName = ARConfiguration.getCurrentConfig().standardLaserDrillOres.get(i);

				String[] args = oreDictName.split(";");
				
				List<Item> items = ItemTags.getCollection().getTagByID(new ResourceLocation(args[0])).getAllElements();
		

				List<ItemStack> globalOres = new LinkedList<ItemStack>();
				for(Item item : items)
				{
					globalOres.add(item.getDefaultInstance());
				}

				if (globalOres != null && !globalOres.isEmpty()) {
					int amt = 5;
					if (args.length > 1) {
						try {
							amt = Integer.parseInt(args[1]);
						} catch (NumberFormatException ignored) {}
					}
					ores.add(new ItemStack(globalOres.get(0).getItem(), amt));
				} else {
					String[] splitStr = oreDictName.split(";");
					ResourceLocation name;
					
					name = ResourceLocation.tryCreate(splitStr[0]);
					if(name == null)
					{
						AdvancedRocketry.logger.error("Cannot load item or itemtag '" + name + "' for void Drill");
						continue;
					}
					
					int size = 1;
					//format: "name size"
					if (splitStr.length > 2) {
						try {
							size = Integer.parseInt(splitStr[2]);
						} catch (NumberFormatException ignored) {}
					}

					ItemStack stack = ItemStack.EMPTY;
					
					if(ForgeRegistries.ITEMS.containsKey(name))
						stack = new ItemStack(ForgeRegistries.ITEMS.getValue(name), size);
					else
					{
						AdvancedRocketry.logger.error("Cannot load item or itemtag '" + name + "' for void Drill");
						continue;
					}

					if (!stack.isEmpty())
						ores.add(stack);
				}
			}
		}
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
		// Ideally, this should be done in the constructor, but the world provider is null there for reasons unknown, so this gets delayed until first activation
		if(!this.planetOresInitialized) {
			DimensionProperties dimProperties = DimensionManager.getInstance().getDimensionProperties(world);
			ores.addAll(dimProperties.laserDrillOres.stream().filter(s->!ores.contains(s)).collect(Collectors.toSet()));
			this.planetOresInitialized = true;
		}
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
