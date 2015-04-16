package zmaster587.advancedRocketry.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
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
		else player.addChatMessage(new ChatComponentText(bb.toString()));
		
		return super.onBlockActivated(world, x, y, z, player, a, b, c, d);
	}
}
