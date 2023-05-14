package wastemanagement;

import repast.simphony.space.SpatialMath;
import repast.simphony.space.continuous.ContinuousSpace;
import java.lang.Iterable;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.environment.RunEnvironment;



import java.util.List;

public class WasteCollectorAgent {
    private ContinuousSpace<Object> space; // a reference to the continuous space where the agent is located
    private Grid<Object> grid; // a reference to the grid where the agent is located
    private int capacity; // the maximum amount of waste that the agent can carry
    private int currentLoad; // the amount of waste that the agent is currently carrying
    private WasteType wasteType; // the type of waste that the agent is responsible for collecting
    private List<WasteGeneratingSite> sites; // a list of Waste Generating Sites that the agent can collect waste from
    private static double totalWasteCollected = 0; // Added static variable to track total waste collected
    private static double totalCollectionTime = 0; // New variable to track the total time spent collecting waste
    private static int numberOfCollections = 0; // New variable to track the total number of waste collections made by all agents
    private static double totalDistanceTraveled = 0; // Added static variable to track total distance traveled

    
    	// Constructor for the WasteCollectorAgent
        public WasteCollectorAgent(ContinuousSpace<Object> space, Grid<Object> grid, int capacity, WasteType wasteType, List<WasteGeneratingSite> sites) {
            this.space = space;
            this.grid = grid;
            this.capacity = capacity;
            this.wasteType = wasteType;
            this.currentLoad = 0;
            this.sites = sites;
            }
       
        
        public static double getTotalWasteCollected() {
            return totalWasteCollected;
        }
        
        public static double getTotalCollectionTime() {
            return totalCollectionTime;
        }

        public static int getNumberOfCollections() {
        	return numberOfCollections;
        }
        
        public static double getTotalDistanceTraveled() {
            return totalDistanceTraveled;
        }
        
        // Method to collect waste from a given site
        public void collectWaste(WasteGeneratingSite site) {
        	double startTime = RunEnvironment.getInstance().getCurrentSchedule().getTickCount(); // Record start time
            // Check if the waste collector agent has enough capacity to collect waste
            if (currentLoad < capacity) {
                double wasteToCollect = Math.min(site.getWasteLevel(), capacity - currentLoad);
                currentLoad += wasteToCollect;
                totalWasteCollected += wasteToCollect;
                site.setWasteLevel(site.getWasteLevel() - wasteToCollect);
                double endTime = RunEnvironment.getInstance().getCurrentSchedule().getTickCount(); // Record end time
                totalCollectionTime += (endTime - startTime); // Update the totalCollectionTime variable
                numberOfCollections++; // Increment the numberOfCollections variable
            } else {
                // Signal another waste collector agent close by to come and collect the remaining waste
                WasteCollectorAgent nearbyAgent = findNearbyAgent(); // find a nearby Waste Collector Agent
                if (nearbyAgent != null) {
                    nearbyAgent.collectWaste(site); // signal the nearby agent to collect the remaining waste
                }
            }
        }
        // Method to find a nearby Waste Collector Agent to help collect waste
        private WasteCollectorAgent findNearbyAgent() {
            // Implement logic to find a nearby waste collector agent
            NdPoint location = space.getLocation(this);
            Iterable<Object> allObjects = space.getObjects();
            WasteCollectorAgent nearbyAgent = null;

            for (Object obj : allObjects) {
                if (obj instanceof WasteCollectorAgent) {
                    WasteCollectorAgent agent = (WasteCollectorAgent) obj;
                    double distance = space.getDistance(location, space.getLocation(agent));
                    if (distance <= 1.0 && agent != this && agent.getCurrentLoad() < agent.getCapacity()) {
                        nearbyAgent = agent;
                        break;
                    }
                }
            }
            return nearbyAgent;
        }

        // Method to find a nearby Waste Transporter Agent to transfer the collected waste to Disposal site
        private WasteTransporterAgent findNearbyTransporter() {
            // Implement logic to find a nearby waste transporter agent
            NdPoint location = space.getLocation(this);
            Iterable<Object> allObjects = space.getObjects();
            
            for (Object obj : allObjects) {
                if (obj instanceof WasteTransporterAgent) {
                    WasteTransporterAgent transporter = (WasteTransporterAgent) obj;
                    double distance = space.getDistance(location, space.getLocation(transporter));
                    if (transporter.getCurrentCollector() == null && distance <= 1.0) {
                        return transporter;
                    }
                }
            }
            return null;
        }

        public void transferWasteToTransporter(WasteTransporterAgent transporter) {
            transporter.collectWaste(this.currentLoad);
            this.currentLoad = 0;
        }

        public int getCapacity() {
            return capacity;
        }
        
        private double speed = 2.0; // Adjust the speed value as desired


        public int getCurrentLoad() {
            return currentLoad;
        }

        public WasteType getWasteType() {
            return wasteType;
            }

        @ScheduledMethod(start = 1, interval = 1)
        public void update() {
            WasteGeneratingSite targetSite = null;
            double minDistance = Double.MAX_VALUE;

            NdPoint thisLocation = space.getLocation(this);

            for (WasteGeneratingSite site : sites) {
                if (site.getWasteLevel() > 0 && site.getWasteType() == wasteType) {
                    NdPoint siteLocation = space.getLocation(site);
                    double distance = space.getDistance(thisLocation, siteLocation);
                    if (distance < minDistance) {
                        minDistance = distance;
                        targetSite = site;
                    }
                }
            }

            if (targetSite != null) {
            	
            moveTowards(space.getLocation(targetSite));
            
            // Record the starting time
            long startTime = System.currentTimeMillis(); 

            collectWaste(targetSite);

            // Record the ending time
            long endTime = System.currentTimeMillis();
            
            // Calculate the time taken for collection in seconds
            double timeTaken = (endTime - startTime) / 1000.0; 
            
            // Update the totalCollectionTime and numberOfCollections
            totalCollectionTime += timeTaken;
            numberOfCollections++;
            } else if (currentLoad > 0) {
                WasteTransporterAgent nearbyTransporter = findNearbyTransporter();
                if (nearbyTransporter != null) {
                    moveTowards(space.getLocation(nearbyTransporter));
                    transferWasteToTransporter(nearbyTransporter);
                    // Record the starting time
                    long startTime = System.currentTimeMillis();

                    transferWasteToTransporter(nearbyTransporter);

                    // Record the ending time
                    long endTime = System.currentTimeMillis();

                    // Calculate the time taken for collection in seconds
                    double timeTaken = (endTime - startTime) / 1000.0;

                    // Update the totalCollectionTime and numberOfCollections
                    totalCollectionTime += timeTaken;
                    numberOfCollections++;
                }
            }
        }

        public void moveTowards(NdPoint targetPoint) {
            NdPoint currentPoint = space.getLocation(this);
            if (!targetPoint.equals(currentPoint)) {
                double angle = SpatialMath.calcAngleFor2DMovement(space, currentPoint, targetPoint);
                double prevX = currentPoint.getX();
                double prevY = currentPoint.getY();
                space.moveByVector(this, speed, angle, 0); // Use the speed variable here
                NdPoint newPoint = space.getLocation(this);
                double newX = newPoint.getX();
                double newY = newPoint.getY();
                double distance = Math.sqrt(Math.pow(newX - prevX, 2) + Math.pow(newY - prevY, 2));
                totalDistanceTraveled += distance;
                grid.moveTo(this, (int) newX, (int) newY);
            }
        }
}