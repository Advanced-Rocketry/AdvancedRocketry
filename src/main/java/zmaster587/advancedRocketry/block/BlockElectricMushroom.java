package zmaster587.advancedRocketry.block;

import java.util.Random;

import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.entity.fx.FxSystemElectricArc;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockElectricMushroom extends BlockMushroom implements IGrowable {

	public BlockElectricMushroom() {
		super();
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		state.getBlock().canSustainPlant(state, worldIn, pos, EnumFacing.UP, this);
		if (pos.getY() >= 0 && pos.getY() < 256)
		{
			IBlockState iblockstate = worldIn.getBlockState(pos.down());
			return iblockstate.getBlock().canSustainPlant(iblockstate, worldIn, pos.down(), EnumFacing.UP, this);
		}
		return false;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state,
			Random rand) {
		if(!world.isRemote && Configuration.electricPlantsSpawnLightning && world.isRaining() && world.getBiomeGenForCoords(pos) == AdvancedRocketryBiomes.stormLandsBiome) {
			int lightningX = pos.getX() + rand.nextInt(24) - 12;
			int lightningZ = pos.getZ() + rand.nextInt(24) - 12;
			BlockPos lightning = new BlockPos(lightningX, 0, lightningZ );
			lightning = world.getTopSolidOrLiquidBlock(lightning);
			
			world.addWeatherEffect(new EntityLightningBolt(world, lightning.getX(), lightning.getY(), lightning.getZ(), true));
		}
	}
	
	@Override
	public void onBlockDestroyedByPlayer(World world, BlockPos pos,
			IBlockState state) {
		super.onBlockDestroyedByPlayer(world, pos, state);
		
		if(world.isRemote) {
			FxSystemElectricArc.spawnArc(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, .3, 7);
			
			world.playSound(pos.getX(), pos.getY(), pos.getZ(), new SoundEvent( new ResourceLocation("advancedrocketry:ElectricShockSmall")), SoundCategory.BLOCKS, .7f,  0.975f + world.rand.nextFloat()*0.05f, false);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World world,
			BlockPos pos, Random rand) {
		
		super.randomDisplayTick(stateIn, world, pos, rand);
		if(world.getTotalWorldTime() % 100 == 0 && world.getBiomeGenForCoords(pos) == AdvancedRocketryBiomes.stormLandsBiome) {
			FxSystemElectricArc.spawnArc(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, .3, 7);
			world.playSound(Minecraft.getMinecraft().thePlayer, pos, new SoundEvent( new ResourceLocation("advancedrocketry:ElectricShockSmall")), SoundCategory.BLOCKS, .7f,  0.975f + world.rand.nextFloat()*0.05f);
		}
	}
}
