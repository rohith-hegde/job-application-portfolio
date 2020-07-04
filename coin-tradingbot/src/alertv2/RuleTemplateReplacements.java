package alertv2;

import java.util.Map;

public interface RuleTemplateReplacements 
{
	/**
	 * 
	 * @return example: {
	 * 		{"quickMA", "0.02439"},
	 * 		{"slowMA", "0.024813"},
	 * 		{"lastCrossTime", "1563409000"}
	 * }
	 */
	public Map<String, String> getTemplateReplacements();
}
