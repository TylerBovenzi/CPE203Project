import processing.core.PImage;

import java.util.List;

public abstract class ActiveEntity extends Entity{

    private final int actionPeriod;

    protected ActiveEntity(String id, Point position, List<PImage> images, int actionPeriod){
        super(id, position, images);
        this.actionPeriod = actionPeriod;
    }

    protected int getActionPeriod(){
        return actionPeriod;
    }

    abstract public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    protected void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent( this, this.createActivityAction(world, imageStore), this.actionPeriod);
    }

     protected Action createActivityAction(WorldModel world, ImageStore imageStore) {
        return new Activity(this, world, imageStore, 0);
    }

}
