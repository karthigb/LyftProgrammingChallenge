import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Distance;

public class DetourChallenge {

    private static final String KEY = System.getenv().get("GOOGLE_API_KEY");

    /**
     * Get the distance it would take for a car to travel between two points with a pickup and dropoff
     * in the middle
     * @param aStart A string representation of the starting point in terms of latitude and longitude
     * @param aEnd A string representation of the end point
     * @param bStart A string representation of the passenger pickup location
     * @param bEnd A string representation of the passenger drop off location
     * @return a long representing the distance of the trip in meters
     * @throws Exception
     */
    public static long getDistance(String aStart, String aEnd,String bStart, String bEnd) throws Exception {

        GeoApiContext context = new GeoApiContext().setApiKey(KEY);
        DirectionsApiRequest req = DirectionsApi.getDirections(context,aStart,aEnd);

        //Set detour way points
        req = req.waypoints(bStart,bEnd);

        //Make API call to get route
        DirectionsRoute[] route = req.await();

        long distance = 0;
        for(DirectionsLeg currentLeg: route[0].legs){
            distance += currentLeg.distance.inMeters;
        }
        return distance;

    }

    /**
     * Calculate the shorter detour: A picks up B, drops off B, then heads to own destination, or vice versa.
     * @param aStart A string representation of A's starting point in terms of latitude and longitude
     * @param aEnd A string representation of A's end point in terms of latitude and longitude
     * @param bStart A string representation of B's starting point in terms of latitude and longitude
     * @param bEnd A string representation of B's end point in terms of latitude and longitude
     * @return an int: -1 if A should pickup B, 1 if B should pick up A, 0 if the distance is equal either way
     * @throws Exception
     */
    public static int getShortestDetour(String aStart, String aEnd, String bStart, String bEnd) throws Exception {
        //Driver A detours to get B, then proceeds to destination
        long detourA = getDistance(aStart,aEnd,bStart,bEnd);

        //Driver B detours to get A, then proceeds to destination
        long detourB = getDistance(bStart,bEnd,aStart,aEnd);

        if(detourA<detourB){
            System.out.println("A picks up B. (" + detourA + " meters)");
            return -1;
        } else if(detourB<detourA) {
            System.out.println("B picks up A. (" + detourA + " meters)");
            return 1;
        } else {
            System.out.println("Both detours are the same length. (" + detourA + " meters)");
            return 0;
        }

    }

    public static void main(String[] args){
        String aStart = "43.675092,-79.432939";
        String aEnd = "43.663982,-79.394715";
        String bStart = "43.670050,-79.386669";
        String bEnd = "43.670050,-79.386669";
        try {
            getShortestDetour(aStart,aEnd,bStart,bEnd);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
