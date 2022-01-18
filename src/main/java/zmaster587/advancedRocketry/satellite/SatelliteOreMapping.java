package zmaster587.advancedRocketry.satellite;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.inventory.ContainerOreMappingSatellite;
import zmaster587.advancedRocketry.inventory.ContainerRegistry;
import zmaster587.advancedRocketry.item.tools.ItemOreScanner;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SatelliteOreMapping extends SatelliteBase implements INamedContainerProvider {

	int blockCenterX, blockCenterZ;
	public static ArrayList<Item> oreList = new ArrayList<>();

	private int selectedSlot = -1;

	public SatelliteOreMapping() {
		super();
	}

	public void setSelectedSlot(int i) { if(canFilterOre()) selectedSlot = i; }

	public int getSelectedSlot() {return selectedSlot;}

	@Override
	public String getInfo(World world) {
		return "Operational";
	}

	public boolean acceptsItemInConstruction(@Nonnull ItemStack item) {
		int flag = SatelliteRegistry.getSatelliteProperty(item).getPropertyFlag();
		return super.acceptsItemInConstruction(item) || SatelliteProperties.Property.DATA.isOfType(flag);
	}

	@Override
	public boolean isAcceptableControllerItemStack(@Nonnull ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ItemOreScanner;
	}

	@Override
	@Nonnull
	public ItemStack getControllerItemStack(@Nonnull ItemStack satIdChip,
											SatelliteProperties properties) {
		ItemStack stack = new ItemStack(AdvancedRocketryItems.itemOreScanner);
		ItemOreScanner scanner = (ItemOreScanner)AdvancedRocketryItems.itemOreScanner;

		scanner.setSatelliteID(stack, properties.getId());
		return stack;
	}

	@Override
	public boolean performAction(PlayerEntity player, World world, BlockPos pos) {
		NetworkHooks.openGui((ServerPlayerEntity)player, this, (packet) -> packet.writeBoolean(false));
		return true;
	}

	public boolean canBeginScan() {
		return battery.extractEnergy(1000, true) == 1000;
	}

	public int[][] scanChunk(World world, int offsetX, int offsetZ, int radius, int blocksPerPixel, @Nonnull ItemStack block, int zoomLevel) {
		blocksPerPixel = Math.max(blocksPerPixel, 1);
		int[][] ret = new int[(radius*2)/blocksPerPixel][(radius*2)/blocksPerPixel];

		Chunk chunk = world.getChunk(offsetX << 4, offsetZ << 4);
		AbstractChunkProvider provider = world.getChunkProvider();

		if (canBeginScan() && battery.extractEnergy(375 * zoomLevel, false) == 375 * zoomLevel) {
			//Base cost is 1000 per scan
			battery.extractEnergy(1000, false);
			//Modified by 375 * zoom level for a filtered scan
			battery.extractEnergy(375 * zoomLevel, true);
			//Now for the actual scanning
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
								BlockState state = world.getBlockState(pos);

                                if (!state.getDrops(new Builder((ServerWorld) world)).isEmpty()) {
									drops = state.getDrops(new Builder((ServerWorld) world));
									for (ItemStack stack : drops) {
										if (stack.getItem() == block.getItem() && stack.getDamage() == block.getDamage()) {
											oreCount++;
											found = true;
										}
									}

								}

								if (!found)
									otherCount++;
							}
						}
					}
					oreCount /= Math.pow(blocksPerPixel, 2);
					otherCount /= Math.pow(blocksPerPixel, 2);

					if (Thread.interrupted())
						return null;


					ret[(x + radius) / blocksPerPixel][(z + radius) / blocksPerPixel] = (int) ((oreCount / (float) Math.max(otherCount, 1)) * 0xFFFF);
				}
			}
		}
		return ret;
	}
	/**
	 * Note: array returned will be [radius/blocksPerPixel][radius/blocksPerPixel]
	 * @param world
	 * @param offsetX
	 * @param offsetZ
	 * @param radius in blocks
	 * @param blocksPerPixel number of blocks squared (n*n) that take up one pixel
	 * @param zoomLevel
	 * @return array of ore vs other block values
	 */
	public int[][] scanChunk(World world, int offsetX, int offsetZ, int radius, int blocksPerPixel, int zoomLevel) {
		blocksPerPixel = Math.max(blocksPerPixel, 1);
		int[][] ret = new int[(radius*2)/blocksPerPixel][(radius*2)/blocksPerPixel];

		Chunk chunk = world.getChunk(offsetX << 4, offsetZ << 4);
		AbstractChunkProvider provider = world.getChunkProvider();
		//Get all the ores we want to look for
		if(oreList.isEmpty()) {
			Collection<ResourceLocation> strings = ItemTags.getCollection().getRegisteredTags();
			for(ResourceLocation loc : strings) {
				String str = loc.getPath();
				if(str.startsWith("ore") || str.startsWith("dust") || str.startsWith("gem"))
					oreList.addAll(ItemTags.getCollection().get(loc).getAllElements());
			}
		}
		if (canBeginScan() && battery.extractEnergy(250 * zoomLevel, false) == 250 * zoomLevel) {
			//Base cost is 1000 per scan
			battery.extractEnergy(1000, false);
			//Modified by 250 * zoom level for a basic, unfiltered scan
			battery.extractEnergy(250 * zoomLevel, true);
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
								for(Item item : oreList) {
									if (item == Item.getItemFromBlock(world.getBlockState(pos).getBlock())) {
										exists = true;
										break;
									}
								}

								if (exists)
									oreCount++;
								else
									otherCount++;
							}
						}
					}
					oreCount /= Math.pow(blocksPerPixel, 2);
					otherCount /= Math.pow(blocksPerPixel, 2);

					if (Thread.interrupted())
						return null;


					ret[(x + radius) / blocksPerPixel][(z + radius) / blocksPerPixel] = (int) ((oreCount / (float) Math.max(otherCount, 1)) * 0xFFFF);
				}
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


	public int getZoomRadius() {
		return Math.min(satelliteProperties.getPowerGeneration()/4,7);
	}

	public boolean canFilterOre() {
		return satelliteProperties.getMaxDataStorage() == 3000;
	}

	@Override
	@ParametersAreNonnullByDefault
	public Container createMenu(int id, PlayerInventory playerinv, PlayerEntity player) {
		return new ContainerOreMappingSatellite(ContainerRegistry.CONTAINER_SATELLITE, id, this, playerinv, player);
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new StringTextComponent(getName());
	}
}
