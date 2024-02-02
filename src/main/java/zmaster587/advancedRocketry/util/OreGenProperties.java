package zmaster587.advancedRocketry.util;

import net.minecraft.block.state.IBlockState;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.dimension.DimensionProperties.AtmosphereTypes;
import zmaster587.advancedRocketry.dimension.DimensionProperties.Temps;

import java.util.LinkedList;
import java.util.List;

public class OreGenProperties {

    /**
     * Array of properties for [pressure][temperature]
     *
     * @see DimensionProperties.AtmosphereTypes
     * @see DimensionProperties.Temps
     */
    private static OreGenProperties[][] oreGenPropertyMap = new OreGenProperties[DimensionProperties.AtmosphereTypes.values().length][DimensionProperties.Temps.values().length];
    private List<OreEntry> oreEntries;

    public OreGenProperties() {
        oreEntries = new LinkedList<>();
    }

    /**
     * Sets any planet with temperature temp to use these properties regardless of pressure
     *
     * @param temp       Temperature to set
     * @param properties
     */
    public static void setOresForTemperature(Temps temp, OreGenProperties properties) {
        for (int i = 0; i < AtmosphereTypes.values().length; i++)
            oreGenPropertyMap[i][temp.ordinal()] = properties;
    }

    public static void setOresForPressure(AtmosphereTypes atmType, OreGenProperties properties) {
        for (int i = 0; i < Temps.values().length; i++)
            oreGenPropertyMap[atmType.ordinal()][i] = properties;
    }

    public static void setOresForPressureAndTemp(AtmosphereTypes atmType, Temps temp, OreGenProperties properties) {
        oreGenPropertyMap[atmType.ordinal()][temp.ordinal()] = properties;
    }

    public static OreGenProperties getOresForPressure(AtmosphereTypes atmType, Temps temp) {
        return oreGenPropertyMap[atmType.ordinal()][temp.ordinal()];
    }

    public void addEntry(IBlockState state, int minHeight, int maxHeight, int clumpSize, int chancePerChunk) {
        oreEntries.add(new OreEntry(state, minHeight, maxHeight, clumpSize, chancePerChunk));
    }

    public List<OreEntry> getOreEntries() {
        return oreEntries;
    }

    public static class OreEntry {
        int minHeight;
        int maxHeight;
        int clumpSize;
        int chancePerChunk;
        private IBlockState state;

        public OreEntry(IBlockState state, int minHeight, int maxHeight, int clumpSize, int chancePerChunk) {
            this.state = state;
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
            this.clumpSize = clumpSize;
            this.chancePerChunk = chancePerChunk;
        }

        public IBlockState getBlockState() {
            return state;
        }

        public int getMinHeight() {
            return minHeight;
        }

        public int getMaxHeight() {
            return maxHeight;
        }

        public int getClumpSize() {
            return clumpSize;
        }

        public int getChancePerChunk() {
            return chancePerChunk;
        }
    }

}
