package alertv2;

/**
 * all classes which extend this are designed to do some kind of action, like placing an order, closing a position or transferring funds.
 * this is different from an alert message
 *
 */
public abstract class Action
{
	private String description;
	
	public Action(String description)
	{
		this.description = description;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public abstract void doAction() throws Exception;
	
	/*public void setInformation(arg, arg, arg, ...);*/ //some version of this will be present in classes which need to be provided some information in order to do the action. The AlertTestRuleWatcher or a serious production rule watcher will take care of this. 
	//or this info can be set in the constructor
	
}
