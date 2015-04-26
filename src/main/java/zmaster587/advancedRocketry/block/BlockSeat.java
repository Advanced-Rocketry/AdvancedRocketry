package zmaster587.advancedRocketry.block;

import java.util.List;

import zmaster587.advancedRocketry.entity.EntityDummy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockSeat extends Block {

	public BlockSeat(Material mat) {
		super(mat);
		this.maxY = 0.2f;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	
	//If the block is destroyed remove any mounting associated with it
	@Override
	public void onBlockPreDestroy(World world, int x,
			int y, int z, int meta) {
		super.onBlockPreDestroy(world, x, y, z,	meta);
		
		List<Entity> list = world.getEntitiesWithinAABB(EntityDummy.class,AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1));

		//We only expect one but just be sure
		for(Entity e : list) {
			if(e instanceof EntityDummy) {
				e.setDead();
			}
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, int x,	int y, int z, EntityPlayer player, int a, float b, float c, float d) {
		if(!world.isRemote) {
			List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1));

			//Try to mount player to dummy entity in the block
			for(Entity e : list) {
				if(e instanceof EntityDummy) {
					if(e.riddenByEntity != null) {
						if(e.riddenByEntity == player) {
							player.mountEntity(null);
							e.setDead();
						}
						return false;
					}
					else {
						//Ensure that the entity is in the correct position
						e.setPosition(x + 0.5d,y + 0.2d, z + 0.5d);
						player.mountEntity(e);
						return false;
					}
				}
			}
			EntityDummy entity = new EntityDummy(world, x + 0.5d,y + 0.2d, z + 0.5d);
			world.spawnEntityInWorld(entity);
			player.mountEntity(entity);
		}

		return false;

	}
}
