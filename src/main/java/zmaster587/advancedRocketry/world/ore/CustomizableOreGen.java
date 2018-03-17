package zmaster587.advancedRocketry.world.ore;

import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.util.OreGenProperties;

import java.awt.Dimension;
import java.util.Random;

import com.google.common.base.Predicate;

public class CustomizableOreGen implements IWorldGenerator {

	IBlockState oreToGen;
	int numPerChunk, clumpSize, heightLevel, difference;


	public CustomizableOreGen(IBlockState oreToGen, int numPerChunk, int clumpSize, int minHeight, int maxHeight) {
		this.oreToGen = oreToGen;
		this.numPerChunk = numPerChunk;
		this.clumpSize = clumpSize;
		difference = maxHeight - minHeight;
		heightLevel = minHeight;
	}

	public CustomizableOreGen(OreGenProperties.OreEntry genProps ) {
		this.oreToGen = genProps.getBlockState();
		this.clumpSize = genProps.getClumpSize();
		this.heightLevel = genProps.getMinHeight();
		this.difference = genProps.getMaxHeight() - this.heightLevel;
		this.numPerChunk = genProps.getChancePerChunk();
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

		Predicate<IBlockState> predicate = null;
		if(DimensionManager.getInstance().isDimensionCreated(world.provider.getDimension()))
		{
			IBlockState state = ((DimensionProperties)DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension())).getStoneBlock();
			if(state != null)
				predicate = new CustomPredicate(state);
		}
		
		for(int i = 0; i < numPerChunk; i++) {
			int coordX = 16*chunkX + random.nextInt(16);
			int coordY = heightLevel + random.nextInt(difference);
			int coordZ = 16*chunkZ + random.nextInt(16);

			if(predicate != null)
				new WorldGenMinable(oreToGen, clumpSize, predicate).generate(world, random, new BlockPos(coordX, coordY, coordZ));
			else
				new WorldGenMinable(oreToGen, clumpSize).generate(world, random, new BlockPos(coordX, coordY, coordZ));
		}

	}

	static class CustomPredicate implements Predicate<IBlockState>
	{
		IBlockState state;
		public CustomPredicate(IBlockState state)
		{
			this.state = state;
		}



		public boolean apply(IBlockState p_apply_1_)
		{
			if (p_apply_1_ != null) {
				if(p_apply_1_.getBlock() == Blocks.STONE)
				{
					BlockStone.EnumType blockstone$enumtype = (BlockStone.EnumType)p_apply_1_.getValue(BlockStone.VARIANT);
					return blockstone$enumtype.isNatural();
				} 
				else if(p_apply_1_.getBlock() == state.getBlock())
				{
					return true;
				}
			}
			
			return false;
		}
	}
}
