package zmaster587.advancedRocketry.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.advancedRocketry.util.StorageChunk;
import zmaster587.libVulpes.block.RotatableBlock;

public class BlockrocketBuilder extends RotatableBlock {

	public BlockrocketBuilder(Material par2Material) {
		super(par2Material);
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileRocketBuilder();
	}
	
	//TODO: open gui giving rocket statistics
	@Override
	public boolean onBlockActivated(World world, int x,
			int y, int z, EntityPlayer player,
			int a, float b, float c,
			float d) {
		AxisAlignedBB thing;
		
		TileRocketBuilder rocketBuilder = (TileRocketBuilder)world.getTileEntity(x, y, z);
		AxisAlignedBB bb = rocketBuilder.getRocketBounds(world, x, y, z);
		
		if(bb == null)
			player.addChatMessage(new ChatComponentText("Null"));
		else {
			boolean whole = true;
			
			boundLoop:
			for(int xx = (int)bb.minX; xx <= (int)bb.maxX; xx++) {
				for(int zz = (int)bb.minZ; zz <= (int)bb.maxZ && whole; zz++) {
					if(world.getBlock(xx, (int)bb.minY-1, zz) != AdvancedRocketry.launchpad) {
						whole = false;
						break boundLoop;
					}
				}
			}
			
			if(whole) {
				player.addChatMessage(new ChatComponentText(bb.toString() + "  Moving..."));
				StorageChunk chunk = StorageChunk.copyWorldBB(world, bb);
				
				chunk.pasteInWorld(world, x, y + 20, z);
			}
			else
				player.addChatMessage(new ChatComponentText("Structure incomplete"));
		}
		
		return super.onBlockActivated(world, x, y, z, player, a, b, c, d);
	}
}
