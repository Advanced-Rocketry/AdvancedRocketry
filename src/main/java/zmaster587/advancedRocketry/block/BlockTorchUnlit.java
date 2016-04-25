package zmaster587.advancedRocketry.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockTorch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockTorchUnlit extends BlockTorch {

	public BlockTorchUnlit() {
		this.setTickRandomly(true);
		this.setCreativeTab(null);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world,
			int x, int y, int z, EntityPlayer player) {
		return new ItemStack(Blocks.torch);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z,
			int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		int count = quantityDropped(metadata, fortune, world.rand);
		for(int i = 0; i < count; i++)
		{
			ret.add(new ItemStack(Blocks.torch, 1, damageDropped(metadata)));
		}
		return ret;
	}

	@Override
	public void randomDisplayTick(World p_149734_1_, int p_149734_2_,
			int p_149734_3_, int p_149734_4_, Random p_149734_5_) {
	}
	
}
