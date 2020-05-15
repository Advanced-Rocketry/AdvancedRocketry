package zmaster587.advancedRocketry.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.tile.station.TilePlanetaryHologram;

public class EntityUIStar extends EntityUIPlanet {
	
	StellarBody star;
	int subStar = -1;
	public final static int starIDoffset = 10000;

	protected static final DataParameter<Integer> subStarData =  EntityDataManager.<Integer>createKey(EntityUIStar.class, DataSerializers.VARINT);
	
	public EntityUIStar(World worldIn, StellarBody properties, TilePlanetaryHologram tile, double x, double y, double z) {
		this(worldIn);
		setPosition(x, y, z);
		setProperties(properties);
		this.tile = tile;
		subStar = -1;
	}
	
	public EntityUIStar(World worldIn, StellarBody properties, int subStar, TilePlanetaryHologram tile, double x, double y, double z) {
		this(worldIn, properties, tile, x,y,z);
		this.dataManager.set(subStarData, (this.subStar = subStar));
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(subStarData, -1);
	}
	
	public EntityUIStar(World worldIn) {
		super(worldIn);
		setSize(0.2f, 0.2f);
		subStar = -1;
	}
	
	public void setProperties(StellarBody properties) {
		this.star = properties;
		if(properties != null)
			this.dataManager.set(planetID, star.getId());
		else
			this.dataManager.set(planetID, Constants.INVALID_PLANET);
	}
	
	public int getPlanetID() {
		//this.dataManager.set(planetID, 256);

		if(!world.isRemote)
			return star == null ? -1 : star.getId();

		int planetId = this.dataManager.get(planetID);

		if(star != null && star.getId() != planetId) {
			if(planetId == Constants.INVALID_PLANET )
				star = null;
			else
				star = DimensionManager.getInstance().getStar(planetId);
		}

		return this.dataManager.get(planetID);
	}
	
	public StellarBody getStarProperties() {
		if((star == null && getPlanetID() != Constants.INVALID_PLANET) || (star != null && getPlanetID() != star.getId())) {
			star = DimensionManager.getInstance().getStar(getPlanetID());
			if((subStar = this.dataManager.get(subStarData)) != -1)
				if(!star.getSubStars().isEmpty())
					star = star.getSubStars().get(subStar);
		}

		return star;
	}
	
	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if(!world.isRemote && tile != null) {
			tile.selectSystem(star.getId() + starIDoffset);
		}
		return true;
	}
}
