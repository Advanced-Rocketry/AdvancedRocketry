package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.block.BlockMeta;

import java.util.List;
import java.util.stream.Collectors;

public class MapGenCrater extends MapGenBase {

    int chancePerChunk;
    boolean largeCraters;

    public MapGenCrater(int chancePerChunk, boolean largeCraters) {
        this.chancePerChunk = chancePerChunk;
        this.largeCraters = largeCraters;
        //We want these to be a little bigger to fit max
        this.range = 9;
    }

    //Ignore liquids, and ignore air. Everything else is fair game
    private static boolean isCraterIgnoredBlock(Block block) {
        return block instanceof BlockLiquid || block instanceof IFluidBlock || block == Blocks.AIR || block == Blocks.ICE;
    }

    @Override
    public void generate(World worldIn, int x, int z, ChunkPrimer primer) {
        int i = this.range;
        this.world = worldIn;
        this.rand.setSeed(worldIn.getSeed());
        long j = this.rand.nextLong();
        long k = this.rand.nextLong();

        for (int l = x - i; l <= x + i; ++l) {
            for (int i1 = z - i; i1 <= z + i; ++i1) {
                long j1 = (long) l * j;
                long k1 = (long) i1 * k;
                this.rand.setSeed(j1 ^ k1 ^ worldIn.getSeed());
                this.recursiveGenerate(worldIn, l, i1, x, z, primer);
            }
        }
    }

    @Override
    protected void recursiveGenerate(World world, int chunkX, int chunkZ, int p_180701_4_, int p_180701_5_, ChunkPrimer chunkPrimerIn) {

        DimensionProperties props = DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension());

        List<IBlockState> ores = props.craterOres.stream()
                .filter(OreDictionary::doesOreNameExist)
                .map(s -> OreDictionary.getOres(s).get(0))
                .map(itemStack -> new BlockMeta(Block.getBlockFromItem(itemStack.getItem()), itemStack.getItemDamage()).getBlockState())
                .collect(Collectors.toList());

