import processing.core.PImage;

import java.util.List;
import java.util.Optional;



public class MinerFull extends Miner {
        public MinerFull(   String id,
                        Point position,
                        List<PImage> images,
                        int resourceLimit,
                        int actionPeriod,
                        int animationPeriod){
        super(id,position, images, resourceLimit, actionPeriod, animationPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(this.getPosition(), Blacksmith.class);

        if (fullTarget.isPresent() && this.move( world, fullTarget.get(), scheduler))
        {
            this.transform(world, scheduler, imageStore);
        }
        else {
            scheduleActions(scheduler, world, imageStore);
        }
    }

    protected boolean move(WorldModel world,Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {return true;}
        else {
            Point nextPos = this.nextPosition( world, target.getPosition());
            if (!this.getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents( occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    protected boolean transform(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        MinerNotFull miner = Factory.createMinerNotFull(this.getId(), this.getResourceLimit(),
                this.getPosition(), this.getActionPeriod(),
                this.getAnimationPeriod(),
                this.getImages());

        world.removeEntity(this);
        scheduler.unscheduleAllEvents( this);

        world.addEntity(miner);
        miner.scheduleActions(scheduler, world, imageStore);
        return true;
    }

}