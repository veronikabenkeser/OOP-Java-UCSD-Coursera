package module5;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PGraphics;

/** Implements a visual marker for ocean earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Veronika Benkeser
 *
 */
public class OceanQuakeMarker extends EarthquakeMarker {
	
	public OceanQuakeMarker(PointFeature quake) {
		super(quake);
		
		// setting field in earthquake marker
		isOnLand = false;
	}
	

	/** Draw the earthquake as a square */
	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		pg.rect(x-radius, y-radius, 2*radius, 2*radius);
		
	}


	@Override
	public void showConnectingLines(PGraphics pg, float x, float y) {
		pg.fill(0);
		System.out.println("want to draw a line");
		pg.line(x, y,x+10,y+10);
	}


	@Override
	public void dontShowConnectingLines(PGraphics pg, float x, float y) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void showConnectingLinesFromCity(PGraphics pg, float x, float y, float x2, float y2) {
		// TODO Auto-generated method stub
		
	}

}
