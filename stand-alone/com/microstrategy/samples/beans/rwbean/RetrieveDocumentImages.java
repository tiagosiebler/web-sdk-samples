package com.microstrategy.samples.beans.rwbean;
import java.util.ArrayList;

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

		// Array with the images objects.
		ArrayList<WebBlob> imagesArrayList = getEmbeddedImagesFromDocument(documentID, session);

		// Path to save the images.
		String pathString = "/Users/mpastrana";
		saveImagesTofile(imagesArrayList, pathString);

	}

	// Extract images embedded in a document using its ID (documentID) and an existing session (session) to an ArrayList of images.
	public static ArrayList<WebBlob> getEmbeddedImagesFromDocument(String documentID, WebIServerSession session) throws WebObjectsException, IllegalArgumentException, WebBeanException {

		RWBean rwb = (RWBean)BeanFactory.getInstance().newBean("RWBean");

		// ID of the object we wish to execute
		rwb.setObjectID(documentID);

		// Session used for execution
		rwb.setSessionInfo(session);

		// Create Document instance.
		WebObjectsFactory objectsFactory = WebObjectsFactory.getInstance();
		RWInstance rwInstance = rwb.getRWInstance();        

		System.out.println("Document Name: " + rwInstance.getDefinition().getName());

		// Retrieve document object definition to retrieve images.
		RWDefinition rwDefinition = rwInstance.getDefinition();

		// Filter object by image type (EnumRWUnitTypes.RWUNIT_IMAGE)
		RWUnitDef[] rwUnitDefs = rwDefinition.findUnitsByType(EnumRWUnitTypes.RWUNIT_IMAGE);

		// Prints number of images.
		System.out.println("Number of images in document: " + rwUnitDefs.length);

		// Creating the ArrayList of WebBlog images objects.
		WebObjectSource webObjectSource = objectsFactory.getObjectSource(); 
		
		// Images in documents are actually metadata objects, stored as WebBlob types, containing the binary image data.
		// https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/ReferenceFiles/reference/com/microstrategy/web/objects/WebBlob.html
		
		// Looping through all the objects to compose the images ArrayList.
		// The RWUnitDef containing the image does not directly expose the image binary data, but it has the object ID for the WebBlob, which can be used to retrieve this.
		// Fetch the ID of the WebBlob, then fetch the WebBlob from the metadata using object ID, which now gives us access to the image binary data in a byte[]
		ArrayList<WebBlob> imagesBlobs = new ArrayList<WebBlob>();
		for (RWUnitDef rwUnitDef : rwUnitDefs) {
			RWImageDef imageBlobImpl = (RWImageDef)rwUnitDef;
			String imageObjectID = imageBlobImpl.getPath();
			WebBlob imageBlob = (WebBlob)webObjectSource.getObject(imageObjectID, EnumDSSXMLObjectTypes.DssXmlTypeBlob);
			imagesBlobs.add(imageBlob);
		}

		//Return ArrayList of images.
		return imagesBlobs;

	}
	
	// Save images from imageList ArrayList to file system. Full path for every image is composed using pathString + index of element in the ArrayList.  
	public static void saveImagesTofile(ArrayList<WebBlob> imageList, String pathString) throws WebObjectsException {
		String imgPrefixString = "Image";

		for (WebBlob imageBlob : imageList) {
			byte[] image = imageBlob.getBlob();
			String fileName = imgPrefixString + imageList.indexOf(imageBlob);
			FileHelper.saveByteArrayToFile(image, pathString + fileName);
		}
	}

}
