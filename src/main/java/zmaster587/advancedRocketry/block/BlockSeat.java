package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.entity.EntityDummy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.List;

public class BlockSeat extends Block {

	private static VoxelShape bb = VoxelShapes.create(0, 0, 0, 1, .125, 1);
	
	public BlockSeat(Properties mat) {
		super(mat);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
			ISelectionContext context) {
		return VoxelShapes.empty();
	}
	
	//If the block is destroyed remove any mounting associated with it
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		// TODO Auto-generated method stub
		super.onReplaced(state, world, pos, newState, isMoving);
		
		List<EntityDummy> list = world.getEntitiesWithinAABB(EntityDummy.class, new AxisAlignedBB(pos, pos.add(1,1,1)));

		//We only expect one but just be sure
		for(EntityDummy e : list) {
			if(e instanceof EntityDummy) {
				// kill
				e.remove();
			}
		}
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return bb;
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if(!world.isRemote) {
			List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(pos, pos.add(1,1,1)));

			//Try to mount player to dummy entity in the block
			for(Entity e : list) {
				if(e instanceof EntityDummy) {
					if(!e.getPassengers().isEmpty()) {
						return ActionResultType.SUCCESS;
					}
					else {
						//Ensure that the entity is in the correct position
						e.setPosition(pos.getX() + 0.5f, pos.getY() + 0.2f, pos.getZ() + 0.5f);
						player.startRiding(e);
						return ActionResultType.SUCCESS;
					}
				}
			}
			EntityDummy entity = new EntityDummy(world, pos.getX() + 0.5f, pos.getY() + 0.2f, pos.getZ() + 0.5f);
			world.addEntity(entity);
			player.startRiding(entity);
		}

		return ActionResultType.SUCCESS;
	}
}
