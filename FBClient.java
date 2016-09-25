import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Comment;
import com.restfb.types.Group;
import com.restfb.types.Post;
import com.restfb.types.User;

public class FBClient 
{
    private static final int MAX_MSG_LEN = 2000;
	private static final String MY_APP_ID = "989545354463165"; //	App ID 989545354463165
	private static final String MY_APP_SECRET = "6d322af714205dd34883bc16148099ef"; //6d322af714205dd34883bc16148099ef

	//my own token
	private static String accessToken = "EAACEdEose0cBALRpsZBqKHTtkwZAEZCBHZBRZBAKroLD0bwbleIziDXGXHeak3N3HCwK5SBIdJTcPSdZCSZCYOYt26DrUptRZCV9nlAHyKcFqPH1H4YsEhX1qGZA9tv9N5CQCod3WnXtTeVchXyXGdPindtwlsSfP00hEoAqtPKqguAZDZD";
    private static GregorianCalendar currentCalendar = new GregorianCalendar();
    private static ArrayList<String> groups = new ArrayList<String>();

    public static void main(String args[]) 
    {
    groups.add(new String("10817829186"));      // Tourism Around The World (1)

	FBClient fbClient = new FBClient();		
	fbClient.postQuery(args);
    }	      	

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

		    Connection<Post> grpFeed = fClient.fetchConnection(
			grId + "/feed", Post.class, Parameter.with("since", daysAgo));	

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

                        }  // end-if (msg != null)                   
                    }   // loop for posts

                    System.out.println(grp.getName() + " # of group posts: " + postList.size());

                }   // endif ( grp != null )
            }   // loop for groups   
        }
	catch (Exception ex) {
		ex.printStackTrace();
	}
    }
}