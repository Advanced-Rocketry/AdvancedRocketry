package zmaster587.advancedRocketry.world.decoration;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenVillage;

public class MapGenSpaceVillage extends MapGenVillage {

    private int size;
    private int distance;
    private final int minTownSeparation;
    public static List<Biome> BLACKLIST_VILLAGE_SPAWN_BIOMES = Arrays.<Biome>asList(Biomes.OCEAN, Biomes.DEEP_OCEAN);

    public MapGenSpaceVillage()
    {
    	super();
        this.distance = 32;
        this.minTownSeparation = 8;
    }
	
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= this.distance - 1;
        }

        if (chunkZ < 0)
        {
            chunkZ -= this.distance - 1;
        }

        int k = chunkX / this.distance;
        int l = chunkZ / this.distance;
        Random random = this.world.setRandomSeed(k, l, 10387312);
        k = k * this.distance;
        l = l * this.distance;
        k = k + random.nextInt(this.distance - 8);
        l = l + random.nextInt(this.distance - 8);

        if (i == k && j == l)
        {
            boolean flag = !this.world.getBiomeProvider().areBiomesViable(i * 16 + 8, j * 16 + 8, 0, BLACKLIST_VILLAGE_SPAWN_BIOMES);

            if (flag)
            {
                return true;
            }
        }

        return false;
    }

}
