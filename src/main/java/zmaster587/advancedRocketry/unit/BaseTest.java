package zmaster587.advancedRocketry.unit;

public class BaseTest {

	protected boolean passed;
	protected String name;
	
	BaseTest()
	{
		passed = false;
	}
	
	public void success()
	{
		passed = true;
	}
	
	public boolean passed()
	{
		return passed;
	}

	public String getName()
	{
		return name;
	}
}
