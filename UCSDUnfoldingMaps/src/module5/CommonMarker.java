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
	private OceanQuakeMarker oqmarker;
	protected ScreenPosition connCity;
	protected ScreenPosition connQuake;
	
	// Records whether this marker has been clicked (most recently)
	protected boolean clicked = false;
	protected boolean quakeO = false;
	
	protected boolean cityIsConnected =false;
	
	public CommonMarker(Location location) {
		super(location);
	}
	
	public CommonMarker(Location location, java.util.HashMap<java.lang.String,java.lang.Object> properties) {
		super(location, properties);
	}
	public void addCoordinates(List<ScreenPosition> pos){
		positions = pos;
	}
	
	public List<ScreenPosition> getPositions(){
		return positions;
	}
	// Getter method for clicked field
	public boolean getClicked() {
		return clicked;
	}
	private float originalX;
	private float originalY;
	
	// Setter method for clicked field
	public void setClicked(boolean state) {
		clicked = state;
	}
	public void cityIsConnected(boolean state){
		cityIsConnected=state;
	}
	public void qs(boolean state){
		quakeO=state;
	}
	public void setConnCity(ScreenPosition position){
		connCity=position;
	}
	
	public void setConnQuake(ScreenPosition position){
		connQuake=position;
	}
	
	// Common piece of drawing method for markers
	public void draw(PGraphics pg, float x, float y) {
		
		if (!hidden) {
			drawMarker(pg, x, y);
			
			if (selected) {
				showTitle(pg, x, y);  // Implemented in the subclasses
			}
			if(clicked){
				showConnectingLines(pg, x, y);
				originalX=x;
				originalY=y;
			}
			
		}
	}
	private List<ScreenPosition> positions = new ArrayList<ScreenPosition>();
	public abstract void showConnectingLinesFromCity(PGraphics pg, float x, float y, float x2, float y2);
	public abstract void showConnectingLines(PGraphics pg, float x, float y);
	public abstract void dontShowConnectingLines(PGraphics pg, float x, float y);
	public abstract void drawMarker(PGraphics pg, float x, float y);
	public abstract void showTitle(PGraphics pg, float x, float y);
}