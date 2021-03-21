package zmaster587.advancedRocketry.satellite;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;
import zmaster587.advancedRocketry.item.ItemBiomeChanger;
import zmaster587.advancedRocketry.util.BiomeHandler;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.util.HashedBlockPosition;

import java.util.*;

public class SatelliteBiomeChanger extends SatelliteBase  {

	private int biomeId;
	private int radius;

	//Stores blocks to be updated
	//Note: we really don't care about order, in fact, lack of order is better
	private List<HashedBlockPosition> toChangeList;
	private Set<Byte> discoveredBiomes;
	private static int MAX_SIZE = 1024;

	public SatelliteBiomeChanger() {
		super();
		radius = 4;
		toChangeList = new LinkedList<HashedBlockPosition>();
		discoveredBiomes = new HashSet<Byte>();
	}

	public void setBiome(int biomeId) {
		this.biomeId = biomeId;
	}

	public int getBiome() {
		return biomeId;
	}

	public Set<Byte> discoveredBiomes() {
		return discoveredBiomes;
	}

	public void addBiome(int biome) {
		byte byteBiome = (byte)biome;
		
		if(!AdvancedRocketryBiomes.instance.getBlackListedBiomes().contains(biome))
			discoveredBiomes.add(byteBiome);
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
		return !stack.isEmpty() && stack.getItem() instanceof ItemBiomeChanger;
	}


	@Override
	public void tickEntity() {
		//This is hacky..
		World world = net.minecraftforge.common.DimensionManager.getWorld(getDimensionId());

		if(world != null) {

			for(int i = 0; i < 10; i++) {
				if(world.getTotalWorldTime() % 1 == 0 && !toChangeList.isEmpty()) {
					if(battery.extractEnergy(120, true) == 120 ) {
						HashedBlockPosition pos = toChangeList.remove(world.rand.nextInt(toChangeList.size()));

						BiomeHandler.changeBiome(world, biomeId, pos.getBlockPos());

					}
					else
						break;
				}
			}
		}
		super.tickEntity();
	}

	public void addBlockToList(HashedBlockPosition pos) {
		if(toChangeList.size() < MAX_SIZE)
			toChangeList.add(pos);
	}

	@Override
	public boolean performAction(EntityPlayer player, World world, BlockPos pos) {
		if(world.isRemote)
			return false;
		Set<Chunk> set = new HashSet<Chunk>();
		radius = 16;
		MAX_SIZE = 1024;
		for(int xx = -radius + pos.getX(); xx < radius + pos.getX(); xx++) {
			for(int zz = -radius + pos.getZ(); zz < radius + pos.getZ(); zz++) {


				addBlockToList(new HashedBlockPosition(xx, 0, zz));
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
		nbt.setInteger("biomeId", biomeId);

		int array[] = new int[toChangeList.size()*3];
		Iterator<HashedBlockPosition> itr = toChangeList.iterator();
		for(int i = 0; i < toChangeList.size(); i+=3) {
			HashedBlockPosition pos = itr.next();
			array[i] = pos.x;
			array[i+1] = pos.y;
			array[i+2] = pos.z;
		}
		nbt.setTag("posList", new NBTTagIntArray(array));

		array = new int[discoveredBiomes.size()];

		int i = 0;
		for(byte biome : discoveredBiomes) {
			array[i] = biome;
			i++;
		}

		nbt.setTag("biomeList", new NBTTagIntArray(array));
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		biomeId = nbt.getInteger("biomeId");

		int array[] = nbt.getIntArray("posList");

		toChangeList.clear();
		for(int i = 0; i < array.length; i +=3) {
			toChangeList.add(new HashedBlockPosition(array[i], array[i+1], array[i+2]));
		}

		array = nbt.getIntArray("biomeList");
		discoveredBiomes.clear();
		for(int i = 0; i < array.length; i ++) {
			discoveredBiomes.add((byte) array[i]);
		}
	}
}
