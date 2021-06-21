package zmaster587.advancedRocketry.item.tools;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.ToolItem;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.LibVulpes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.WeakHashMap;

public class ItemBasicLaserGun extends Item {

<<<<<<< HEAD
	int reachDistance = 50;
	private WeakHashMap<LivingEntity, BlockPos> posMap;
	ItemTier toolMaterial;

	public ItemBasicLaserGun( Properties props ) {
		super(props);
		toolMaterial = ItemTier.DIAMOND;
		posMap = new WeakHashMap<LivingEntity, BlockPos>();
=======
	private int reachDistance = 50;
	private WeakHashMap<EntityLivingBase, BlockPos> posMap;
	private ToolMaterial toolMaterial;

	public ItemBasicLaserGun() {
		super();
		toolMaterial = ToolMaterial.DIAMOND;
		setMaxStackSize(1);
		setMaxDamage(0);
		posMap = new WeakHashMap<>();
	}

	@Override
	public float getStrVsBlock(@Nonnull ItemStack stack, IBlockState state) {
		return 0;
>>>>>>> origin/feature/nuclearthermalrockets
	}
	
	public boolean canHarvestBlock(BlockState blockIn)
	{
		Block block = blockIn.getBlock();

		if (block == Blocks.OBSIDIAN)
		{
			return this.toolMaterial.getHarvestLevel() == 3;
		}
		else if (block != Blocks.DIAMOND_BLOCK && block != Blocks.DIAMOND_ORE)
		{
			if (block != Blocks.EMERALD_ORE && block != Blocks.EMERALD_BLOCK)
			{
				if (block != Blocks.GOLD_BLOCK && block != Blocks.GOLD_ORE)
				{
					if (block != Blocks.IRON_BLOCK && block != Blocks.IRON_ORE)
					{
						if (block != Blocks.LAPIS_BLOCK && block != Blocks.LAPIS_ORE)
						{
							if (block != Blocks.REDSTONE_ORE && block != Blocks.REDSTONE_ORE)
							{
								Material material = blockIn.getMaterial();
								return material == Material.ROCK || material == Material.IRON || material == Material.ANVIL;
							}
							else
							{
								return this.toolMaterial.getHarvestLevel() >= 2;
							}
						}
						else
						{
							return this.toolMaterial.getHarvestLevel() >= 1;
						}
					}
					else
					{
						return this.toolMaterial.getHarvestLevel() >= 1;
					}
				}
				else
				{
					return this.toolMaterial.getHarvestLevel() >= 2;
				}
			}
			else
			{
				return this.toolMaterial.getHarvestLevel() >= 2;
			}
		}
		else
		{
			return this.toolMaterial.getHarvestLevel() >= 2;
		}
	}


	@Override
<<<<<<< HEAD
	public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
=======
	public boolean isFull3D() {
		return true;
	}

	@Override
	public void onUsingTick(@Nonnull ItemStack stack, EntityLivingBase player, int count) {
>>>>>>> origin/feature/nuclearthermalrockets

		World world = player.getEntityWorld();

		RayTraceResult rayTrace = rayTraceEntity(world,player);

		if(rayTrace != null && rayTrace.hitInfo instanceof Entity) {
			((Entity)rayTrace.hitInfo).attackEntityFrom(DamageSource.GENERIC, 1f);
			if(world.isRemote)
				LibVulpes.proxy.playSound(world, new BlockPos(player.getPositionVec()), AudioRegistry.basicLaser, SoundCategory.PLAYERS, 1, 1f);
			AdvancedRocketry.proxy.spawnLaser(player, rayTrace.getHitVec());
			player.resetActiveHand();
			return;
		}

		rayTrace = rayTrace(world, (PlayerEntity) player, false);

		if(rayTrace == null)
			return;

		if(posMap.get(player) != null && !posMap.get(player).equals(new BlockPos(rayTrace.getHitVec()))) {
			player.resetActiveHand();
			return;
		}
<<<<<<< HEAD
		else if(posMap.get(player) == null && new BlockPos(rayTrace.getHitVec()) != null) {
			posMap.put(player, new BlockPos(rayTrace.getHitVec()));
=======
		else if(posMap.get(player) == null) {
			posMap.put(player, rayTrace.getBlockPos());
>>>>>>> origin/feature/nuclearthermalrockets
		}

		if(rayTrace.getType() == Type.BLOCK) {
			BlockState state = world.getBlockState(new BlockPos(rayTrace.getHitVec()));

			if(count % 5 == 0 && world.isRemote)
				LibVulpes.proxy.playSound(world, new BlockPos(player.getPositionVec()), AudioRegistry.basicLaser, SoundCategory.PLAYERS, 1, 1f);
			//
			AdvancedRocketry.proxy.spawnLaser(player, rayTrace.getHitVec());



			super.onUsingTick(stack, player, count);
		}
	}


<<<<<<< HEAD
	
	protected RayTraceResult rayTrace(World worldIn, PlayerEntity playerIn,
=======
	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getMaxItemUseDuration(@Nonnull ItemStack stack)
	{
		return 16;
	}


	@Override
	protected RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn,
>>>>>>> origin/feature/nuclearthermalrockets
			boolean useLiquids) {
		float f = playerIn.rotationPitch;
		float f1 = playerIn.rotationYaw;
		double d0 = playerIn.getPosX();
		double d1 = playerIn.getPosY() + (double)playerIn.getEyeHeight();
		double d2 = playerIn.getPosZ();
		Vector3d vec3d = new Vector3d(d0, d1, d2);
		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
		float f4 = -MathHelper.cos(-f * 0.017453292F);
		float f5 = MathHelper.sin(-f * 0.017453292F);
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d3 = reachDistance;

<<<<<<< HEAD
		Vector3d vec3d1 = vec3d.add((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
		
		return worldIn.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, BlockMode.COLLIDER, FluidMode.NONE, null));
=======
		Vec3d vec3d1 = vec3d.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
		return worldIn.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	@Override
	@Nonnull
	public EnumAction getItemUseAction(@Nonnull ItemStack stack)
	{
		return EnumAction.NONE;
>>>>>>> origin/feature/nuclearthermalrockets
	}

	@Override
<<<<<<< HEAD
	public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entityLiving)
=======
	@Nonnull
	public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World world, EntityLivingBase entityLiving)
>>>>>>> origin/feature/nuclearthermalrockets
	{
		RayTraceResult rayTrace = rayTrace(world, (PlayerEntity) entityLiving, false);

		if(rayTrace != null && rayTrace.getType() == Type.BLOCK) {
			BlockState state = world.getBlockState(new BlockPos(rayTrace.getHitVec()));
			if(state.getBlockHardness(world, new BlockPos(rayTrace.getHitVec())) != -1) {

				//
				if(!world.isRemote) {
					((ServerPlayerEntity)entityLiving).interactionManager.tryHarvestBlock(new BlockPos(rayTrace.getHitVec()));
					//world.destroyBlock(rayTrace.getBlockPos(), true);
				}

				//state.getPlayerRelativeBlockHardness((PlayerEntity)player, world, rayTrace.getBlockPos());
			}
		}

		posMap.remove(entityLiving);

		return stack;
	}

	public RayTraceResult rayTraceEntity(World world, Entity entity) {

		Vector3d vec3d = new Vector3d(entity.getPosX(), entity.getPosY() + entity.getEyeHeight(), entity.getPosZ());
		Vector3d vec3d1 = entity.getLook(0);
		Vector3d vec3d2 = vec3d.add(vec3d1.x * reachDistance, vec3d1.y * reachDistance, vec3d1.z * reachDistance);


		List<Entity> list = world.getEntitiesInAABBexcluding(entity, entity.getBoundingBox().grow(vec3d1.x * reachDistance, vec3d1.y * reachDistance, vec3d1.z * reachDistance).expand(1.0D, 1.0D, 1.0D), new Predicate<Entity>()				{
			public boolean apply(@Nullable Entity p_apply_1_)
			{
				return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
			}
				});

<<<<<<< HEAD
		for (int j = 0; j < list.size(); ++j)
		{
			Entity entity1 = (Entity)list.get(j);
			AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double)entity1.getCollisionBorderSize());
			boolean raytraceresult = axisalignedbb.intersects(vec3d, vec3d2);

			if (axisalignedbb.contains(vec3d))
			{
			}
			else if (raytraceresult)
			{
				return new EntityRayTraceResult(entity1);
=======
		for (Entity value : list) {
			AxisAlignedBB axisalignedbb = value.getEntityBoundingBox().grow(value.getCollisionBorderSize());
			RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);

			if (!axisalignedbb.contains(vec3d) && raytraceresult != null) {
				raytraceresult.entityHit = value;
				return raytraceresult;
>>>>>>> origin/feature/nuclearthermalrockets
			}
		}

		return null;
	}



