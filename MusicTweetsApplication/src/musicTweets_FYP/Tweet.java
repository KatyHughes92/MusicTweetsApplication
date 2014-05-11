package musicTweets_FYP;

import java.sql.Timestamp;

import de.fhpotsdam.unfolding.marker.*;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.*;

//modified code from Unfolding Maps 'Markers' tutorial found at http://unfoldingmaps.org/tutorials/markers-simple.html

public class Tweet extends SimplePointMarker{
	private long tweet_id;
	  private String screen_name;
	  private String text;
	  private String source;
	  private String time_zone;
	  private PVector screen_location;
	  private Timestamp time_stamp;
	  private int colour;


	  public Tweet(Location location) 
	  {
		  super(location); //use parent class
	  }
	  
	  public PVector getScreenLocation()
	  {
		  return this.screen_location;
	  }
	  
	  
	  public void setScreenLocation(PVector screen_location)
	  {
		  this.screen_location = screen_location;
	  }

	  public long getTweetId()
	  {
	    return this.tweet_id;
	  }

	  public String getScreenName()
	  {
	    return this.screen_name;
	  }

	  public String getText()
	  {
	    return this.text;
	  }

	  public String getSource()
	  {
	    return this.source;
	  }

	  public String getTimeZone()
	  {
	    return this.time_zone;
	  }

	  public Timestamp getCreatedAt()
	  {
		  return this.time_stamp;	
	  }
	  
	  public void setTweetId(long tweet_id)
	  {
	    this.tweet_id = tweet_id;
	  }

	  public void setScreenName(String screen_name)
	  {
	    this.screen_name = screen_name;
	  }

	  public void setText(String text)
	  {
	    this.text = text;
	  }

	  public void setSource(String source)
	  {
	    this.source = source;
	  }

	  public void setTimeZone(String time_zone)
	  {
	    this.time_zone = time_zone;
	  }


	 public void setCreatedAt(Timestamp time_stamp){

		 this.time_stamp = time_stamp;  
	 }
	 public void setColour(int colour){
		 this.colour = colour;
		 
	 }

	@Override
	
	public void draw(PGraphics pg, float x, float y) {
	//code obtained and modified from 'create your own marker' http://unfoldingmaps.org/tutorials/markers-simple.html
		pg.pushStyle();
	    pg.noStroke();
	    pg.fill(this.colour);
	    pg.ellipse(x, y, 5, 5);
	   
	}
}
