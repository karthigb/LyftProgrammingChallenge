import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Distance;

public class DetourChallenge {

    private static final String KEY = System.getenv().get("GOOGLE_API_KEY");

    /**
     * Get the distance it would take for a car to travel between two points
     * @param a A string representation of the starting point in terms of latitude and longitude
     * @param b A string representation of the end point in terms of latitude and longitude
     * @return a long representing the distance of the trip in meters
     * @throws Exception
     */
    public static long getDistance(String a, String b) throws Exception {
        GeoApiContext context = new GeoApiContext().setApiKey(KEY);
        DirectionsApiRequest req = DirectionsApi.getDirections(context,a,b);

        DirectionsRoute[] route = req.await();
        Distance distanceAtoB = route[0].legs[0].distance;
        return distanceAtoB.inMeters;

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
        long distanceAtoB = getDistance(aStart,bStart);
        long distanceBtrip = getDistance(bStart,bEnd);
        long distanceAresume = getDistance(bEnd,aEnd);
        long detourA = distanceAtoB + distanceBtrip + distanceAresume;

        //Driver B detours to get A, then proceeds to destination
        long distanceBtoA = getDistance(bStart,aStart);
        long distanceAtrip = getDistance(aStart,aEnd);
        long distanceBresume = getDistance(aEnd,bEnd);
        long detourB = distanceBtoA + distanceAtrip + distanceBresume;

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
