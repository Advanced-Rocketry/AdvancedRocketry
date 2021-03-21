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

	abstract ItemStack[] performOperation();

	abstract boolean activate(World world, int x, int z);

	abstract void deactivate();

	abstract boolean isFinished();

	abstract boolean needsRestart();
}
