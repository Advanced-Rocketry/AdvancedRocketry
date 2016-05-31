package zmaster587.advancedRocketry.block;

import java.util.ArrayList;
import java.util.Random;

import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import net.minecraft.block.BlockTorch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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
	public boolean onBlockActivated(World world, int x,
			int y, int z, EntityPlayer player,
			int p_149727_6_, float p_149727_7_, float p_149727_8_,
			float p_149727_9_) {
		
		if(player.getCurrentEquippedItem() != null && AtmosphereHandler.getOxygenHandler(world.provider.dimensionId).getAtmosphereType(x, y, z).allowsCombustion() && (player.getCurrentEquippedItem().getItem() == Item.getItemFromBlock(Blocks.torch) || 
				player.getCurrentEquippedItem().getItem() == Items.flint_and_steel || 
				player.getCurrentEquippedItem().getItem() == Items.fire_charge)) {
			
			world.setBlock(x, y, z, Blocks.torch, world.getBlockMetadata(x, y, z), 3);
			
			return true;
		}
			
		return super.onBlockActivated(world, x, y,
				z, player, p_149727_6_, p_149727_7_, p_149727_8_,
				p_149727_9_);
	}
	
	@Override
	public void randomDisplayTick(World p_149734_1_, int p_149734_2_,
			int p_149734_3_, int p_149734_4_, Random p_149734_5_) {
	}
	
}
