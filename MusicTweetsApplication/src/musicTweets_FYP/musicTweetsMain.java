package musicTweets_FYP;

import twitter4j.conf.*;
import twitter4j.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;

import processing.core.*;
import processing.data.Table;
import de.fhpotsdam.unfolding.*;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.*;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.marker.*;

import org.gicentre.utils.stat.*;
import org.gicentre.handy.*;
import org.gicentre.utils.colour.ColourTable;
import org.gicentre.utils.gui.*;
import org.gicentre.utils.move.*;


public class musicTweetsMain extends PApplet {

	public musicTweetsMain() {
		
	}
	  private Twitter twitter;  //instantiate new Twitter object
	  ZoomPan zoomer;	//declare zoomer for zooming functionality on wordle
	  Tooltip tooltip;	//declare tooltip for mouse over functionality on barcharts and markers
	  PImage wordleMusic, ldnWordle, parWordle, nyWordle, munWordle;	//declare pimage to display wordle
	  HandyRenderer h, h1, h2;	 //declare handy renderer for rects
	  HandyRenderer hLondon, hParis, hNewYork, hMunich;		//declare handy renderer for borders for maps
	  BarChart barChart1, barChart2, barChart3, barChart4, barChartLdn, barChartPar, barChartNY, barChartMun;	//declare BarCharts 
	  UnfoldingMap mapLondon, mapLondonOnly, mapParis, mapNewYork, mapMunich, mapLondonBar,mapParisBar, mapNewYorkBar, mapMunichBar;	  //declare unfolding maps 
	 
	  ArrayList<Square> squares = new ArrayList<Square>();	//create new arraylist of type square - stores squares for londonMode
	  ArrayList<Tweet> londonOnlyTweets = new ArrayList<Tweet>();	//create new arraylist of type tweet - stores tweets for londonMode
	  private ArrayList<Status> raw_tweets; //create arraylist of type status to store tweets grabbed from twitter but not stored
	  private ArrayList<GeoLocation> geolocations;	 //create arraylist of type locations to store locations  of cities
	  private ArrayList<Tweet> incoming_tweets = new ArrayList<Tweet>();	//create arraylist of type tweet to store tweets that are being stored from twitter in real time
	  private ArrayList<Tweet> database_tweets = new ArrayList<Tweet>();	//create arraylist of type tweet to store tweets grabbed from the database to plot
	  private ArrayList<Tweet> tweets = new ArrayList<Tweet>(); //create arraylist of type tweet to store tweet information
	 
	  private int cT;	//int variable to store current tweet
	  private int z = 0;	//set z(minutes) variable = 0 
	  private int nyTweetCount, ldnTweetCount, parTweetCount, munTweetCount; //int variables to store total tweet counts for each city
	  private int london, paris, new_york, munich;	//int variables to store values for all cities
	  int n = 10;	//set size of grid to 10 for use in londonMode
	  private int itemToHighlight;	//store itemtohighlight as int value - holds value of -1 at start up
	  private int ldn, par, ny, mun; 	//store integer values for london paris new york and munich
	  
	  private boolean set_densities = false;		//set set_densities variable to false - variable used to set the density of squares in londonMode
	  private boolean dragging = false;	//set dragging variable to false on start up
	  private boolean fourMaps = true;	//set fourmaps variable to true on start up to display all four maps
	  private boolean labelsDisp = true;	//set labelsDisp variable to true on start up to display all labels for maps
	  private boolean mapTweets = true;	//set maptweets variable to true on start up so tweets can be plotted onto maps
	  private boolean barChartAll = false;	//set barchartsall to false on start up so barcharts are not displayed
	  private boolean cityBars = false;	//set citybarcharts to false on start up so barcharts are not displayed
	  private boolean wordle = false;	//set wordle to false on start up so wordle is not displayed
	  private boolean large = false;		//set large to false on start up - used to set size of londonMode grid
	  private boolean medium = false;	//set medium to false on start up - used to set size of londonMode grid
	  private boolean small = false;		//set small to false on start up - used to set size of londonMode grid
	  private boolean londonMode = false;	//set londonMode to false on start up - start up on normal mode
	  private boolean offset_locations_ny, offset_locations_london, offset_locations_paris, offset_locations_munich = false;	//set offset locations to false - only used when all 
	  private boolean artist = false;
	  private boolean storeLabel = false;	//set tweets saved to database label to false on start up
	  private boolean londonWordle, parisWordle, newYorkWordle, munichWordle = false; 	//set london wordle's visibility to false
	  
	  private float countPop, countHipHop, countDance, countCountry, countRock, countAlt, countRNB, count90, count80, countIndie, countRap;	//genre variables for count used in barchart store as floats
	  private float countMusicLdn, countMusicPar, countMusicNY, countMusicMun;	//floats storing counts of the word 'music' mentioned in tweets from the four cities
	  private float eminem, avicii, katyPerry, lorde, oneDirection, beyonce, imagineDragons, drake, pitbull, rihanna;
	  private float countPopLdn, countHipHopLdn, countDanceLdn, countCountryLdn, countRockLdn, countAltLdn, countRNBLdn, count90Ldn, count80Ldn, countIndieLdn, countRapLdn;	//floats to store counts of tweets about each genre from London
	  private float countPopPar, countHipHopPar, countDancePar, countCountryPar, countRockPar, countAltPar, countRNBPar, count90Par, count80Par, countIndiePar, countRapPar;	//floats to store counts of tweets about each genre from Paris
	  private float countPopNY, countHipHopNY, countDanceNY, countCountryNY, countRockNY, countAltNY, countRNBNY, count90NY, count80NY, countIndieNY, countRapNY;	//floats to store counts of tweets about each genre from New York
	  private float countPopMun, countHipHopMun, countDanceMun, countCountryMun, countRockMun, countAltMun, countRNBMun, count90Mun, count80Mun, countIndieMun, countRapMun;	//floats to store counts of tweets about each genre from Munich
	
	  private ColourTable barColourTable = new ColourTable();	//create new bar colour table for barchart1, barchart2, barchart3, barchart4
	  private ColourTable barColourTableLdn = new ColourTable();	//create new bar colour table for barchartLdn
	  private ColourTable barColourTablePar = new ColourTable();	//create new bar colour table for barchartPar
	  private ColourTable barColourTableNy = new ColourTable();	//create new bar colour table for barchartNy
	  private ColourTable barColourTableMun = new ColourTable();	//create new bar colour table for barchartMun
	
	  private Table nyTable, ldnTable, parTable, munTable, ldnOnlyTable;	//declare tables as 
	
	  private String tweet_text;	//store text from tweets in londonMode that mention a listed genre
	
	 // private float[] cityMusic;	//store citymusic as array of floats for use in pie chart	
	  private int[] artists;
	  private float[] data;	//store data sets as array of floats for use in barcharts
	  private float[] xPosition;	//store xposition as array of floats for use in londonMode for labels
	  private float[] yPosition;	//store yposition as array of floats for use in londonMode for labels
	  float[] barColourData = {countPop, countHipHop, countDance, countCountry, countRock, countAlt, countRNB, count90, count80, countIndie, countRap};	//add variables to colour table for bar charts
	  float[] cityMusic = {countMusicLdn, countMusicPar, countMusicNY, countMusicMun};
	  private String[] londonText;	//store genres in a string array for use in londonMode
	  private int[] londonColour;	//store colours as int array for use in londonMode to change fill of grid squares
	  private String[] artistText; //stores genres in a string array for use in normal mode
	  private float[] xPosArtist;
	  private float[] yPosArtist;
	 
	  PFont smallFont = loadFont("Gungsuh-13.vlw");	//load small font from data file
	  PFont largeFont = loadFont("Gungsuh-30.vlw");	//load large font from data file
	  
