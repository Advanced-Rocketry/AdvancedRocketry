package zmaster587.advancedRocketry.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.tile.station.TilePlanetaryHologram;

public class EntityUIStar extends EntityUIPlanet {
	
	StellarBody star;
	int subStar = -1;
	public final static int starIDoffset = 10000;
	protected static final int subStarData = 4;
	
	public EntityUIStar(World worldIn, StellarBody properties, TilePlanetaryHologram tile, double x, double y, double z) {
		this(worldIn);
		setPosition(x, y, z);
		setProperties(properties);
		this.tile = tile;
		subStar = -1;
	}
	
	public EntityUIStar(World worldIn, StellarBody properties, int subStar, TilePlanetaryHologram tile, double x, double y, double z) {
		this(worldIn, properties, tile, x,y,z);
		this.dataWatcher.updateObject(subStarData, new Integer((this.subStar = subStar)));
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(subStarData, new Integer(-1));
	}
	
	public EntityUIStar(World worldIn) {
		super(worldIn);
		setSize(0.2f, 0.2f);
		subStar = -1;
	}
	
	public void setProperties(StellarBody properties) {
		this.star = properties;
		if(properties != null)
			this.dataWatcher.updateObject(PLANET_ID, star.getId());
		else
			this.dataWatcher.updateObject(PLANET_ID, -1);
	}
	
	public int getPlanetID() {
		//this.dataManager.set(planetID, 256);

		if(!worldObj.isRemote)
			return star == null ? -1 : star.getId();

		int planetId = this.dataWatcher.getWatchableObjectInt(PLANET_ID);

		if(star != null && star.getId() != planetId) {
			if(planetId == -1 )
				star = null;
			else
				star = DimensionManager.getInstance().getStar(planetId);
		}

		return this.dataWatcher.getWatchableObjectInt(PLANET_ID);
	}
	
	public StellarBody getStarProperties() {
		if((star == null && getPlanetID() != -1) || (star != null && getPlanetID() != star.getId())) {
			star = DimensionManager.getInstance().getStar(getPlanetID());
			if((subStar = this.dataWatcher.getWatchableObjectInt(subStarData)) != -1)
				star = star.getSubStars().get(subStar);
		}

		return star;
	}
	
	@Override
	public boolean interactFirst(EntityPlayer player) {
		if(!worldObj.isRemote && tile != null) {
			tile.selectSystem(star.getId() + starIDoffset);
		}
		return true;
	}
}
