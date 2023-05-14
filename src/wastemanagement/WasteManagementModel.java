package wastemanagement;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;


public class WasteManagementModel {
    private Context<Object> context;
    private double totalWasteGenerated; // Add this variable
    

    public WasteManagementModel(Context<Object> context) {
        this.context = context;
        this.totalWasteGenerated = 0; // Initialize it to 0
        
    }
    
    
    
    // Add a getter for totalWasteGenerated
    public double getTotalWasteGenerated() {
        return totalWasteGenerated;
    }

    // Add this method to increment the totalWasteGenerated
    public void incrementTotalWasteGenerated(double wasteGenerated) {
        this.totalWasteGenerated += wasteGenerated;
    }
    
    // Add this method to get the average collection time
    public double getAverageCollectionTime() {
        int numberOfCollections = WasteCollectorAgent.getNumberOfCollections();
        if (numberOfCollections == 0) {
            return 0;
        } else {
            double totalCollectionTime = WasteCollectorAgent.getTotalCollectionTime();
            return totalCollectionTime / numberOfCollections;
        }
    }
    
    public double getTotalDistanceTraveledByAllAgents() {
        double collectorDistance = WasteCollectorAgent.getTotalDistanceTraveled();
        double transporterDistance = WasteTransporterAgent.getTotalDistanceTraveled();
        return collectorDistance + transporterDistance;
    }
    
    public double getWasteCollectionPercentage() {
        double totalWasteGenerated = WasteGeneratingSite.getTotalWasteGenerated();
        double totalWasteCollected = WasteCollectorAgent.getTotalWasteCollected();
        return (totalWasteCollected / totalWasteGenerated) * 100;
    }

    @ScheduledMethod(start = 1, interval = 1)
    public void step() {
        System.out.println("Running step() method");
        System.out.println("Total distance traveled by all agents: " + 
        getTotalDistanceTraveledByAllAgents());
        
        // Loop through all WasteCollectorAgent instances and execute their behavior
        for (Object obj : context.getObjects(WasteCollectorAgent.class)) {
            WasteCollectorAgent collector = (WasteCollectorAgent) obj;
            collector.update();
        }

        // Loop through all WasteTransporterAgent instances and execute their behavior
        for (Object obj : context.getObjects(WasteTransporterAgent.class)) {
            WasteTransporterAgent transporter = (WasteTransporterAgent) obj;
            transporter.update();
        }

        // Loop through all WasteGeneratingSite instances and generate waste
        for (Object obj : context.getObjects(WasteGeneratingSite.class)) {
            WasteGeneratingSite site = (WasteGeneratingSite) obj;
            site.generateWaste(/* Define the waste generation speed here */
                3.0
            );
         }
        
     // Loop through all WasteGeneratingSite instances and generate waste
        for (Object obj : context.getObjects(WasteGeneratingSite.class)) {
            WasteGeneratingSite site = (WasteGeneratingSite) obj;
            double wasteGenerated = site.generateWaste(3.0); // Store the returned wasteGenerated value
            incrementTotalWasteGenerated(wasteGenerated); // Update the totalWasteGenerated
        }
    }
    
    @ScheduledMethod(start = 1, interval = 1)
    public void printWasteProcessed() {
	    //Loop through all DisposalSite instances and print the amount of waste processed
	   for (Object obj : context.getObjects(DisposalSite.class)) {
	    DisposalSite disposalSite = (DisposalSite) obj;
	      disposalSite.update();
	       }
     }
    
    
    @ScheduledMethod(start = 1, interval = 1)
    public void printWasteProcessedAndCollectionPercentage() {
        // Loop through all DisposalSite instances and print the amount of waste processed
        for (Object obj : context.getObjects(DisposalSite.class)) {
            DisposalSite disposalSite = (DisposalSite) obj;
            disposalSite.update();
        	}

        double totalWasteGenerated = WasteGeneratingSite.getTotalWasteGenerated();
        double totalWasteCollected = WasteCollectorAgent.getTotalWasteCollected();
        double collectionPercentage = (totalWasteCollected / totalWasteGenerated) * 100;
        double averageCollectionTime = getAverageCollectionTime();
        
        System.out.println("Total waste generated: " + totalWasteGenerated);
        System.out.println("Total waste collected: " + totalWasteCollected);
        System.out.println("Waste collection percentage: " + collectionPercentage + "%");
        System.out.println("Average time taken for waste collection: " + averageCollectionTime);
        }

    @ScheduledMethod(start = 1, interval = 1)
    public void printTransportTimeStats() {
        double totalTransportTime = WasteTransporterAgent.getTotalTransportTime();
        int numberOfTransports = WasteTransporterAgent.getNumberOfTransports();
        double averageTransportTime = 0;

        if (numberOfTransports > 0) {
            averageTransportTime = totalTransportTime / numberOfTransports;
        }

        System.out.println("Total transport time: " + totalTransportTime);
        System.out.println("Number of transports: " + numberOfTransports);
        System.out.println("Average transport time: " + averageTransportTime);
        
        
    }
    
}

