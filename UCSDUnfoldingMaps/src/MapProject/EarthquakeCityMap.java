package MapProject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.mapdisplay.AbstractMapDisplay;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import parsing.ParseFeed;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Veronika Benkeser
 * Date: March 7, 2016
 * */
public class EarthquakeCityMap extends PApplet {
	
	// We will use member variables, instead of local variables, to store the data
	// that the setUp and draw methods will need to access (as well as other methods)
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";

	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	// The map
	private UnfoldingMap map;
	
	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;
	
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	private CommonMarker cmTyped;
	private EarthquakeMarker lastClickedQuake;
	private int val = 0;
	private int count = 0;
	private List<EarthquakeMarker> currentlySortedArr;
	
	//create a buffer to draw boxes to
	PGraphics buffer;
		
	public void setup() {		
		// (1) Initializing canvas and map tiles
		size(900, 700, OPENGL);
		buffer = createGraphics(900, 700);
		
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 650, 600, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// FOR TESTING: Set earthquakesURL to be one of the testing files by uncommenting
		// one of the lines below.  This will work whether you are online or offline
//		earthquakesURL = "test1.atom";
//		earthquakesURL = "test2.atom";
		
		// Uncomment this line to take the quiz
		//earthquakesURL = "quiz2.atom";
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  Marker newCity = new CityMarker(city);
		  cityMarkers.add(newCity);
		  cityHashMap.put(newCity, newCity.getLocation());
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  }
		  // OceanQuakes
		  else {
			OceanQuakeMarker oceanQuake = new OceanQuakeMarker(feature);
			oceanQ.add(oceanQuake);
			
			List<Marker> affectedCities = new ArrayList<Marker>();
			double kmCircle = oceanQuake.threatCircle();
			
			for(Marker marker: cityMarkers){
				if(Math.abs(marker.getLocation().getDistance(oceanQuake.getLocation()))<=kmCircle){
					affectedCities.add(marker);
				}
			}
			oceanQuake.setProperty("affectedCities", affectedCities);
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }

	    // could be used for debugging
	    printQuakes();
	 		
	    // (3) Add markers to map
	    //NOTE: Country markers are not added to the map.  They are used
	    //for their geometric properties
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    
	    sortAndPrint(20);
	}  
	
//	We have to rewrite the x and y coordinates of the affected cities. We need to do this
//	because the map may have been zoomed in, moved, etc. 
	private ScreenPosition getScreenPositionOfCity(Location location){
		
		ScreenPosition rawPosition = map.getScreenPosition(location);
		
		float rawX= rawPosition.x;
		float rawY= rawPosition.y;
		float rawZ = rawPosition.z;
		
		AbstractMapDisplay mapdisplay=map.mapDisplay;
		
		float mapOffsetX= mapdisplay.offsetX;
		float mapOffsetY = mapdisplay.offsetY;
		
		rawPosition.set(rawX-mapOffsetX, rawY-mapOffsetY, rawZ);
		return rawPosition;
	}
	
