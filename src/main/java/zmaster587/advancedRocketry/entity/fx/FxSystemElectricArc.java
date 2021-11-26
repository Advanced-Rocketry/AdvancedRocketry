package zmaster587.advancedRocketry.entity.fx;

import net.minecraft.world.World;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryParticleTypes;
import zmaster587.libVulpes.util.Vector3F;

public class FxSystemElectricArc {
	public static void spawnArc(World world, double x, double y, double z, double sizeMult, int numrecursion) {

		//left child = 2*current index
		//right child = 2*current index + 1
		// parent = index/2
		makeNextPosition(world, new Vector3F<>(x, y, z), sizeMult, numrecursion);
	}

	private static void makeNextPosition(World world, Vector3F<Double> parent, double scale, int numrecursion) {
		double radius = scale*2;
		double angle = world.getRandom().nextDouble()*Math.PI*2;
		
		double xOffset = radius* Math.cos(angle);
		double zOffset = radius* Math.sin(angle);
		
		Vector3F<Double> blockPosL = new Vector3F<>(parent.x - xOffset, parent.y + scale * 4, parent.z + zOffset);
		Vector3F<Double> blockPosR = new Vector3F<>(parent.x + xOffset, parent.y + scale * 4, parent.z - zOffset);
		

		int numParticles = (int)(scale*1000/numrecursion);
		for(int i = 0; i < numParticles; i++) {
			double distance = i/(double)numParticles;
			double offset = scale*(0.1*Math.sin(numrecursion*1.5f*i/(Math.PI*8f)) + 0.1*Math.sin(numrecursion*0.5*i/(Math.PI*8f)));
			
			AdvancedRocketry.proxy.spawnParticle(AdvancedRocketryParticleTypes.fxElectricArc, world,
					parent.x + distance*(blockPosL.x - parent.x +  offset),
					parent.y + distance*(blockPosL.y - parent.y + offset), 
					parent.z + distance*(blockPosL.z - parent.z), scale/4f, 0, 0);
			
			AdvancedRocketry.proxy.spawnParticle(AdvancedRocketryParticleTypes.fxElectricArc, world,
					parent.x + distance*(blockPosR.x - parent.x +  offset),
					parent.y + distance*(blockPosR.y - parent.y - offset), 
					parent.z + distance*(blockPosR.z - parent.z), scale/4f, 0, 0);
		}
		
		numrecursion--;

		if(numrecursion > 0) {
			if(world.getRandom().nextInt(4) < 2)
			makeNextPosition(world, blockPosL, scale/2.0, numrecursion);
			if(world.getRandom().nextInt(4) < 2)
			makeNextPosition(world, blockPosR, scale/2.0, numrecursion);
		}


	}
}
