package zmaster587.advancedRocketry.world;

import java.util.List;
import java.util.Random;

import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.util.OreGenProperties;
import zmaster587.advancedRocketry.util.OreGenProperties.OreEntry;
import zmaster587.advancedRocketry.world.decoration.MapGenHighCaves;
import zmaster587.advancedRocketry.world.decoration.MapGenMassiveRavine;
import zmaster587.advancedRocketry.world.ore.CustomizableOreGen;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCavesHell;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorSimplex;

public class ChunkProviderAsteroids extends ChunkProviderPlanet {

    protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
    private final World world;
    private final boolean generateStructures;
    private final Random rand;
    private double[] buffer;
    private NoiseGeneratorOctaves lperlinNoise1;
    private NoiseGeneratorOctaves lperlinNoise2;
    private NoiseGeneratorOctaves perlinNoise1;
    /** Determines whether slowsand or gravel can be generated at a location */
    private NoiseGeneratorOctaves slowsandGravelNoiseGen;
    /** Determines whether something other than nettherack can be generated at a location */
    private NoiseGeneratorOctaves netherrackExculsivityNoiseGen;
    public NoiseGeneratorOctaves scaleNoise;
    public NoiseGeneratorOctaves depthNoise;
    public NoiseGeneratorSimplex islandNoise;
    private MapGenBase genNetherCaves = new MapGenCavesHell();
    double[] pnr;
    double[] ar;
    double[] br;
    private IBlockState oceanBlock;
    private IBlockState fillblock;


    public ChunkProviderAsteroids(World worldIn, boolean p_i45637_2_, long seed, String p_i46668_5_)
    {
        super(worldIn, seed, p_i45637_2_, p_i46668_5_);
        this.world = worldIn;
        this.generateStructures = p_i45637_2_;
        this.rand = new Random(seed);
        this.lperlinNoise1 = new NoiseGeneratorOctaves(this.rand, 16);
        this.lperlinNoise2 = new NoiseGeneratorOctaves(this.rand, 16);
        this.perlinNoise1 = new NoiseGeneratorOctaves(this.rand, 8);
        this.slowsandGravelNoiseGen = new NoiseGeneratorOctaves(this.rand, 4);
        this.netherrackExculsivityNoiseGen = new NoiseGeneratorOctaves(this.rand, 8);
        this.scaleNoise = new NoiseGeneratorOctaves(this.rand, 10);
        this.depthNoise = new NoiseGeneratorOctaves(this.rand, 16);
        this.islandNoise = new NoiseGeneratorSimplex(this.rand);

        DimensionProperties dimProps = DimensionManager.getInstance().getDimensionProperties(worldIn.provider.getDimension());

        worldIn.setSeaLevel(dimProps.getSeaLevel());

        IBlockState oceanBlock = dimProps.getOceanBlock();
        if(oceanBlock != null) {
            this.oceanBlock = oceanBlock;
        } else {
            this.oceanBlock = Blocks.WATER.getDefaultState();
        }

        IBlockState fillBlock = dimProps.getStoneBlock();
        if(fillBlock != null) {
            this.fillblock = fillBlock;
        } else {
            this.fillblock = Blocks.STONE.getDefaultState();
        }

        net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextHell ctx =
                new net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextHell(lperlinNoise1, lperlinNoise2, perlinNoise1, slowsandGravelNoiseGen, netherrackExculsivityNoiseGen, scaleNoise, depthNoise);
        ctx = net.minecraftforge.event.terraingen.TerrainGen.getModdedNoiseGenerators(worldIn, this.rand, ctx);
        this.lperlinNoise1 = ctx.getLPerlin1();
        this.lperlinNoise2 = ctx.getLPerlin2();
        this.perlinNoise1 = ctx.getPerlin();
        this.slowsandGravelNoiseGen = ctx.getPerlin2();
        this.netherrackExculsivityNoiseGen = ctx.getPerlin3();
        this.scaleNoise = ctx.getScale();
        this.depthNoise = ctx.getDepth();
        this.genNetherCaves = net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(genNetherCaves, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.NETHER_CAVE);
        super.heightmapOffset = 0;
        super.heightmapMult *= 1D;
    }

