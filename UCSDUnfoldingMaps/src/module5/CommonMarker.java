package module5;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.Map;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PGraphics;

/** Implements a common marker for cities and earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Veronika Benkeser
 *
 */
public abstract class CommonMarker extends SimplePointMarker {
	
	// Records whether this marker has been clicked (most recently)
	protected boolean clicked = false;
	
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
	
	// Common piece of drawing method for markers
	public void draw(PGraphics pg, float x, float y) {
		
		if (!hidden) {
			drawMarker(pg, x, y);
			
			if (selected) {
				showTitle(pg, x, y);  // Implemented in the subclasses
			}
		}
	}
	
	public abstract void drawMarker(PGraphics pg, float x, float y);
	public abstract void showTitle(PGraphics pg, float x, float y);
}