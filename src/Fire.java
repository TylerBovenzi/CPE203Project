import processing.core.PImage;

import java.util.List;

public class Fire extends AnimatedEntity {
    private static final String FIRE_ID = "fire";
    private static final int FIRE_ACTION_PERIOD = 1100;
    private static final int FIRE_ANIMATION_PERIOD = 100;
    private static final int FIRE_ANIMATION_REPEAT_COUNT = 10;

    public Fire(Point position,List<PImage> images){
        super(FIRE_ID, position, images, FIRE_ACTION_PERIOD, FIRE_ANIMATION_PERIOD);

    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        scheduleActions(scheduler, world, imageStore);
        //scheduler.unscheduleAllEvents( this);
        //world.removeEntity(this);
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent( this, this.createActivityAction(world, imageStore),this.getActionPeriod());
        scheduler.scheduleEvent( this, this.createAnimationAction(FIRE_ANIMATION_REPEAT_COUNT),this.getAnimationPeriod());
    }

}