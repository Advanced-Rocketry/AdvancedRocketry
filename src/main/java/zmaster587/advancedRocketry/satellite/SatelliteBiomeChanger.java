package zmaster587.advancedRocketry.satellite;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.item.ItemBiomeChanger;
import zmaster587.advancedRocketry.network.PacketBiomeIDChange;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.BlockPosition;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;

public class SatelliteBiomeChanger extends SatelliteEnergy implements IUniversalEnergy {

	private int biomeId;
	private int radius;
	//Stores blocks to be updated
	//Note: we really don't care about order, in fact, lack of order is better
	private List<BlockPosition> toChangeList;
	private static int MAX_SIZE = 1024;

	public SatelliteBiomeChanger() {
		radius = 4;
		toChangeList = new LinkedList<BlockPosition>();
	}

	public void setBiome(int biomeId) {
		this.biomeId = biomeId;
	}

	public int getBiome() {
		return biomeId;
	}

	@Override
	public String getInfo(World world) {
		return "Ready";
	}

	@Override
	public String getName() {
		return "Biome Changer";
	}

	@Override
	public ItemStack getContollerItemStack(ItemStack satIdChip,
			SatelliteProperties properties) {

		ItemBiomeChanger idChipItem = (ItemBiomeChanger)satIdChip.getItem();
		idChipItem.setSatellite(satIdChip, properties);
		return satIdChip;
	}

	@Override
	public boolean isAcceptableControllerItemStack(ItemStack stack) {
		return stack != null && stack.getItem() instanceof ItemBiomeChanger;
	}

	@Override
	public boolean canTick() {
		return true;
	}

	@Override
	public void tickEntity() {
		//This is hacky..
		World world = net.minecraftforge.common.DimensionManager.getWorld(getDimensionId());

		if(world != null) {

			for(int i = 0; i < 10; i++) {
				if(world.getTotalWorldTime() % 1 == 0 && !toChangeList.isEmpty()) {
					if(extractEnergy(10, true) ==10 ) {
						extractEnergy(10, false);
						BlockPosition pos = toChangeList.remove(world.rand.nextInt(toChangeList.size()));

						Chunk chunk = world.getChunkFromBlockCoords(pos.x, pos.z);

						BiomeGenBase biome = world.getBiomeGenForCoords(pos.x, pos.z);
						BiomeGenBase biomeTo = BiomeGenBase.getBiome(biomeId);
						if(biome.topBlock != biomeTo.topBlock) {
							int yy = world.getHeightValue(pos.x, pos.z);
							if(world.getBlock(pos.x, yy - 1, pos.z) == biome.topBlock)
								world.setBlock(pos.x, yy - 1, pos.z, biomeTo.topBlock);

							pos.y = (short)yy;
						}

						byte[] biomeArr = chunk.getBiomeArray();
						biomeArr[(pos.x % 16)+ (pos.z % 16)*16] = (byte)biomeId;

						PacketHandler.sendToNearby(new PacketBiomeIDChange(chunk, world, pos), world.provider.dimensionId, pos.x, pos.y, pos.z, 256);

					}
					else
						break;
				}
			}
		}
	}

	public void addBlockToList(BlockPosition pos) {
		if(toChangeList.size() < MAX_SIZE)
			toChangeList.add(pos);
	}

	@Override
	public boolean performAction(EntityPlayer player, World world, int x,
			int y, int z) {
		if(world.isRemote)
			return false;
		Set<Chunk> set = new HashSet<Chunk>();
		radius = 16;
		MAX_SIZE = 1024;
		for(int xx = -radius + x; xx < radius + x; xx++) {
			for(int zz = -radius + z; zz < radius + z; zz++) {


				addBlockToList(new BlockPosition(xx, 0, zz));
				/*BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
				BiomeGenBase biomeTo = BiomeGenBase.getBiome(biomeId);
				if(biome.topBlock != biomeTo.topBlock) {
					int yy = world.getHeightValue(xx, zz);
					if(world.getBlock(xx, yy - 1, zz) == biome.topBlock)
						world.setBlock(xx, yy-1, zz, biomeTo.topBlock);
				}*/

			}
		}

		//Some kind of compiler optimization is breaking if we assign block and biome in the same loop
		//Causing execution order to vary from source
		/*for(int xx = -radius + x; xx < radius + x; xx++) {
			for(int zz = -radius + z; zz < radius + z; zz++) {
				set.add(world.getChunkFromBlockCoords(xx, zz));
				byte[] biomeArr = world.getChunkFromBlockCoords(xx, zz).getBiomeArray();
				biomeArr[(xx % 16)+ (zz % 16)*16] = (byte)biomeId;
			}
		}*/

		/*for(Chunk chunk : set) {
			PacketHandler.sendToNearby(new PacketBiomeIDChange(chunk, world), world.provider.dimensionId, x, y, z, 64);
		}*/
		return false;
	}

	@Override
	public double failureChance() {
		return 0;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("biome", biomeId);

		int array[] = new int[toChangeList.size()*3];
		Iterator<BlockPosition> itr = toChangeList.iterator();
		for(int i = 0; i < toChangeList.size(); i+=3) {
			BlockPosition pos = itr.next();
			array[i] = pos.x;
			array[i+1] = pos.y;
			array[i+2] = pos.z;
		}
		nbt.setTag("posList", new NBTTagIntArray(array));
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		biomeId = nbt.getInteger("biomeId");

		int array[] = nbt.getIntArray("posList");

		toChangeList.clear();
		for(int i = 0; i < array.length; i +=3) {
			toChangeList.add(new BlockPosition(array[i], array[i+1], array[i+2]));
		}
	}

	@Override
	public void setEnergyStored(int amt) {
		battery.setEnergyStored(amt);
	}

	@Override
	public int extractEnergy(int amt, boolean simulate) {
		if(getDimensionId() != -1) {
			World world = net.minecraftforge.common.DimensionManager.getWorld(getDimensionId());
			if(world != null) {
				battery.acceptEnergy(energyCreated(world), false);
			}
		}
		return battery.extractEnergy(amt, simulate);
	}

	@Override
	public int getEnergyStored() {

		if(getDimensionId() != -1) {
			World world = net.minecraftforge.common.DimensionManager.getWorld(getDimensionId());
			if(world != null) {
				battery.acceptEnergy(energyCreated(world), false);
			}
		}

		return battery.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return battery.getMaxEnergyStored();
	}

	@Override
	public int acceptEnergy(int amt, boolean simulate) {
		return battery.acceptEnergy(amt, simulate);
	}
}
