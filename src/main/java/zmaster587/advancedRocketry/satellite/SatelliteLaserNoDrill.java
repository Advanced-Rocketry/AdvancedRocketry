package zmaster587.advancedRocketry.satellite;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.util.ZUtils;

public class SatelliteLaserNoDrill extends SatelliteBase {
	protected boolean  jammed;
	protected IInventory boundChest;
	World world;
	private static List<BlockMeta> ores;
	Random random;

	public SatelliteLaserNoDrill(IInventory boundChest) {
		this.boundChest = boundChest;
		random = new Random(System.currentTimeMillis());

		if(ores == null) {
			ores = new LinkedList<BlockMeta>();
			for(int i = 0; i < Configuration.standardLaserDrillOres.length; i++) {
				String oreDictName = Configuration.standardLaserDrillOres[i];
				List<ItemStack> ores2 = OreDictionary.getOres(oreDictName);

				if(ores2 != null && !ores2.isEmpty()) {
					ores.add(new BlockMeta(Block.getBlockFromItem(ores2.get(0).getItem()), ores2.get(0).getItemDamage()));
				}
			}
		}
	}

	public boolean isAlive() {
		return world != null;
	}

	public boolean isFinished() {
		return false;
	}

	public boolean getJammed() { return jammed; }

	public void setJammed(boolean newJam) { jammed = newJam; }

	public void deactivateLaser() {
		this.world = null;
	}

	/**
	 * creates the laser and begins mining.  This can
	 * fail if the chunk cannot be force loaded
	 * @param world world to spawn the laser into
	 * @param x x coord
	 * @param z z coord
	 * @return whether creating the laser is successful
	 */
	public boolean activateLaser(World world, int x, int z) {
		this.world = world;
		return true;
	}

	
	public void performOperation() {

		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		if(random.nextInt(10) == 0) {
			BlockMeta item = ores.get(random.nextInt(ores.size()));
			items.add(new ItemStack(item.getBlock(), 5, item.getMeta()));
		}
		else
			items.add(new ItemStack(Blocks.COBBLESTONE, 5));
		
		
		//TODO: generate Items

		if(boundChest != null){
			ItemStack stacks[] = new ItemStack[items.size()];

			stacks = items.toArray(stacks);

			ZUtils.mergeInventory(stacks, boundChest);

			if(!ZUtils.isInvEmpty(stacks)) {
				//TODO: drop extra items
				this.deactivateLaser();
				this.jammed = true;
				return;
			}
		}
	}

	@Override
	public String getInfo(World world) {
		return null;
	}

	@Override
	public String getName() {
		return "Laser";
	}

	@Override
	public boolean performAction(EntityPlayer player, World world, BlockPos pos) {
		performOperation();
		return false;
	}

	@Override
	public double failureChance() {
		return 0;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("jammed", jammed);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		jammed = nbt.getBoolean("jammed");
	}

	@Override
	public boolean canTick() {
		return false;
	}

	@Override
	public void tickEntity() {
	}
}