	  public void setup() {
	    size(800, 1500, P2D);	//set the size of the window for which the application will be displayed
	    smooth();	
	    zoomer = new ZoomPan(this);	//initialise zoomer for us in the wordle
	    itemToHighlight = -1;	//set item to highlight as -1 used in londonMode to check whether item in array has been clicked. Array starts at 0
	    
	    //declare all locations for London, Paris, New York and Munich using specific coordinates obtained from http://www.latlong.net/
	    //paste in latitude and longitude values for required cities
	    Location londonLocation = new Location(51.511214, -0.119824);	
	    Location parisLocation = new Location(48.856614, 2.352222);
	    Location newYorkLocation = new Location(40.714353, -74.005973);
	    Location munichLocation = new Location(48.1333, 11.5667);
	   
	    //code modified from tutorial (Nagel, 2013) http://unfoldingmaps.org/tutorials/multi-maps.html
	    //initialise four unfolding maps, set position and type of map to be used in fourmaps mode
	    mapLondon = new UnfoldingMap(this, "mapLondon", 0,0, 295,295, true, false, new StamenMapProvider.TonerLite());
	    mapParis = new UnfoldingMap(this, "mapParis", 295, 0, 295, 295, true, false, new StamenMapProvider.TonerLite());
	    mapNewYork = new UnfoldingMap(this, "mapNewYork", 0, 295, 295, 295, true, false, new StamenMapProvider.TonerLite());
	    mapMunich = new UnfoldingMap(this, "mapMunich", 295, 295, 295, 295, true, false, new StamenMapProvider.TonerLite());
	    MapUtils.createDefaultEventDispatcher(this, mapLondon, mapParis, mapNewYork, mapMunich);
	    
	    //code modified from tutorial (Nagel, 2013) http://unfoldingmaps.org/tutorials/multi-maps.html
	    //initialise unfolding maps for when application is to display barcharts
	    mapLondonBar = new UnfoldingMap(this, "mapLondon", 0,0, 295,295, true, false, new StamenMapProvider.TonerLite());
	    mapParisBar = new UnfoldingMap(this, "mapParis", 295, 0, 295, 295, true, false, new StamenMapProvider.TonerLite());
	    mapNewYorkBar = new UnfoldingMap(this, "mapNewYork", 0, 295, 295, 295, true, false, new StamenMapProvider.TonerLite());
	    mapMunichBar = new UnfoldingMap(this, "mapMunich", 295, 295, 295, 295, true, false, new StamenMapProvider.TonerLite());
	    MapUtils.createDefaultEventDispatcher(this, mapLondonBar, mapParisBar, mapNewYorkBar, mapMunichBar);
	    
	    //initialise londonOnly unfolding map for use in londonOnly mode 
	    //code modified from tutorial (Nagel, 2013) http://unfoldingmaps.org/tutorials/multi-maps.html
	    mapLondonOnly = new UnfoldingMap(this, "mapLondonOnly", 0, 0, 590, 590, true, false, new StamenMapProvider.TonerLite());
	    MapUtils.createDefaultEventDispatcher(this, mapLondonOnly);
	  
	    //set zoom on initialisation of maps
	    //code modified from tutorial (Nagel, 2013) http://unfoldingmaps.org/tutorials/multi-maps.html
	    mapLondon.zoomAndPanTo(londonLocation, 11);
	    mapParis.zoomAndPanTo(parisLocation, 11);
	    mapNewYork.zoomAndPanTo(newYorkLocation, 11);
	    mapMunich.zoomAndPanTo(munichLocation, 11);
	    mapLondonOnly.zoomAndPanTo(londonLocation, 13);
	    mapLondonBar.zoomAndPanTo(londonLocation, 11);
	    mapParisBar.zoomAndPanTo(parisLocation, 11);
	    mapNewYorkBar.zoomAndPanTo(newYorkLocation, 11);
	    mapMunichBar.zoomAndPanTo(munichLocation, 11);
	    
	    //allow for tweening on all maps
	    mapLondon.setTweening(true);    
	    mapParis.setTweening(true);    
	    mapNewYork.setTweening(true);    
	    mapMunich.setTweening(true);
	        
	    //instantiate arraylist for geolocations
	    geolocations = new ArrayList<GeoLocation>();
	    geolocations.add(new GeoLocation(51.511214, -0.119824));	// add new location - London
	    geolocations.add(new GeoLocation(48.856614, 2.352222));		// add new location - Paris
	    geolocations.add(new GeoLocation(40.714353, -74.005973));	//add new location - New York 
	    geolocations.add(new GeoLocation(48.1333, 11.5667));		//add new location - Munich

	    //tutorial followed; method and source code adapted from (Stark, 2013) http://codasign.com/tutorials/processing-and-twitter-searching-twitter-for-tweets/
	    //Twitter authorisation credentials obtained from Twitter.dev
	    ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setOAuthConsumerKey("************");
	    cb.setOAuthConsumerSecret("*********************");
	    cb.setOAuthAccessToken("***************************");
	    cb.setOAuthAccessTokenSecret("*********************************");

	    
	    //new instance of twitter factory
	  //tutorial followed, method and source code adapted from (Stark, 2013) http://codasign.com/tutorials/processing-and-twitter-searching-twitter-for-tweets/
	    TwitterFactory tf = new TwitterFactory(cb.build());
	    twitter = tf.getInstance();

	    raw_tweets = new ArrayList<Status>();	//creates new array list for raw tweets
	        
	    //loops through list of geos using it as a parameter to be call getNewTweets method, for each geo in geolocations
	  //tutorial followed, method and source code adapted from (Stark, 2013) http://codasign.com/tutorials/processing-and-twitter-searching-twitter-for-tweets/
	    for (GeoLocation geo : geolocations) 	
	    {
	      getNewTweets((GeoLocation)geo);	
	    }
	    
	    //sets current tweet count to 0

	    //randomise list of tweets
	    //tutorial followed, method and source code adapted from (Stark, 2013) http://codasign.com/tutorials/processing-and-twitter-searching-twitter-for-tweets/
	    Collections.shuffle(raw_tweets);
	    thread("refreshTweets");   
	
	    //set handy renderer detail
	    h = new HandyRenderer(this);
	    h1 = HandyPresets.createPencil(this);
	    h1.setSeed(4000);
	    h1.setFillWeight(1);
	    h2 = HandyPresets.createWaterAndInk(this);
	    h2.setSeed(4000);
	    
	    //load all tables from hypothetical locations pde - .tsv files contain geo locations for markers. Change NAMEOFTABLE to reflect tsv name
	    println("Start loading tables at : " + System.currentTimeMillis()/1000);
	    nyTable = loadTable("NAMEOFFILE.tsv", "tsv");
	    ldnTable = loadTable("NAMEOFFILE.tsv", "tsv");
	    parTable = loadTable("NAMEOFFILE.tsv", "tsv");
	    munTable = loadTable("NAMEOFTABLE.tsv", "tsv");	 
	    println("Start loading London only table at : " + System.currentTimeMillis()/1000);
	    //load in london(or any city) music file - used for inter city music culture exploration
	    ldnOnlyTable = loadTable("NAMEOFTABLE.txt", "header, tsv");
	    println("London Only table loaded at : " + System.currentTimeMillis()/1000);
	    println("Tables loaded at : " + System.currentTimeMillis()/1000);
	   
	    initiateSquares();	//initiate squares for londonMode
   	 	for(int i = 0; i < ldnOnlyTable.getRowCount() -1 ; i++) {
			 check_point_in_square(new Location(ldnOnlyTable.getFloat(i,"LATITUDE"), (ldnOnlyTable.getFloat(i, "LONGITUDE"))));
			 tweet_text = ldnOnlyTable.getString(i, "MESSAGETEXT");		
	    }
	    
   	 	//style tooltip
   	 	//code modified from Data visualisation, Session 5: Building Interaction (Wood, 2014) -  http://gicentre.org/datavis/session05/index.html
	    tooltip = new Tooltip(this, loadFont("Gungsuh-13.vlw"), 10, 150);
	    tooltip.setIsCurved(true);
	    tooltip.setBorderWidth(2);
	    
	    wordleMusic = loadImage("musicWordle.png");	//load image from data file - all tweets about music
	    ldnWordle = loadImage("londonWordle.png"); 	//load image from data file - tweets from london
	    parWordle = loadImage("parisWordle.png");	//load image from data file - tweets from paris
	    nyWordle = loadImage("newYorkWordle.png"); //load image from data file - tweets from new york
	    munWordle = loadImage("munichWordle.png");	//load image from data file - tweets from munich
	    getArtistCount();		//call getArtistCount method
	    
	  }
	  public void draw()
	  {  
		//  tooltip.setIsActive(false);
	    background(255, 0);
	    if (wordle){
	    	zoomer.transform();
	    }
	  	    
	    if (fourMaps){
	    //draw 4 maps  
		  mapLondon.draw();
		  mapParis.draw();
		  mapNewYork.draw();
		  mapMunich.draw();
		  noFill();			
			h2.setSeed(1234);	//style handy renderer - makes border seem solid
			h2.rect(0, 0, 295, 295);	//rectangle border for londonMap
			h2.rect(0,295,295, 295);	//rectangle border for parisMap
			h2.rect(295, 295, 295, 295);	//rectangle border for newYorkMap
			h2.rect(295, 0, 295, 295);	//rectanble border for munichMap
			h2.setFillWeight(0);
			h2.setFillColour(255);
			h2.setRoughness(1);
			h2.setSeed(1234);
	    }else if (cityBars){	
	    		//if city bars is true, draw unfodlingmaps
			  mapLondonBar.draw();
			  mapParisBar.draw();
			  mapNewYorkBar.draw();
			  mapMunichBar.draw();
			  noFill();			
				h2.setSeed(1234);
				h2.rect(0, 0, 295, 295);
				h2.rect(0,295,295, 295);
				h2.rect(295, 295, 295, 295);
				h2.rect(295, 0, 295, 295);
				h2.setFillWeight(0);
				h2.setFillColour(255);
				h2.setRoughness(1);
				h2.setSeed(1234);
					
			} else{
	    	londonMode = true;
	    	londonMode();
	    }
	    
	    if (labelsDisp){
	    //if labelsDisp is true display city labels
		textFont(largeFont);
		fill(255, 0,0, 255);
		text("London ", 5, 285);
		text("Paris  " , 300, 285);
		text("New York  " , 5, 580);
		text("Munich " , 300, 580);
		
		}
	  
	    cT = cT + 1;	//increment current tweet method - moves to next tweet
	    
	    //if current tweets count is greater than or equal to the size of the raw_tweets array set cT to 0
	    if (cT >= raw_tweets.size()){
	      cT = 0;
	    }
	    
	    if (raw_tweets.size() > 0){	//if size of the tweets array is greater than 0
	      Status raw_tweet = raw_tweets.get(cT);   //put current tweets in raw_tweets arraylist
	      try {
	        Tweet tweetData;        //create tweet object
	        tweetData = new Tweet(new Location(0,0));	 // Tweet requires a location for the constructor because it extends abstract marker but at this point a location is not needed so we create it with a location of 0 0 
	        //add values to items in tweetData with values obtained from raw_tweets
	        tweetData.setTweetId(raw_tweet.getUser().getId());
	        tweetData.setScreenName(raw_tweet.getUser().getScreenName()); 
	        tweetData.setText(raw_tweet.getText());
	        tweetData.setSource(raw_tweet.getSource());
	        tweetData.setTimeZone(raw_tweet.getUser().getTimeZone());
	        Timestamp ts = new Timestamp(raw_tweet.getCreatedAt().getTime());
	        tweetData.setCreatedAt(ts);      
	        
	        //if loops - if timezone stored about the current tweet in the raw_tweets arraylist matches any of the below
	        //set screen location of ellipse
	        //add tweet data to incoming_tweets arrayList
	        if (raw_tweets.get(cT).getUser().getTimeZone().equals("Paris")){ 
	          incoming_tweets.add(tweetData);	//add tweet to incoming tweets array
	        }else if (raw_tweets.get(cT).getUser().getTimeZone().equals("London")){
	         incoming_tweets.add(tweetData);	//add tweet to incoming tweets array
	        }else if (raw_tweets.get(cT).getUser().getTimeZone().equals("Berlin")){
	         incoming_tweets.add(tweetData);	//add tweet to incoming tweets array
	        }else if (raw_tweets.get(cT).getUser().getTimeZone().equals("Eastern Time (US & Canada)")){
	        	incoming_tweets.add(tweetData);	//add tweet to incoming tweets array
	        }
	      
	      } catch (Exception e) {
	    }
	  }
	    if (fourMaps){
			  //call plot tweets function
		    plotTweets();
			}
		    h1.setFillColour(0);
		    h1.setRoughness(1);
		    h1.setSeed(10);
		    rectMode(CORNER);
	  
		if (cityBars) {
			mapTweets = false;
			//followed tutorial and modified code (Wood, 2013) from  http://www.gicentre.net/utils/chart/
			barChartLdn.draw(0,0,295, 295); 
			barChartPar.draw(295, 0, 295, 295);
			barChartNY.draw(0, 295, 295, 295);
			barChartMun.draw(295, 295, 295,295);
		
			if (barChartAll){
			 barChart1.draw(0, 0, 295, 295);	//draw London total count bar chart
			 float temp[] = barChart1.getData();	// set temp to current data of bar chart during animation
			 for(int i = 0; i < data.length; i++) {	 // go through all the items pulled out in data (the final values)
				 // if the current data of bar chart is less then the final value
				 if (data[i] != barChart1.getData()[i]) {
					 temp[i] = barChart1.getData()[i] + 1;	 // increment temporary value - looks as if bar is growing
				 }
			 }
			 
			 barChart2.draw(0, 295, 295, 295);	//draw New York total count bar chart
			 barChart2.setData(temp);	//set_densities temp to current data of bar chart during animation
			 for(int i = 0; i < data.length; i++) {	// go through all the items that we pulled out in data (the final values)
				 if (data[i] != barChart2.getData()[i]) {	 // if the current data of bar chart is less then the final value
					temp[i] = barChart2.getData()[i] + 1;	 // increment temporary value - looks as if bar is growing
				 }
			 }
			 
			 barChart3.draw(295, 0, 295, 295);	//draw Paris total count bar chart
			 barChart3.setData(temp);	//set_densities temp to current data of bar chart during animation
			 for(int i = 0; i < data.length; i++) {	// go through all the items that we pulled out in data (the final values)
				 if (data[i] != barChart3.getData()[i]) {	 // if the current data of bar chart is less then the final value
					 temp[i] = barChart3.getData()[i] + 1;	// increment temporary value - looks as if bar is growing
				 }
			 }
			 
			 barChart4.draw(295, 295, 295, 295);	//draw Munich total count bar chart
			 barChart4.setData(temp); //set_densities temp to current data of bar chart during animation
			 for(int i = 0; i < data.length; i++) {		 // go through all the items that we pulled out in data (the final values)
				 if (data[i] != barChart4.getData()[i]) { // if the current data of bar chart is less then the final value
					 temp[i] = barChart4.getData()[i] + 1;	// increment temporary value - looks as if bar is growing
				 }
			 }
		}

	}
		
		if (wordle){	//display music wordle - containing 150 most common words associated with tweets about music - the obvious phrase 'music'
			rectMode(CORNERS);
			image(wordleMusic, 0, 0, width-5, height-100);
			textFont(smallFont);
			fill(255, 0, 0);
			//keypress instructions
			text("London Wordle: Press 1", 0, 640);
			text("Paris Wordle: Press 2", 200, 640);
			text("New York Wordle: Press 3", 400, 640);
			text("Munich Wordle: Press 4", 625, 640);	
		}
	
		
		if (londonWordle){	//display music wordle - containing 150 most common words associated with tweets about music - the obvious phrase 'music'
			rectMode(CORNERS);
			image(ldnWordle, 0, 0, width-5, height-100);
			textFont(smallFont);
			fill(255, 0, 0);
			//keypress instructions
			text("Music Wordle: Press W", 0, 640);
			text("Paris Wordle: Press 2", 190, 640);
			text("New York Wordle: Press 3", 370, 640);
			text("Munich Wordle: Press 4", 620, 640);	
		}
		
		if (parisWordle){	//display music wordle - containing 150 most common words associated with tweets about music - the obvious phrase 'music'
			rectMode(CORNERS);
			image(parWordle, 0, 0, width-5, height-100);
			textFont(smallFont);
			fill(255, 0, 0);
			//keypress instructions
			text("Music Wordle: Press W", 0, 640);
			text("London Wordle: Press 1", 190, 640);
			text("New York Wordle: Press 3", 370, 640);
			text("Munich Wordle: Press 4", 620, 640);	
		}
		
		if (newYorkWordle){	//display music wordle - containing 150 most common words associated with tweets about music - the obvious phrase 'music'
			rectMode(CORNERS);
			image(nyWordle, 0, 0, width-5, height-100);
			textFont(smallFont);
			fill(255, 0, 0);
			//keypress instructions
			text("Music Wordle: Press W", 0, 640);
			text("London Wordle: Press 1", 190, 640);
			text("Paris Wordle: Press 2", 380, 640);
			text("Munich Wordle: Press 4", 620, 640);	
		}
		
		if (munichWordle){	//display music wordle - containing 150 most common words associated with tweets about music - the obvious phrase 'music'
			rectMode(CORNERS);
			image(munWordle, 0, 0, width-5, height-100);
			textFont(smallFont);
			fill(255, 0, 0);
			//keypress instructions
			text("Music Wordle: Press W", 0, 640);
			text("London Wordle: Press 1", 200, 640);
			text("Paris Wordle: Press 2", 400, 640);
			text("New York Wordle: Press 4", 600, 640);	
		}
		
		
		
		//declare float array contents used in londonMode
		xPosition = new float[] {670, 670, 670, 670, 670, 670,670, 670, 670, 670, 670};
		yPosition = new float[] {50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550};
		londonText = new String[] {"Pop", "HipHop", "Dance","Country", "Rock", "Alternative", "RnB", "90's", "80's", "Indie", "Rap"};
		londonColour = new int[] {color(250, 30, 162), color(53, 205, 250), color(0, 224,1), color(13, 116,13), color(134, 0, 0), color(193, 185, 185), color(94, 0, 113),  color(210, 31, 234), color(255, 223, 3), color(113, 38, 3),color(25, 68, 222)};
		//londonColourClicked = new int[] {color(250, 30, 162), color(0,0,0), color(0, 224,1), color(13, 116,13), color(134, 0, 0), color(193, 185, 185), color(94, 0, 113),  color(210, 31, 234), color(255, 223, 3), color(113, 38, 3),color(25, 68, 222)};
		
		tooltip.draw(mouseX,mouseY);	//draw tooltip at mouse point
		//declare array contents used in normal mode to display artists and their mention counts
		artistText = new String[]{"Eminem", "Avicii", "Katy Perry", "Lorde", "One Direction", "Beyonce", "Imagine Dragons", "Drake", "Pitbull", "Rihanna"};
		xPosArtist = new float[]  {670, 670, 670, 670, 670, 670,670, 670, 670};
		yPosArtist = new float[]  {50, 100, 150, 200, 250, 300, 350, 400, 450, 500};
		int[] artistColour = {color(25, 68,222), color(0, 224, 1), color(250, 30, 162), color(250, 30, 162), color(250, 30, 162), color(94, 0,113), color(13, 38, 3), color(53, 205, 250), color(94, 0,113), color(94, 0,113)};
		int [] artists = {floor(eminem), floor(avicii), floor(katyPerry), floor(lorde), floor(oneDirection), floor(beyonce), floor(imagineDragons), floor(drake), floor(pitbull), floor(rihanna)};
		
		if(artist){
			getArtistCount();		//call getArtistCount method to obtain values 	
			for (int i=0; i<xPosArtist.length; i++){	//loop through array, if i is less than length of array...
				 noFill();
				 fill(artistColour[i]);	//set fill to [i] position in artist colour array
				 text(artistText[i] + " : " + artists[i],xPosArtist[i], yPosArtist[i]);  //write text from [i] position in artistText, xPosArtist and yPosArtist arrays
				}
		}
		
		if(storeLabel){
			//show database updated label
			textFont(smallFont);		//select small font
		      fill(255, 0,0);	//fill red
		      text("Database Updated!", 0, 605);
		}
	  }
	  private void getNewTweets(GeoLocation geo) {
		//tutorial followed, method and source code adapted from (Stark, 2013) http://codasign.com/tutorials/processing-and-twitter-searching-twitter-for-tweets/
		try
	    { 
	      //run query based on spotify ratings - using 2012 Spotify. (2012). Spotify Review of the Year. Available: https://www.spotify.com/uk/about-us/2012/. Last accessed 10th December 2013.
	      Query query = new Query("(music) OR (pop music) OR (hiphop) OR (dance music) OR (country music) OR (rock music) OR (alternative) OR"
	      		+ " (rnb) OR (90smusic) OR (80smusic) OR (indie) OR (rap) OR (Eminem) OR (Avicii) OR (Katy Perry) OR (Lorde) OR (One Direction)"
	      		+ " OR (Beyonce) OR (Imagine Dragons) OR (Drake) OR (Pitbull) OR (Rihanna)").geoCode(geo, 10, "mi");
	      query.setCount(100);	//allow for 100 tweets to be obtained from Twitter
	      println(query);
	      QueryResult result = twitter.search(query);    //add results to query

	      //for loop for Data type Status element status array result add tweets to status object whilst there are new tweets 
	      int i=0;
	      for (Status status: result.getTweets())
	      {
	        raw_tweets.add(status);	//add status items to raw_tweet arrayList
	        i++;	//increment count
	      }
	      println("i is\t"+i);    
	    }
	    catch (TwitterException te)	//catch exceptions - display error message
	    {
	      System.out.println("Could not connect: " + te.getMessage());
	      System.exit(-1);
	    }
	  }
	  public void refreshTweets()
	  {
	    //refresh tweets
	    while (true)
	    {
	      raw_tweets.removeAll(raw_tweets);	//remove all tweets from raw_tweets arrayList

	      for (GeoLocation geo : geolocations) 	//for each geo
	      { 
	        getNewTweets((GeoLocation)geo);	//get new tweets from GeoLocation
	      }
	      println("Updated Tweets");
	      Collections.shuffle(raw_tweets);	//shuffle order tweets are recieved in
	      delay(300000);	   //run every 300000 seconds
	      }
	  }

