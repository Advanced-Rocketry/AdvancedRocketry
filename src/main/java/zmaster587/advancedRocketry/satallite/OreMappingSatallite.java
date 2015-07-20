package zmaster587.advancedRocketry.satallite;

import java.util.ArrayList;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.ISatallite;

public class OreMappingSatallite implements ISatallite  {

	int blockCenterX, blockCenterZ;
	public static ArrayList<Integer> oreList = new ArrayList<Integer>();

	ItemStack inv;

	int selectedSlot = -1;

	public OreMappingSatallite(int x, int z) {
		blockCenterX = x;
		blockCenterZ = z;
	}

	public int getBlockCenterX() {
		return blockCenterX;
	}

	public int getBlockCenterZ() {
		return blockCenterZ;
	}

	public void setSelectedSlot(int i) { selectedSlot = i; }

	public int getSelectedSlot() {return selectedSlot;}

	@Override
	public String getInfo() {
		return "Operational";
	}

	@Override
	public boolean performAction(EntityPlayer player, World world, int x, int y, int z) {
		player.openGui(AdvancedRocketry.instance, 100, world, x, y, z);
		return true;
	}


	public static int[][] scanChunk(World world, int offsetX, int offsetZ, int radius, int blocksPerPixel, ItemStack block) {
		blocksPerPixel = Math.max(blocksPerPixel, 1);
		int[][] ret = new int[(radius*2)/blocksPerPixel][(radius*2)/blocksPerPixel];

		Chunk chunk = world.getChunkFromBlockCoords(offsetX, offsetZ);
		IChunkProvider provider = world.getChunkProvider();


		for(int z = -radius; z < radius; z+=blocksPerPixel){
			for(int x = -radius; x < radius; x+=blocksPerPixel) {
				int oreCount = 0, otherCount = 0;


				for(int y = world.getHeight(); y > 0; y--) {
					for(int deltaY = 0; deltaY < blocksPerPixel; deltaY++) {
						for(int deltaZ = 0; deltaZ < blocksPerPixel; deltaZ++) {


							if(world.isAirBlock(x + offsetX, y, z + offsetZ))
								continue;

							//Note:May not work with tileEntities (GT ores)
							boolean found = false;
									for(ItemStack stack : world.getBlock(x + offsetX, y, z + offsetZ).getDrops(world,x + offsetX, y, z + offsetZ, world.getBlockMetadata(x + offsetX, y, z + offsetZ), 0)) {
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

		Chunk chunk = world.getChunkFromBlockCoords(offsetX, offsetZ);
		IChunkProvider provider = world.getChunkProvider();

		if(oreList.isEmpty()) {
			String[] strings = OreDictionary.getOreNames();
			for(String str : strings) {
				if(str.contains("ore"))
					oreList.add(OreDictionary.getOreID(str));
			}
		}

		for(int z = -radius; z < radius; z+=blocksPerPixel){
			for(int x = -radius; x < radius; x+=blocksPerPixel) {
				int oreCount = 0, otherCount = 0;


				for(int y = world.getHeight(); y > 0; y--) {
					for(int deltaY = 0; deltaY < blocksPerPixel; deltaY++) {
						for(int deltaZ = 0; deltaZ < blocksPerPixel; deltaZ++) {


							if(world.isAirBlock(x + offsetX, y, z + offsetZ))
								continue;
							boolean exists = false;
							out:
								for(int i : oreList) {
									ArrayList<ItemStack> itemlist = OreDictionary.getOres(i);

									for(ItemStack item : itemlist) {
										if(item.getItem() == Item.getItemFromBlock(world.getBlock(x + offsetX, y, z + offsetZ))) {
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
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("CenterX", blockCenterX);
		nbt.setInteger("CenterZ", blockCenterZ);


	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		blockCenterX = nbt.getInteger("CenterX");
		blockCenterZ = nbt.getInteger("CenterZ");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Ore Mapper";
	}
}
