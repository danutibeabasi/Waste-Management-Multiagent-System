package wastemanagement;

import repast.simphony.engine.schedule.ScheduledMethod;

public class WasteGeneratingSite {
    private double wasteLevel; // Changed from int to double
    private WasteType wasteType;

    public WasteGeneratingSite(int wasteLevel, WasteType wasteType) {
        this.wasteLevel = wasteLevel;
        this.wasteType = wasteType;
    }
    
    private static double totalWasteGenerated = 0; // Add this variable to the class


    // Generates waste at a certain speed level and returns the generated waste
    public double generateWaste(double amount) {
        this.wasteLevel += amount;
        totalWasteGenerated += amount; // Update totalWasteGenerated
        return amount; // Return the generated waste value
    }
    
    public static double getTotalWasteGenerated() {
        return totalWasteGenerated;
    }

    public double getWasteLevel() { // Changed return type from int to double
        return wasteLevel;
    }

    public void setWasteLevel(double wasteLevel) { // Changed parameter type from int to double
        this.wasteLevel = wasteLevel;
    }

    public WasteType getWasteType() {
        return wasteType;
    }

    public void setWasteType(WasteType wasteType) {
        this.wasteType = wasteType;
    }

    @ScheduledMethod(start = 1, interval = 1)
    public void update() {
        generateWaste(/* Define the waste generation speed here */ 2.0);
        
    }
    
}


