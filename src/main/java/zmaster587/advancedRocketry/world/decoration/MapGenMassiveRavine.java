package zmaster587.advancedRocketry.world.decoration;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class MapGenMassiveRavine extends MapGenRavineExt {

    /**
     * Recursively called by generate()
     */
	@Override
    protected void func_151538_a(World worldIn, int chunkX, int chunkZ, int originalX, int originalZ, Block[] chunkPrimerIn)
    {
        if (this.rand.nextInt(50) == 0)
        {
            double d0 = (double)(chunkX * 16 + this.rand.nextInt(16));
            double d1 = (double)(this.rand.nextInt(this.rand.nextInt(40) + 8) + 20);
            double d2 = (double)(chunkZ * 16 + this.rand.nextInt(16));
            int i = 1;

            for (int j = 0; j < 24; ++j)
            {
                float f = this.rand.nextFloat() * ((float)Math.PI * 2F);
                float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float f2 = (this.rand.nextFloat() * 2.0F + this.rand.nextFloat()) * 2.0F;
                this.func_151540_a(this.rand.nextLong(), originalX, originalZ, chunkPrimerIn, d0, d1, d2, f2, f, f1, 0, 0, 3.0D);
            }
        }
    }
}
