package zmaster587.advancedRocketry.world.type;

import net.minecraft.world.WorldType;

public class WorldTypeSpace extends WorldType {

    public WorldTypeSpace(String string) {
        super(string);
    }


    @Override
    public boolean canBeCreated() {
        return false;
    }
}
