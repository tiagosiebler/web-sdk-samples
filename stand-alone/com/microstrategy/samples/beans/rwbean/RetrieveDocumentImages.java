package com.microstrategy.samples.beans.rwbean;

import java.util.ArrayList;
import java.util.List;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.samples.util.FileHelper;
import com.microstrategy.web.beans.BeanFactory;
import com.microstrategy.web.beans.RWBean;
import com.microstrategy.web.beans.WebBeanException;
import com.microstrategy.web.objects.WebBlob;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.objects.rw.EnumRWUnitTypes;
import com.microstrategy.web.objects.rw.RWDefinition;
import com.microstrategy.web.objects.rw.RWImageDef;
import com.microstrategy.web.objects.rw.RWInstance;
import com.microstrategy.web.objects.rw.RWUnitDef;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;

public class RetrieveDocumentImages {

	public static void main(String[] args) throws WebBeanException, WebObjectsException {
		 // Connectivity for the intelligence server
	    String intelligenceServerName = "10.23.5.242";
	    String projectName = "MicroStrategy Tutorial";
	    String microstrategyUsername = "Administrator";
	    String microstrategyPassword = "";
	    
	    // The GUID of the document to use
	    String documentID = "911D069848D92F6A07AF65A7658E97A0";
	    
	    // Create our I-Server Session
	    WebIServerSession session = SessionManager.getSessionWithDetails(intelligenceServerName, projectName, microstrategyUsername, microstrategyPassword);

	    //Array with the images objects.
	    ArrayList<WebBlob> imagesArrayList = getEmbeddedImagesFromDocument(documentID, session);
	    //TODO:
	    //Code a function to retrieve any type of object from a document.
	    //- Add 3rd parameter to getEmbeddedImagesFromDocument to filter by object type.
	    //INclude object type with: EnumDSSXMLObjectTypes

	}
	
	
	 public static ArrayList<WebBlob> getEmbeddedImagesFromDocument(String documentID, WebIServerSession session) throws WebBeanException, WebObjectsException {
		    //Usage: Saves the images embedded in the documentID to image file located in 'pathString'
		  
		    RWBean rwb = (RWBean)BeanFactory.getInstance().newBean("RWBean");
		    // ID of the object we wish to execute
		    rwb.setObjectID(documentID);
		    // session used for execution
		    rwb.setSessionInfo(session);
		    
		    //Create Document instance.
		    WebObjectsFactory objectsFactory = WebObjectsFactory.getInstance();
			RWInstance rwInstance = rwb.getRWInstance();        
			//Show document's name.
			System.out.println("Document Name: " + rwInstance.getDefinition().getName());
			
			//Retrieve document object definition to retrieve images.
			RWDefinition rwDefinition = rwInstance.getDefinition();
			
			//Filter object by image type (EnumRWUnitTypes.RWUNIT_IMAGE)
			RWUnitDef[] rwUnitDefs = rwDefinition.findUnitsByType(EnumRWUnitTypes.RWUNIT_IMAGE);

			//Prints number of images.
			System.out.println("Number o fimages in document: " + rwUnitDefs.length);
			
			//Creating the ArrayList of WebBlog images objects.
			RWImageDef imageBlobImpl = null;
			String pathString = null;
			WebObjectSource webObjectSource = null;
			//WebBlob[] imagesBlobs = null;
			ArrayList<WebBlob> imagesBlobs = new ArrayList<WebBlob>();
			for (RWUnitDef rwUnitDef : rwUnitDefs) {
				imageBlobImpl = (RWImageDef)rwUnitDef;
				pathString = imageBlobImpl.getPath();
				webObjectSource = objectsFactory.getObjectSource();
				WebBlob imageBlob = (WebBlob)webObjectSource.getObject(pathString, EnumDSSXMLObjectTypes.DssXmlTypeBlob);
				imagesBlobs.add(imageBlob);
			}
			//Return ArrayList of images.
			return imagesBlobs;
			
	  }

}
