package zmaster587.advancedRocketry.util;

import net.minecraft.block.Block;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class MobileAABB extends AxisAlignedBB {


	StorageChunk chunk;
	boolean isRemote;

	public MobileAABB(double x1, double y1,
			double z1, double x2, double y2,
			double z2) {
		super(x1, y1, z1, x2, y2, z2);
	}

	public MobileAABB(AxisAlignedBB aabb) {
		super(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
	}

	public void setStorageChunk(StorageChunk chunk) {
		this.chunk = chunk;
	}
	
	//Good thing mojang's bounding boxes are offset by one depending on if server or client, otherwise i may not need to do this
	public void setRemote(boolean isRemote) {
		this.isRemote = isRemote;
	}

	/*@Override
	public double calculateXOffset(AxisAlignedBB aabbIn, double var2) {
        if (aabbIn.maxY > this.minY && aabbIn.minY < this.maxY)
        {
            if (aabbIn.maxZ > this.minZ && aabbIn.minZ < this.maxZ)
            {
                double d1;

                if (var2 > 0.0D && aabbIn.maxX <= this.minX)
                {

                    d1 = this.minX - aabbIn.maxX;

                    if (d1 < var2)
                    {
                        var2 = d1;
                    }
                }
                else if(var2 > 0.0D && aabbIn.maxX < this.maxX)
                {

                	double maxUp = 256;
                	for(int deltaY = (int) Math.floor(aabbIn.minY - this.minY ); deltaY <= Math.ceil(aabbIn.maxY - this.minY ); deltaY++) {
                    	for(int deltaZ = (int) Math.floor(aabbIn.minZ - this.minZ ); deltaZ < Math.ceil(aabbIn.maxZ - this.minZ ); deltaZ++) {
                    		if(deltaY >= 0 && deltaZ >= 0 && deltaY < chunk.sizeY && deltaZ < chunk.sizeZ) {
                    			double checkVar = chunk.blocks[(int)(aabbIn.maxX - this.minX)][deltaY][deltaZ].getBlockBoundsMinX() + Math.floor(aabbIn.minX - this.minX) + 0.5 + minX - aabbIn.minX;
                    			if(maxUp > checkVar)
                    				maxUp = checkVar;
                    		}
                    	}
                	}

					d1 = maxUp;
					if(d1 < var2)
						var2 = d1;
                }

                if (var2 < 0.0D && aabbIn.minX >= this.maxX)
                {
                    d1 = this.maxX - aabbIn.minX;

                    if (d1 > var2)
                    {
                        var2 = d1;
                    }
                }
                else if(var2 < 0.0D && aabbIn.minX > this.minX)
                {

                	double maxUp = -256;
                	for(int deltaY = (int) Math.floor(aabbIn.minY - this.minY ); deltaY <= Math.ceil(aabbIn.maxY - this.minY ); deltaY++) {
                    	for(int deltaZ = (int) Math.floor(aabbIn.minZ - this.minZ ); deltaZ < Math.ceil(aabbIn.maxZ - this.minZ ); deltaZ++) {
                    		if(deltaY >= 0 && deltaZ >= 0 && deltaY < chunk.sizeX && deltaZ < chunk.sizeZ) {
                    			double checkVar = chunk.blocks[(int)(aabbIn.minX - this.minX)][deltaY][deltaZ].getBlockBoundsMaxX() + Math.floor(aabbIn.minX - this.minX) - 1 + minX - aabbIn.minX;
                    			if(maxUp < checkVar)
                    				maxUp = checkVar;
                    		}
                    	}
                	}

					d1 = maxUp;
					if(d1 > var2)
						var2 = d1;
                }

                return var2;
            }
            else
            {
                return var2;
            }
        }
        else
        {
            return var2;
        }
	}

	@Override
	public double calculateZOffset(AxisAlignedBB aabbIn, double var2) {
        if (aabbIn.maxX > this.minX && aabbIn.minX < this.maxX)
        {
            if (aabbIn.maxY > this.minY && aabbIn.minY < this.maxY)
            {
                double d1;

                if (var2 > 0.0D && aabbIn.maxZ <= this.minZ)
                {

                    d1 = this.minZ - aabbIn.maxZ;

                    if (d1 < var2)
                    {
                        var2 = d1;
                    }
                }
                else if(var2 > 0.0D && aabbIn.maxZ < this.maxZ)
                {

                	double maxUp = 256;
                	for(int deltaX = (int) Math.floor(aabbIn.minX - this.minX ); deltaX <= Math.ceil(aabbIn.maxX - this.minX ); deltaX++) {
                    	for(int deltaY = (int) Math.floor(aabbIn.minY - this.minY ); deltaY < Math.ceil(aabbIn.maxY - this.minY ); deltaY++) {
                    		if(deltaX >= 0 && deltaY >= 0 && deltaX < chunk.sizeX && deltaY < chunk.sizeY) {
                    			double checkVar = chunk.blocks[deltaX][deltaY][(int)(aabbIn.maxZ - this.minZ)].getBlockBoundsMinZ() + Math.floor(aabbIn.minZ - this.minZ) + 1 + minZ - aabbIn.minZ;
                    			if(maxUp > checkVar)
                    				maxUp = checkVar;
                    		}
                    	}
                	}

					d1 = maxUp;
					if(d1 < var2)
						var2 = d1;
                }

                if (var2 < 0.0D && aabbIn.minZ >= this.maxZ)
                {
                    d1 = this.maxZ - aabbIn.minZ;

                    if (d1 > var2)
                    {
                        var2 = d1;
                    }
                }
                else if(var2 < 0.0D && aabbIn.minZ > this.minZ)
                {

                	double maxUp = -256;
                	for(int deltaX = (int) Math.floor(aabbIn.minX - this.minX ); deltaX <= Math.ceil(aabbIn.maxX - this.minX ); deltaX++) {
                    	for(int deltaY = (int) Math.floor(aabbIn.minY - this.minY ); deltaY < Math.ceil(aabbIn.maxY - this.minY ); deltaY++) {
                    		if(deltaX >= 0 && deltaY >= 0 && deltaX < chunk.sizeX && deltaY < chunk.sizeY) {
                    			double checkVar = chunk.blocks[deltaX][deltaY][(int)(aabbIn.minZ - this.minZ)].getBlockBoundsMaxZ() + Math.floor(aabbIn.minZ - this.minZ) - 0.5 + minZ - aabbIn.minZ;
                    			if(maxUp < checkVar)
                    				maxUp = checkVar;
                    		}
                    	}
                	}

					d1 = maxUp;
					if(d1 > var2)
						var2 = d1;
                }

                return var2;
            }
            else
            {
                return var2;
            }
        }
        else
        {
            return var2;
        }
	}*/

	@Override
	public double calculateYOffset(AxisAlignedBB aabbIn, double var2) {
		if (aabbIn.maxX > this.minX && aabbIn.minX < this.maxX)
		{
			if (aabbIn.maxZ > this.minZ && aabbIn.minZ < this.maxZ)
			{
				double d1;

				if (var2 > 0.0D && aabbIn.maxY <= this.minY)
				{

					d1 = this.minY - aabbIn.maxY;

					if (d1 < var2)
					{
						var2 = d1;
					}
				}
				else if(var2 > 0.0D && aabbIn.maxY < this.maxY)
				{

					double maxUp = 256;
					for(int deltaX = (int) Math.floor(aabbIn.minX - this.minX ); deltaX <= Math.ceil(aabbIn.maxX - this.minX ); deltaX++) {
						for(int deltaZ = (int) Math.floor(aabbIn.minZ - this.minZ ); deltaZ < Math.ceil(aabbIn.maxZ - this.minZ ); deltaZ++) {
							if(deltaX >= 0 && deltaZ >= 0 && deltaX < chunk.sizeX && deltaZ < chunk.sizeZ) {

								//Screw 1.10.2
								int offset = isRemote ? 0 : 1;
								AxisAlignedBB aabb = chunk.blocks[deltaX][(int)(aabbIn.maxY - this.minY) - 1][deltaZ].getDefaultState().getCollisionBoundingBox(this.chunk.world, new BlockPos(deltaX,(int)(aabbIn.maxY - this.minY),deltaZ));

								if(aabb != null) {
								double checkVar = aabb.minY + Math.floor(aabbIn.minY - this.minY) + minY - aabbIn.minY- 0.05 + offset;
								if(maxUp > checkVar)
									maxUp = checkVar;
								}
							}
						}
					}

					d1 = maxUp;
					if(d1 < var2)
						var2 = d1;
				}

				if (var2 < 0.0D && aabbIn.minY >= this.maxY)
				{
					d1 = this.maxY - aabbIn.minY;

					if (d1 > var2)
					{
						var2 = d1;
					}
				}
				else if(var2 < 0.0D && aabbIn.minY > this.minY)
				{

					double maxUp = -256;
					for(int deltaX = (int) Math.floor(aabbIn.minX - this.minX ); deltaX <= Math.ceil(aabbIn.maxX - this.minX ); deltaX++) {
						for(int deltaZ = (int) Math.floor(aabbIn.minZ - this.minZ ); deltaZ < Math.ceil(aabbIn.maxZ - this.minZ ); deltaZ++) {
							if(deltaX >= 0 && deltaZ >= 0 && deltaX < chunk.sizeX && deltaZ < chunk.sizeZ && (int)(aabbIn.minY - this.minY) < chunk.sizeY) {
								
								//Screw 1.10.2
								int offset = isRemote ? 0 : 1;
								AxisAlignedBB aabb = chunk.blocks[deltaX][(int)(aabbIn.minY - this.minY - offset)][deltaZ].getDefaultState().getCollisionBoundingBox(this.chunk.world, new BlockPos(deltaX,(int)(aabbIn.minY - this.minY - offset),deltaZ));

								if(aabb != null) {
									float offset2 = isRemote ? 1 : 1;
									double checkVar = aabb.maxY + Math.floor(aabbIn.minY - this.minY) + minY - aabbIn.minY - offset2 - offset;
									if(maxUp < checkVar)
										maxUp = checkVar;
								}
							}
						}
					}

					d1 = maxUp;
					if(d1 > var2)
						var2 = d1;
				}

				return var2;
			}
			else
			{
				return var2;
			}
		}
		else
		{
			return var2;
		}
	}

	@Override
	public boolean intersects(AxisAlignedBB aabb) {

		//if(chunk == null || true)
		//	return super.intersectsWith(aabb);

		boolean collides = false;

		double diffX = 0;//((this.maxX - this.minX) - chunk.sizeX)/2f;
		double diffZ = 0;//((this.maxX - this.minX) - chunk.sizeX)/2f;

		out:
			for(int x = 0; x < chunk.blocks.length; x++) {
				for(int y = 0; y < chunk.blocks[0].length; y++) {
					for(int z = 0; z < chunk.blocks[0][0].length; z++) {
						Block block = chunk.blocks[x][y][z];

						if(!block.getDefaultState().getMaterial().isSolid())
							continue;

						AxisAlignedBB bb = block.getDefaultState().getCollisionBoundingBox(chunk.world, new BlockPos(x,y,z));

						if(bb == null)
							continue;




						bb = bb.offset(this.minX + x + diffX, this.minY + y, this.minZ + z + diffZ);
						if(bb.intersects(aabb)) {
							collides = true;
							break out;
						}
					}
				}
			}

		return collides;
	}

}
