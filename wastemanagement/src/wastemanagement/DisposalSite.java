package wastemanagement;

import repast.simphony.engine.schedule.ScheduledMethod;

public class DisposalSite {
    private int wasteProcessed;

    public DisposalSite() {
        this.wasteProcessed = 0;
    }

    // Define disposal site behavior here

    public void addWaste(int wasteAmount) {
        this.wasteProcessed += wasteAmount;
    }

    public int getWasteProcessed() {
        return wasteProcessed;
    }

    public void setWasteProcessed(int wasteProcessed) {
        this.wasteProcessed = wasteProcessed;
    }

     @ScheduledMethod(start = 1, interval = 1)
     public void update() {
         // Implement logic to print the amount of waste processed
         System.out.println("Waste processed: " + this.wasteProcessed);
     }
}
