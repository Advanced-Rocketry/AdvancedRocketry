package zmaster587.advancedRocketry.atmosphere;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.atmosphere.AtmosphereRegister;

public class AtmosphereType implements IAtmosphere {

    //We're probably not getting a polluted atmosphere type
    public static final AtmosphereType AIR = new AtmosphereType(false, true, "air");
    public static final AtmosphereType PRESSURIZEDAIR = new AtmosphereType(false, true, true, "PressurizedAir");
    public static final AtmosphereType LOWOXYGEN = new AtmosphereLowOxygen(true, false, true, "lowO2");
    public static final AtmosphereType VACUUM = new AtmosphereVacuum();
    public static final AtmosphereType HIGHPRESSURE = new AtmosphereHighPressure(true, false, true, "HighPressure");
    public static final AtmosphereType SUPERHIGHPRESSURE = new AtmosphereSuperHighPressure(true, false, true, "SuperHighPressure");
    public static final AtmosphereType VERYHOT = new AtmosphereVeryHot(true, false, true, "VeryHot");
    public static final AtmosphereType SUPERHEATED = new AtmosphereSuperheated(true, false, true, "Superheated");
    public static final AtmosphereType NOO2 = new AtmosphereNoOxygen(true, false, false, "NoO2");
    public static final AtmosphereType HIGHPRESSURENOO2 = new AtmosphereHighPressureNoOxygen(true, false, false, "HighPressureNoO2");
    public static final AtmosphereType SUPERHIGHPRESSURENOO2 = new AtmosphereSuperHighPressureNoOxygen(true, false, false, "SuperHighPressureNoO2");
    public static final AtmosphereType VERYHOTNOO2 = new AtmosphereVeryHotNoOxygen(true, false, false, "VeryHotNoO2");
    public static final AtmosphereType SUPERHEATEDNOO2 = new AtmosphereSuperheatedNoOxygen(true, false, false, "SuperheatedNoOxygen");

    static {
        AtmosphereRegister.getInstance().registerAtmosphere(AIR);
        AtmosphereRegister.getInstance().registerAtmosphere(PRESSURIZEDAIR);
        AtmosphereRegister.getInstance().registerAtmosphere(VACUUM);
        AtmosphereRegister.getInstance().registerAtmosphere(LOWOXYGEN);
        AtmosphereRegister.getInstance().registerAtmosphere(HIGHPRESSURE);
        AtmosphereRegister.getInstance().registerAtmosphere(SUPERHIGHPRESSURE);
        AtmosphereRegister.getInstance().registerAtmosphere(VERYHOT);
        AtmosphereRegister.getInstance().registerAtmosphere(SUPERHEATED);
        AtmosphereRegister.getInstance().registerAtmosphere(NOO2);
        AtmosphereRegister.getInstance().registerAtmosphere(HIGHPRESSURENOO2);
        AtmosphereRegister.getInstance().registerAtmosphere(SUPERHIGHPRESSURENOO2);
        AtmosphereRegister.getInstance().registerAtmosphere(VERYHOTNOO2);
        AtmosphereRegister.getInstance().registerAtmosphere(SUPERHEATEDNOO2);
    }

    private boolean allowsCombustion;
    private boolean isBreathable;
    private boolean canTick;
    private String name;

    public AtmosphereType(boolean canTick, boolean isBreathable, String name) {
        this.allowsCombustion = isBreathable;
        this.isBreathable = isBreathable;
        this.canTick = canTick;
        this.name = name;
    }

    public AtmosphereType(boolean canTick, boolean isBreathable, boolean allowsCombustion, String name) {
        this(canTick, isBreathable, name);
        this.allowsCombustion = allowsCombustion;
    }

    /**
     * Should the gas run a tick on every player in it?  Calls onTick(EntityLiving base)
     *
     * @return true if the atmosphere performs an action every tick
     */
    public boolean canTick() {
        return canTick;
    }

    //TODO: check for all entities

    /**
     * @param player living entity inside this atmosphere we are ticking
     * @return true if the atmosphere does not affect the entity in any way
     */
    public boolean isImmune(EntityLivingBase player) {
        return isBreathable;
    }

    public boolean isImmune(Class<? extends Entity> clazz) {
        return isBreathable() || ARConfiguration.getCurrentConfig().bypassEntity.contains(clazz);
    }

    @Override
    public boolean isBreathable() {
        return isBreathable;
    }

    /**
     * To be used to check if combustion can occur in this atmosphere, furnaces, torches, engines, etc could run this check
     *
     * @return true if the atmosphere is combustable
     */
    public boolean allowsCombustion() {
        return allowsCombustion;
    }

    /**
     * Sets the atmosphere to be breathable or not breathable
     *
     * @param isBreathable
     */
    public void setIsBreathable(boolean isBreathable) {
        this.isBreathable = isBreathable;
    }

    /**
     * Sets the atmosphere to allow combustion or not to allow combustion
     *
     * @param allowsCombustion
     */
    public void setAllowsCombustion(boolean allowsCombustion) {
        this.allowsCombustion = allowsCombustion;
    }

    /**
     * @return unlocalized message to display when player is in the gas with no protection
     */
    public String getDisplayMessage() {
        return "";
    }

    //TODO: tick for all entities

    /**
     * If the canTick() returns true then then this is called every tick on EntityLivingBase objects located inside this atmosphere
     *
     * @param player entity being ticked
     */
    public void onTick(EntityLivingBase player) {
    }

    @Override
    public String getUnlocalizedName() {
        return name;
    }
}
