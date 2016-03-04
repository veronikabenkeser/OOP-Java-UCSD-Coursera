package module4;

import de.fhpotsdam.unfolding.data.PointFeature;
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

	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		// Draws a centered circle for land quakes
		float radius = (float)((double)getRadius()*0.75);
		pg.ellipse(x-radius,y-radius,radius*2,radius*2);
	}

	// Get the country the earthquake is in
	public String getCountry() {
		return (String) getProperty("country");
	}		
}