package zmaster587.advancedRocketry.api.dimension.solar;

import java.util.Collection;
import java.util.List;

public interface IGalaxy {
	Collection<StellarBody> getStars();
	
	StellarBody getStar(int id);
}
