package MapProject;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

/** Implements a common marker for cities and earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Veronika Benkeser
 */
public abstract class CommonMarker extends SimplePointMarker {

	PGraphics pg = new PGraphics();
	
	// Records whether this marker has been clicked (most recently)
	protected boolean clicked = false;
	protected boolean cmTyped=false;
	protected int cmVal=0;
	
	public CommonMarker(Location location) {
		super(location);
	}
	
	public CommonMarker(Location location, java.util.HashMap<java.lang.String,java.lang.Object> properties) {
		super(location, properties);
	}
	
	// Getter method for clicked field
	public boolean getClicked() {
		return clicked;
	}
	
	// Setter method for clicked field
	public void setClicked(boolean state) {
		clicked = state;
	}
	
	//Setter method for the typed command
	public void setCMTyped(boolean state){
		cmTyped = state;
	}
	
	//Getter method for the typed command
	public boolean getCMTyped(){
		return cmTyped;
	}
	
	//Setter method for color value based on the typed command
	public void setCMValue(int val){
		cmVal=val;
	}
	
	//Getter method for color value based on the typed command
	public int getCMValue(){
		return cmVal;
	}
	
	//Method for drawing markers
	public void draw(PGraphics buffer, float x, float y) {
		if (!hidden) {
			drawMarker(buffer, x, y);
		}
	}
	
	//Method for coloring earthquakes based on how they compare to each in depth, magnitude, or radius (as picked by the user)
	public void colorRel(PGraphics buffer,int col, float x, float y){
			colorMarkerRelatToOthers(buffer, col, x,y);
	}
	
	//Positions the title on top of objects of the CommonMarkers class
	public void drawTitleOnTop(PGraphics buffer, float x, float y){
		if (!hidden) {
			if(selected && cmTyped){
				buffer.clear();
				showDetailedTitle(buffer, x, y);  // Implemented in the subclasses
			} else if (selected){
				buffer.clear();
				showTitle(buffer, x, y);  // Implemented in the subclasses
			} else {
				buffer.clear();
			}
		}
	}
	
	public abstract void drawMarker(PGraphics buffer, float x, float y);
	public abstract void showTitle(PGraphics buffer, float x, float y);
	public abstract void showDetailedTitle(PGraphics buffer, float x, float y);
	public abstract void colorMarkerRelatToOthers(PGraphics buffer, int colorChange, float x,float y);
}