package zmaster587.advancedRocketry.block;

import java.util.Random;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.entity.fx.FxSystemElectricArc;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.IGrowable;
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
	public void randomDisplayTick(World world, int x,
			int y, int z, Random rand) {
		super.randomDisplayTick(world, x, y, z,
				rand);

		if(world.getTotalWorldTime() % 100 == 0) {
			FxSystemElectricArc.spawnArc(world, x + 0.5f, y + 0.5f, z + 0.5f, .3, 7);
			world.playSound(x, y, z, "advancedrocketry:ElectricShockSmall", .7f,  0.975f + world.rand.nextFloat()*0.05f, false);
		}
	}
}
