//package com.nmodes.facebookclient;

import java.io.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;

import org.json.JSONObject;
import org.json.JSONException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.util.EntityUtils;
import org.apache.http.params.CoreProtocolPNames;

import com.restfb.types.*;
import com.restfb.FacebookClient;
import com.restfb.json.*;
import com.restfb.*;

public class MessagesFromGroup 
{
    private static final int MAX_MSG_LEN = 2000;
	private static final String MY_APP_ID = "989545354463165"; //	App ID 989545354463165
	private static final String MY_APP_SECRET = "6d322af714205dd34883bc16148099ef"; //6d322af714205dd34883bc16148099ef

// YI - be sure that Application (Name) is nmayya  get it from: https://developers.facebook.com/tools/explorer/145634995501895/
	//old token
	//private static String accessToken = "EAACEdEose0cBAFrUQWpHjfbMvHVeOohUt7BX0ZCkujReWZC8WDSr1wQ2dHTfrwMZBI0AlqyWC4MP86VHTluYmEu5zxirvRd0L5fugXmHJt6v8TZAoT6jSwlQBWhsd9dWBoCtRt6PTZAjY9rhzvwjWxMH6BhkZCZANiheVZCkjvotSZAZC6LxKRJHmr";
	
	//my own token
	private static String accessToken = "EAACEdEose0cBALRpsZBqKHTtkwZAEZCBHZBRZBAKroLD0bwbleIziDXGXHeak3N3HCwK5SBIdJTcPSdZCSZCYOYt26DrUptRZCV9nlAHyKcFqPH1H4YsEhX1qGZA9tv9N5CQCod3WnXtTeVchXyXGdPindtwlsSfP00hEoAqtPKqguAZDZD";
    private static GregorianCalendar currentCalendar = new GregorianCalendar();
    private static ArrayList<String> groups = new ArrayList<String>();

//	private DefaultFacebookClient facebookClient;

    public static void main(String args[]) 
    {
    //    groups.add( new String("191776715771") );   // yyzdeals*
  //      groups.add(new String("302373339893904"));  // onlinetravelagents
      
    	// groups.add(new String("531547270259092"));  // Travel in the World
      //  groups.add(new String("211823115659727"));  // Canadian Hitchicker 
        groups.add(new String("10817829186"));      // Tourism Around The World (1)
      //  groups.add(new String("14305125747"));      // Tourism Around The World (2)
       // groups.add(new String("23538244294"));      // Jump From Around The World (Group)
      //  groups.add(new String("12708295429"));      // Servas Travel
      //  groups.add(new String("590786314367435"));   // Salsa Trip Advisor
        //groups.add(new String("151833271525800"));   // Shadow Travel And Tours
        

/*
https://www.facebook.com/groups/dreamtravelagent/
https://www.facebook.com/groups/thetravelworm/
https://www.facebook.com/groups/YTPTO/
*/

	MessagesFromGroup fbClient = new MessagesFromGroup();
 //   fbClient.getFirstToken(); //to obtain Extended Access Token
		
	fbClient.postQuery(args);
    }	
      
