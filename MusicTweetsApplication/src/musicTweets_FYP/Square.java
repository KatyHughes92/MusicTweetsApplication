package musicTweets_FYP;

import processing.core.PVector;

public class Square {
	private float density;
	private PVector pos;
	private float size;
	
	public Square(PVector pos, float size) {
		super();		//calls parents constructor
		this.density = 0;
		this.pos = pos;
		this.size = size;
	}
	
	public float getDensity(){
		return this.density;
	}
	
	public PVector getPos(){
		return this.pos;
	}
	
	public float getSize(){
		return this.size;
	}
	
	public void setDensity(float density){
		this.density = density;
	}
	
	public void setPos(PVector pos){
		this.pos = pos;
	}
	 
	public void setSize(float size){
		this.size = size;
	}
	

}