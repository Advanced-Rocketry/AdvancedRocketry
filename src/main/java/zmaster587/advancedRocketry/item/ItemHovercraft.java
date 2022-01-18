package zmaster587.advancedRocketry.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.entity.EntityHoverCraft;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class ItemHovercraft extends Item {

	public ItemHovercraft(Properties props) {
		super(props);
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	 */
	@Nonnull
	@ParametersAreNonnullByDefault
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		float f = 1.0F;
		float f1 = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch);
		float f2 = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw);
		double d0 = playerIn.prevPosX + (playerIn.getPosX() - playerIn.prevPosX);
		double d1 = playerIn.prevPosY + (playerIn.getPosY() - playerIn.prevPosY) + (double)playerIn.getEyeHeight();
		double d2 = playerIn.prevPosZ + (playerIn.getPosZ() - playerIn.prevPosZ);
		Vector3d vec3d = new Vector3d(d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = 5.0D;
		Vector3d vec3d1 = vec3d.add((double)f7 * 5.0D, (double)f6 * 5.0D, (double)f8 * 5.0D);
		BlockRayTraceResult raytraceresult = worldIn.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, BlockMode.COLLIDER, FluidMode.ANY, null));

		if (raytraceresult == null) {
			return new ActionResult<>(ActionResultType.PASS, itemstack);
		} else {
			Vector3d vec3d2 = playerIn.getLook(1.0F);
			boolean flag = false;
			List<Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity(playerIn, playerIn.getBoundingBox().expand(vec3d2.x * 5.0D, vec3d2.y * 5.0D, vec3d2.z * 5.0D).grow(1.0D));

			for (Entity entity : list) {
				if (entity.canBeCollidedWith()) {
					AxisAlignedBB axisalignedbb = entity.getBoundingBox().grow(entity.getCollisionBorderSize());

					if (axisalignedbb.contains(vec3d)) {
						flag = true;
					}
				}
			}

			if (flag) {
				return new ActionResult<>(ActionResultType.PASS, itemstack);
			} else if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
				return new ActionResult<>(ActionResultType.PASS, itemstack);
			} else {
				Block block = worldIn.getBlockState(raytraceresult.getPos()).getBlock();
				boolean flag1 = block == Blocks.WATER;
				EntityHoverCraft entityboat = new EntityHoverCraft(worldIn, raytraceresult.getHitVec().x, flag1 ? raytraceresult.getHitVec().y - 0.12D : raytraceresult.getHitVec().y, raytraceresult.getHitVec().z);
				entityboat.rotationYaw = playerIn.rotationYaw;

				if (!worldIn.isRemote) {
					worldIn.addEntity(entityboat);
				}

				if (!playerIn.abilities.isCreativeMode) {
					itemstack.shrink(1);
				}
				return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
			}
		}
	}

	@Override
	@ParametersAreNonnullByDefault
	public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new TranslationTextComponent("item.hovercraft.tooltip"));
	}
}
