package module5;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PGraphics;

/** Implements a visual marker for land earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Veronika Benkeser
 *
 */
public class LandQuakeMarker extends EarthquakeMarker {
	
	
	public LandQuakeMarker(PointFeature quake) {
		
		// calling EarthquakeMarker constructor
		super(quake);
		
		// setting field in earthquake marker
		isOnLand = true;
	}


	/** Draw the earthquake as an ellipse */
	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		pg.ellipse(x, y, 2*radius, 2*radius);
		
	}
	

	// Get the country the earthquake is in
	public String getCountry() {
		return (String) getProperty("country");
	}


	@Override
	public void dontShowConnectingLines(PGraphics pg, float x, float y) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void showConnectingLines(PGraphics pg, float x, float y) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void showConnectingLinesFromCity(PGraphics pg, float x, float y, float x2, float y2) {
		// TODO Auto-generated method stub
		
	}

		
}