//	Once an ocean earthquake is clicked, it will show lines to cities that may be affected by it.
	private void calculateLines(){
		for(OceanQuakeMarker oceanQuake: oceanQ){
			
			List<ScreenPosition> affectedLocations = new ArrayList<ScreenPosition>();
			List<Marker> affectedCities = (List<Marker>) oceanQuake.getProperty("affectedCities");
			
			for(Marker marker: affectedCities){
				if(cityHashMap.get(marker) !=null){
					Location location = cityHashMap.get(marker);
					
					ScreenPosition position = getScreenPositionOfCity(location);
					affectedLocations.add(position);	
				}
			}
			
			oceanQuake.setProperty("affectedLocations", affectedLocations);
		}
	}
	
	//Main draw method executes in a loop after the setup method is completed.
	public void draw() {
		background(0);
		buffer.beginDraw();
		map.draw();
		buffer.endDraw();
		image(buffer, 0, 0);
		calculateLines();
		addKey();
		buffer.clear();
		  
		if(lastSelected !=null ){
			lastSelected.drawTitleOnTop(buffer, mouseX, mouseY);
		}
	}
	
	//This method sorts and prints earthquakes based on their magnitude. If we're asked to print more items 
	//that are available in the array, only print as many items as are available in the array.
	   private void sortAndPrint(int numToPrint){
		   
		   int sizeOfQuakesArr = quakeMarkers.size();
		   
		   if(numToPrint>sizeOfQuakesArr){
			   numToPrint=sizeOfQuakesArr;
		   }
		   
		   List<EarthquakeMarker> quakeArr= sortEarthquakes("magnitude");
		   
		   if(quakeArr==null){
			   return;
		   }
		   for(int i=0; i<numToPrint; i++){
			   System.out.println(quakeArr.get(i));
		   }
	   }
	
	   //This method determines which comparator method to use in order to compare earthquakes. 
	   //The user can compare earthquakes by their depth, magnitude, and radius.
	   private Comparator<EarthquakeMarker>  getComparatorType(String sortParameter){
		   Comparator<EarthquakeMarker> comparatorType=null;
		   if(sortParameter == "magnitude"){
			   comparatorType= EarthquakesComparator.getMagnitudeComparator();
		   } else if(sortParameter == "radius"){
			   comparatorType= EarthquakesComparator.getRadiusComparator();
		   } else {
			   comparatorType= EarthquakesComparator.getDepthComparator();
		   }
		   return comparatorType;
	   }
	   
	   //This method sorts earthquakes by invoking a custom Comparator
	   private List<EarthquakeMarker> sortEarthquakes( String sortParameter){
		   Comparator<EarthquakeMarker> comparatorType = getComparatorType(sortParameter);
		   if(comparatorType ==null){
			   return null;
		   }
		   
		   if(quakeMarkers.get(0) instanceof EarthquakeMarker){
			   List<?> arr= quakeMarkers;
			   List<EarthquakeMarker> quakeArr = (List<EarthquakeMarker>) arr;
			   
			   Collections.sort(quakeArr, comparatorType);
			   return quakeArr;
		   }
		   return null;
	   }
	   
	   //This method executes the command the user types via her keyboard by first sorting all of the earthquakes
	   //by a parameter specified by the user and then coloring them based on how they compare to other earthquakes
	   //along that parameter.
	   
	   //CD - Color by Depth
	   //CM - Color by Magnitude
	   //CR - Color by Radius
	   
	   private void executeCombination(){
		   
		   String parameter="";
		   
		   if(val !=3 && val != 4 && val !=5){
			   return;
		   }
		   if(val==3){
			   parameter="depth";
		   } else if(val ==4){
			   parameter="magnitude";
		   } else {
			   parameter="radius";
		   }
		   
		   List<EarthquakeMarker> quakeArr= sortEarthquakes(parameter);
		   currentlySortedArr = quakeArr;
		   
		   if(parameter == "magnitude"){
			   colorByMagnitude(currentlySortedArr);
		   } else if (parameter == "depth"){
			   colorByDepth(currentlySortedArr);
		   } else {
			   colorByRadius(currentlySortedArr);
		   }		   
	   }
	   
	   //This method takes an array of EarthquakeMarkers as a parameter and resets the color of each marker
	   //to its original color
	   private void resetColor(List<EarthquakeMarker> quakeArr){
		   for(int i=0; i<quakeArr.size();i++){
				   quakeArr.get(i).setCMTyped(false); 
		   }
		   cmTyped=null;  
	   }
	   
	   //This method sets the EarthquakeMarker whose color needs to be changes based on its depth, magnitude, or
	   //radius as compared to other earthquakes
	   private void changeRelativeColor(EarthquakeMarker marker, int colorChange){
		   cmTyped = marker;
		   cmTyped.setCMTyped(true);
		   cmTyped.setCMValue(colorChange);
	   }
	   
	   //This method takes in an array of EarthquakeMarkers that have been sorted by their magnitude
	   //and colors them appropriately, depending on their position in the array. High
	   //earthquake magnitude is associated with the color red.
	   private void colorByMagnitude(List<EarthquakeMarker> quakeArr){
		   int colorChange=255;
		   for(int i=0; i<quakeArr.size();i++){
			   if(i-1>=0 &&  quakeArr.get(i).getMagnitude() ==  quakeArr.get(i-1).getMagnitude()){
				   colorChange+=10;
			   }
			   changeRelativeColor(quakeArr.get(i), colorChange);
			   colorChange-=10;
		   }
	   }
	   
	   //This method takes in an array of EarthquakeMarkers that have been sorted by their depth
	   //and colors them appropriately, depending on their position in the array. Large
	   //earthquake depth is associated with the color red.
	   private void colorByDepth(List<EarthquakeMarker> quakeArr){
		   int colorChange=255;
		   for(int i=0; i<quakeArr.size();i++){
			   if(i-1>=0 &&  quakeArr.get(i).getDepth() ==  quakeArr.get(i-1).getDepth()){
				   colorChange+=2;
			   }
			   changeRelativeColor(quakeArr.get(i), colorChange);
			   colorChange-=2;
		   }
	   }
	   
	   //This method takes in an array of EarthquakeMarkers that have been sorted by their radius
	   //and colors them appropriately, depending on their position in the array. High
	   //earthquake radius is associated with the color red. 
	   private void colorByRadius(List<EarthquakeMarker> quakeArr){
		   int colorChange=255;
		   for(int i=0; i<quakeArr.size();i++){
			   if(i-1>=0 &&  quakeArr.get(i).getRadius() ==  quakeArr.get(i-1).getRadius()){
				   colorChange+=20;
			   }
			   changeRelativeColor(quakeArr.get(i), colorChange);
			   colorChange-=20;
		   }
	   }
	   
	   /** Event handler that gets called automatically when the 
		 * a key is released. This method allows the user to enter in commands.
		 */
	   public void keyReleased() {
		   
		   if(key == 'C' || key =='c'){
			   val=1;
			   count=1;
		   } else if (count ==1 && (key == 'D' || key =='d')){
			   val+=2;
			   count++;
		   } else if(count ==1 && (key == 'M' || key =='m')){
			   val+=3;
			   count++;
		   } else if(count ==1 && (key == 'R' || key =='r')){
			   val+=4;
			   count++;
		   } else if(key=='Q'|| key=='q'){
			   
			   //return to original state
			   if(cmTyped !=null){
				   resetColor(currentlySortedArr);
			   }
			   val=0;
			   count=0;
		   } else {
			   val=0;
			   count=0;
		   }
		   executeCombination();
		 }
	   
	   
	   /** Event handler that gets called automatically when the 
		 * mouse moves.
		 */
		@Override
		public void mouseMoved()
		{
			// clear the last selection
			if (lastSelected != null) {
				lastSelected.setSelected(false);
				lastSelected = null;
			}
			selectMarkerIfHover(quakeMarkers);
			selectMarkerIfHover(cityMarkers);
		}
		
		// If the user hovers over a city or an earthquake, 1 label becomes visible.
		private void selectMarkerIfHover(List<Marker> markers)
		{
			//To prevent labels from being shown when the user hovers outside of the map
			if(mouseX<200 || mouseX> 850|| mouseY<50|| mouseY>650){
				return;
			}
			
			for(Marker marker: markers){
				if(marker.isInside(map, mouseX, mouseY) && lastSelected == null){
					lastSelected = (CommonMarker) marker;
					lastSelected.setSelected(true);
					break;
				}
			}
		}
		
	/** The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
		@Override
		public void mouseClicked()
		{
			
			if(lastClicked !=null){
				lastClicked.setClicked(false);
				lastClicked= null;
				lastClickedQuake=null;
				unhideMarkers();
			} else {
				displayThreat(mouseX, mouseY);
			}
		}
		
		//Method which determines whether an earthquake marker was clicked
		private boolean quakeMarkerClicked(float x, float y){
			return markerClicked(quakeMarkers, x, y);
		}
		
		//Method which determines whether a city marker was clicked
		private boolean cityMarkerClicked(float x, float y){
			return markerClicked(cityMarkers, x,y);
		}
		
		//This method goes through the markers of a particular category in order to determine and set the
		//value of the lastClicked variable
		private boolean markerClicked(List<Marker> category, float x, float y){
			for(Marker marker: category){
				if(marker.isInside(map, x, y) && lastClicked == null){
					lastClicked = (CommonMarker) marker;
					lastClicked.setSelected(true);
					
					if(marker.getClass().toString().equals("class MapProject.LandQuakeMarker") ||
					   marker.getClass().toString().equals("class MapProject.OceanQuakeMarker")){
						lastClickedQuake = (EarthquakeMarker) marker;
					} 
					return true;
				}
			}
			return false;
		}
		
		//Method for displaying cities that could be affected by a particular earthquake
		private void displayCitiesWithinThreatCircle(double dangerRadius, Location quakeLocation){
			for(Marker marker: cityMarkers){
				
				if(Math.abs(marker.getLocation().getDistance(quakeLocation))>dangerRadius){
					hideMarker(marker);
				} else {
					displayMarker(marker);
				}
			}
		}
		
		//Method for displaying a marker
		private void displayMarker(Marker marker){
			marker.setHidden(false);
		}
		
		//Method for hiding a marker
		private void hideMarker(Marker marker){
			marker.setHidden(true);
		}
		
		//This method determines whether the user clicked on a city or an earthquake. If the user clicked on a city,
		//the method determines all the earthquakes that could threaten that city. If the user clicked on
		//an earthquake, the method determines all the cities that could be affected by that earthquake. All
		//other earthquakes and cities are hidden.
		
		private void displayThreat(float x, float y){
			
			boolean quakeMarkerSelected = false;
			boolean cityMarkerSelected = cityMarkerClicked(x,y);
			
			if(!cityMarkerSelected){
				quakeMarkerSelected = quakeMarkerClicked(x,y);
				if(!quakeMarkerSelected){
					return;
				} else{
					double kmCircle = lastClickedQuake.threatCircle();
					displayCitiesWithinThreatCircle(kmCircle,lastClicked.getLocation());
					//All other earthquakes are hidden
					hideAllExceptOne(quakeMarkers, lastClicked);
				}
			} else {
				//All earthquakes which contain that city in their threat circle are displayed; all other earthquakes are hidden
				Location cityLoc = lastClicked.getLocation();
				markDangerousQuakes(cityLoc);
				//All other cities are hidden
				hideAllExceptOne(cityMarkers, lastClicked);
			}
			lastClicked.setClicked(true);
		}
		
		//Method for finding all earthquakes that contain a given city in their threat circle
		private void markDangerousQuakes(Location cityLocation){
			for(Marker quake: quakeMarkers){
				EarthquakeMarker m = (EarthquakeMarker) quake;
				double km = m.threatCircle();
				if(Math.abs(cityLocation.getDistance(quake.getLocation()))>km){
					hideMarker(quake);
				}else{
					displayMarker(quake);
				}
			}
		}
		
		//Method for hiding all markers except the clicked marker
		private void hideAllExceptOne(List<Marker> markerType, CommonMarker notHidden){
			for(Marker marker : markerType) {
				if(marker != (Marker)notHidden){
					hideMarker(marker);
				} else {
					displayMarker(marker);
				}
			}
		}
		
	
	// Method for unhiding all markers
	private void unhideMarkers() {
		for(Marker marker : quakeMarkers) {
			marker.setHidden(false);
		}
			
		for(Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
	}
	
	// Helper method to draw key in GUI
	private void addKey() {	
		stroke(0);
		fill(255, 250, 240);
		
		int xbase = 25;
		int ybase = 50;
		
		rect(xbase, ybase, 170, 380);
		
		fill(0);
		textAlign(LEFT, CENTER);
		
		text("Earthquake Key", xbase+25, ybase+25);
		
		fill(0, 230, 230);
		int tri_xbase = xbase + 22;
		int tri_ybase = ybase + 50;
		triangle(tri_xbase, tri_ybase-CityMarker.TRI_SIZE, tri_xbase-CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE, tri_xbase+CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE);

		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		text("City Marker", tri_xbase + 24, tri_ybase);
		
		text("Land Quake", xbase+47, ybase+70);
		text("Ocean Quake", xbase+47, ybase+90);
		text("Size ~ Magnitude", xbase+16, ybase+115);
		
		fill(255, 255, 255);
		ellipse(xbase+22, 
				ybase+70, 
				10, 
				10);
		rect(xbase+22-5, ybase+90-5, 10, 10);
		
		fill(color(255, 255, 0));
		ellipse(xbase+22, ybase+140, 12, 12);
		fill(color(0, 0, 255));
		ellipse(xbase+22, ybase+160, 12, 12);
		fill(color(255, 0, 0));
		ellipse(xbase+22, ybase+180, 12, 12);
		
		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Shallow", xbase+47, ybase+140);
		text("Intermediate", xbase+47, ybase+160);
		text("Deep", xbase+47, ybase+180);

		text("Past hour", xbase+47, ybase+200);
		
		fill(255, 255, 255);
		int centerx = xbase+22;
		int centery = ybase+200;
		ellipse(centerx, centery, 12, 12);

		strokeWeight(2);
		line(centerx-8, centery-8, centerx+8, centery+8);
		line(centerx-8, centery+8, centerx+8, centery-8);
		
		fill(0, 0, 0);
		text("Keyboard Commands", xbase+16, ybase+225);
		text("CM - Compare Magnitude", xbase+15, ybase+249);
		text("CD - Compare Depth ", xbase+15, ybase+269);
		text("CR - Compare Radius", xbase+15, ybase+289);
		text("Q", xbase+15, ybase+309);
		text("- Original View",xbase+35, ybase+309);
		
		float w = 110;
		float h = 20;
		int xStart= xbase+25;
		int yStart= ybase+329;
		
		noFill();
		
		int c2 = color(255, 0, 0);
		int c1 = color(0, 0, 255);
			for (int i = xStart; i <=  xStart+w; i++) {
				float inter = map(i, xStart,  xStart+w, 0, 1);
			    int c = lerpColor(c1, c2, inter);
			    stroke(c);
			    line(i, yStart, i, yStart+h);
			}
			
			text("high", xbase+116, ybase+353);
			text("low", xbase+20, ybase+353);
	}

	
	
	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.
	private boolean isLand(PointFeature earthquake) {
		
		// If it is, add 1 to the entry in countryQuakes corresponding to this country.
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}
		
		// not inside any country
		return false;
	}
	
	// Prints countries with number of earthquakes
	private void printQuakes() {
		int totalWaterQuakes = quakeMarkers.size();
		for (Marker country : countryMarkers) {
			String countryName = country.getStringProperty("name");
			int numQuakes = 0;
			for (Marker marker : quakeMarkers)
			{
				EarthquakeMarker eqMarker = (EarthquakeMarker) marker;
				if (eqMarker.isOnLand()) {
					if (countryName.equals(eqMarker.getStringProperty("country"))) {
						numQuakes++;
					}
				}
			}
			if (numQuakes > 0) {
				totalWaterQuakes -= numQuakes;
				System.out.println(countryName + ": " + numQuakes);
			}
		}
		System.out.println("OCEAN QUAKES: " + totalWaterQuakes);
	}
	
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake feature if 
	// it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// Check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}
	
	List<OceanQuakeMarker> oceanQ = new ArrayList<OceanQuakeMarker>();
	HashMap<Marker,Location> cityHashMap = new HashMap<Marker,Location>();
}
