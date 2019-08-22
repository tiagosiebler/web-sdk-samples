package com.microstrategy.samples.serverconfiguration;
import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.web.objects.WebFolder;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.objects.WebServerDef;
import com.microstrategy.web.objects.WebServerSetting;
import com.microstrategy.web.objects.WebServerSettings;
import com.microstrategy.webapi.EnumDSSXMLFolderNames;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.microstrategy.webapi.EnumDSSXMLServerSettingID;

public class UpdateServerDefinitionSettings {

	public static void main(String[] args) {
		//Connectivity for the intelligence server
		String intelligenceServerName = "10.23.5.242";
		String projectName = "MicroStrategy Tutorial";
		String microstrategyUsername = "Administrator";
		String microstrategyPassword = "";

		//Obtain IServer session
		WebIServerSession session = SessionManager.getSessionWithDetails(intelligenceServerName, 
				projectName, 
				microstrategyUsername, 
				microstrategyPassword);

		//Web server setting to modify.
		//List of server settings are defined by EnumDSSXMLServerSettingID.
		//https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/ReferenceFiles/reference/com/microstrategy/webapi/EnumDSSXMLServerSettingID.html
		int serverSetting = EnumDSSXMLServerSettingID.DssXmlServerMaxWebConnectionIdleTime;

		try {
			WebObjectsFactory factory = session.getFactory();
			WebObjectSource woSource = factory.getObjectSource();
			
			//Retrieving server definition.
			WebServerDef serverDef = getServerDefForCurrentSession(woSource);
			
			//Setting new value for the setting.
			SetNewServerSetting(serverDef, serverSetting, "600");
			
			//Important: need to save setting using the ObjectSource object.
			woSource.save(serverDef);
			System.out.println("Setting changed.");
				
		} catch (WebObjectsException e) {
			e.printStackTrace();
		}
	}
	
	
	
	//Retrieve server definition from current session's ObjectSource.
	public static WebServerDef getServerDefForCurrentSession(WebObjectSource woSource) throws WebObjectsException  {

		String serverDefFolderID = woSource.getFolderID(EnumDSSXMLFolderNames.DssXmlFolderNameConfigureServerDefs);
		
		Boolean populateImmediately = true;
		WebFolder serverDefFolder = (WebFolder) woSource.getObject(serverDefFolderID, EnumDSSXMLObjectTypes.DssXmlTypeFolder, populateImmediately);

		//Displaying how many server definitions there are in the folder.
		int serverDefsCount = serverDefFolder.getChildCount();
		System.out.println("#Server defintions: " + serverDefsCount);
		 
		//Retrieving the 1st. server definitions object.
		WebServerDef serverDef = (WebServerDef) serverDefFolder.get(0);
		serverDef.populate();
		System.out.println("Server definition selected: " + serverDef.getName());
		
		return serverDef;
		
	}
	
	
	
	//Set a new value (newValue) for the server setting identified by setting in the server definition identified by serverDef.
	public static void SetNewServerSetting(WebServerDef serverDef, int setting,  String newValue) {
		
		WebServerSettings serverSettings = serverDef.getServerSettings();
		WebServerSetting serverSetting = serverSettings.get(setting);

		//Showing current value.
		System.out.println("Current value [" + serverSetting.getDataType() + "]: " + serverSetting.getValue());

		//Setting new value.
		//Data type of the setting must be taken into account. See link to documentation below (setValue()).
		//https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/ReferenceFiles/reference/com/microstrategy/web/objects/WebServerSetting.html#setValue(java.lang.String)
		serverSetting.setValue(newValue);
		System.out.println("New value [" + serverSetting.getDataType() + "]:" + serverSetting.getValue() );
	}


}

