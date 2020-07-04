package alertv2;

import java.io.File;
import java.util.Scanner;

import configv2.Config;
import net.hammereditor.designutilities.errors.ValueNotFoundException;


public class FileTemplateMessageReader implements TemplateMessageReader 
{
	private File templateMessageFile;
	
	public File getTemplateMessageFile() {
		return templateMessageFile;
	}

	public void setTemplateMessageFile(File templateMessageFile) {
		this.templateMessageFile = templateMessageFile;
	}

	public FileTemplateMessageReader(File templateMessageFile)
	{
		this.templateMessageFile = templateMessageFile;
	}
	
	/**
	 * use one of the built-in template files inside this program, according to the templateMessageFolder set inside the configuration
	 * @param builtinTMFfilename example: 'screenshotMessage1.html"
	 * @throws ValueNotFoundException
	 */
	public FileTemplateMessageReader(String builtinTMFfilename) throws ValueNotFoundException
	{
		String templateMessageFolderPath = EmailAlertMessage.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		Object tfpObj = Config.config.get("eam_templateMessageFolder");
		if (tfpObj != null)
			templateMessageFolderPath = (String)tfpObj;
		
		templateMessageFile = new File(templateMessageFolderPath + "/" + builtinTMFfilename);
	}

	public String read() throws Exception 
	{
		try
		{
			if (!templateMessageFile.exists())
				throw new Exception("File \'" + templateMessageFile.getAbsolutePath() + "\' doesn\'t exist");
			 
			Scanner tfin = new Scanner(templateMessageFile);
			StringBuilder sb = new StringBuilder();
			
			while (tfin.hasNext())
			{
				sb.append(tfin.nextLine());
				sb.append("\n");
			}
			
			tfin.close();
			return sb.toString();
		}
		catch (Exception e) {
			throw new Exception("Error while reading HTML template e-mail file", e);
		}
	}
}
