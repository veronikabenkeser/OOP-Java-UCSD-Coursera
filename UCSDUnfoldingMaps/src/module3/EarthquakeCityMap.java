package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Veronika Benkeser
 * Date: March 1, 2016
 * */
public class EarthquakeCityMap extends PApplet {

	// To keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;
	
	private final int[] colors ={color(0,0,255),color(255,255,0),color(255,0,0)};
	private final int[] radii ={6,9,12};
	private final String[] descriptions = {"Below 4.0","4.0+ Magnitude","5.0+ Magnitude"};

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//Markers
	private List<Marker> markers;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	
	public void setup() {
		size(950, 600, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    // These print statements show you (1) all of the relevant properties 
	    // in the features, and (2) how to get one property and use it
	    if (earthquakes.size() > 0) {
//	    	PointFeature f = earthquakes.get(0);
//	    	System.out.println(f.getProperties());
	    	for(PointFeature pf: earthquakes){
	    		markers.add(createMarker(pf));
	    	}
	    	drawMarkedMap(markers, map);
	    }
	}
		
	// Helper method that takes in an earthquake feature and 
	// returns a SimplePointMarker for that earthquake
	private SimplePointMarker createMarker(PointFeature feature)
	{
		int col;
		int radius;
		// finish implementing and use this method, if it helps.
		SimplePointMarker marker =  new SimplePointMarker(feature.getLocation());
		Object magObj = feature.getProperty("magnitude");
		float mag = Float.parseFloat(magObj.toString());
		
		if(mag<4.0){
			col=colors[0];
			radius=radii[0];
		}else if(mag>=4.0 && mag<=4.9){
			col=colors[1];
			radius=radii[1];
		} else {
			col = colors[2];
			radius=radii[2];
		}
		
		marker.setColor(col);
		marker.setStrokeColor(col);
		marker.setRadius(radius);
		
		return marker;
	}
	
	private void drawMarkedMap(List<Marker> markers,  UnfoldingMap map){
		map.addMarkers(markers);
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}


	// helper method to draw key in GUI
	private void addKey() {	
		fill(250);
		rect(15,50,170,140,7);
		float yCoord=80;
		for(int i=colors.length-1;i>=0;i--){
			fill(colors[i]);
			ellipse(40,yCoord,radii[i]*2,radii[i]*2);
			fill(0);
			text(descriptions[i],75,yCoord+4);
			yCoord+=30+radii[i];
		}
	}
}
