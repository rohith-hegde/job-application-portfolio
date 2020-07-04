package alertv2;

import java.util.HashMap;
import java.util.Map;

public abstract class TemplatedAlertMessage extends AlertMessage
{
	private Map<String, String> templateReplacements;

	public TemplatedAlertMessage(String title, String content, Map<String, String> templateReplacements) {
		super(title, content);
		this.templateReplacements = templateReplacements;
	}
	
	public TemplatedAlertMessage(String title, String content) {
		super(title, content);
		this.templateReplacements = new HashMap<String, String> ();
	}
	
	public void putTemplateReplacement(String orig, String rpl)
	{
		templateReplacements.put(orig, rpl);
		//System.out.println("put replacement: " + orig + "->" + rpl);
	}
	
	public void putTemplateReplacements(Map<String, String> r)
	{
		for (String o : r.keySet())
		{
			templateReplacements.put(o, r.get(o));
			System.out.println("put replacement: " + o + "->" + r.get(o));
		}
	}
	
	public Map<String, String> getTemplateReplacements()
	{
		return templateReplacements;
	}

}