        if (rand.nextInt(chancePerChunk) == Math.abs(chunkX) % chancePerChunk || rand.nextInt(chancePerChunk) == Math.abs(chunkZ) % chancePerChunk && shouldCraterSpawn(DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()), world.getBiome(new BlockPos(chunkX * 16, 0, chunkZ * 16)))) {

            //Random coefficients for the sin functions
            int[] sinCoefficients = {rand.nextInt(10) + 1, rand.nextInt(10) + 1, rand.nextInt(10) + 1, rand.nextInt(10) + 1, rand.nextInt(10) + 1};
            //Radius determination, with heavy weight towards smaller craters
            int baseRadius = getBaseRadius(rand.nextInt(500));
            boolean large = baseRadius > 32;
            //Perturbation # calculation
            int numBulges = rand.nextInt(large ? 5 : 4) + 1;

            //Turn the coordinates from chunk stuff into their actual values
            int xCoord = -chunkX + p_180701_4_;
            int zCoord = -chunkZ + p_180701_5_;

            //Set up fluid fill, if needed
            IBlockState fillBlock = Blocks.AIR.getDefaultState();
            int fluidMaxY = 0;
            for (int x = 15; x >= 0; x--) {
                for (int z = 15; z >= 0; z--) {
                    for (int y = 254; y >= 0; y--) {
                        if (chunkPrimerIn.getBlockState(x, y, z).getBlock() instanceof BlockLiquid || chunkPrimerIn.getBlockState(x, y, z).getBlock() instanceof IFluidBlock) {
                            if (y > fluidMaxY) {
                                fillBlock = chunkPrimerIn.getBlockState(x, y, z);
                                fluidMaxY = y;
                            }
                        } else if (chunkPrimerIn.getBlockState(x, y, z).getBlock() != Blocks.AIR)
                            break;
                    }
                }
            }

            //Actually generate the crater
            for (int x = 15; x >= 0; x--) {
                for (int z = 15; z >= 0; z--) {
                    for (int y = 254; y >= 0; y--) {
                        if (y <= fluidMaxY && fillBlock.getBlock() != Blocks.AIR && chunkPrimerIn.getBlockState(x, y, z).getBlock() == Blocks.AIR) {
                            chunkPrimerIn.setBlockState(x, y, z, fillBlock);
                        }
                        if (!isCraterIgnoredBlock(chunkPrimerIn.getBlockState(x, y, z).getBlock())) {
                            //Get us some funky radii up in here
                            int radius = getRadius(baseRadius, (xCoord * 16) + x, (zCoord * 16) + z, numBulges, sinCoefficients);

                            //Standard inverseHalfRadius stuff
                            int distancesSquared = ((xCoord * 16) + x) * ((xCoord * 16) + x) + ((zCoord * 16) + z) * ((zCoord * 16) + z);
                            int blockRadius = (int) Math.sqrt(distancesSquared);
                            int inversePartialSquareRadius = (radius * radius - distancesSquared) / (radius * 2);
                            int inverseRadius = radius - blockRadius;

                            //Places filler blocks to excavate the crater
                            for (int dist = 0; dist < inversePartialSquareRadius; dist++) {
                                if (y - dist > 2) {
                                    chunkPrimerIn.setBlockState(x, y - Math.min(large ? 15 : 11, dist), z, (y - dist <= fluidMaxY) ? fillBlock : Blocks.AIR.getDefaultState());
                                }
                            }

                            //Places blocks to form the ridges
                            double ridgeSize = Math.max(1, (12 * (radius) / 64.0));
                            if (inverseRadius <= radius / 4 && inverseRadius > -2 * radius) {
                                //The graph of this function and the old one can be found here https://www.desmos.com/calculator/x02rgy2wlf
                                for (int dist = -1; dist < 9 * ridgeSize * ((1 - inverseRadius) / (0.8 * radius + (inverseRadius - 1) * (inverseRadius - 1))) - 1.06; dist++) {
                                    //Place the bank thrown up by the impact, and have some of the farthest be dispersed
                                    if (y + dist < 255 && inverseRadius > -0.5 * radius)
                                        chunkPrimerIn.setBlockState(x, y + dist, z, this.getBlockToPlace(world, chunkX, chunkZ, ores));
                                    else if (y + dist < 255 && inverseRadius >= -0.625 * radius)
                                        chunkPrimerIn.setBlockState(x, y + dist, z, this.getBlockToPlace(world, chunkX, chunkZ, ores));
                                    else if (y + dist < 255 && inverseRadius < -0.625 * radius && rand.nextInt(Math.abs(inverseRadius + (int) (radius * 0.625)) + 1) == 0)
                                        chunkPrimerIn.setBlockState(x, y + dist, z, this.getBlockToPlace(world, chunkX, chunkZ, ores));

                                    //Ejecta blocks on top, then ejecta blocks below farther out
                                    if (rand.nextInt(Math.abs(inverseRadius) + 1) == 0 && baseRadius > 40) {
                                        double ejectaRadius = -(1.0 + Math.max((baseRadius - 20) / 20f, 0.5));
                                        if (inverseRadius < -0.375 * radius && inverseRadius >= ejectaRadius * radius)
                                            chunkPrimerIn.setBlockState(x, y + dist + 1, z, this.getBlockToPlace(world, chunkX, chunkZ, ores));
                                        else if (inverseRadius < ejectaRadius * radius)
                                            chunkPrimerIn.setBlockState(x, y + dist + 1 + rand.nextInt(2), z, this.getBlockToPlace(world, chunkX, chunkZ, ores));
                                    }
                                }
                            }

                            //Places blocks to form the surface of the bowl
                            if (inversePartialSquareRadius >= 0 && (y - inversePartialSquareRadius > 0)) {
                                //Two blocks to remove weird stone
                                chunkPrimerIn.setBlockState(x, y - Math.min(large ? 16 : 12, inversePartialSquareRadius), z, this.getBlockToPlace(world, chunkX, chunkZ, ores));
                                chunkPrimerIn.setBlockState(x, y - 1 - Math.min(large ? 16 : 12, inversePartialSquareRadius), z, this.getBlockToPlace(world, chunkX, chunkZ, ores));
                            }

                            break;
                        }
                    }
                }
            }
        }
    }

    //Place some ores but not a lot, if ore list exists
    private IBlockState getBlockToPlace(World world, int chunkX, int chunkZ, List<IBlockState> ores) {
        if (rand.nextInt(24) == 0 && !ores.isEmpty()) {
            return ores.get(rand.nextInt(ores.size()));
        } else {
            return world.getBiome(new BlockPos(chunkX * 16, 0, chunkZ * 16)).topBlock;
        }
    }

    //Check biome frequency
    private boolean shouldCraterSpawn(DimensionProperties properties, Biome biome) {
        if (properties.getCraterBiomeWeights().isEmpty()) return true;
        for (BiomeManager.BiomeEntry biomeEntry : properties.getCraterBiomeWeights()) {
            if (biomeEntry.biome.equals(biome) && biomeEntry.itemWeight > rand.nextInt(99))
                return true;
        }
        return false;
    }

    //Random radius determination
    private int getBaseRadius(int random) {
        int radius = 8;
        if (random < 440)
            radius += rand.nextInt(16);
        else if (random < 485)
            radius += 24 + rand.nextInt(16);
        else if (largeCraters && random < 495)
            radius += 40 + rand.nextInt(16);
        else if (largeCraters && random < 499)
            radius += 56 + rand.nextInt(28);
        return radius;
    }

    //Very fun function for fancy radius
    //Int[] MUST be the same size as max bumps or larger!
    private int getRadius(int base, int x, int z, int bumps, int[] random) {
        //We need to start this out with polar coordinates
        double radians = Math.atan2(x, z);

        //Then we want to add some sin-function bumps to it, as determined by the bumps
        //They increase theta each time because then we can get different-placed perturbations
        //An example graph for this is here: https://www.desmos.com/calculator/5ojoqscuxv
        int extras = 0;
        for (int i = 2; i < Math.min(5, bumps) + 2; i++) {
            extras += random[i - 2] * base * Math.sin(i * radians) * 0.0075;
        }

        return base + extras;
    }
}