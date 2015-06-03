package zmaster587.advancedRocketry.block;

import net.minecraft.block.BlockOre;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockMultimine extends BlockOre {
	public BlockMultimine() {
	}
	
	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x,
			int y, int z, boolean willHarvest) {
		
		dropBlockAsItem(world, x, y, z, getDamageValue(world, x, y, z), 0);
		
		world.setBlock(x, y, z, this, getDamageValue(world, x, y, z),3);
		
		return false;
		//return super.removedByPlayer(world, player, x, y, z, willHarvest);
	}
	
}
