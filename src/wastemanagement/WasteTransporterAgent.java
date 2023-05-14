package wastemanagement;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.stream.Stream;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;
import repast.simphony.context.Context;


public class WasteTransporterAgent {
    private ContinuousSpace<Object> space;
    private Grid<Object> grid;
    private DisposalSite disposalSite;
    private WasteCollectorAgent currentCollector;
    private static double totalTransportTime = 0; // Add this variable
    private static int numberOfTransports = 0; // Add this variable
    private static double totalDistanceTraveled = 0; // Added static variable to track total distance traveled


    public WasteTransporterAgent(ContinuousSpace<Object> space, Grid<Object> grid, DisposalSite disposalSite) {
        this.space = space;
        this.grid = grid;
        this.disposalSite = disposalSite; 
    }
    
    // Add getters for totalTransportTime and numberOfTransports
    public static double getTotalTransportTime() {
        return totalTransportTime;
    }

    public static int getNumberOfTransports() {
        return numberOfTransports;
    }
    
    public static double getTotalDistanceTraveled() {
        return totalDistanceTraveled;
    }
    

    public WasteCollectorAgent getCurrentCollector() {
        return currentCollector;
    }

    public void collectWaste(int wasteAmount) {
    	// Record the starting time
        long startTime = System.currentTimeMillis();

        this.disposalSite.addWaste(wasteAmount);

        // Record the ending time
        long endTime = System.currentTimeMillis();
        
        long timeTaken = endTime - startTime;
        totalTransportTime += timeTaken;
        numberOfTransports++;
    }


    @ScheduledMethod(start = 1, interval = 1)
    public void update() {
        if (this.currentCollector == null) {
            this.currentCollector = findNearbyCollector();
        }

        if (this.currentCollector != null) {
            NdPoint collectorLocation = space.getLocation(this.currentCollector);
            NdPoint transporterLocation = space.getLocation(this);
            double distanceToCollector = space.getDistance(transporterLocation, collectorLocation);

            if (distanceToCollector <= 1.0) {
                this.currentCollector.transferWasteToTransporter(this);
                this.currentCollector = null; // Reset the current collector after transferring waste

                // Move towards the disposal site after collecting waste
                NdPoint disposalSiteLocation = space.getLocation(disposalSite);
                moveTowards(disposalSiteLocation);
            } else {
                // Move towards the collector
                moveTowards(collectorLocation);
            }
        } else {
            // Move towards the disposal site
            NdPoint disposalSiteLocation = space.getLocation(disposalSite);
            moveTowards(disposalSiteLocation);
        }
    }


        

    private WasteCollectorAgent findNearbyCollector() {
        Context<Object> context = ContextUtils.getContext(this);
        Stream<Object> wasteCollectorsStream = StreamSupport.stream(context.getObjects(WasteCollectorAgent.class).spliterator(), false);
        List<WasteCollectorAgent> wasteCollectors = wasteCollectorsStream.map(obj -> (WasteCollectorAgent) obj).collect(Collectors.toList());

        double searchDistance = 50.0;
        WasteCollectorAgent nearestCollector = null;
        double nearestDistance = Double.MAX_VALUE;

        for (WasteCollectorAgent collector : wasteCollectors) {
            if (collector.getCurrentLoad() > 0) {
                NdPoint collectorLocation = space.getLocation(collector);
                NdPoint currentLocation = space.getLocation(this);
                double distance = space.getDistance(currentLocation, collectorLocation);

                if (distance < searchDistance && distance < nearestDistance) {
                    nearestCollector = collector;
                    nearestDistance = distance;
                }
            }
        }
        return nearestCollector;
    }
    
    public void moveTowards(NdPoint targetPoint) {
        NdPoint currentPoint = space.getLocation(this);
        if (!targetPoint.equals(currentPoint)) {
            double angle = SpatialMath.calcAngleFor2DMovement(space, currentPoint, targetPoint);
            double prevX = currentPoint.getX();
            double prevY = currentPoint.getY();
            space.moveByVector(this, 1, angle, 0);
            NdPoint newPoint = space.getLocation(this);
            double newX = newPoint.getX();
            double newY = newPoint.getY();
            double distance = Math.sqrt(Math.pow(newX - prevX, 2) + Math.pow(newY - prevY, 2));
            totalDistanceTraveled += distance;
            grid.moveTo(this, (int) newX, (int) newY);
        }
    }


}