	@Override
<<<<<<< HEAD
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand hand) {
=======
	@ParametersAreNonnullByDefault
	@Nonnull
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
>>>>>>> origin/feature/nuclearthermalrockets

		player.setActiveHand(hand);

		posMap.remove(player);
		ItemStack stack = player.getHeldItem(hand);



		//if(true)
		//	return super.onItemRightClick(stack, worldIn, player, hand);
		World world = player.getEntityWorld();

		RayTraceResult rayTrace = rayTraceEntity(world,player);

		if(rayTrace != null) {
			((EntityRayTraceResult)rayTrace).getEntity().attackEntityFrom(DamageSource.GENERIC, .5f);
			if(world.isRemote)
				LibVulpes.proxy.playSound(worldIn, new BlockPos(player.getPositionVec()), AudioRegistry.basicLaser, SoundCategory.PLAYERS, Minecraft.getInstance().gameSettings.getSoundLevel(SoundCategory.PLAYERS), 1f);

<<<<<<< HEAD
			return new ActionResult(ActionResultType.PASS, stack);
		}

		rayTrace = rayTrace(world, (PlayerEntity) player, false);
=======
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}

		rayTrace = rayTrace(world, player, false);
>>>>>>> origin/feature/nuclearthermalrockets

		if(rayTrace != null && rayTrace.getType() == Type.BLOCK) {
			BlockState state = world.getBlockState(((BlockRayTraceResult)rayTrace).getPos());

			if(world.isRemote)
				LibVulpes.proxy.playSound(worldIn, new BlockPos(player.getPositionVec()), AudioRegistry.basicLaser, SoundCategory.PLAYERS, Minecraft.getInstance().gameSettings.getSoundLevel(SoundCategory.PLAYERS), 1f);

<<<<<<< HEAD
			return new ActionResult(ActionResultType.PASS, stack);
		}
		return new ActionResult(ActionResultType.PASS, stack);
=======
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}
		return new ActionResult<>(EnumActionResult.PASS, stack);
>>>>>>> origin/feature/nuclearthermalrockets
	}
}
