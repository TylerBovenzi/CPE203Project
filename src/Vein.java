import processing.core.PImage;

import java.util.List;
import java.util.Optional;



public class Vein extends ActiveEntity {

    public Vein(String id,Point position, List<PImage> images, int actionPeriod){
        super(id, position, images, actionPeriod);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Point> openPt = world.findOpenAround(this.getPosition());

        if (openPt.isPresent()) {
            Ore ore = Factory.createOre(Functions.ORE_ID_PREFIX + this.getId(), openPt.get(),
                    Functions.ORE_CORRUPT_MIN + Functions.rand.nextInt(
                            Functions.ORE_CORRUPT_MAX - Functions.ORE_CORRUPT_MIN),
                    imageStore.getImageList(Functions.getOreKey()));
            world.addEntity(ore);
            ore.scheduleActions( scheduler, world, imageStore);
        }
        Action action = new Activity(this, world, imageStore, 0);
        scheduler.scheduleEvent( this, action, this.getActionPeriod());
    }

}