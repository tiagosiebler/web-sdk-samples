/* FOREWORD:

This sample searches through a parent folder for folders that can either be visible-only or hidden.
The change is only a matter of commenting out the object flag on search: EnumDSSXMLObjectFlags.DssXmlObjectFindHidden
If provided, hidden and visible folders will be found. If not, only visible folders will be found.

The information that you have to fill in is: 
1. The package
2. The details of the I-Server session
3. The GUID of the folder that you want to search through - the sample one used is the Public Objects folder.

Made with love by Edgar, based on [278906/DE124645/KB16830]
*/

package com.microstrategy.samples.searching;

import java.util.Enumeration;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.web.objects.WebFolder;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebSearch;
import com.microstrategy.webapi.EnumDSSXMLObjectFlags;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.microstrategy.webapi.EnumDSSXMLSearchFlags;

public class hiddenObjects {

  public static void main(String[] args) throws WebObjectsException { 
    // Connectivity details for the intelligence server
    String intelligenceServerName = "aps-tsiebler-vm";
    String projectName = "MicroStrategy Tutorial";
    String microstrategyUsername = "Administrator";
    String microstrategyPassword = "";
    
    // Parent folder we want to search within. The sample ID points to the Public Objects folder in the MicroStrategy Tutorial project.
    String parentFolderObjectID = "98FE182C2A10427EACE0CD30B6768258"; 

    // Create our Intelligence Server Session
    System.out.println("Connecting to intelligence server...");
    WebIServerSession serverSession = SessionManager.getSessionWithDetails(intelligenceServerName, projectName, microstrategyUsername, microstrategyPassword);

    // Execute a search for folders, including hidden folders, and return the results
    WebFolder searchResults = searchForFoldersWithinFolder(serverSession, parentFolderObjectID);

    // Print all folders found in search
    printSearchResults(searchResults);
  }

  public static WebFolder searchForFoldersWithinFolder(WebIServerSession serverSession, String parentFolderObjectID) throws WebObjectsException {
    
    System.out.println("Performing the search.");
    
    WebObjectSource source = serverSession.getFactory().getObjectSource();
    
    // Prepare a new search using the WebSearch object. The WebObjectSource connects this to the session provided above
    WebSearch search = source.getNewSearchObject();
    
    // Look for any results, within this folder in the metadata (or any child of that folder)
    search.setSearchRoot(parentFolderObjectID);
    
    // Flags that affect how this search will be done
    search.setFlags(EnumDSSXMLSearchFlags.DssXmlSearchNameWildCard | EnumDSSXMLSearchFlags.DssXmlSearchRootRecursive | EnumDSSXMLObjectFlags.DssXmlObjectFindHidden);
    
    // Wait until results are ready
    search.setAsync(false);

    // Limit search results to only objects of type: folder. Any other object types are ignored.
    search.types().add(EnumDSSXMLObjectTypes.DssXmlTypeFolder);
    
    // Execute the search against the metadata
    search.submit();	

    System.out.println("Search submitted, returning results");
    return search.getResults();
  }

  public static void printSearchResults(WebFolder searchResults) {
    
    System.out.println("This search found " + searchResults.size() + " results");

    // Getting a list of results into Enumeration
    Enumeration<WebObjectInfo> results = searchResults.elements();

    // Iterating through each result, while results remain
    System.out.println("Looping through results by name: ");
    while (results.hasMoreElements()) {
      WebObjectInfo result = results.nextElement();
      System.out.println(result.getName());
    }
    
    System.out.println("Finished looping through search results.");
  }
}