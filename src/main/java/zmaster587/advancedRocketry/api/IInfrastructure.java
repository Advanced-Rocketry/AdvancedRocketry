package zmaster587.advancedRocketry.api;



/**
 * Implemented by a class if it can be linked to a rocket by a linking tool
 */

public interface IInfrastructure {
	
	/**
	 * Called when the rocket unlinks the object
	 */
	public void unlinkRocket();
	
	/**
	 * @return true if the object is to be automatically unlinked by the rocket when launched
	 */
	public boolean disconnectOnLiftOff();
	
	/**
	 * Called when the rocket attempts to link to the block
	 * @param rocket Rocket Requesting the link
	 * @return true if the infrastructure should be allowed to link
	 */
	public boolean linkRocket(EntityRocketBase rocket);
	
	/**
	 * Called when a mission attempts to link to the block
	 * @param misson
	 * @return
	 */
	public boolean linkMission(IMission misson);
	
	/**
	 * Called when mission is to be unlinked from the tile
	 * @param misson
	 * @return
	 */
	public void unlinkMission();
	
	/**
	 * @return maximum link distance in block for the tile
	 */
	public int getMaxLinkDistance();
	
	/**
	 * @return true if a connection can be rendered to the infrastructure
	 */
	public boolean canRenderConnection();
}
