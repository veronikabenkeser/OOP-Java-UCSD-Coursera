package MapProject;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;

/** Implements a visual marker for cities on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Veronika Benkeser
 */
public class CityMarker extends CommonMarker {
	
	public static int TRI_SIZE = 5;  // The size of the triangle marker
	
	public CityMarker(Location location) {
		super(location);
	}
	
	public CityMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		// Cities have properties: "name" (city name), "country" (country name)
		// and "population" (population, in millions)
	}
	
	/**
	 * Implementation of method to draw marker on the map.
	 */
	public void drawMarker(PGraphics buffer, float x, float y) {
		buffer.beginDraw();
		buffer.fill(0, 230, 230);
		buffer.triangle(x-5, y+10, x, y, x+5, y+10);
		buffer.endDraw();	
	}
	
	/** Show the title of the city if this marker is selected */
	public void showTitle(PGraphics	pg, float x, float y)
	{
		pg.clear();
		
		String name = getCity() + " " + getCountry() + " ";
		String pop = "Pop: " + getPopulation() + " Million";
		
		if (850-x<pg.textWidth(name)){
			x = x-pg.textWidth(name);
		} 
		if (800-y<29){
			y = 771;
		} 
		
		pg.pushStyle();
		
		pg.fill(255, 255, 255);
		pg.textSize(12);
		pg.rectMode(PConstants.CORNER);
		pg.rect(x, y-TRI_SIZE-39, Math.max(pg.textWidth(name), pg.textWidth(pop)) + 6, 39);
		pg.fill(0, 0, 0);
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.text(name, x+3, y-TRI_SIZE-33);
		pg.text(pop, x+3, y - TRI_SIZE -18);
		
		pg.popStyle();
	}
	
	private String getCity()
	{
		return getStringProperty("name");
	}
	
	private String getCountry()
	{
		return getStringProperty("country");
	}
	
	private float getPopulation()
	{
		return Float.parseFloat(getStringProperty("population"));
	}

	@Override
	public void colorMarkerRelatToOthers(PGraphics buffer, int colorChange, float x, float y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showDetailedTitle(PGraphics buffer, float x, float y) {
		// TODO Auto-generated method stub
		
	}
}
