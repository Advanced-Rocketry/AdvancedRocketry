package zmaster587.advancedRocketry.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import zmaster587.advancedRocketry.network.PacketBiomeIDChange;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.util.HashedBlockPosition;

public class BiomeHandler {


    public static void changeBiome(World world, Biome biomeId, BlockPos pos) {
        Chunk chunk = world.getChunkFromBlockCoords(pos);

        Biome biome = world.getBiome(pos);

        if (biome == biomeId)
            return;

        if (biome.topBlock != biomeId.topBlock) {
            BlockPos yy = world.getHeight(pos);

            while (!world.getBlockState(yy.down()).isOpaqueCube() && yy.getY() > 0)
                yy = yy.down();

            if (world.getBlockState(yy.down()) == biome.topBlock)
                world.setBlockState(yy.down(), biomeId.topBlock);
        }

        byte[] biomeArr = chunk.getBiomeArray();
        try {
            biomeArr[(pos.getX() & 15) + (pos.getZ() & 15) * 16] = (byte) Biome.getIdForBiome(biomeId);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        PacketHandler.sendToNearby(new PacketBiomeIDChange(chunk, world, new HashedBlockPosition(pos)), world.provider.getDimension(), pos, 256);
    }
}
