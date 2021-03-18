package zmaster587.advancedRocketry.satellite;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.item.ItemOreScanner;

import java.util.ArrayList;
import java.util.List;

public class SatelliteOreMapping extends SatelliteBase  {

	int blockCenterX, blockCenterZ;
	public static ArrayList<Integer> oreList = new ArrayList<Integer>();

	ItemStack inv;

	int selectedSlot = -1;

	public SatelliteOreMapping() {
	}

	public void setSelectedSlot(int i) { if(canFilterOre()) selectedSlot = i; }

	public int getSelectedSlot() {return selectedSlot;}

	@Override
	public String getInfo(World world) {
		return "Operational";
	}

	public boolean acceptsItemInConstruction(ItemStack item) {
		int flag = SatelliteRegistry.getSatelliteProperty(item).getPropertyFlag();
		return super.acceptsItemInConstruction(item) || SatelliteProperties.Property.DATA.isOfType(flag);
	}

	@Override
	public boolean isAcceptableControllerItemStack(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ItemOreScanner;
	}

	@Override
	public ItemStack getContollerItemStack(ItemStack satIdChip,
			SatelliteProperties properties) {
		ItemStack stack = new ItemStack(AdvancedRocketryItems.itemOreScanner);
		ItemOreScanner scanner = (ItemOreScanner)AdvancedRocketryItems.itemOreScanner;

		scanner.setSatelliteID(stack, properties.getId());

		return stack;
	}

	@Override
	public boolean performAction(EntityPlayer player, World world, BlockPos pos) {
		player.openGui(AdvancedRocketry.instance, 100, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	//TODO: Make satellite require energy, and a lot of it, to scan
	public boolean canBeginScan() {
		return battery.getUniversalEnergyStored() >= 10000;
	}


	public static int[][] scanChunk(World world, int offsetX, int offsetZ, int radius, int blocksPerPixel, ItemStack block) {
		blocksPerPixel = Math.max(blocksPerPixel, 1);
		int[][] ret = new int[(radius*2)/blocksPerPixel][(radius*2)/blocksPerPixel];

		Chunk chunk = world.getChunkFromChunkCoords(offsetX << 4, offsetZ << 4);
		IChunkProvider provider = world.getChunkProvider();


		for(int z = -radius; z < radius; z+=blocksPerPixel){
			for(int x = -radius; x < radius; x+=blocksPerPixel) {
				int oreCount = 0, otherCount = 0;


				for(int y = world.getHeight(); y > 0; y--) {
					for(int deltaY = 0; deltaY < blocksPerPixel; deltaY++) {
						for(int deltaZ = 0; deltaZ < blocksPerPixel; deltaZ++) {

							BlockPos pos = new BlockPos(x + offsetX, y, z + offsetZ);
							if(world.isAirBlock(pos))
								continue;

							//Note:May not work with tileEntities (GT ores)
							boolean found = false;
							List<ItemStack> drops;
							IBlockState state = world.getBlockState(pos);
							if((drops = state.getBlock().getDrops(world,pos, state, 0)) != null)
								for(ItemStack stack : drops) {
									if(stack.getItem() == block.getItem() && stack.getItemDamage() == block.getItemDamage()) {
										oreCount++;
										found = true;
									}
								}

							if(!found)
								otherCount++;
						}
					}
				}
				oreCount /= Math.pow(blocksPerPixel,2);
				otherCount /= Math.pow(blocksPerPixel,2);

				if(Thread.interrupted())
					return null;


				ret[(x+radius)/blocksPerPixel][(z+radius)/blocksPerPixel] = (int)((oreCount/(float)Math.max(otherCount,1))*0xFFFF);
			}
		}

		return ret;
	}
	/**
	 * Note: array returned will be [radius/blocksPerPixel][radius/blocksPerPixel]
	 * @param world
	 * @param offsetX
	 * @param offsetY
	 * @param radius in blocks
	 * @param blocksPerPixel number of blocks squared (n*n) that take up one pixel
	 * @return array of ore vs other block values
	 */
	public static int[][] scanChunk(World world, int offsetX, int offsetZ, int radius, int blocksPerPixel) {
		blocksPerPixel = Math.max(blocksPerPixel, 1);
		int[][] ret = new int[(radius*2)/blocksPerPixel][(radius*2)/blocksPerPixel];

		Chunk chunk = world.getChunkFromChunkCoords(offsetX << 4, offsetZ << 4);
		IChunkProvider provider = world.getChunkProvider();

		if(oreList.isEmpty()) {
			String[] strings = OreDictionary.getOreNames();
			for(String str : strings) {
				if(str.startsWith("ore") || str.startsWith("dust") || str.startsWith("gem"))
					oreList.add(OreDictionary.getOreID(str));
			}
		}

		for(int z = -radius; z < radius; z+=blocksPerPixel){
			for(int x = -radius; x < radius; x+=blocksPerPixel) {
				int oreCount = 0, otherCount = 0;


				for(int y = world.getHeight(); y > 0; y--) {
					for(int deltaY = 0; deltaY < blocksPerPixel; deltaY++) {
						for(int deltaZ = 0; deltaZ < blocksPerPixel; deltaZ++) {

							BlockPos pos = new BlockPos(x + offsetX, y, z + offsetZ);
							if(world.isAirBlock(pos))
								continue;
							boolean exists = false;
							out:
								for(int i : oreList) {
									List<ItemStack> itemlist = OreDictionary.getOres(OreDictionary.getOreName(i));

									for(ItemStack item : itemlist) {
										if(item.getItem() == Item.getItemFromBlock(world.getBlockState(pos).getBlock())) {
											exists = true;
											break out;
										}
									}
								}
							if(exists)
								oreCount++;
							else
								otherCount++;
						}
					}
				}
				oreCount /= Math.pow(blocksPerPixel,2);
				otherCount /= Math.pow(blocksPerPixel,2);

				if(Thread.interrupted())
					return null;


				ret[(x+radius)/blocksPerPixel][(z+radius)/blocksPerPixel] = (int)((oreCount/(float)Math.max(otherCount,1))*0xFFFF);
			}
		}

		return ret;
	}

	@Override
	public double failureChance() { return 0D;}

	@Override
	public String getName() {
		return "Ore Mapper";
	}

	@Override
	public boolean canTick() {
		return false;
	}


	public int getZoomRadius() {
		return Math.min(satelliteProperties.getPowerGeneration(),7);
	}

	public boolean canFilterOre() {
		return satelliteProperties.getMaxDataStorage() == 3000;
	}
}
