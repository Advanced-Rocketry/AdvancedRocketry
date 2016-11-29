package zmaster587.advancedRocketry.item.tools;

import java.util.List;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.entity.FxSkyLaser;
import zmaster587.advancedRocketry.entity.fx.FxLaser;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.LibVulpes;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

public class ItemBasicLaserGun extends Item {

	int reachDistance = 50;
	private WeakHashMap<EntityLivingBase, BlockPos> posMap;
	ToolMaterial toolMaterial;

	public ItemBasicLaserGun() {
		super();
		toolMaterial = ToolMaterial.DIAMOND;
		setMaxStackSize(1);
		setMaxDamage(0);
		posMap = new WeakHashMap<EntityLivingBase, BlockPos>();
	}

	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state) {
		return 0;
	}

	public boolean canHarvestBlock(IBlockState blockIn)
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
							if (block != Blocks.REDSTONE_ORE && block != Blocks.LIT_REDSTONE_ORE)
							{
								Material material = blockIn.getMaterial();
								return material == Material.ROCK ? true : (material == Material.IRON ? true : material == Material.ANVIL);
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
	public boolean isFull3D() {
		return true;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {

		World world = player.getEntityWorld();

		RayTraceResult rayTrace = rayTraceEntity(world,player);

		if(rayTrace != null) {
			rayTrace.entityHit.attackEntityFrom(DamageSource.generic, 1f);
			if(world.isRemote)
				LibVulpes.proxy.playSound(world, player.getPosition(), AudioRegistry.basicLaser, SoundCategory.PLAYERS, 1, 1f);
			AdvancedRocketry.proxy.spawnLaser(player, rayTrace.hitVec);
			player.resetActiveHand();
			return;
		}

		rayTrace = rayTrace(world, (EntityPlayer) player, false);

		if(rayTrace == null)
			return;

		if(posMap.get(player) != null && !posMap.get(player).equals(rayTrace.getBlockPos())) {
			player.resetActiveHand();
			return;
		}
		else if(posMap.get(player) == null && rayTrace.getBlockPos() != null) {
			posMap.put(player, rayTrace.getBlockPos());
		}

		if(rayTrace.typeOfHit == Type.BLOCK) {
			IBlockState state = world.getBlockState(rayTrace.getBlockPos());

			if(count % 5 == 0 && world.isRemote)
				LibVulpes.proxy.playSound(world, player.getPosition(), AudioRegistry.basicLaser, SoundCategory.PLAYERS, 1, 1f);
			//
			AdvancedRocketry.proxy.spawnLaser(player, rayTrace.hitVec);



			super.onUsingTick(stack, player, count);
		}
	}


	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 16;
	}


	@Override
	protected RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn,
			boolean useLiquids) {
		float f = playerIn.rotationPitch;
		float f1 = playerIn.rotationYaw;
		double d0 = playerIn.posX;
		double d1 = playerIn.posY + (double)playerIn.getEyeHeight();
		double d2 = playerIn.posZ;
		Vec3d vec3d = new Vec3d(d0, d1, d2);
		float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
		float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
		float f4 = -MathHelper.cos(-f * 0.017453292F);
		float f5 = MathHelper.sin(-f * 0.017453292F);
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		double d3 = reachDistance;

		Vec3d vec3d1 = vec3d.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
		return worldIn.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.NONE;
	}



	@Nullable
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving)
	{
		RayTraceResult rayTrace = rayTrace(world, (EntityPlayer) entityLiving, false);

		if(rayTrace != null && rayTrace.typeOfHit == Type.BLOCK) {
			IBlockState state = world.getBlockState(rayTrace.getBlockPos());
			if(state.getBlockHardness(world, rayTrace.getBlockPos()) != -1) {

				//
				if(!world.isRemote) {
					((EntityPlayerMP)entityLiving).interactionManager.tryHarvestBlock(rayTrace.getBlockPos());
					//world.destroyBlock(rayTrace.getBlockPos(), true);
				}

				//state.getPlayerRelativeBlockHardness((EntityPlayer)player, world, rayTrace.getBlockPos());
			}
		}

		posMap.remove(entityLiving);

		return stack;
	}

	public RayTraceResult rayTraceEntity(World world, Entity entity) {

		Vec3d vec3d = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
		Vec3d vec3d1 = entity.getLook(0);
		Vec3d vec3d2 = vec3d.addVector(vec3d1.xCoord * reachDistance, vec3d1.yCoord * reachDistance, vec3d1.zCoord * reachDistance);


		List<Entity> list = world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec3d1.xCoord * reachDistance, vec3d1.yCoord * reachDistance, vec3d1.zCoord * reachDistance).expand(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
				{
			public boolean apply(@Nullable Entity p_apply_1_)
			{
				return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
			}
				}));

		for (int j = 0; j < list.size(); ++j)
		{
			Entity entity1 = (Entity)list.get(j);
			AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expandXyz((double)entity1.getCollisionBorderSize());
			RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);

			if (axisalignedbb.isVecInside(vec3d))
			{
			}
			else if (raytraceresult != null)
			{
				raytraceresult.entityHit = entity1;
				return raytraceresult;
			}
		}

		return null;
	}



	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack,
			World worldIn, EntityPlayer player, EnumHand hand) {

		player.setActiveHand(hand);

		posMap.remove(player);



		//if(true)
		//	return super.onItemRightClick(stack, worldIn, player, hand);
		World world = player.getEntityWorld();

		RayTraceResult rayTrace = rayTraceEntity(world,player);

		if(rayTrace != null) {
			rayTrace.entityHit.attackEntityFrom(DamageSource.generic, .5f);
			if(world.isRemote)
				LibVulpes.proxy.playSound(worldIn, player.getPosition(), AudioRegistry.basicLaser, SoundCategory.PLAYERS, Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.PLAYERS), 1f);

			return new ActionResult(EnumActionResult.PASS, stack);
		}

		rayTrace = rayTrace(world, (EntityPlayer) player, false);

		if(rayTrace != null && rayTrace.typeOfHit == Type.BLOCK) {
			IBlockState state = world.getBlockState(rayTrace.getBlockPos());

			if(world.isRemote)
				LibVulpes.proxy.playSound(worldIn, player.getPosition(), AudioRegistry.basicLaser, SoundCategory.PLAYERS, Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.PLAYERS), 1f);

			return new ActionResult(EnumActionResult.PASS, stack);
		}
		return new ActionResult(EnumActionResult.PASS, stack);
	}
}
