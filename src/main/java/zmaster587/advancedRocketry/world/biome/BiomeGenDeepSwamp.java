package zmaster587.advancedRocketry.world.biome;

import java.util.Random;

import zmaster587.advancedRocketry.world.decoration.MapGenCrater;
import zmaster587.advancedRocketry.world.gen.WorldGenNoTree;
import zmaster587.advancedRocketry.world.gen.WorldGenSwampTree;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenSwamp;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenDeepSwamp extends BiomeGenBase {

	public static MapGenBase swampTree;
	private final static WorldGenNoTree noTree = new WorldGenNoTree(false);
	
	public BiomeGenDeepSwamp(int biomeId, boolean register) {
		super(biomeId,register);
		this.biomeName="DeepSwamp";
		rootHeight=-0.1f;
		heightVariation=0.2f;
		rainfall = 0.9f;
		temperature = 0.9f;
		this.theBiomeDecorator.treesPerChunk = 10;
        this.theBiomeDecorator.flowersPerChunk = 1;
        this.theBiomeDecorator.deadBushPerChunk = 1;
        this.theBiomeDecorator.mushroomsPerChunk = 8;
        this.theBiomeDecorator.reedsPerChunk = 10;
        this.theBiomeDecorator.clayPerChunk = 1;
        this.theBiomeDecorator.waterlilyPerChunk = 4;
        this.theBiomeDecorator.sandPerChunk2 = 0;
        this.theBiomeDecorator.sandPerChunk = 0;
        this.theBiomeDecorator.grassPerChunk = 5;
        this.waterColorMultiplier = 14745518;
        this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySlime.class, 1, 1, 1));
        this.flowers.clear();
        this.addFlower(Blocks.red_flower, 1, 10);
		swampTree = new WorldGenSwampTree(2);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getSkyColorByTemp(float p_76731_1_) {
		return 0x203020;
	}
	
	
    public WorldGenAbstractTree func_150567_a(Random p_150567_1_)
    {
        return this.worldGeneratorSwamp;
    }

    public String func_150572_a(Random p_150572_1_, int p_150572_2_, int p_150572_3_, int p_150572_4_)
    {
        return BlockFlower.field_149859_a[1];
    }
	
	@Override
	public void genTerrainBlocks(World world, Random rand,
			Block[] block, byte[] abyte, int x,
			int z, double noise) {
        double d1 = plantNoise.func_151601_a((double)x * 0.25D, (double)z * 0.25D);

        if (d1 > 0.0D)
        {
            int k = x & 15;
            int l = z & 15;
            int i1 = block.length / 256;

            for (int j1 = 255; j1 >= 0; --j1)
            {
                int k1 = (l * 16 + k) * i1 + j1;

                if (block[k1] == null || block[k1].getMaterial() != Material.air)
                {
                    if (j1 == 62 && block[k1] != Blocks.water)
                    {
                    	block[k1] = Blocks.water;

                        if (d1 < 0.12D)
                        {
                        	block[k1 + 1] = Blocks.waterlily;
                        }
                    }

                    break;
                }
            }
        }

        this.genBiomeTerrain(world, rand, block, abyte, x, z, noise);
        
		//Decoration time takes too long due to block relights, so run at terrain gen time
		///swampTree.func_151539_a(null, world, x, z, block); //Arg 1 never actually used so fake it
		//Yes this is hacky
		if(x % 16 == 0 && z % 16 == 0 )
			swampTree.func_151539_a(null, world, x/16, z/16, block);
	}
	
    /**
     * Provides the basic grass color based on the biome temperature and rainfall
     */
    @SideOnly(Side.CLIENT)
    public int getBiomeGrassColor(int p_150558_1_, int p_150558_2_, int p_150558_3_)
    {
        double d0 = plantNoise.func_151601_a((double)p_150558_1_ * 0.0225D, (double)p_150558_3_ * 0.0225D);
        return d0 < -0.1D ? 5011004 : 6975545;
    }

    /**
     * Provides the basic foliage color based on the biome temperature and rainfall
     */
    @SideOnly(Side.CLIENT)
    public int getBiomeFoliageColor(int p_150571_1_, int p_150571_2_, int p_150571_3_)
    {
        return 6975545;
    }
}
