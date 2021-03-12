import processing.core.PImage;

import java.util.List;



public class Ore extends ActiveEntity {


    public Ore(String id, Point position, List<PImage> images, int actionPeriod){
        super(id, position, images, actionPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Point pos = this.getPosition();

        world.removeEntity(this);
        scheduler.unscheduleAllEvents( this);

        OreBlob blob = Factory.createOreBlob(this.getId() + Functions.getBlobIdSuffix(), pos,
                this.getActionPeriod() / Functions.getBlobPeriodScale(),
                Functions.getBlobAnimationMin() + Functions.rand.nextInt(
                        Functions.getBlobAnimationMax()
                                - Functions.getBlobAnimationMin()),
                imageStore.getImageList( Functions.getBlobKey()));

        world.addEntity(blob);
        blob.scheduleActions(scheduler, world, imageStore);
    }

}