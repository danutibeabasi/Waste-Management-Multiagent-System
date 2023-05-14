package wastemanagement;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.random.RandomHelper;

// WasteManagementBuilder.java
public class WasteManagementBuilder implements ContextBuilder<Object> {

    @Override
    public Context<Object> build(Context<Object> context) {
        context.setId("WasteManagement");
         
        // Initialize and add the WasteManagementModel to the context
        WasteManagementModel model = new WasteManagementModel(context);
        context.add(model);

        ContinuousSpace<Object> space = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null).createContinuousSpace("space", context, 
                new RandomCartesianAdder<Object>(),
                new repast.simphony.space.continuous.WrapAroundBorders(),
                50, 50);
        Grid<Object> grid = GridFactoryFinder.createGridFactory(null).createGrid("grid", context, 
                new GridBuilderParameters<Object>(new repast.simphony.space.grid.WrapAroundBorders(),
                        new SimpleGridAdder<Object>(),
                        true, 50, 50));

       
        List<WasteGeneratingSite> wasteSites = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            WasteGeneratingSite site = new WasteGeneratingSite(RandomHelper.nextIntFromTo(1, 100), WasteType.values()[RandomHelper.nextIntFromTo(0, 3)]);
            context.add(site);
            wasteSites.add(site);
            int x = RandomHelper.nextIntFromTo(0, 49);
            int y = RandomHelper.nextIntFromTo(0, 49);
            grid.moveTo(site, x, y);
            space.moveTo(site, x, y);
        }

        List<DisposalSite> disposalSites = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            DisposalSite disposalSite = new DisposalSite();
            context.add(disposalSite);
            disposalSites.add(disposalSite);
            int x = RandomHelper.nextIntFromTo(0, 49);
            int y = RandomHelper.nextIntFromTo(0, 49);
            grid.moveTo(disposalSite, x, y);
            space.moveTo(disposalSite, x, y);
        }

        for (int i = 0; i < 4; i++) {
            WasteCollectorAgent collector = new WasteCollectorAgent(space, grid, RandomHelper.nextIntFromTo(50, 100), WasteType.values()[RandomHelper.nextIntFromTo(0, 3)], wasteSites);
            context.add(collector);
            int x = RandomHelper.nextIntFromTo(0, 49);
            int y = RandomHelper.nextIntFromTo(0, 49);
            grid.moveTo(collector, x, y);
            space.moveTo(collector, x, y);
        }

        for (int i = 0; i < 5; i++) {
            DisposalSite disposalSite = disposalSites.get(RandomHelper.nextIntFromTo(0, disposalSites.size() - 1));
            WasteTransporterAgent transporter = new WasteTransporterAgent(space, grid, disposalSite);
            context.add(transporter);
            int x = RandomHelper.nextIntFromTo(0, 49);
            int y = RandomHelper.nextIntFromTo(0, 49);
            grid.moveTo(transporter, x, y);
            space.moveTo(transporter, x, y);
        }

        return context;
    }
}