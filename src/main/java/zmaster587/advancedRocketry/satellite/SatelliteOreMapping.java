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
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.SatelliteRegistry;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.inventory.ContainerOreMappingSatallite;
import zmaster587.advancedRocketry.inventory.ContainerRegistry;
import zmaster587.advancedRocketry.item.ItemOreScanner;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SatelliteOreMapping extends SatelliteBase implements INamedContainerProvider {

	int blockCenterX, blockCenterZ;
	public static ArrayList<Item> oreList = new ArrayList<Item>();

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
		return SatelliteProperties.Property.MAIN.isOfType(flag) || SatelliteProperties.Property.POWER_GEN.isOfType(flag) || SatelliteProperties.Property.DATA.isOfType(flag);
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
;
		return stack;
	}

	@Override
	public boolean performAction(PlayerEntity player, World world, BlockPos pos) {
		NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)this, (packet) -> { packet.writeBoolean(false); });
		return true;
	}


	public static int[][] scanChunk(World world, int offsetX, int offsetZ, int radius, int blocksPerPixel, ItemStack block) {
		blocksPerPixel = Math.max(blocksPerPixel, 1);
		int[][] ret = new int[(radius*2)/blocksPerPixel][(radius*2)/blocksPerPixel];

		Chunk chunk = world.getChunk(offsetX << 4, offsetZ << 4);
		AbstractChunkProvider provider = world.getChunkProvider();


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

							if((drops = state.getDrops(new Builder((ServerWorld) world))) != null)
								for(ItemStack stack : drops) {
									if(stack.getItem() == block.getItem() && stack.getDamage() == block.getDamage()) {
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

		Chunk chunk = world.getChunk(offsetX << 4, offsetZ << 4);
		AbstractChunkProvider provider = world.getChunkProvider();

		if(oreList.isEmpty()) {
			Collection<ResourceLocation> strings = ItemTags.getCollection().getRegisteredTags();
			for(ResourceLocation loc : strings) {
				String str = loc.getPath();
				if(str.startsWith("ore") || str.startsWith("dust") || str.startsWith("gem"))
					oreList.addAll(ItemTags.getCollection().get(loc).getAllElements());
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
								for(Item item : oreList) {
									if(item == Item.getItemFromBlock(world.getBlockState(pos).getBlock())) {
										exists = true;
										break out;
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

	@Override
	public void tickEntity() {		
	}

	public int getZoomRadius() {
		return Math.min(satelliteProperties.getPowerGeneration(),7);
	}

	public boolean canFilterOre() {
		return satelliteProperties.getMaxDataStorage() == 3000;
	}

	@Override
	public Container createMenu(int id, PlayerInventory playerinv, PlayerEntity player) {
		return new ContainerOreMappingSatallite(ContainerRegistry.CONTAINER_SATELLITE, id, this, playerinv, player);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new StringTextComponent(getName());
	}
}
