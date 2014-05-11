import twitter4j.conf.*;
import twitter4j.*;
import twitter4j.auth.*;
import twitter4j.api.*;
import java.util.*;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.io.BufferedWriter;
import java.io.FileWriter;


//create twitter object
Twitter twitter;

//create array list to store tweets
ArrayList<Status> tweets;
int currentTweet;

//make arraylist for each column header
ArrayList userName;
ArrayList geoLoc;

//printwriter object type output
PrintWriter output;

//create array list for locations
ArrayList<GeoLocation> geolocations;

int count = 100;

String user = "username";  //insert database username
String pass = "password";  //insert database password

String path = "file path to save .tsv to";

//set the stage for tweets to be displayed
void setup() {
  size(800, 600);
  background(0);
  //instantiate arrylist for geolocations
  geolocations = new ArrayList<GeoLocation>();

  // add new location - london
  //change Paris, New York and Munich using specific coordinates obtained from http://www.latlong.net/
  geolocations.add(new GeoLocation(51.511214, -0.119824));  

  //Twitter auth credentials 
  //tutorial followed; method and source code adapted from (Stark, 2013) http://codasign.com/tutorials/processing-and-twitter-searching-twitter-for-tweets/
  //Twitter authorisation credentials obtained from Twitter.dev
  ConfigurationBuilder cb = new ConfigurationBuilder();
  cb.setOAuthConsumerKey("**************");
  cb.setOAuthConsumerSecret("****************************");
  cb.setOAuthAccessToken("********************");
  cb.setOAuthAccessTokenSecret("*****************");

  TwitterFactory tf = new TwitterFactory(cb.build());
  twitter = tf.getInstance();

  //creates new array list for tweets
  tweets = new ArrayList<Status>();

  //loops through list of geos using it as a parameter to be call getNewTweets method
  for (GeoLocation geo : geolocations) 
  {
    getNewTweets((GeoLocation)geo);
  }
  //sets current tweet count to 0
  currentTweet = 0 ;

  //randomise list of tweets
  Collections.shuffle(tweets);
  thread("refreshTweets"); 
}

void draw()
{
  //background colour and size
  fill(0, 40);
  rect(0, 0, width, height);

  //increment current tweet method - moves to next tweet
   //tutorial followed; method and source code adapted from (Stark, 2013) http://codasign.com/tutorials/processing-and-twitter-searching-twitter-for-tweets/
  currentTweet = currentTweet + 1;

  if (currentTweet >= tweets.size())
  {
    currentTweet = 0;
  }

  if (tweets.size() > 0)
  {
    //put current tweets in status object
    Status status = tweets.get(currentTweet);
    fill(200);
    text("@" + status.getUser().getScreenName() + "\n" +status.getGeoLocation(), random(width-300), random(height-200), 300, 200);

  }
  delay (100);
}

void getNewTweets(GeoLocation geo) {
  try
  { 
      //tutorial followed; method and source code adapted from (Stark, 2013) http://codasign.com/tutorials/processing-and-twitter-searching-twitter-for-tweets/
    Query query = new Query("( )").geoCode(geo, 5, "mi");  //search for tweets containing a space
    query.setCount(100);  //get 100 tweets
    println(query);
    QueryResult result = twitter.search(query);  

    //for loop - add tweets to status object whilst there are new tweets 
    int i=0;
    for (Status status: result.getTweets())
    {
      tweets.add(status);
      i++;    
     println(i); 
    }
  }
  //catch exceptions - display error message
  catch (TwitterException te)
  {
    System.out.println("Could not connect: " + te.getMessage());
    System.exit(-1);
  }
}

void refreshTweets()
{
  //refresh tweets
  while (true)
  {
    tweets.removeAll(tweets);

    for (GeoLocation geo : geolocations) 
    {
      getNewTweets((GeoLocation)geo);
    }
    println("Updated Tweets");
    //shuffle order tweets are recieved in
    Collections.shuffle(tweets);
    delay(300000);
  }
}

void jTweet () {

  for (int i =0; i<tweets.size(); i++) {
    //put current tweets in status object
    Status status = tweets.get(i);    
    if (status.getGeoLocation() != null){
    //output.println(status.getUser().getScreenName() +  status.getGeoLocation());
    String screenName = status.getUser().getScreenName();
    double latitude = status.getGeoLocation().getLatitude();
    double longitude = status.getGeoLocation().getLongitude();
    appendTextToFile(path, screenName, latitude, longitude);
    }
  }
}

void keyPressed() {

  if (key == 'j') {
    jTweet();
    
    stop();
  }
}

void appendTextToFile(String path, String uname, double latitude, double longitude){
   File f = new File(dataPath(path));
    try {
    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));
    out.print(uname);
    out.print('\t');
    out.print(latitude);
    out.print('\t');
    out.println(longitude);
    out.close();
  }catch (IOException e){
      e.printStackTrace();
  }
}