	  public void keyPressed()
	  {
	    //if j key is pressed call stop application
	    if (key == 'j') {
	      stop();
	      londonMode = false;
	      storeLabel = false;
	    }
	    
	  //if a key is pressed display artist data with four maps
	    if(key == 'a'){
	    	mapTweets = true;
	    	fourMaps = true;
	    	artist = true;
	    	storeLabel = false;
	    }
	    	    
	    //if s key is pressed print start information, call store data method and print end information
	    if (key == 's') {
	      storeData();
	      storeLabel = true;
	    }
	    //if p key is pressed, call plot tweets method
	    if (key == 'p')  {
	      plotTweets();     
	      londonMode = false;
	      storeLabel = false;
	    }
	   
	    //if l is pressed invoke london mode - remove four maps, set grid to small and call londonMode
	    if (key == 'l'){
	    	itemToHighlight = -1;
	    	labelsDisp = false;
	    	small = true;
	    	medium = false;
	    	large = false;
	    	storeLabel = false;
	    	setGridSmall();
	    	fourMaps = false;
	    	if (small){
	    	initiateSquares();	//initiate squares method styles the gri
	    	 for(int i = 0; i < ldnOnlyTable.getRowCount() -1 ; i++) {	//for every element in londonOnlyTable loop through
				 check_point_in_square(new Location(ldnOnlyTable.getFloat(i,"LATITUDE"), (ldnOnlyTable.getFloat(i, "LONGITUDE"))));	//call check_point_in_squares method and parse lat and long values from table into method
				 tweet_text = ldnOnlyTable.getString(i, "MESSAGETEXT");	//get message text(text of tweet) from position i and add it to tweet_text variable
		    }
	    	londonMode();	//call london mode
	    }
	  }
	    //if m key is pressed invoke london mode, remove four maps and update grid to medium size
	    if (key == 'm'){
	    	small = false;
	    	labelsDisp = false;
	    	medium = true;
	    	large = false;
	    	storeLabel = false;
	    	setGridMedium();	//call setGridMedium method to change n value
	    	
	    	if (medium) {
	    		initiateSquares();	//initiate squares reflect new n value
	    		println(itemToHighlight);
		    	for(int i = 0; i < ldnOnlyTable.getRowCount() -1 ; i++) {	//for every element in ldnOnlyTable loop through
		    		if (itemToHighlight != -1) {	//if genre has been clicked
		    			tweet_text = ldnOnlyTable.getString(i, "MESSAGETEXT");		//get message text from position i and add to tweet_text variable 
						if (tweet_text.contains(londonText[itemToHighlight])) {	//if variable tweet_text assigned contains genre from list 
							check_point_in_square(new Location(ldnOnlyTable.getFloat(i,"LATITUDE"), (ldnOnlyTable.getFloat(i, "LONGITUDE"))));	//invoke check_point_in_square method, parsing lat and long parameters - this checks which square the tweet occured in
						}
		    		} else {	//if no genre has been selected
						check_point_in_square(new Location(ldnOnlyTable.getFloat(i,"LATITUDE"), (ldnOnlyTable.getFloat(i, "LONGITUDE"))));//invoke check_point_in_square method, parsing lat and long parameters - this checks which square the tweet occured in
						tweet_text = ldnOnlyTable.getString(i, "MESSAGETEXT"); //get message_text from ldnOnlyTable and set to variable tweet_text
					}
			    }
	    	}
	    }
	  
	    //if t key is pressed invoke london mode, remove four maps and update grid to large size
	    if (key == 't'){
	    	small = false;
	    	labelsDisp = false;
	    	medium = false;
	    	large = true;
	    	storeLabel = false;
	    	setGridLarge();	//change n value
	    	squares.clear();	//clear previous squares
	    	londonOnlyTweets.clear();
		    	if (large){
		    		initiateSquares();	//call initiateSquares to reflect new n value
	    		println(itemToHighlight);
	    	
	    		for(int i = 0; i < ldnOnlyTable.getRowCount() -1 ; i++) {
		    		if (itemToHighlight != -1) {	//if a genre has been selected
		    			tweet_text = ldnOnlyTable.getString(i, "MESSAGETEXT");	
						if (tweet_text.contains(londonText[itemToHighlight])) {	//get message text from position i and add to tweet_text variable 
							check_point_in_square(new Location(ldnOnlyTable.getFloat(i,"LATITUDE"), (ldnOnlyTable.getFloat(i, "LONGITUDE"))));	//invoke check_point_in_square method, parsing lat and long parameters - this checks which square the tweet occured in
						}
		    		} else {	//if no genre has been selected
						check_point_in_square(new Location(ldnOnlyTable.getFloat(i,"LATITUDE"), (ldnOnlyTable.getFloat(i, "LONGITUDE"))));	//invoke check_point_in_square method, parsing lat and long parameters - this checks which square the tweet occured in
						tweet_text = ldnOnlyTable.getString(i, "MESSAGETEXT"); 
					}
			    }
	    }
	   }
	    //if n key is pressed, returns to 'normal mode', shows four maps with all functionality and markers
	    if (key == 'n'){
	    	fourMaps = true;
	    	mapTweets = true;
	    	labelsDisp = true;
	    	londonMode = false;
	    	storeLabel = false;
	    	barChartAll = false;
	    	cityBars = false;
	    	wordle = false;
	    	londonWordle = false;
	    	parisWordle = false;
	    	newYorkWordle = false;
	    	munichWordle = false;
	    	
	    }
	    
	    //if b is pressed - shows all bar charts
	    if(key == 'b'){
	    	artist = false;
	    	mapTweets = false;
	    	labelsDisp = false;
	    	barChartData();	//call method to create bar charts
	    	barChartAll = true;	//set boolean to true to draw bar charts
	    	londonMode = false;
	    	storeLabel = false;
	    }
	    
	    //if c is pressed bar charts with inidividual genre counts are displayed
	    if(key =='c'){
	    	cityBars = true;	    	
	    	artist = false;
	    	mapTweets = false;
	    	fourMaps = false;
	    	labelsDisp = false;
	    	barChartPerCity();	//call methods to create bar charts
	    	londonMode = false;
	    	storeLabel = false;
	    	
	    }
	    
	    //if w key is pressed, display wordle image
	    if (key == 'w'){
	    	mapTweets = false;
	    	labelsDisp = false;
	    	cityBars = false;
	    	barChartAll = false;
	    	wordle = true;
	    	londonMode = false;
	    	fourMaps = false;
	    	storeLabel = false;
	    	londonWordle = false;
	    	parisWordle = false;
	    	newYorkWordle = false;
	    	munichWordle = false;
	    }	    
	    
	    //if 1 key is pressed, display london wordle image
	    if(key =='1'){
	    	mapTweets = false;
	    	labelsDisp = false;
	    	cityBars = false;
	    	barChartAll = false;
	    	wordle = false;
	    	londonMode = false;
	    	fourMaps = false;
	    	storeLabel = false;
	    	londonWordle = true;
	    	parisWordle = false;
	    	newYorkWordle = false;
	    	munichWordle = false;
	    }
	    //if 2 key is pressed, display paris wordle image
	    if(key =='2'){
	    	mapTweets = false;
	    	labelsDisp = false;
	    	cityBars = false;
	    	barChartAll = false;
	    	wordle = false;
	    	londonMode = false;
	    	fourMaps = false;
	    	storeLabel = false;
	    	londonWordle = false;
	    	parisWordle = true;
	    	newYorkWordle = false;
	    	munichWordle = false;
	    }
	    
	    //if 3 key is pressed, display new york wordle image
	    if(key =='3'){
	    	mapTweets = false;
	    	labelsDisp = false;
	    	cityBars = false;
	    	barChartAll = false;
	    	wordle = false;
	    	londonMode = false;
	    	fourMaps = false;
	    	storeLabel = false;
	    	londonWordle = false;
	    	parisWordle = false;
	    	newYorkWordle = true;
	    	munichWordle = false;
	    }
	    
	    //if 4 key is pressed, display new york wordle image
	    if(key =='4'){
	    	mapTweets = false;
	    	labelsDisp = false;
	    	cityBars = false;
	    	barChartAll = false;
	    	wordle = false;
	    	londonMode = false;
	    	fourMaps = false;
	    	storeLabel = false;
	    	londonWordle = false;
	    	parisWordle = false;
	    	newYorkWordle = false;
	    	munichWordle = true;
	    }
	    
	  }
	  