    public static void getFirstToken()
    {
    	DefaultFacebookClient defClient = new DefaultFacebookClient();
    	FacebookClient.AccessToken newToken = defClient.obtainAppAccessToken(MY_APP_ID, MY_APP_SECRET);
        accessToken = newToken.toString(); 
        System.out.println("My new access token: " + accessToken);
    }
    /******/
    public static void getAccessToken()
    {
    	DefaultFacebookClient defClient = new DefaultFacebookClient();
        FacebookClient.AccessToken extToken = defClient.obtainExtendedAccessToken( MY_APP_ID,
                                   MY_APP_SECRET, accessToken);
        accessToken = extToken.toString(); 
System.out.println("My extended access token: " + accessToken);

// Nov. 2014 - OAuth errors
//     accessToken = defClient.obtainAppAccessToken(MY_APP_ID, MY_APP_SECRET).toString();
// June 2014 remark - returns the same token again
// System.out.println( "My application access token: " + accessToken);
    }
		
//===========================ƒŒÀ∆≈Õ «¿œ”—“»“‹—ﬂ
    public void postQuery(String args[])
    { 
        String location = "";
        GregorianCalendar postCal = new GregorianCalendar();
        Date daysAgo = new Date(currentCalendar.getTimeInMillis() - 1000L * 60L * 60L * 24L * 1L);   // 1 days  
        System.out.println("since: " + daysAgo);

        try
	{			
            FacebookClient fClient = new DefaultFacebookClient(accessToken);

            for ( String grId : groups )
            {
            	Group grp = fClient.fetchObject(grId, Group.class);
                String groupLink = "";
	        if ( grp != null )
	        {
                    groupLink = grp.getLink();
System.out.println("====================================================");
System.out.println("Group: Name: " + grp.getName() + "\nType: " + grp.getType() 
+ "  Time: " + grp.getUpdatedTime() + "  Link: " + groupLink); 
// + "\nDescription: " + grp.getDescription() 

		    Connection<Post> grpFeed = fClient.fetchConnection(
			grId + "/feed", Post.class, Parameter.with("since", daysAgo));	
//			Parameter.with("limit", 100));
//YI - fix the limit
// Parameter.with("since", "yesterday"));
// selecting the fields
// Parameter.with("fields", "id, name"));
/* nested fields
GET graph.facebook.com
  /{node-id}?fields={first-level}.fields({second-level})
*/
		    List<Post> postList = grpFeed.getData();
		    ListIterator<Post> postIter = postList.listIterator();

		    while ( postIter.hasNext() )
                    {	
                        String contactInfo = "";
                        location = "";
                        String linkname = "";	
		        
                        Post post = postIter.next();
                        Date createDate = post.getCreatedTime();
                        postCal.setTime(createDate);
                        
                        String msg = post.getMessage();
                        if ( msg != null && msg.length() > 0 )
                        {
                            msg.replace("\n\n\n", "\n");
                            msg.replace("\n\n", "\n");
                            String name = post.getFrom().getName();
                            String userId = post.getFrom().getId();
                            User user = fClient.fetchObject(userId, User.class);

                            if ( user != null )
                            {
                                if ( user.getEmail() != null )
                                    contactInfo = contactInfo + "Email: " + user.getEmail();

                                String link =  user.getLink(); 
                                if ( link != null )
                                {   
                                    linkname = link.substring(25);
                                    contactInfo = contactInfo + " Link: " + link;
                                }
 
                                if ( user.getLocation() != null )
                                    location = " Location: " + user.getLocation();
                                if ( user.getWebsite() != null )
                                    contactInfo = contactInfo + " Website: " + user.getWebsite();
                                
                                String username = user.getUsername();
                                if ( username != null && !username.equals(linkname) )
                                    contactInfo = contactInfo + " Username: " + username + " vs. " + linkname;
                            }

                            System.out.println("-------------------------------------");
                            System.out.println(
"Post: From: " + userId + " " + name + " " + contactInfo.trim() + location + 
" Create Time: " + post.getCreatedTime() + " Update Time: " + post.getUpdatedTime());
System.out.println("Message: " + msg);

                            Long commCount = post.getLikesCount(); //should be get comments count
                            if (commCount != null) {
                            if (commCount > 0) 
                            {
                                List<Comment> commList = post.getComments().getData();
                                System.out.println("\nComments: " + commCount + " ? null or bug? \n");
                           }
                            }

//              System.out.println("First post in group feed: " + postList.get(0));                                                       
                          // finalMsg = 

                          //   sendData(finalMsg+ location + contact+ group name + group link);

                        }  // end-if (msg != null)                   
                    }   // loop for posts

                    System.out.println(grp.getName() + " # of group posts: " + postList.size());

                }   // endif ( grp != null )
            }   // loop for groups   

   /*         
            System.out.println("============!!!!!!!!!!============================");
            
            //why check ShadowTravelandTours separately?
            String pageId = "151833271525800"; //https://www.facebook.com/shadowtravelandtours/
            Page pg = fClient.fetchObject(pageId, Page.class);            
            if ( pg != null )
            {
            	
           System.out.println("Group: Name: " + pg.getName() + "\nType: " + pg.getType() 
           + "  Link: " + pg.getLink());
            	
System.out.println("Page: Link: " + pg.getLink() + " Phone: " + pg.getPhone() 
+ " Location: " + pg.getLocation() + " About: " + pg.getAbout() );
 
                Connection<Post> pageFeed = fClient.fetchConnection(
                        pageId + "/feed", Post.class, Parameter.with("since", daysAgo));
                 
                List<Post> postList = pageFeed.getData();
                ListIterator<Post> postIter = postList.listIterator();

                    while ( postIter.hasNext() )
                    {
                        String contactInfo = "";
                        location = "";
                        String linkname = "";

                        Post post = postIter.next();
                        Date createDate = post.getCreatedTime();
                        postCal.setTime(createDate);
                        String msg = post.getMessage();
                         
                        if ( msg != null && msg.length() > 0 )
                        {
                            msg.replace("\n\n\n", "\n");
                            msg.replace("\n\n", "\n");
                            int len = msg.length();
                            if ( len > MAX_MSG_LEN + 600 )
                            {
                                int pos = msg.indexOf('\n', MAX_MSG_LEN); 
                                msg = msg.substring(0, pos-1) + " ... ";
                            } 

                            String name = post.getFrom().getName();
                            String userId = post.getFrom().getId();
                            User user = fClient.fetchObject(userId, User.class);

                            if ( user != null )
                            {
                                if ( user.getEmail()!= null )
                                    contactInfo = contactInfo + "Email: " + user.getEmail();

                                String link =  user.getLink();
                                if ( link != null )
                                {
                                    linkname = link.substring(25);
                                    contactInfo = contactInfo + " Link: " + link;
                                }

                                if ( user.getLocation() != null )
                                    location = " Location: " + user.getLocation();
                                if ( user.getWebsite() != null )
                                    contactInfo = contactInfo + " Website: " + user.getWebsite();

                                String username = user.getUsername();
                                if ( username != null && !username.equals(linkname) )
                                    contactInfo = contactInfo + " Username: " + username + " vs. " + linkname;
                            }
System.out.println(
"\nPOST:  From: " + userId + " " + name + " " + contactInfo.trim() + location +
" Create Time: " + post.getCreatedTime() + " Update Time: " + post.getUpdatedTime());
System.out.println("Message: new len = " + msg.length() + " " + msg);

                            Long commCount = post.getLikesCount();
                            
                            if (commCount != null) {
                            if ( commCount > 0 )
                            {
                                List<Comment> commList = post.getComments().getData();
                                System.out.println("\nComments: " + commCount + " ? null or bug? \n");
                            }
                            }
                        }   // endif ( msg != null )
                    }   // loop for posts

                    System.out.println(pageId + " # of page posts: " + postList.size());

            }    // endif ( pg != null)

// Check my ID https://www.facebook.com/profile.php?id=100004121747971
		*/
        }
	catch (Exception ex) {
		ex.printStackTrace();
	}
    }

// YI - should we use the location field for group e-mail address, e.g., 5694188393@groups.facebook.com *
// Include group name and email into message
	public void sendData(String text)
	{
		String ipString = "http://198.100.45.94:8082";
		HttpPost httppost = new HttpPost(ipString);
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.putOpt("user", "user");
			jsonObject.putOpt("txt", text);
			jsonObject.putOpt("location", "location");
			jsonObject.putOpt("contact", "email");
			jsonObject.putOpt("pref", "pref");
                        // YI jsonObject.putObject("interest", "travel"); this may not work 

			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

			ClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
			HttpClient httpclient = new DefaultHttpClient(cm);
			HttpParams params = httpclient.getParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

			//HttpConnectionParams.setConnectionTimeout(params, 60);
			//HttpConnectionParams.setSoTimeout(params, 4);
			HttpConnectionParams.setTcpNoDelay(params, true);

			JSONObject finalObject = new JSONObject();

			finalObject.put("facebook", jsonObject);

			StringEntity entity = new StringEntity(finalObject.toString(), "UTF-8");
//			System.out.println(finalObject.toString());
			httppost.setEntity(entity);
/* DBG
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity responseEntity = response.getEntity();

			System.out.println(response.getStatusLine());
			
			EntityUtils.consume(responseEntity);
*/
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}