package zmaster587.advancedRocketry.cable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import zmaster587.advancedRocketry.tile.cables.TilePipe;
import zmaster587.libVulpes.util.SingleEntry;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class CableNetwork {

    protected static HashSet<Integer> usedIds = new HashSet<>();
    protected int numCables = 0;
    int networkID;
    CopyOnWriteArraySet<Entry<TileEntity, EnumFacing>> sources;
    CopyOnWriteArraySet<Entry<TileEntity, EnumFacing>> sinks;

    protected CableNetwork() {

        sources = new CopyOnWriteArraySet<>();
        sinks = new CopyOnWriteArraySet<>();
    }

    public static CableNetwork initWithID(int id) {
        CableNetwork net = new CableNetwork();
        net.networkID = id;

        return net;
    }

    public static CableNetwork initNetwork() {
        Random random = new Random(System.currentTimeMillis());

        int id = random.nextInt();

        while (usedIds.contains(id)) {
            id = random.nextInt();
        }

        CableNetwork net = new CableNetwork();

        usedIds.add(id);
        net.networkID = id;

        return net;
    }

    public Set<Entry<TileEntity, EnumFacing>> getSources() {
        return sources;
    }

    public Set<Entry<TileEntity, EnumFacing>> getSinks() {
        return sinks;
    }

    public void addSource(TileEntity tile, EnumFacing dir) {

        for (Entry<TileEntity, EnumFacing> entry : sources) {
            TileEntity tile2 = entry.getKey();
            if (tile2.equals(tile)) {
                return;
            }
            if (tile2.getPos().compareTo(tile.getPos()) == 0) {
                sources.remove(entry);
                //iter.remove();
                break;
            }
        }

        sources.add(new SingleEntry<>(tile, dir));
    }

    public void addSink(TileEntity tile, EnumFacing dir) {

        for (Entry<TileEntity, EnumFacing> entry : sinks) {
            TileEntity tile2 = entry.getKey();
            if (tile2.equals(tile)) {
                return;
            }
            if (tile2.getPos().compareTo(tile.getPos()) == 0) {
                sinks.remove(entry);
                //iter.remove();
                break;
            }
        }

        sinks.add(new SingleEntry<>(tile, dir));
    }

    public void writeToNBT(NBTTagCompound nbt) {

    }

    public void readFromNBT(NBTTagCompound nbt) {

    }

    public int getNetworkID() {
        return networkID;
    }

    public void removeFromAll(TileEntity tile) {
        Iterator<Entry<TileEntity, EnumFacing>> iter = sources.iterator();

        while (iter.hasNext()) {
            Entry<TileEntity, EnumFacing> entry = iter.next();
            TileEntity tile2 = entry.getKey();
            if (tile2.getPos().compareTo(tile.getPos()) == 0) {
                sources.remove(entry);
                break;
            }
        }

        iter = sinks.iterator();

        while (iter.hasNext()) {
            Entry<TileEntity, EnumFacing> entry = iter.next();
            TileEntity tile2 = entry.getKey();
            if (tile2.getPos().compareTo(tile.getPos()) == 0) {
                sinks.remove(entry);
                break;
            }
        }

    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("NumCables:   " + numCables + "     Sources: ");
        for (Entry<TileEntity, EnumFacing> obj : sources) {
            TileEntity tile = obj.getKey();
            output.append(tile.getPos().getX()).append(",").append(tile.getPos().getY()).append(",").append(tile.getPos().getZ()).append(" ");
        }

        output.append("    Sinks: ");
        for (Entry<TileEntity, EnumFacing> obj : sinks) {
            TileEntity tile = obj.getKey();
            output.append(tile.getPos().getX()).append(",").append(tile.getPos().getY()).append(",").append(tile.getPos().getZ()).append(" ");
        }
        return output.toString();
    }

    /**
     * Merges this network with the one specified.  Normally the specified one is removed
     *
     * @param cableNetwork
     */
    public boolean merge(CableNetwork cableNetwork) {
        sinks.addAll(cableNetwork.getSinks());

        for (Entry<TileEntity, EnumFacing> obj : cableNetwork.getSinks()) {
            //boolean canMerge = true;
            for (Entry<TileEntity, EnumFacing> obj2 : sinks) {
                if (obj.getKey().getPos().compareTo(obj2.getKey().getPos()) == 0 && obj.getValue() == obj2.getValue()) {
                    //canMerge = false;
                    return false;
                }
            }

            //if(canMerge) {
            sinks.add(obj);
            //}
        }

        for (Entry<TileEntity, EnumFacing> obj : cableNetwork.getSources()) {
            //boolean canMerge = true;
            for (Entry<TileEntity, EnumFacing> obj2 : sources) {
                if (obj.getKey().getPos().compareTo(obj2.getKey().getPos()) == 0 && obj.getValue() == obj2.getValue()) {
                    //canMerge = false;
                    return false;
                }
            }

            //if(canMerge) {
            sources.add(obj);
            //}
        }
        return true;
    }

    public void addPipeToNetwork(TilePipe tile) {
        numCables++;
    }

    public void tick() {
    }

    public void removePipeFromNetwork(TilePipe tilePipe) {
        numCables--;

    }
}
