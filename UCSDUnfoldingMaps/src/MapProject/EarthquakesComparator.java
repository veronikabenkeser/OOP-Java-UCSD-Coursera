package MapProject;

import java.util.Comparator;

/** Implements a custom Comparator in order to compare earthquakes along different parameters
 * 
 * @author Veronika Benkeser
 *
 */
public class EarthquakesComparator {
	public static Comparator<EarthquakeMarker> getDepthComparator(){
		return new Comparator<EarthquakeMarker>(){

			@Override
			public int compare(EarthquakeMarker m1, EarthquakeMarker m2) {
				 if(m1.getDepth()<m2.getDepth()){
					 return 1;
				 } else if (m1.getDepth()>m2.getDepth()){
					 return -1;
				 } else {
					 return 0;
				 }
			}
			
		};
	}
	
	public static Comparator<EarthquakeMarker> getMagnitudeComparator(){
		return new Comparator<EarthquakeMarker>(){

			@Override
			public int compare(EarthquakeMarker m1, EarthquakeMarker m2) {
				 if(m1.getMagnitude()<m2.getMagnitude()){
					 return 1;
				 } else if (m1.getMagnitude()>m2.getMagnitude()){
					 return -1;
				 } else {
					 return 0;
				 }
			}
			
		};
	}
	
	public static Comparator<EarthquakeMarker> getRadiusComparator(){
		return new Comparator<EarthquakeMarker>(){
			
			@Override
			public int compare(EarthquakeMarker m1, EarthquakeMarker m2){
				if(m1.getRadius()>m2.getRadius()){
					return -1;
				}else if(m1.getRadius()<m2.getRadius()){
					return 1;
				}else{
					return 0;
				}
			}
		};
	}
}
