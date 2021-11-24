package zmaster587.advancedRocketry.block.plant;

import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.block.MushroomBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.entity.fx.FxSystemElectricArc;
import zmaster587.advancedRocketry.util.AudioRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class BlockElectricMushroom extends MushroomBlock implements IGrowable {

	public BlockElectricMushroom(Properties properties) {
		super(properties);
	}

	@Override
	@ParametersAreNonnullByDefault
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		if(!world.isRemote && ARConfiguration.getCurrentConfig().electricPlantsSpawnLightning.get() && world.isRaining() && world.getBiome(pos) == AdvancedRocketryBiomes.stormLandsBiome) {
			int lightningX = pos.getX() + rand.nextInt(24) - 12;
			int lightningZ = pos.getZ() + rand.nextInt(24) - 12;
			BlockPos lightning = new BlockPos(lightningX, 0, lightningZ );
			lightning = world.getHeight(Type.WORLD_SURFACE, lightning);


			LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(world);
			lightningboltentity.moveForced(lightning.getX(), lightning.getY(), lightning.getZ());
			lightningboltentity.setEffectOnly(true);
			world.addEntity(lightningboltentity);
		}
	}

	@Override
	@ParametersAreNonnullByDefault
	public void onPlayerDestroy(IWorld world, BlockPos pos, BlockState state) {
		super.onPlayerDestroy(world, pos, state);

		if(world.isRemote()) {
			FxSystemElectricArc.spawnArc((ClientWorld)world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, .1, 7);
			world.playSound(null, pos, new SoundEvent( new ResourceLocation("advancedrocketry:electricshocksmall")), SoundCategory.BLOCKS, .7f,  0.975f + world.getRandom().nextFloat()*0.05f);
		}
	}
	
	@Override
	@ParametersAreNonnullByDefault
	@OnlyIn(value=Dist.CLIENT)
	public void animateTick(BlockState stateIn, World world, BlockPos pos, Random rand) {
		super.animateTick(stateIn, world, pos, rand);
		if(world.getGameTime() % 100 == 0 && world.getBiome(pos) == AdvancedRocketryBiomes.stormLandsBiome) {
			FxSystemElectricArc.spawnArc(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, .1, 7);
			world.playSound(Minecraft.getInstance().player, pos, AudioRegistry.electricShockSmall, SoundCategory.BLOCKS, .7f,  0.975f + world.rand.nextFloat()*0.05f);
		}
	}
	
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		Material material = worldIn.getBlockState(pos.down()).getMaterial();
		return material == Material.CLAY || material == Material.CORAL || material == Material.WOOD || material == Material.SPONGE || material == Material.SAND || material == Material.ROCK || material == Material.NETHER_WOOD;
	}
}
