package zmaster587.advancedRocketry.block;

import java.util.Random;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.entity.fx.FxSystemElectricArc;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockElectricMushroom extends BlockMushroom implements IGrowable {

	public BlockElectricMushroom() {
		super();
		setBlockBounds(0f, 0f, 0f, 1f, 1f, 1f);
	}

	public boolean canBlockStay(World p_149718_1_, int p_149718_2_, int p_149718_3_, int p_149718_4_)
	{
		if (p_149718_3_ >= 0 && p_149718_3_ < 256)
		{
			Block block = p_149718_1_.getBlock(p_149718_2_, p_149718_3_ - 1, p_149718_4_);
			return block.canSustainPlant(p_149718_1_, p_149718_2_, p_149718_3_ - 1, p_149718_4_, ForgeDirection.UP, this);
		}
		return false;
	}

	@Override
	public void updateTick(World world, int x, int y,
			int z, Random rand) {
		super.updateTick(world, x, y, z,
				rand);

		if(!world.isRemote && Configuration.electricPlantsSpawnLightning && world.isRaining() && world.getBiomeGenForCoords(x, z) == AdvancedRocketryBiomes.stormLandsBiome) {
			int lightningX = x + rand.nextInt(24) - 12;
			int lightningZ = z + rand.nextInt(24) - 12;
			world.addWeatherEffect(new EntityLightningBolt(world, lightningX, world.getTopSolidOrLiquidBlock(lightningX, lightningZ), lightningZ));
		}
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x,
			int y, int z, int p_149664_5_) {
		super.onBlockDestroyedByPlayer(world, x, y,
				z, p_149664_5_);

		if(world.isRemote) {
			FxSystemElectricArc.spawnArc(world, x + 0.5f, y + 0.5f, z + 0.5f, .3, 7);
			world.playSound(x, y, z, "advancedrocketry:ElectricShockSmall", .7f,  0.975f + world.rand.nextFloat()*0.05f, false);
		}
	}

	@Override
	public void randomDisplayTick(World world, int x,
			int y, int z, Random rand) {
		super.randomDisplayTick(world, x, y, z,
				rand);

		if(world.getTotalWorldTime() % 100 == 0 && world.getBiomeGenForCoords(x, z) == AdvancedRocketryBiomes.stormLandsBiome) {
			FxSystemElectricArc.spawnArc(world, x + 0.5f, y + 0.5f, z + 0.5f, .3, 7);
			world.playSound(x, y, z, "advancedrocketry:ElectricShockSmall", .7f,  0.975f + world.rand.nextFloat()*0.05f, false);
		}
	}
}
