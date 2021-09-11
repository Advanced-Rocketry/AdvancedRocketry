package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.entity.EntityDummy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.List;

public class BlockSeat extends Block {

	private static final AxisAlignedBB bb = new AxisAlignedBB(0, 0, 0, 1, .125, 1);
	
	public BlockSeat(Material mat) {
		super(mat);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@Nonnull
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
        return NULL_AABB;
    }
	
	@Override
	@ParametersAreNullableByDefault
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world,
			BlockPos pos, EnumFacing side) {
		return side == EnumFacing.DOWN;
	}
	
	//If the block is destroyed remove any mounting associated with it
	@Override
	public void onBlockDestroyedByExplosion(World world, BlockPos pos,
			Explosion explosionIn) {
		super.onBlockDestroyedByExplosion(world, pos, explosionIn);
		
		List<EntityDummy> list = world.getEntitiesWithinAABB(EntityDummy.class, new AxisAlignedBB(pos, pos.add(1,1,1)));

		//We only expect one but just be sure
		for(EntityDummy entityDummy : list) {
			if(entityDummy != null) {
				entityDummy.setDead();
			}
		}
	}
	
	@Override
	@Nonnull
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source,
			BlockPos pos) {
		return bb;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos,
			IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY,
			float hitZ) {
		
		if(!world.isRemote) {
			List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(pos, pos.add(1,1,1)));

			//Try to mount player to dummy entity in the block
			for(Entity e : list) {
				if(e instanceof EntityDummy) {
					if(!e.getPassengers().isEmpty()) {
						return true;
					}
					else {
						//Ensure that the entity is in the correct position
						e.setPosition(pos.getX() + 0.5f, pos.getY() + 0.2f, pos.getZ() + 0.5f);
						player.startRiding(e);
						return true;
					}
				}
			}
			EntityDummy entity = new EntityDummy(world, pos.getX() + 0.5f, pos.getY() + 0.2f, pos.getZ() + 0.5f);
			world.spawnEntity(entity);
			player.startRiding(entity);
		}

		return true;
	}
}
