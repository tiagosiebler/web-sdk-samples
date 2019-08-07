package com.microstrategy.samples.websubscriptions;
import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.objects.WebSubscriptionAddress;
import com.microstrategy.web.objects.admin.users.WebUser;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;

//Searches through a MicroStrategy user and prints out all Subscription Delivery Sources with non-empty Email Addresses
public class ObtainWebSubscriptionEmailAddress {

	
	public static void main(String[] args)  {
		String intelligenceServerName = "10.27.72.72";
		String projectName = "MicroStrategy Tutorial";
		String microstrategyUsername = "administrator";
		String microstrategyPassword = "";
		//Establishes a connection to the Intelligence Server
		WebIServerSession session = SessionManager.getSessionWithDetails(intelligenceServerName, projectName, microstrategyUsername, microstrategyPassword);
		
		//Establish factory and source from the valid session to obtain the user object
		WebObjectsFactory factory = session.getFactory();
		WebObjectSource source = factory.getObjectSource();
		
		try {
			WebUser user = (WebUser) source.getObject("A69FE9944C16237E19C2BAAB320CC0F3", EnumDSSXMLObjectTypes.DssXmlTypeUser);
			WebSubscriptionAddress currentSubscriptionAddress;
			//size of for loop used to iterate through the Subscription Address object
			int numberOfDeliverySources = user.getAddresses().size();
			String currentName;
			String currentEmail;
			//currentSubscriptionAddress can only be used to store one specific element, so a for loop was created to
			//search through all WebSubScriptionAddresses (Delivery Sources) and print out the name and Email of all non-empty Email addresses
			for (int i = 0; i < numberOfDeliverySources; i++)
			{
				currentSubscriptionAddress = user.getAddresses().get(i);
				currentName = currentSubscriptionAddress.getName();
				currentEmail = currentSubscriptionAddress.getValue();
				if (currentEmail.isEmpty()) {
					//Do Nothing, because the email address is empty
				}
				else {
					System.out.println("Delivery Name " + currentName);
					System.out.println("Email Address " + currentEmail);
				}
			}
		}
		catch (IllegalArgumentException | WebObjectsException e) {
		      // TODO Auto-generated catch block
		      e.printStackTrace();
		    }
		  }

}