	  //method for when mouse is moved - holds tooltip
	  public void mouseMoved() {
		  tooltip.setIsActive(false);	//set tooltip to not active
		  //code modified from 'Selecting a marker' (Nagel, 2013) http://unfoldingmaps.org/tutorials/markers-simple.html
		    Tweet new_york_tweet_marker = (Tweet) mapNewYork.getFirstHitMarker(mouseX, mouseY);	//get tweet marker that has been hit at point mouseX and mouseY
		    if (new_york_tweet_marker != null) {	//if a marker has been hit
		        // Select current marker and display tooltip with info
		    	tooltip.setText("@" + new_york_tweet_marker.getScreenName() + "\n");
		    	tooltip.setText(new_york_tweet_marker.getText());
		    	tooltip.setIsActive(true);	//set tooltip to true
		    	
		    }
		    Tweet london_tweet_marker = (Tweet) mapLondon.getFirstHitMarker(mouseX, mouseY); //get tweet marker that has been hit at point mouseX and mouseY
		    if (london_tweet_marker != null){	//if a marker has been hit
		    	//select current marker and hisplay tooltip with info
		    	tooltip.setText("@" + london_tweet_marker.getScreenName() + "\n");
		    	tooltip.setText(london_tweet_marker.getText());
		    	tooltip.setIsActive(true);	//set tooltip to true
		    	
		    }
		    Tweet paris_tweet_marker = (Tweet) mapParis.getFirstHitMarker(mouseX,  mouseY);	//get tweet marker that has been hit at point mouseX and mouseY
		    if (paris_tweet_marker != null){	//if a marker has been hit
		    	tooltip.setText("@" + paris_tweet_marker.getScreenName() + "\n");
		    	tooltip.setText(paris_tweet_marker.getText());
		    	tooltip.setIsActive(true);	//set tooltip to true
		    	
		    }
		    Tweet munich_tweet_marker = (Tweet) mapMunich.getFirstHitMarker(mouseX,  mouseY);	//get tweet marker that has been hit at point mouseX and mouseY
		    if (munich_tweet_marker != null){	//if a marker has been hit
		    	tooltip.setText("@" + munich_tweet_marker.getScreenName() + "\n");
		    	tooltip.setText(munich_tweet_marker.getText());
		    	tooltip.setIsActive(true);	//set tooltip to true
		    	
		    }
	    
		    if (barChartAll){
		    	
		    //relates screen locations to data in bar charts at mouseX and mouseY
		    //code reused and modified from Data Visualization, Session 8: Tranforming data (Wood, 2014) 	http://gicentre.org/datavis/session08/index.html
		    PVector barData1 = barChart1.getScreenToData(new PVector(mouseX, mouseY));
		    PVector barData2 = barChart2.getScreenToData(new PVector(mouseX, mouseY));
		    PVector barData3 = barChart3.getScreenToData(new PVector(mouseX, mouseY));
		    PVector barData4 = barChart4.getScreenToData(new PVector(mouseX, mouseY));
		    //styling for tooltips if bar has been selected
		    if (barData1 != null){
		    	int selectedBar =  (int) (barData1.x);
		    	if (selectedBar ==0 ){
		    		tooltip.setText("Pop total :" + floor(countPop) + "\n" + ("London Pop : " + floor(countPopLdn) + "\n" + "Genre Percentage : " + floor((countPopLdn/ countPop *100)) + "%"));
		    		tooltip.setIsActive(true);
		    	}if (selectedBar ==1 ){
		    		tooltip.setText("HipHop total :" + floor(countHipHop) + "\n" + ("London HipHop : " + floor(countHipHopLdn) + "\n" + "Genre Percentage : " + floor((countHipHopLdn/ countHipHop *100)) + "%"));
		    		tooltip.setIsActive(true);
		    	}if (selectedBar ==2 ){
		    		tooltip.setText("Dance total :" + floor(countDance) + "\n" + ("London Dance : " + floor(countDanceLdn) + "\n" + "Genre Percentage : " + floor((countDanceLdn/ countDance *100)) + "%"));
		    		tooltip.setIsActive(true);
		    	}if (selectedBar ==3 ){
		    		tooltip.setText("Country total :" + floor(countCountry) + "\n" + ("London Country : " + floor(countCountryLdn) + "\n" + "Genre Percentage : " + floor((countCountryLdn/ countCountry *100)) + "%"));
		    		tooltip.setIsActive(true);
		    	}if (selectedBar ==4 ){
		    		tooltip.setText("Rock total :" + floor(countRock) + "\n" + ("London Rock : " + floor(countRockLdn) + "\n" + "Genre Percentage : " + floor((countRockLdn/ countRock *100)) + "%"));
		    		tooltip.setIsActive(true);
		    	}if (selectedBar ==5 ){
		    		tooltip.setText("Alternative total :" + floor(countAlt) + "\n" + ("London Alternative : " + floor(countAltLdn) + "\n" + "Genre Percentage : " + floor((countAltLdn/ countAlt *100)) + "%"));
		    		tooltip.setIsActive(true);
		    	}if (selectedBar ==6 ){
		    		tooltip.setText("R'n'B total :" + floor(countRNB) + "\n" + ("London R'n'B : " + floor(countRNBLdn) + "\n" + "Genre Percentage : " + floor((countRNBLdn/ countRNB *100)) + "%"));
		    		tooltip.setIsActive(true);
		    	}if (selectedBar ==7 ){
		    		tooltip.setText("90's total :" + floor(count90) + "\n" + ("London 90's : " + floor(count90Ldn) + "\n" + "Genre Percentage : " + floor((count90Ldn/ count90 *100)) + "%"));
		    		tooltip.setIsActive(true);
		    	}if (selectedBar ==8 ){
		    		tooltip.setText("80's total :" + floor(count80) + "\n" + ("London 80's : " + floor(count80Ldn) + "\n" + "Genre Percentage : " + floor((count80Ldn/ count80 *100)) + "%"));
		    		tooltip.setIsActive(true);
		    	}if (selectedBar ==9 ){
		    		tooltip.setText("Indie total :" + floor(countIndie) + "\n" + ("London Indie : " + floor(countIndieLdn) + "\n" + "Genre Percentage : " + floor((countIndieLdn/ countIndie *100)) + "%"));
		    		tooltip.setIsActive(true);
		    	}if (selectedBar ==10 ){
		    		tooltip.setText("Rap total :" + floor(countRap) + "\n" + ("London Rap : " + floor(countRapLdn) + "\n" + "Genre Percentage : " + floor((countRapLdn/ countRap *100)) + "%"));
		    		tooltip.setIsActive(true);
		    	}
	    	
		    	}if (barData3 != null){
		    		int selectedBar =  (int) (barData3.x);
		    		if (selectedBar ==0 ){
			    		tooltip.setText("Pop total :" + floor(countPop) + "\n" + ("Paris Pop : " + floor(countPopPar) + "\n" + "Genre Percentage : " + floor((countPopPar/ countPop *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==1 ){
			    		tooltip.setText("HipHop total :" + floor(countHipHop) + "\n" + ("Paris HipHop : " + floor(countHipHopPar) + "\n" + "Genre Percentage : " + floor((countHipHopPar/ countHipHop *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==2 ){
			    		tooltip.setText("Dance total :" + floor(countDance) + "\n" + ("Paris Dance : " + floor(countDancePar) + "\n" + "Genre Percentage : " + floor((countDancePar/ countDance *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==3 ){
			    		tooltip.setText("Country total :" + floor(countCountry) + "\n" + ("Paris Country : " + floor(countCountryPar) + "\n" + "Genre Percentage : " + floor((countCountryPar/ countCountry *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==4 ){
			    		tooltip.setText("Rock total :" + floor(countRock) + "\n" + ("Paris Rock : " + floor(countRockPar) + "\n" + "Genre Percentage : " + floor((countRockPar/ countRock *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==5 ){
			    		tooltip.setText("Alternative total :" + floor(countAlt) + "\n" + ("Paris Alternative : " + floor(countAltPar) + "\n" + "Genre Percentage : " + floor((countAltPar/ countAlt *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==6 ){
			    		tooltip.setText("R'n'B total :" + floor(countRNB) + "\n" + ("Paris R'n'B : " + floor(countRNBPar) + "\n" + "Genre Percentage : " + floor((countRNBPar/ countRNB *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==7 ){
			    		tooltip.setText("90's total :" + floor(count90) + "\n" + ("Paris 90's : " + floor(count90Par) + "\n" + "Genre Percentage : " + floor((count90Par/ count90 *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==8 ){
			    		tooltip.setText("80's total :" + floor(count80) + "\n" + ("Paris 80's : " + floor(count80Par) + "\n" + "Genre Percentage : " + floor((count80Par/ count80 *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==9 ){
			    		tooltip.setText("Indie total :" + floor(countIndie) + "\n" + ("Paris Indie : " + floor(countIndiePar) + "\n" + "Genre Percentage : " + floor((countIndiePar/ countIndie *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==10 ){
			    		tooltip.setText("Rap total :" + floor(countRap) + "\n" + ("Paris Rap : " + floor(countRapPar) + "\n" + "Genre Percentage : " + floor((countRapPar/ countRap *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}
			    	
		    	}if (barData2 != null){
		    		int selectedBar =  (int) (barData2.x);
		    		if (selectedBar ==0 ){
			    		tooltip.setText("Pop total :" + floor(countPop) + "\n" + ("New York Pop : " + floor(countPopNY) + "\n" + "Genre Percentage : " + floor((countPopNY/ countPop *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==1 ){
			    		tooltip.setText("HipHop total :" + floor(countHipHop) + "\n" + ("New York HipHop : " + floor(countHipHopNY) + "\n" + "Genre Percentage : " + floor((countHipHopNY/ countHipHop *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==2 ){
			    		tooltip.setText("Dance total :" + floor(countDance) + "\n" + ("New York Dance : " + floor(countDanceNY) + "\n" + "Genre Percentage : " + floor((countDanceNY/ countDance *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==3 ){
			    		tooltip.setText("Country total :" + floor(countCountry) + "\n" + ("New York Country : " + floor(countCountryNY) + "\n" + "Genre Percentage : " + floor((countCountryNY/ countCountry *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==4 ){
			    		tooltip.setText("Rock total :" + floor(countRock) + "\n" + ("New York Rock : " + floor(countRockNY) + "\n" + "Genre Percentage : " + floor((countRockNY/ countRock *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==5 ){
			    		tooltip.setText("Alternative total :" + floor(countAlt) + "\n" + ("New York Alternative : " + floor(countAltNY) + "\n" + "Genre Percentage : " + floor((countAltNY/ countAlt *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==6 ){
			    		tooltip.setText("R'n'B total :" + floor(countRNB) + "\n" + ("New York R'n'B : " + floor(countRNBNY) + "\n" + "Genre Percentage : " + floor((countRNBNY/ countRNB *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==7 ){
			    		tooltip.setText("90's total :" + floor(count90) + "\n" + ("New York 90's : " + floor(count90NY) + "\n" + "Genre Percentage : " + floor((count90NY/ count90 *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==8 ){
			    		tooltip.setText("80's total :" + floor(count80) + "\n" + ("New York 80's : " + floor(count80NY) + "\n" + "Genre Percentage : " + floor((count80NY/ count80 *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==9 ){
			    		tooltip.setText("Indie total :" + floor(countIndie) + "\n" + ("New York Indie : " + floor(countIndieNY) + "\n" + "Genre Percentage : " + floor((countIndieNY/ countIndie *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}if (selectedBar ==10 ){
			    		tooltip.setText("Rap total :" + floor(countRap) + "\n" + ("New York Rap : " + floor(countRapNY) + "\n" + "Genre Percentage : " + floor((countRapNY/ countRap *100)) + "%"));
			    		tooltip.setIsActive(true);
			    	}

				    	}if (barData4 != null){
				    		int selectedBar =  (int) (barData4.x);
				    		if (selectedBar ==0 ){
					    		tooltip.setText("Pop total :" + floor(countPop) + "\n" + ("Munich Pop : " + floor(countPopMun) + "\n" + "Genre Percentage : " + floor((countPopMun/ countPop *100)) + "%"));
					    		tooltip.setIsActive(true);
					    	}if (selectedBar ==1 ){
					    		tooltip.setText("HipHop total :" + floor(countHipHop) + "\n" + ("Munich HipHop : " + floor(countHipHopMun) + "\n" + "Genre Percentage : " + floor((countHipHopMun/ countHipHop *100)) + "%"));
					    		tooltip.setIsActive(true);
					    	}if (selectedBar ==2 ){
					    		tooltip.setText("Dance total :" + floor(countDance) + "\n" + ("Munich Dance : " + floor(countDanceMun) + "\n" + "Genre Percentage : " + floor((countDanceMun/ countDance *100)) + "%"));
					    		tooltip.setIsActive(true);
					    	}if (selectedBar ==3 ){
					    		tooltip.setText("Country total :" + floor(countCountry) + "\n" + ("Munich Country : " + floor(countCountryMun) + "\n" + "Genre Percentage : " + floor((countCountryMun/ countCountry *100)) + "%"));
					    		tooltip.setIsActive(true);
					    	}if (selectedBar ==4 ){
					    		tooltip.setText("Rock total :" + floor(countRock) + "\n" + ("Munich Rock : " + floor(countRockMun) + "\n" + "Genre Percentage : " + floor((countRockMun/ countRock *100)) + "%"));
					    		tooltip.setIsActive(true);
					    	}if (selectedBar ==5 ){
					    		tooltip.setText("Alternative total :" + floor(countAlt) + "\n" + ("Munich Alternative : " + floor(countAltMun) + "\n" + "Genre Percentage : " + floor((countAltMun/ countAlt *100)) + "%"));
					    		tooltip.setIsActive(true);
					    	}if (selectedBar ==6 ){
					    		tooltip.setText("R'n'B total :" + floor(countRNB) + "\n" + ("Munich R'n'B : " + floor(countRNBMun) + "\n" + "Genre Percentage : " + floor((countRNBMun/ countRNB *100)) + "%"));
					    		tooltip.setIsActive(true);
					    	}if (selectedBar ==7 ){
					    		tooltip.setText("90's total :" + floor(count90) + "\n" + ("Munich 90's : " + floor(count90Mun) + "\n" + "Genre Percentage : " + floor((count90Mun/ count90 *100)) + "%"));
					    		tooltip.setIsActive(true);
					    	}if (selectedBar ==8 ){
					    		tooltip.setText("80's total :" + floor(count80) + "\n" + ("Munich 80's : " + floor(count80Mun) + "\n" + "Genre Percentage : " + floor((count80Mun/ count80 *100)) + "%"));
					    		tooltip.setIsActive(true);
					    	}if (selectedBar ==9 ){
					    		tooltip.setText("Indie total :" + floor(countIndie) + "\n" + ("Munich Indie : " + floor(countIndieMun) + "\n" + "Genre Percentage : " + floor((countIndieMun/ countIndie *100)) + "%"));
					    		tooltip.setIsActive(true);
					    	}if (selectedBar ==10 ){
					    		tooltip.setText("Rap total :" + floor(countRap) + "\n" + ("Munich Rap : " + floor(countRapMun) + "\n" + "Genre Percentage : " + floor((countRapMun/ countRap *100)) + "%"));
					    		tooltip.setIsActive(true);
					    	}

		    	}
		    }
	  }
	  
	  public void storeData() {
	    for (Tweet tweet : incoming_tweets) {	 //for each datatype Tweet of element tweet in data array 
	          
	    try {
	    	//tutorial followed for MySQL connection, code modified (Oracle, 2014) from - http://docs.oracle.com/javase/tutorial/jdbc/overview/
	        //try MySQL connection
	    	//pre-requisite, use database schema to create MySQL table in MySQL Workbench 6.0. follow https://dev.mysql.com/doc/workbench/en/wb-tutorials.html to set database up
	        String myDriver = "org.gjt.mm.mysql.Driver";
	        String myUrl = "jdbc:myDriver:myDatabase";	//replace myDriver and myDatabase, with driver and database information
	        Class.forName(myDriver);        
	        Connection conn = DriverManager.getConnection(myUrl, "username", "password");    //connection statement . Insert username and password for database  
	        //query to add elements into tweet table in MySQL db
	        String query = "INSERT into tweet ( id, screen_name, status_text, source, timezone, timestamp) VALUES(?, ?, ?, ?, ?, ?)";
	        PreparedStatement preparedStmt = conn.prepareStatement(query);	 //initialise prepared statement
	        //set prepared statements to values from tweet 
	        //code modified from for prepared statement (Oracle, 2014) from http://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html
	        preparedStmt.setLong (1, tweet.getTweetId());
	        preparedStmt.setString (2, tweet.getScreenName());
	        preparedStmt.setString (3, tweet.getText());
	        preparedStmt.setString (4, tweet.getSource());
	        preparedStmt.setString (5, tweet.getTimeZone());
	        preparedStmt.setTimestamp (6, tweet.getCreatedAt());
	        preparedStmt.execute();	//execute prepared statement - adds all elements as specified to tweet table
	        
	        println("Database updated!");	//print confirmation of database update to consoles
	        conn.close();	//close db connection
	      }
	      //catch exceptions and print error details to console
	      catch (Exception e)
	      {
	        println("Unable to save to database!");
	        System.err.println(e.getMessage());
	      }
	    }	   
	    incoming_tweets.remove(incoming_tweets);	 //remove all data from incoming_tweets arrayList ready for more to be added
	  }
	  
	  //plot tweets method
	  public void plotTweets() {
	if (mapTweets){
	    //loops through all tweets in data using screen location to plot on screen
	    for (Tweet tweet : database_tweets) {
	    	 Collections.shuffle(database_tweets);	//shuffle order of tweets receieved
	    	if(tweet != null) {	//if there is a tweet
	    	 if (tweet.getText().contains("pop") | tweet.getText().contains("Pop") | tweet.getText().contains("Katy Perry") | tweet.getText().contains("katyperry") | tweet.getText().contains("Lorde") | tweet.getText().contains("lorde") | tweet.getText().contains("One Direction")| tweet.getText().contains("one direction") ){
     	       tweet.setColour(color(250, 30, 162)); //fill marker pink
     	      	}else if (tweet.getText().contains("hiphop") | tweet.getText().contains("HipHop") | tweet.getText().contains("Drake") | tweet.getText().contains("drake")){
     	      		tweet.setColour(color(53, 205, 250));	//fill marker blue
     	      		}else if (tweet.getText().contains("dance") | tweet.getText().contains("Dance") | tweet.getText().contains("Avicii")){
     	      				tweet.setColour(color(0, 224, 1));	//fill marker orange
     	      				}else if (tweet.getText().contains("country")| tweet.getText().contains("Country") | tweet.getText().contains("county music")){
     	      					tweet.setColour(color(13, 116, 13));	 //fill marker forest green
     	      					}else if (tweet.getText().contains("rock")| tweet.getText().contains("Rock")){
     	      						tweet.setColour(color(134, 0, 0));	 //fill marker blood red
     	      						}else if (tweet.getText().contains("alt") | tweet.getText().contains("alternative")| tweet.getText().contains("Alt") | tweet.getText().contains("Imagine Dragons") | tweet.getText().contains("imagine dragons")){
     	      							tweet.setColour(color(193, 185, 185));	 //fill marker silver
     	      							}else if (tweet.getText().contains("rnb") | tweet.getText().contains("RnB")| tweet.getText().contains("RNB")| tweet.getText().contains("Beyonce") | tweet.getText().contains("beyonce") | tweet.getText().contains("beyoncé") | tweet.getText().contains("Beyoncé")| tweet.getText().contains("Pitbull") | tweet.getText().contains("pitbull") | tweet.getText().contains("Rihanna") | tweet.getText().contains("rihanna")){
     	      								tweet.setColour(color(94, 0, 113));//fill marker purple
     	      								}else if (tweet.getText().contains("90")){
     	      									tweet.setColour(color(210, 31, 234));	//fill marker lilac
     	      									}else if (tweet.getText().contains("80")){
     	      										tweet.setColour(color(255, 223, 3));	//fill marker lyellow      	        
     	      										}else if (tweet.getText().contains("indie") | tweet.getText().contains("Indie")){
     	      											tweet.setColour(color(113, 38, 3));	  //fill marker brown
     	      											}else if (tweet.getText().contains("rap")| tweet.getText().contains("Rap") | tweet.getText().contains("Eminem")| tweet.getText().contains("eminem")){
     	      													tweet.setColour(color(25, 68, 222));	  //fill marker blue
     	      													}else if (tweet.getText().contains("music") | tweet.getText().contains("Music") | tweet.getText().contains("MUSIC")) {
     	      											     	       tweet.setColour(color(255, 0, 0));	 //fill marker red
     	      													}
     	      	}
	    	}
	
	    //draw slider on line
	    fill(32,32);
	    h1.line(0, 640, 590, 640);    
	    
	    if (dragging && mouseX > 0 && mouseX < 590  && mouseY > 620 && mouseY < 660)	  //if mouse position is between x(0-590) and y(620-660) and being dragged
	    {
	    //map code adapted from	https://www.processing.org/reference/map_.html
	      z = floor(map(mouseX, 0, 590, 0, 1440));	//map position of mouse on screen (0 - 590) to minutes of the day (0 - 1440)
	      database_tweets = fetchTweetsByTimeDB(z);	// call fetchTweetsByTimeDB 
	    }
		    rectMode(CENTER);  
		    fill(0);
		    textFont(smallFont);
		    text((z/ 60), map(z, 0, 1400, 0, 590), 660);
		    //map rect to position on screen
		    fill(32, 32);
		    h1.rect(map(z, 0, 1400, 0, 590),640, 20, 20);
		}
  }


	//call fetchTweetsByTimeDB function in arraylist declaration
	  public ArrayList<Tweet> fetchTweetsByTimeDB(int time)
	  {
	    ArrayList<Tweet> tweets = new ArrayList<Tweet>();	//create new arraylist of type tweets
	    
	      try {
	    	//tutorial followed for MySQL connection, code modified (Oracle, 2014) from - http://docs.oracle.com/javase/tutorial/jdbc/overview/
		    	//pre-requisite, use database schema to create MySQL table in MySQL Workbench 6.0. follow https://dev.mysql.com/doc/workbench/en/wb-tutorials.html to set database up
		        String myDriver = "org.gjt.mm.mysql.Driver";
		        String myUrl = "jdbc:myDriver:myDatabase";	//replace myDriver and myDatabase, with driver and database information
		        Class.forName(myDriver);        
		        Connection conn = DriverManager.getConnection(myUrl, "username", "password");    //connection statement . Insert username and password for database  
	                  
	        //query to grab tweets from db
	        //query - get hours* 60 to get minutes + number of minutes, display everything less than that amount of minutes
	        String query = "select * from tweet where (extract(HOUR from timestamp) * 60) + extract(MINUTE from timestamp) < ?";
	      //code modified from for prepared statement (Oracle, 2014) from http://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html
	        PreparedStatement preparedStmt = conn.prepareStatement(query);	//initialise prepared statement
	        preparedStmt.setInt(1, time);	//prepared statement set column to 1
	         ResultSet results = preparedStmt.executeQuery();	//results = the results from the SELECT * from tweet query. Store query results in results variable
	        
	         while(results.next()) //while there are more results
	        {
	          Tweet tweet = null;	//set statements to results 
	          //if time on in tweet array = London, Paris, Berlin, Eastern Time (US & Canada) add marker add tweet to tweets array
	         if (mapTweets){
	        	 
	          if (results.getString(5).equals("Paris")){  //if result is from paris
	        	  if (parTweetCount == parTable.getRowCount() - 1) {	//if parTweetCount is the same as the length of the par table
	        		  parTweetCount = 0;	//set parTweetCount to 0
	        		  offset_locations_paris = true;	//apply an offset location
	        	  } else {
	        		  parTweetCount++;	//increment par tweet count
	        	  } 
	           	  println("Paris : " + parTweetCount);
	        	  tweet = new Tweet(geoParisTweets(parTweetCount)); //set tweet to invoke geoParisTweets
	        	  mapParis.addMarker(tweet);	//add tweet marker with location obtained from geoParisTweets to paris map
	        	  tweets.add(tweet);	//add tweet to tweet array
	        	    if (results.getString(3).contains("music")){
	        		  countMusicPar++;
	        	  }
	          }else if (results.getString(5).equals("London")){	//if result is from london
	        	  if (ldnTweetCount == ldnTable.getRowCount() - 1) {	//if ldnTweetCount is the same as the length of the ldn table
	        		  ldnTweetCount = 0;	//setldnTweetCount to 0
	        		  offset_locations_london = true;	//apply offset location
	        	  } else {
	        		  ldnTweetCount++;	//increment ldnTweetCount
	        	  } 
	        	  println("london : " + ldnTweetCount);
	        	  tweet = new Tweet(geoLDNTweets(ldnTweetCount));	//set tweet to invoke geoLDNtweets
	        	  mapLondon.addMarker(tweet);	//add tweet marker with location obtained from geoLDNTweets to london map
	        	  tweets.add(tweet);	//add to tweet array
	        	  if (results.getString(3).contains("music")){
	        		  countMusicLdn++;
	        	  }
	          }else if (results.getString(5).equals("Berlin")){	//if result is from munich
	        	  if(munTweetCount == munTable.getRowCount() -1){	//if munTweetcount is the same as the length of muTable
	        		  munTweetCount = 0;	//set munTweetCount to 0
	        		  offset_locations_munich = true;	//apply offset location
	        	  }else{	
	        		  munTweetCount++;	//increment munTweetCount
	        	  }
	        	  tweet = new Tweet(geoMunTweets(munTweetCount));	//set tweet to invoke geoMunTweets
	        	  mapMunich.addMarker(tweet);	//add tweet marker with location obtained from geoMunTweets to munich map
	        	  tweets.add(tweet);	//add to tweet array
	        	  if (results.getString(3).contains("music")){
        		  countMusicMun++;
	            }
	          }else if (results.getString(5).equals("Eastern Time (US & Canada)")){	//if result is from new york
	        	  if (nyTweetCount == nyTable.getRowCount() - 1) {	//if nyTweetCount is the same length and nyTable
	        		  nyTweetCount = 0;	//set nyTweetCount to 0
	        		  offset_locations_ny = true;	//apply offset location
	        	  } else {
	        		  nyTweetCount++;	//increment nyTweetCount
	        	  }
	        	  println(nyTweetCount);
	        	  tweet = new Tweet(geoNYTweets(nyTweetCount));	//set tweet to invoke geoNYTweets
	        	  mapNewYork.addMarker(tweet);	//add tweet marker with location obtained from geoNYTweets to new york map
	        	  tweets.add(tweet);	//add to tweet array
	        	  if (results.getString(3).contains("music")){
	        		  countMusicNY++;
	        	  }
	        	  
	          }
	        }
	          if (tweet != null) {
	        	  tweet.setTweetId(results.getLong(1));
		          tweet.setScreenName(results.getString(2));
		          tweet.setSource(results.getString(4));
		          tweet.setText(results.getString(3));
		          tweet.setTimeZone(results.getString(5));
		          tweet.setCreatedAt(results.getTimestamp(6));
	          }	
	          
	        }
	        
	        conn.close();	//close db connection
	    }
	      
	      catch (Exception e)	//catch exceptions and print error details to console
	      {
	        println("Unable to fetch tweets from database!");
	        System.err.println(e.getMessage());
	      }
	     
	      return tweets;	 //return tweets value
	    }
	 
	  //if mouse is pressed set dragging boolean to true
	  public void mousePressed()
	  {
	    dragging=true;
	  }
	  
	
	//if mouse is released set dragging boolean back to false
	  public void mouseReleased()
	  {
	    dragging=false;
	  }
	    	  
	  public void mouseClicked(){
		    	//for londonMode squares
				for (int genre=0; genre<xPosition.length; genre++){	//loop through xposition array
					
					if (dist (xPosition[genre]+10, yPosition[genre],mouseX, mouseY) < 20){	//if mouse is clicked within 10 pixels of x and y positions of list of genres
						itemToHighlight = genre;
						//initiate new grid to reflect change in content
						initiateSquares();
						
						for(int i = 0; i < ldnOnlyTable.getRowCount() -1 ; i++) {
							tweet_text = ldnOnlyTable.getString(i, "MESSAGETEXT");
							if (tweet_text.contains(londonText[itemToHighlight])){	//if tweet_text contains a mentioned genre
								check_point_in_square(new Location(ldnOnlyTable.getFloat(i,"LATITUDE"), (ldnOnlyTable.getFloat(i, "LONGITUDE"))));	//call check_point_in_squares method to determine what square the tweet occured in
								
								println("Found with genre");
							}
								
					    }
						println("Genre: " + genre);
						break;
					}
				}
				
		    	
	  }
	  public void londonMode(){
		if (londonMode){
			tooltip.setIsActive(false);
			mapLondonOnly.draw();
			textFont(largeFont);
			fill(255, 0,0);
			text("London", 0, 570);
			 for(Square square : squares) {	//for each square in squares
				 noStroke();
				 if (itemToHighlight == -1) {
					 fill(255,0,0,square.getDensity());	//set density decided in check_point_in_squares
				 } else {
					 fill(londonColour[itemToHighlight],square.getDensity());	//fill square with corresponding colour of which hs been hihglighted
				 }
				 
				 rect(square.getPos().x, square.getPos().y, square.getSize(), square.getSize()); //create grid
			 }
			
			 noFill();			
			 h2.setSeed(1234);
			 h2.rect(0, 0, 590, 590);
			 mapTweets= false;		 
	 
			 for (int genre=0; genre<xPosition.length; genre++){	//assign genre labels to each position in xPosition array
				 
				 noFill();
				 rect(xPosition[genre], yPosition[genre], 50, 50);	// x and y positions for genre labels from xPosition and yPosition arrays at the array position of [genre]
				 fill(londonColour[genre]);	//use correct colour to colour genre labels from londonColour array at the array position of [genre]
				 text(londonText[genre],xPosition[genre], yPosition[genre]);	//create genre labels, populating each x and y positions, get text from londonText array and positions from xPosition and yPosition at the array position of [genre]	    	
				}
			}
	 }
	
	  public Location geoNYTweets(int i) {
			//get values from NewYorkTweets.tsv
			float nyLat = nyTable.getFloat(i, 1);	//(i position, column number)
			float nyLon = nyTable.getFloat(i, 2);	//(i position, column number)
			
			float randomLat;
			float randomLon;
			if (offset_locations_ny) {
				randomLat = randomFloat(-0.020000f, 0.000010f);	//use random number generator between these limits
				randomLon = randomFloat(-0.0200000f, 0.000010f);	//use random number generator between thes limits
			} else {	//if no offset is required, add 0
			randomLat = 0;
			randomLon = 0;
			}	
			return new Location(nyLat + randomLat, nyLon + randomLon);	//add numbers (offset or 0). Return location

		}
		
		
	  public Location geoLDNTweets(int i){
		  //get values from londonTweets.tsv
			float ldnLat = ldnTable.getFloat(i,  1);	//(i position, column number)
			float ldnLon = ldnTable.getFloat(i, 2);		//(i position, column number)
			
			float randomLat;
			float randomLon;
			if(offset_locations_london){
				randomLat = randomFloat(-0.020000f, 0.000010f);	//use random number generator between these limits
				randomLon = randomFloat(-0.020000f, 0.000010f);	//use random number generator between these limits
			} else{	//if no offset is required, add 0
				randomLat = 0;
				randomLon = 0;
			}
			return new Location(ldnLat + randomLat, ldnLon + randomLon); //add numbers (offset or 0). Return location
		}
	
	 public Location geoParisTweets(int i){
		//get values from parisTweets.tsv
			float parLat = parTable.getFloat(i, 1);	//(i position, column number)
			float parLon = parTable.getFloat(i, 2);	//(i position, column number)
			float randomLat;
			float randomLon;
			if(offset_locations_paris){	//if all locations from table are used assign an offset location
				randomLat = randomFloat(-0.020000f, 0.000010f);	//use random number generator between these limits
				randomLon = randomFloat(-0.020000f, 0.000010f);	//use random number generator between these limits		
			} else {	//if no offset if required, add 0
				randomLat =0;
				randomLon =0;	
			}
			return new Location (parLat+randomLat, parLon+randomLon);	//add numbers (offset or 0). Return location
		}
	public Location geoMunTweets(int i){
		//get values from munichTweets.tsv
		float munLat = munTable.getFloat(i,  1);	//(i position, column number)
		float munLon = munTable.getFloat(i, 2);		//(i position, column number)
		
		float randomLat;
		float randomLon;
		if(offset_locations_munich){
			randomLat = randomFloat(-0.020000f, 0.000010f);	//use random number generator between these limits
			randomLon = randomFloat(-0.020000f, 0.000010f);	//use random number generator between these limits
		} else{	//if no offset is required, add 0
			randomLat = 0;
			randomLon = 0;
		}
		return new Location(munLat + randomLat, munLon + randomLon);	////add numbers (offset or 0). Return location
	}
		
	
	 public static float randomFloat(float min, float max) {
		 //code resued and modified from stackoverflow forum (Taylor, 2012) - http://stackoverflow.com/questions/363681/generating-random-numbers-in-a-range-with-java
	        return min + (float) (Math.random() * ((1 + max) - min));
	    }

	 
		
	 public void barChartData(){
		 mapTweets = false;
		 //style bar charts for total counts - use temporary data stores for animation
		 try {
		        //try MySQL connection 
			 	//tutorial followed for MySQL connection, code modified (Oracle, 2014) from - http://docs.oracle.com/javase/tutorial/jdbc/overview/
		    	//pre-requisite, use database schema to create MySQL table in MySQL Workbench 6.0. follow https://dev.mysql.com/doc/workbench/en/wb-tutorials.html to set database up
		        String myDriver = "org.gjt.mm.mysql.Driver";
		        String myUrl = "jdbc:myDriver:myDatabase";	//replace myDriver and myDatabase, with driver and database information
		        Class.forName(myDriver);        
		        Connection conn = DriverManager.getConnection(myUrl, "username", "password");    //connection statement . Insert username and password for database  
		        Statement stmt = conn.createStatement();
		        String barQueryPop = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%pop%'";
		        ResultSet resultsPop = stmt.executeQuery(barQueryPop);
		        resultsPop.next();
		        countPop = resultsPop.getInt(1);
		        println("pop : " + countPop);
		        
		        String barQueryHipHop = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%hiphop%'";
		        ResultSet resultsHipHop = stmt.executeQuery(barQueryHipHop);
		        resultsHipHop.next();
		        countHipHop = resultsHipHop.getInt(1);
		        println("Hip hop : " + countHipHop);	
		        
		        String barQueryDance = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%dance%'";
		        ResultSet resultsDance = stmt.executeQuery(barQueryDance);
		        resultsDance.next();
		        countDance = resultsDance.getInt(1);
		        println("Dance : " + countDance);	
		        
		        String barQueryCountry = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%country%'";
		        ResultSet resultsCountry  = stmt.executeQuery(barQueryCountry);
		        resultsCountry.next();
		        countCountry  = resultsCountry.getInt(1);
		        println("Country : " + countCountry);		   	
		        
		        String barQueryRock = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%rock%'";
		        ResultSet resultsRock = stmt.executeQuery(barQueryRock);
		        resultsRock.next();
		        countRock = resultsRock.getInt(1);
		        println("Rock: " + countRock);
		        
		        String barQueryAlt= "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%alt%' OR status_text LIKE '%alternative%'";
		        ResultSet resultsAlt  = stmt.executeQuery(barQueryAlt);
		        resultsAlt.next();
		        countAlt  = resultsAlt.getInt(1);
		        println("Alt : " + countAlt);		   	
		        
		        String barQueryRNB = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%rnb%'";
		        ResultSet resultsRNB  = stmt.executeQuery(barQueryRNB);
		        resultsRNB.next();
		        countRNB = resultsRNB.getInt(1);
		        println("RNB: " + countRNB);
		        
		        String barQuery90= "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%90%'";
		        ResultSet results90  = stmt.executeQuery(barQuery90);
		        results90.next();
		        count90  = results90.getInt(1);
		        println("90s: " + count90);		   	
		        
		        String barQuery80 = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%80%'";
		        ResultSet results80 = stmt.executeQuery(barQuery80);
		        results80.next();
		        count80 = results80.getInt(1);
		        println("80s: " + count80);
		        
		        String barQueryIndie= "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%indie%'";
		        ResultSet resultsIndie  = stmt.executeQuery(barQueryIndie);
		        resultsIndie.next();
		        countIndie  = resultsIndie.getInt(1);
		        println("Indie : " + countIndie);		   	
		        
		        String barQueryRap = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%rap%'";
		        ResultSet resultsRap  = stmt.executeQuery(barQueryRap);
		        resultsRap.next();
		        countRap = resultsRap.getInt(1);
		        println("Rap: " + countRap);
		        }catch (Exception e){
		        	
		        }
		 
		    //add colours to colourtable for bar chart
		    float[] barColourData = {countPop, countHipHop, countDance, countCountry, countRock, countAlt, countRNB, count90, count80, countIndie, countRap};
		    barColourTable.addDiscreteColourRule(countPop, color(250, 30, 162, 127));
		    barColourTable.addDiscreteColourRule(countHipHop, color(53, 205, 250, 127));
		    barColourTable.addDiscreteColourRule(countDance, color(0, 224, 1, 127));
		    barColourTable.addDiscreteColourRule(countCountry, color(13, 116, 13, 127));
		    barColourTable.addDiscreteColourRule(countRock, color(134, 0, 0, 127));
		    barColourTable.addDiscreteColourRule(countAlt, color(193, 185, 185, 127));
		    barColourTable.addDiscreteColourRule(countRNB, color(94, 0, 113, 127));
		    barColourTable.addDiscreteColourRule(count90, color(210, 31, 234, 127));
		    barColourTable.addDiscreteColourRule(count80, color(255, 223, 3, 127));
		    barColourTable.addDiscreteColourRule(countIndie, color(113, 38, 3, 127));
		    barColourTable.addDiscreteColourRule(countRap, color(25, 68, 222, 127));
		  //followed tutorial and modified code (Wood, 2013) from http://www.gicentre.net/utils/chart/
		 	barChart1 = new BarChart(this);
		    data = new  float[] { countPop, countHipHop, countDance, countCountry, countRock, countAlt, countRNB, count90, count80, countIndie, countRap};
		    float[] data0 = new  float[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		    barChart1.setData(data0);
		    barChart1.setMinValue(0);
		    barChart1.setMaxValue(300);
		    barChart1.setBarGap(1);
		    barChart1.setBarColour(barColourData, barColourTable);
		    barChart2 = new BarChart(this);
		    barChart2.setData(data0);
		    barChart2.setMinValue(0);
		    barChart2.setMaxValue(300);
		    barChart2.setBarGap(1);
		    barChart2.setBarColour(barColourData, barColourTable);
		    barChart3 = new BarChart(this);
		    barChart3.setData(data0);
		    barChart3.setMinValue(0);
		    barChart3.setMaxValue(300);
		    barChart3.setBarGap(1);
		    barChart3.setBarColour(barColourData, barColourTable);
		    barChart4 = new BarChart(this);
		    barChart4.setData(data0);
		    barChart4.setMinValue(0);
		    barChart4.setMaxValue(300);
		    barChart4.setBarGap(1);
		    barChart4.setBarColour(barColourData, barColourTable);
		    
	 }
		
	 public void barChartPerCity(){
		 mapTweets = false;
		 //style all city bar charts
		 try {	
		        //try MySQL connection 
			//tutorial followed for MySQL connection, code modified (Oracle, 2014) from - http://docs.oracle.com/javase/tutorial/jdbc/overview/
		    	//pre-requisite, use database schema to create MySQL table in MySQL Workbench 6.0. follow https://dev.mysql.com/doc/workbench/en/wb-tutorials.html to set database up
		        String myDriver = "org.gjt.mm.mysql.Driver";
		        String myUrl = "jdbc:myDriver:myDatabase";	//replace myDriver and myDatabase, with driver and database information
		        Class.forName(myDriver);        
		        Connection conn = DriverManager.getConnection(myUrl, "username", "password");    //connection statement . Insert username and password for database  
		        Statement stmt = conn.createStatement();	 //query to grab tweets from db	
		        
		        String barQueryPopLdn = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'London' AND status_text LIKE '%pop%'";
		        ResultSet resultsPopLdn = stmt.executeQuery(barQueryPopLdn);
		        resultsPopLdn.next();
		        countPopLdn = resultsPopLdn.getInt(1);
		        String barQueryHipHopLdn = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'London' AND status_text LIKE '%hiphop%'";
		        ResultSet resultsHipHopLdn = stmt.executeQuery(barQueryHipHopLdn);
		        resultsHipHopLdn.next();
		        countHipHopLdn = resultsHipHopLdn.getInt(1);
		        String barQueryDanceLdn = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'London' AND status_text LIKE '%dance%'";
		        ResultSet resultsDanceLdn = stmt.executeQuery(barQueryDanceLdn);
		        resultsDanceLdn.next();
		        countDanceLdn = resultsDanceLdn.getInt(1);
		        String barQueryCountryLdn = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'London' AND status_text LIKE '%country%'";
		        ResultSet resultsCountryLdn = stmt.executeQuery(barQueryCountryLdn);
		        resultsCountryLdn.next();
		        countCountryLdn = resultsCountryLdn.getInt(1);
		        String barQueryRockLdn = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'London' AND status_text LIKE '%rock%'";
		        ResultSet resultsRockLdn = stmt.executeQuery(barQueryRockLdn);
		        resultsRockLdn.next();
		        countRockLdn = resultsRockLdn.getInt(1);
		        String barQueryAltLdn = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'London' AND status_text LIKE '%alt%' OR status_text LIKE '%alternative%'";
		        ResultSet resultsAltLdn = stmt.executeQuery(barQueryAltLdn);
		        resultsAltLdn.next();
		        countAltLdn = resultsAltLdn.getInt(1);
		        String barQueryRNBLdn = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'London' AND status_text LIKE '%rnb%'";
		        ResultSet resultsRNBLdn = stmt.executeQuery(barQueryRNBLdn);
		        resultsRNBLdn.next();
		        countRNBLdn = resultsRNBLdn.getInt(1);
		        String barQuery90Ldn = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'London' AND status_text LIKE '%90%'";
		        ResultSet results90Ldn = stmt.executeQuery(barQuery90Ldn);
		        results90Ldn.next();
		        count90Ldn = results90Ldn.getInt(1);
		        String barQuery80Ldn = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'London' AND status_text LIKE '%80%'";
		        ResultSet results80Ldn = stmt.executeQuery(barQuery80Ldn);
		        results80Ldn.next();
		        count80Ldn = results80Ldn.getInt(1);
		        String barQueryIndieLdn = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'London' AND status_text LIKE '%indie%'";
		        ResultSet resultsIndieLdn = stmt.executeQuery(barQueryIndieLdn);
		        resultsIndieLdn.next();
		        countIndieLdn = resultsIndieLdn.getInt(1);
		        String barQueryRapLdn = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'London' AND status_text LIKE '%rap%'";
		        ResultSet resultsRapLdn = stmt.executeQuery(barQueryRapLdn);
		        resultsRapLdn.next();
		        countRapLdn = resultsRapLdn.getInt(1);	  
			    
			    barColourTableLdn.addDiscreteColourRule(countPopLdn, color(250, 30, 162));
			    barColourTableLdn.addDiscreteColourRule(countHipHopLdn, color(53, 205, 250));
			    barColourTableLdn.addDiscreteColourRule(countDanceLdn, color(0, 224, 1));
			    barColourTableLdn.addDiscreteColourRule(countCountryLdn, color(13, 116, 13));
			    barColourTableLdn.addDiscreteColourRule(countRockLdn, color(134, 0, 0));
			    barColourTableLdn.addDiscreteColourRule(countAltLdn, color(193, 185, 185));
			    barColourTableLdn.addDiscreteColourRule(countRNBLdn, color(94, 0, 113));
			    barColourTableLdn.addDiscreteColourRule(count90Ldn, color(210, 31, 234));
			    barColourTableLdn.addDiscreteColourRule(count80Ldn, color(255, 223, 3));
			    barColourTableLdn.addDiscreteColourRule(countIndieLdn, color(113, 38, 3));
			    barColourTableLdn.addDiscreteColourRule(countRapLdn, color(25, 68, 222));
			  //followed tutorial and modified code (Wood, 2013) from http://www.gicentre.net/utils/chart/
			    float[] barColourDataLdn = {countPopLdn, countHipHopLdn, countDanceLdn, countCountryLdn, countRockLdn, countAltLdn, countRNBLdn, count90Ldn, count80Ldn, countIndieLdn, countRapLdn};
			 	barChartLdn = new BarChart(this);
			    data = new  float[] {countPopLdn, countHipHopLdn, countDanceLdn, countCountryLdn, countRockLdn, countAltLdn, countRNBLdn, 
			    		count90Ldn, count80Ldn, countIndieLdn, countRapLdn};
			   
			    barChartLdn.setData(data);
			    barChartLdn.setMinValue(0);
			    barChartLdn.setMaxValue(200);
			    barChartLdn.setBarGap(1);
			    barChartLdn.setBarColour(barColourDataLdn, barColourTableLdn);
		        			    
			    String barQueryPopPar = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Paris' AND status_text LIKE '%pop%'";
		        ResultSet resultsPopPar = stmt.executeQuery(barQueryPopPar);
		        resultsPopPar.next();
		        countPopPar = resultsPopPar.getInt(1);
		        String barQueryHipHopPar = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Paris' AND status_text LIKE '%hiphop%'";
		        ResultSet resultsHipHopPar = stmt.executeQuery(barQueryHipHopPar);
		        resultsHipHopPar.next();
		        countHipHopPar = resultsHipHopPar.getInt(1);
		        String barQueryDancePar = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Paris' AND status_text LIKE '%dance%'";
		        ResultSet resultsDancePar = stmt.executeQuery(barQueryDancePar);
		        resultsDancePar.next();
		        countDancePar = resultsDancePar.getInt(1);
		        String barQueryCountryPar = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Paris' AND status_text LIKE '%country%'";
		        ResultSet resultsCountryPar = stmt.executeQuery(barQueryCountryPar);
		        resultsCountryPar.next();
		        countCountryPar = resultsCountryPar.getInt(1);
		        String barQueryRockPar = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Paris' AND status_text LIKE '%rock%'";
		        ResultSet resultsRockPar = stmt.executeQuery(barQueryRockPar);
		        resultsRockPar.next();
		        countRockPar = resultsRockPar.getInt(1);
		        String barQueryAltPar = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Paris' AND status_text LIKE '%alt%' OR status_text LIKE '%alternative%'";
		        ResultSet resultsAltPar = stmt.executeQuery(barQueryAltPar);
		        resultsAltPar.next();
		        countAltPar = resultsAltPar.getInt(1);
		        String barQueryRNBPar = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Paris' AND status_text LIKE '%rnb%'";
		        ResultSet resultsRNBPar = stmt.executeQuery(barQueryRNBPar);
		        resultsRNBPar.next();
		        countRNBPar = resultsRNBPar.getInt(1);
		        String barQuery90Par = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Paris' AND status_text LIKE '%90%'";
		        ResultSet results90Par = stmt.executeQuery(barQuery90Par);
		        results90Par.next();
		        count90Par = results90Par.getInt(1);
		        String barQuery80Par = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Paris' AND status_text LIKE '%80%'";
		        ResultSet results80Par = stmt.executeQuery(barQuery80Par);
		        results80Par.next();
		        count80Par = results80Par.getInt(1);
		        String barQueryIndiePar = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Paris' AND status_text LIKE '%indie%'";
		        ResultSet resultsIndiePar = stmt.executeQuery(barQueryIndiePar);
		        resultsIndiePar.next();
		        countIndiePar = resultsIndiePar.getInt(1);
		        String barQueryRapPar = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Paris' AND status_text LIKE '%rap%'";
		        ResultSet resultsRapPar = stmt.executeQuery(barQueryRapPar);
		        resultsRapPar.next();
		        countRapPar = resultsRapPar.getInt(1);
		        barColourTablePar.addDiscreteColourRule(countPopPar, color(250, 30, 162));
			    barColourTablePar.addDiscreteColourRule(countHipHopPar, color(53, 205, 250));
			    barColourTablePar.addDiscreteColourRule(countDancePar, color(0, 224, 1));
			    barColourTablePar.addDiscreteColourRule(countCountryPar, color(13, 116, 13));
			    barColourTablePar.addDiscreteColourRule(countRockPar, color(134, 0, 0));
			    barColourTablePar.addDiscreteColourRule(countAltPar, color(193, 185, 185));
			    barColourTablePar.addDiscreteColourRule(countRNBPar, color(94, 0, 113));
			    barColourTablePar.addDiscreteColourRule(count90Par, color(210, 31, 234));
			    barColourTablePar.addDiscreteColourRule(count80Par, color(255, 223, 3));
			    barColourTablePar.addDiscreteColourRule(countIndiePar, color(113, 38, 3));
			    barColourTablePar.addDiscreteColourRule(countRapPar, color(25, 68, 222));
			  //followed tutorial and modified code (Wood, 2013) from http://www.gicentre.net/utils/chart/
			    float[] barColourDataPar = {countPopPar, countHipHopPar, countDancePar, countCountryPar, countRockPar, countAltPar, countRNBPar, count90Par, count80Par, countIndiePar, countRapPar};
			 	barChartPar = new BarChart(this);
			    data = new  float[] {countPopPar, countHipHopPar, countDancePar, countCountryPar, countRockPar, countAltPar, countRNBPar, count90Par, count80Par, countIndiePar, countRapPar};
			    barChartPar.setData(data);
			    barChartPar.setMinValue(0);
			    barChartPar.setMaxValue(200);
			    barChartPar.setBarGap(1);
			    barChartPar.setBarColour(barColourDataPar, barColourTablePar);

			    String barQueryPopNY = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Eastern Time (US & Canada)' AND status_text LIKE '%pop%'";
		        ResultSet resultsPopNY = stmt.executeQuery(barQueryPopNY);
		        resultsPopNY.next();
		        countPopNY = resultsPopNY.getInt(1);
		        String barQueryHipHopNY = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Eastern Time (US & Canada)' AND status_text LIKE '%hiphop%'";
		        ResultSet resultsHipHopNY = stmt.executeQuery(barQueryHipHopNY);
		        resultsHipHopNY.next();
		        countHipHopNY = resultsHipHopNY.getInt(1);
		        String barQueryDanceNY = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Eastern Time (US & Canada)' AND status_text LIKE '%dance%'";
		        ResultSet resultsDanceNY = stmt.executeQuery(barQueryDanceNY);
		        resultsDanceNY.next();
		        countDanceNY = resultsDanceNY.getInt(1);
		        String barQueryCountryNY = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Eastern Time (US & Canada)' AND status_text LIKE '%country%'";
		        ResultSet resultsCountryNY = stmt.executeQuery(barQueryCountryNY);
		        resultsCountryNY.next();
		        countCountryNY = resultsCountryNY.getInt(1);
		        String barQueryRockNY = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Eastern Time (US & Canada)' AND status_text LIKE '%rock%'";
		        ResultSet resultsRockNY = stmt.executeQuery(barQueryRockNY);
		        resultsRockNY.next();
		        countRockNY = resultsRockNY.getInt(1);
		        String barQueryAltNY = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Eastern Time (US & Canada)' AND status_text LIKE '%alt%' OR status_text LIKE '%alternative%'";
		        ResultSet resultsAltNY = stmt.executeQuery(barQueryAltNY);
		        resultsAltNY.next();
		        countAltNY = resultsAltNY.getInt(1);
		        String barQueryRNBNY = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Eastern Time (US & Canada)' AND status_text LIKE '%rnb%'";
		        ResultSet resultsRNBNY = stmt.executeQuery(barQueryRNBNY);
		        resultsRNBNY.next();
		        countRNBNY = resultsRNBNY.getInt(1);
		        String barQuery90NY = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Eastern Time (US & Canada)' AND status_text LIKE '%90%'";
		        ResultSet results90NY = stmt.executeQuery(barQuery90NY);
		        results90NY.next();
		        count90NY = results90NY.getInt(1);
		        String barQuery80NY = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Eastern Time (US & Canada)' AND status_text LIKE '%80%'";
		        ResultSet results80NY = stmt.executeQuery(barQuery80NY);
		        results80NY.next();
		        count80NY = results80NY.getInt(1);
		        String barQueryIndieNY = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Eastern Time (US & Canada)' AND status_text LIKE '%indie%'";
		        ResultSet resultsIndieNY = stmt.executeQuery(barQueryIndieNY);
		        resultsIndieNY.next();
		        countIndieNY = resultsIndieNY.getInt(1);
		        String barQueryRapNY = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Eastern Time (US & Canada)' AND status_text LIKE '%rap%'";
		        ResultSet resultsRapNY = stmt.executeQuery(barQueryRapNY);
		        resultsRapNY.next();
		        countRapNY = resultsRapNY.getInt(1);
		       	
			    barColourTableNy.addDiscreteColourRule(countPopNY, color(250, 30, 162));
			    barColourTableNy.addDiscreteColourRule(countHipHopNY, color(53, 205, 250));
			    barColourTableNy.addDiscreteColourRule(countDanceNY, color(0, 224, 1));
			    barColourTableNy.addDiscreteColourRule(countCountryNY, color(13, 116, 13));
			    barColourTableNy.addDiscreteColourRule(countRockNY, color(134, 0, 0));
			    barColourTableNy.addDiscreteColourRule(countAltNY, color(193, 185, 185));
			    barColourTableNy.addDiscreteColourRule(countRNBNY, color(94, 0, 113));
			    barColourTableNy.addDiscreteColourRule(count90NY, color(210, 31, 234));
			    barColourTableNy.addDiscreteColourRule(count80NY, color(255, 223, 3));
			    barColourTableNy.addDiscreteColourRule(countIndieNY, color(113, 38, 3));
			    barColourTableNy.addDiscreteColourRule(countRapNY, color(25, 68, 222));
			  //followed tutorial and modified code (Wood, 2013) from http://www.gicentre.net/utils/chart/
			    float[] barColourDataNY = {countPopNY, countHipHopNY, countDanceNY, countCountryNY, countRockNY, countAltNY, countRNBNY, count90NY, count80NY, countIndieNY, countRapNY};
			 	barChartNY = new BarChart(this);
			    data = new  float[] { countPopNY, countHipHopNY, countDanceNY, countCountryNY, countRockNY, countAltNY, countRNBNY, count90NY, count80NY, countIndieNY, countRapNY};
			    barChartNY.setData(data);
			    barChartNY.setMinValue(0);
			    barChartNY.setMaxValue(200);
			    barChartNY.setBarGap(1);
			    barChartNY.setBarColour(barColourDataNY, barColourTableNy);

			    String barQueryPopMun = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Berlin' AND status_text LIKE '%pop%'";
		        ResultSet resultsPopMun = stmt.executeQuery(barQueryPopMun);
		        resultsPopMun.next();
		        countPopMun = resultsPopMun.getInt(1);
		        String barQueryHipHopMun = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Berlin' AND status_text LIKE '%hiphop%'";
		        ResultSet resultsHipHopMun = stmt.executeQuery(barQueryHipHopMun);
		        resultsHipHopMun.next();
		        countHipHopMun = resultsHipHopMun.getInt(1);
		        String barQueryDanceMun = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Berlin' AND status_text LIKE '%dance%'";
		        ResultSet resultsDanceMun = stmt.executeQuery(barQueryDanceMun);
		        resultsDanceMun.next();
		        countDanceMun = resultsDanceMun.getInt(1);
		        String barQueryCountryMun = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Berlin' AND status_text LIKE '%country%'";
		        ResultSet resultsCountryMun = stmt.executeQuery(barQueryCountryMun);
		        resultsCountryMun.next();
		        countCountryMun = resultsCountryMun.getInt(1);
		        String barQueryRockMun = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Berlin' AND status_text LIKE '%rock%'";
		        ResultSet resultsRockMun = stmt.executeQuery(barQueryRockMun);
		        resultsRockMun.next();
		        countRockMun = resultsRockMun.getInt(1);
		        String barQueryAltMun = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Berlin' AND status_text LIKE '%alt%' OR status_text LIKE '%alternative%'";
		        ResultSet resultsAltMun = stmt.executeQuery(barQueryAltMun);
		        resultsAltMun.next();
		        countAltMun = resultsAltMun.getInt(1);
		        String barQueryRNBMun = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Berlin' AND status_text LIKE '%rnb%'";
		        ResultSet resultsRNBMun = stmt.executeQuery(barQueryRNBMun);
		        resultsRNBMun.next();
		        countRNBMun = resultsRNBMun.getInt(1);
		        String barQuery90Mun = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Berlin' AND status_text LIKE '%90%'";
		        ResultSet results90Mun = stmt.executeQuery(barQuery90Mun);
		        results90Mun.next();
		        count90Mun = results90Mun.getInt(1);
		        String barQuery80Mun = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Berlin' AND status_text LIKE '%80%'";
		        ResultSet results80Mun = stmt.executeQuery(barQuery80Mun);
		        results80Mun.next();
		        count80Mun = results80Mun.getInt(1);
		        String barQueryIndieMun = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Berlin' AND status_text LIKE '%indie%'";
		        ResultSet resultsIndieMun = stmt.executeQuery(barQueryIndieMun);
		        resultsIndieMun.next();
		        countIndieMun = resultsIndieMun.getInt(1);
		        String barQueryRapMun = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Berlin' AND status_text LIKE '%rap%'";
		        ResultSet resultsRapMun = stmt.executeQuery(barQueryRapMun);
		        resultsRapMun.next();
		        countRapMun = resultsRapMun.getInt(1);
		        
		        barColourTableMun.addDiscreteColourRule(countPopMun, color(250, 30, 162));
			    barColourTableMun.addDiscreteColourRule(countHipHopMun, color(53, 205, 250));
			    barColourTableMun.addDiscreteColourRule(countDanceMun, color(0, 224, 1));
			    barColourTableMun.addDiscreteColourRule(countCountryMun, color(13, 116, 13));
			    barColourTableMun.addDiscreteColourRule(countRockMun, color(134, 0, 0));
			    barColourTableMun.addDiscreteColourRule(countAltMun, color(193, 185, 185));
			    barColourTableMun.addDiscreteColourRule(countRNBMun, color(94, 0, 113));
			    barColourTableMun.addDiscreteColourRule(count90Mun, color(210, 31, 234));
			    barColourTableMun.addDiscreteColourRule(count80Mun, color(255, 223, 3));
			    barColourTableMun.addDiscreteColourRule(countIndieMun, color(113, 38, 3));
			    barColourTableMun.addDiscreteColourRule(countRapMun, color(25, 68, 222));
			  //followed tutorial and modified code (Wood, 2013)from http://www.gicentre.net/utils/chart/
			    float[] barColourDataMun = {countPopMun, countHipHopMun, countDanceMun, countCountryMun, countRockMun, countAltMun, countRNBMun, count90Mun, count80Mun, countIndieMun, countRapMun};
			 	barChartMun = new BarChart(this);
			    data = new  float[] {countPopMun, countHipHopMun, countDanceMun, countCountryMun, countRockMun, countAltMun, countRNBMun, count90Mun, count80Mun, countIndieMun, countRapMun};
			    barChartMun.setData(data);
			    barChartMun.setMinValue(0);
			    barChartMun.setMaxValue(200);
			    barChartMun.setBarGap(1);
			    barChartMun.setBarColour(barColourDataMun, barColourTableMun);
			    
			    

		 }catch(Exception e) {
			 
		 }
	 }
	 
	 public void initiateSquares(){
		this.squares.clear();
		 //loop through x and y to ensure all squares are plotted
		for(int x = 0; x< n; x++){		//for loop, set x to 0, if x is less than grid size, increment x
			for(int y=0; y<n; y++){		//for loop, set y to 0, if y is less than grid size, increment y
				int size = 590/n;		//size = size of map/ grid size
				PVector pos = new PVector(x * size, y * size);	//add new PVector (x * size, y*size) example x = 0 y = 0 n = 4 PVector(0, 0) x = 1 y = 0 PVector(4, 0)
				this.squares.add(new Square(pos, size));		//add new squares to squares
			}
		}
	}
	 
	public void check_point_in_square(Location loc) { 
		  
		PVector pos = mapLondonOnly.getScreenPosition(loc);		//get screen location of mapLondonOnly and store as PVector
		float size = 590 / this.n;		//size = map size/ grid size
		//loop through x and y to ensure all squares are used 
		for (int x = 0; x < this.n; x++) {		//for loop, set x = 0, if x is smaller than grid size, increment x
			for (int y = 0; y < this.n; y++) {		//for loop, set y = 0, if y is smaller than grid size, increment y 
				if (pos.x >= x * size && pos.x <= (x + 1) * size && pos.y >= y * size && pos.y <= (y + 1) * size) {  	//if point is between x parameters and y parameters, pos is a pvector
					float current_density = squares.get((x * n) + y).getDensity();		//store density values for each square in current_density
					if (itemToHighlight != -1){
						current_density += 50.0;	
					} else {
						current_density += 0.2;
					}
					//increment density 
					squares.get((x * n) + y).setDensity(current_density);		//set the density of the square to the current density depth
				}
			}
		}
	}

	public void setGridSmall(){
		n = 10;
	}
	
	public void setGridMedium(){
		n = 20;
	}
	
	public void setGridLarge(){
		n = 30;
	}
	
	public void getArtistCount(){
		//try MySQL connection 
		 
		 try {
		        //try MySQL connection 
			 	//tutorial followed and code modified (Oracle, 2014) from - http://docs.oracle.com/javase/tutorial/jdbc/overview/
		        String myDriver = "org.gjt.mm.mysql.Driver";
		        String myUrl = "jdbc:mysql://localhost/tweets";
		        Class.forName(myDriver);
		        Connection conn = DriverManager.getConnection(myUrl, "root", "KatyFYP");	       //connection statement		        
		        Statement stmt = conn.createStatement();
			        String queryEminem = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%eminem%'";
			        ResultSet resultsEminem= stmt.executeQuery(queryEminem);
			        resultsEminem.next();
			        eminem = resultsEminem.getInt(1);	
			        
			        String queryAvicii = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%avicii%'";
			        ResultSet resultsAvicii = stmt.executeQuery(queryAvicii);
			        resultsAvicii.next();
			        avicii = resultsAvicii.getInt(1);
			        
			        String queryKatyPerry = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%katy perry%'";
			        ResultSet resultsKatyPerry = stmt.executeQuery(queryKatyPerry);
			        resultsKatyPerry.next();
			        katyPerry = resultsKatyPerry.getInt(1);
			        
			        
			        String queryLorde = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%lorde%'";
			        ResultSet resultsLorde = stmt.executeQuery(queryLorde);
			        resultsLorde.next();
			        lorde = resultsLorde.getInt(1);
			        
			        String queryOneDirection = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%one direction%'";
			        ResultSet resultsOneDirection = stmt.executeQuery(queryOneDirection);
			        resultsOneDirection.next();
			        oneDirection = resultsOneDirection.getInt(1);
			        
			        String queryBeyonce = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%beyonce%'";
			        ResultSet resultsBeyonce = stmt.executeQuery(queryBeyonce);
			        resultsBeyonce.next();
			        beyonce = resultsBeyonce.getInt(1);
			        
			        String queryImagineDragons = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%imagine dragons%'";
			        ResultSet resultsImagineDragons = stmt.executeQuery(queryImagineDragons);
			        resultsImagineDragons.next();
			        imagineDragons = resultsImagineDragons.getInt(1);
			        
			        String queryDrake = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%drake%'";
			        ResultSet resultsDrake = stmt.executeQuery(queryDrake);
			        resultsDrake.next();
			        drake = resultsDrake.getInt(1);
        
			        String queryPitbull = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%pitbull%'";
			        ResultSet resultsPitbull = stmt.executeQuery(queryPitbull);
			        resultsPitbull.next();
			        pitbull = resultsPitbull.getInt(1);
			        
			        String queryRihanna = "SELECT COUNT(*) FROM tweet WHERE status_text LIKE '%rihanna%'";
			        ResultSet resultsRihanna = stmt.executeQuery(queryRihanna);
			        resultsRihanna.next();
			        rihanna = resultsRihanna.getInt(1);     
			        
			        String queryLdnMusic = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'London' AND status_text LIKE 'music'";
			        ResultSet resultsLdnMusic = stmt.executeQuery(queryLdnMusic);
			        resultsLdnMusic.next();
			       ldn = resultsLdnMusic.getInt(1); 
			        
			        String queryParMusic = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Paris' AND status_text LIKE 'music'";
			        ResultSet resultsParMusic = stmt.executeQuery(queryParMusic);
			        resultsParMusic.next();
			        par = resultsParMusic.getInt(1); 
			        
			        
			        String queryNyMusic = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Eastern Time (US & Canada)' AND status_text LIKE 'music'";
			        ResultSet resultsNyMusic = stmt.executeQuery(queryNyMusic);
			        resultsNyMusic.next();
			        ny = resultsNyMusic.getInt(1); 
			        
			        String queryMunMusic = "SELECT COUNT(*) FROM tweet WHERE timezone LIKE 'Berlin' AND status_text LIKE 'music'";
			        ResultSet resultsMunMusic = stmt.executeQuery(queryMunMusic);
			        resultsMunMusic.next();
			        mun = resultsMunMusic.getInt(1); 
			        
		}catch (Exception e){
			println("Error : " + e);
		}
	}
	//main function
	  public static void main(String args[])
	  {
	    PApplet.main(new String[] {"--present", "musicTweetsMain"});  
	  }

}