    public void prepareHeights(int p_185936_1_, int p_185936_2_, int yOffset, ChunkPrimer primer) {
        this.buffer = this.getHeights(this.buffer, p_185936_1_ * 4, 0, p_185936_2_ * 4, 5, 17, 5);

        for (int j1 = 0; j1 < 4; ++j1) {
            for (int k1 = 0; k1 < 4; ++k1) {
                for (int l1 = 0; l1 < 16; ++l1) {
                    double d0 = 0.125D;
                    double d1 =  this.buffer[((j1) * 5 + k1) * 17 + l1];
                    double d2 = this.buffer[((j1) * 5 + k1 + 1) * 17 + l1];
                    double d3 = this.buffer[((j1 + 1) * 5 + k1) * 17 + l1];
                    double d4 = this.buffer[((j1 + 1) * 5 + k1 + 1) * 17 + l1];
                    double d5 = (this.buffer[((j1) * 5 + k) * 17 + l1 + 1] - d1) * d0;
                    double d6 = (this.buffer[((j1) * 5 + k1 + 1) * 17 + l1 + 1] - d2) * d0;
                    double d7 = (this.buffer[((j1 + 1) * 5 + k1) * 17 + l1 + 1] - d3) * d0;
                    double d8 = (this.buffer[((j1 + 1) * 5 + k1 + 1) * 17 + l1 + 1] - d4) * d0;

                    for (int i2 = 0; i2 < 8; ++i2) {
                        double d9 = 0.25D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * 0.25D;
                        double d13 = (d4 - d2) * 0.25D;

                        for (int j2 = 0; j2 < 4; ++j2) {
                            double d14 = 0.25D;
                            double d15 = d10;
                            double d16 = (d11 - d10) * 0.25D;

                            for (int k2 = 0; k2 < 4; ++k2)
                            {
                                IBlockState iblockstate = null;

                                if (d15 > 10.0D)
                                {
                                    iblockstate = this.fillblock;
                                }

                                int l2 = j2 + j1 * 4;
                                int i3 = i2 + l1 * 8;
                                int j3 = k2 + k1 * 4;
                                primer.setBlockState(l2, i3 + yOffset, j3, iblockstate);
                                d15 += d16;
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
    }

    private double[] getHeights(double[] p_185963_1_, int p_185963_2_, int p_185963_3_, int p_185963_4_, int p_185963_5_, int p_185963_6_, int p_185963_7_) {
        net.minecraftforge.event.terraingen.ChunkGeneratorEvent.InitNoiseField event = new net.minecraftforge.event.terraingen.ChunkGeneratorEvent.InitNoiseField(this, p_185963_1_, p_185963_2_, p_185963_3_, p_185963_4_, p_185963_5_, p_185963_6_, p_185963_7_);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == net.minecraftforge.fml.common.eventhandler.Event.Result.DENY) return event.getNoisefield();

        if (p_185963_1_ == null) {
            p_185963_1_ = new double[p_185963_5_ * p_185963_6_ * p_185963_7_];
        }

        double d0 = 684.412D;
        double d1 = 684.412D;
        double scale = 1;
        d0 = d0 * 6D;
        this.pnr = this.perlinNoise1.generateNoiseOctaves(this.pnr, (int)(scale*p_185963_2_), (int)(scale*p_185963_3_), (int)(scale*p_185963_4_), p_185963_5_, p_185963_6_, p_185963_7_, scale*d0 / 80.0D, scale*4.277575000000001D, scale*d0 / 80.0D);
        this.ar = this.lperlinNoise1.generateNoiseOctaves(this.ar, (int)(scale*p_185963_2_), (int)(scale*p_185963_3_), (int)(scale*p_185963_4_), p_185963_5_, p_185963_6_, p_185963_7_, scale*d0, scale*684.412D, scale*d0);
        this.br = this.lperlinNoise2.generateNoiseOctaves(this.br, (int)(scale*p_185963_2_), (int)(scale*p_185963_3_), (int)(scale*p_185963_4_), p_185963_5_, p_185963_6_, p_185963_7_, scale*d0, scale*684.412D, scale*d0);
        int i = p_185963_2_ / 2;
        int j = p_185963_4_ / 2;
        int k = 0;

        for (int l = 0; l < p_185963_5_; ++l) {
            for (int i1 = 0; i1 < p_185963_7_; ++i1) {
                float f = this.getIslandHeightValue(i, j, l, i1);

                for (int j1 = 0; j1 < p_185963_6_; ++j1) {
                    double d2 = this.ar[k] / 512.0D;
                    double d3 = this.br[k] / 512.0D;
                    double d5 = (this.pnr[k] / 10.0D + 1.0D) / 2.0D;
                    double d4;

                    if (d5 < 0.0D) {
                        d4 = d2;
                    } else if (d5 > 1.0D) {
                        d4 = d3;
                    } else {
                        d4 = d2 + (d3 - d2) * d5;
                    }

                    d4 = d4 - 8.0D;
                    d4 = d4 + (double)f;
                    int k1 = 2;

                    if (j1 > p_185963_6_ / 2 - k1) {
                        double d6 = (float)(j1 - (p_185963_6_ / 2 - k1)) / 64.0F;
                        d6 = MathHelper.clamp(d6, 0.0D, 1.0D);
                        d4 = d4 * (1.0D - d6) + -3000.0D * d6;
                    }

                    k1 = 8;

                    if (j1 < k1) {
                        double d7 = (float)(k1 - j1) / ((float)k1 - 1.0F);
                        d4 = d4 * (1.0D - d7) + -30.0D * d7;
                    }

                    p_185963_1_[k] = d4;
                    ++k;
                }
            }
        }

        return p_185963_1_;
    }

    private float getIslandHeightValue(int p_185960_1_, int p_185960_2_, int p_185960_3_, int p_185960_4_)
    {
        float f = (float)(p_185960_1_ * 2 + p_185960_3_);
        float f1 = (float)(p_185960_2_ * 2 + p_185960_4_);
        float f2 = 100.0F - MathHelper.sqrt(f * f + f1 * f1) * 8.0F;

        if (f2 > 80.0F) {
            f2 = 80.0F;
        }

        if (f2 < -100.0F) {
            f2 = -100.0F;
        }

        for (int i = -12; i <= 12; ++i) {
            for (int j = -12; j <= 12; ++j) {
                long k = p_185960_1_ + i;
                long l = p_185960_2_ + j;

                if (this.islandNoise.getValue((double)k, (double)l) < -0.9399999761581421D) {
                    float f3 = (MathHelper.abs((float)k) * 3439.0F + MathHelper.abs((float)l) * 147.0F) % 13.0F + 9.0F;
                    f = (float)(p_185960_3_ - i * 2);
                    f1 = (float)(p_185960_4_ - j * 2);
                    float f4 = 100.0F - MathHelper.sqrt(f * f + f1 * f1) * f3;

                    if (f4 > 80.0F)
                    {
                        f4 = 80.0F;
                    }

                    if (f4 < -100.0F)
                    {
                        f4 = -100.0F;
                    }

                    if (f4 > f2)
                    {
                        f2 = f4;
                    }
                }
            }
        }

        return f2*2;
    }

    /**
     * Generates the chunk at the specified position, from scratch
     */
    public Chunk generateChunk(int x, int z) {
        this.rand.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
        ChunkPrimer chunkprimer = new ChunkPrimer();

        //this.makeasteroids(x, z, chunkprimer);
        this.prepareHeights(x, z, 0, chunkprimer);
        this.prepareHeights(x + 500, z + 500, 100, chunkprimer);
        //this.genNetherCaves.generate(this.world, x, z, chunkprimer);

        Chunk chunk = new Chunk(this.world, chunkprimer, x, z);
        Biome[] abiome = this.world.getBiomeProvider().getBiomes(null, x * 16, z * 16, 16, 16);
        byte[] abyte = chunk.getBiomeArray();

        for (int i = 0; i < abyte.length; ++i) {
            abyte[i] = (byte)Biome.getIdForBiome(abiome[i]);
        }

        chunk.setLightPopulated(true);
        return chunk;
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    @Override
    public List<SpawnListEntry> getPossibleCreatures(
            EnumCreatureType creatureType, BlockPos pos) {
        Biome biome = this.world.getBiome(pos);

        return biome.getSpawnableList(creatureType);
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {

    }

    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void populate(int x, int z) {
        net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.world, this.rand, x, z, false);

        OreGenProperties oreGenProperties = DimensionManager.getInstance().getDimensionProperties(this.world.provider.getDimension()).getOreGenProperties(this.world);

        if(oreGenProperties != null) {
            for(OreEntry entry : oreGenProperties.getOreEntries()) {
                new CustomizableOreGen(entry).generate(rand, x, z, this.world, this, this.world.getChunkProvider());
            }
        }

        BlockFalling.fallInstantly = false;

    }
}
