package com.microstrategy.samples.subscriptions;

import java.util.Iterator;

import com.microstrategy.samples.sessions.SessionManager;
import com.microstrategy.web.objects.*;
import com.microstrategy.webapi.EnumDSSXMLNodeType;

public class ListSubscriptions {
  private static String DEFAULT_SERVER = "APS-TSIEBLER-VM";
  private static String DEFAULT_PROJECT = "MicroStrategy Tutorial";
  private static String DEFAULT_USERNAME = "Administrator";
  private static String DEFAULT_PASSWORD = "";

  public static void main(String[] args) {
    // TODO Auto-generated method stub

    // create a session in any way
    WebIServerSession exampleSession = SessionManager.getSessionWithDetails(DEFAULT_SERVER, DEFAULT_PROJECT, DEFAULT_USERNAME, DEFAULT_PASSWORD);

    // Get WebObjectsFactory using this valid session, these are connected
    WebObjectsFactory webObjectFactory = exampleSession.getFactory();

    // get a subscription source
    WebSubscriptionsSource subSource = webObjectFactory.getSubscriptionsSource();

    // use subscription source to retrieve a list of subscriptions
    SimpleList subscriptions = getSubscriptionsForSource(subSource);
    
    // get a prompted subscription from list
    WebSubscription subscription = getPromptedSubscriptionFromList(subscriptions);
    
    // get prompts for subscription
    WebPrompts prompts = getPromptsForSubscription(subscription);
    
    // print prompt information
    printAnswersForPrompts(prompts);
  }

  public static SimpleList getSubscriptionsForSource(WebSubscriptionsSource subSource) {
    System.out.println("Getting subscriptions");
    try {
      SimpleList subscriptions = subSource.getSubscriptions();

      System.out.println("\tTotal subscriptions: " + subscriptions.size());

      return subscriptions;

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    System.out.println("Done getting subscriptions");
    return null;
  }

  public static WebSubscription getPromptedSubscriptionFromList(SimpleList sl) {
    for (int i = 0; i < sl.size(); i++) {
      WebSubscription ws = (WebSubscription)sl.item(i);

      String subscriptionName = ws.getName();
      try {
        String contentName = ws.getContent().getName();
        
        boolean hasPrompts = ws.isPersonalized();
        if (hasPrompts) {
          System.out.println("\t\t Name: " + subscriptionName + " : Report/Document: " + contentName);
          return ws;
        }
        
      } catch (WebObjectsException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    }
    return null;
  }
  
  public static WebPrompts getPromptsForSubscription(WebSubscription subscription) {
    System.out.println("Reading prompts for subscription " + subscription.getName());
    
    try {
      WebSubscriptionContent content = subscription.getContent();
      
      int contentType = content.getContentType();
      if (contentType != EnumWebSubscriptionContentTypes.CONTENT_TYPE_REPORT) {
        System.out.println("Unhandled content type for subscription: " + contentType);
        return null;
      }
      
      // cast to a type
      WebSubscriptionContentReport contentReport = (WebSubscriptionContentReport)content;
      
      // get prompts for report content
      return contentReport.getPrompts();
      
      
    } catch (WebObjectsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    System.out.println("Finished reading prompts");
    return null;
  }
  
  public static void printAnswersForPrompt(WebPrompt prompt) {
    
    int promptType = prompt.getPromptType();
    if (promptType != EnumWebPromptType.WebPromptTypeExpression) {
      System.out.println("Unhandled prompt type: " + promptType);
      return;
    }
    
    WebExpressionPrompt exPrompt = (WebExpressionPrompt)prompt;
    WebExpression expression = exPrompt.getAnswer();
    
    // process expression
    WebNode root = expression.getRootNode();
    
    WebNode firstChild = root.getFirstChild();
    
    int children = firstChild.getChildCount();
    
    for (int i = 0; i < children;i++) {
      WebNode subNode = firstChild.getChild(i);
      int nodeType = subNode.getNodeType();
      
      if (nodeType == EnumDSSXMLNodeType.DssXmlNodeConstant) {
        
        WebConstantNode constantNode = (WebConstantNode) firstChild.getChild(i);
        System.out.println("constant name: " + constantNode.getDisplayName());
        System.out.println("constant value: " + constantNode.getValue());
        
      } else if (nodeType == EnumDSSXMLNodeType.DssXmlNodeShortcut) {
        
        WebShortcutNode shortcutNode = (WebShortcutNode) firstChild.getChild(i);
        System.out.println("shortcut name: " + shortcutNode.getDisplayName());
        System.out.println("shortcut xml value: " + shortcutNode.getShortXML());

      } else {
        
        System.out.println("node type: " + nodeType);
      }
    }
    

    
    System.out.println("No more answers: " );
  }
  
  public static void printAnswersForPrompts(WebPrompts prompts) {
    System.out.println("Printing details on " + prompts.size() + " prompts...");
        
    for (int i = 0; i < prompts.size(); i++) {
      // get prompt from list
      WebPrompt prompt = prompts.get(i);
      
      printAnswersForPrompt(prompt);
    }
    
    System.out.println("Finished printing prompts");

  }
  

}
