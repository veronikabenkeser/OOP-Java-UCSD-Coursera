package module5;

import java.util.List;

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
	
	private List<ScreenPosition> affectedLocations;

	public OceanQuakeMarker(PointFeature quake) {
		super(quake);
		
		// setting field in earthquake marker
		isOnLand = false;
	}
	
	/** Draw the earthquake as a square */
	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		pg.rect(x-radius, y-radius, 2*radius, 2*radius);
		
		//If an ocean earthquake is clicked, lines are drawn to all the cities that may be affected by it
		if(getClicked()){
			affectedLocations = (List<ScreenPosition>) this.getProperty("affectedLocations"); 
			for(ScreenPosition city: affectedLocations){
				pg.line(x, y,city.x,city.y);
			}
		}
	}
}