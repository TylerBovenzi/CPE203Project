import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Alien extends AnimatedEntity {

    public Alien(String id,
                 Point position,
                 List<PImage> images,
                 int actionPeriod,
                 int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> alienTarget =world.findNearest(this.getPosition(),MinerNotFull.class);
        long nextPeriod = this.getActionPeriod();

        if (alienTarget.isPresent()) {
            Point tgtPos = alienTarget.get().getPosition();
            if (this.moveToAlien(world, alienTarget.get(), scheduler)) {
                world.removeEntity(alienTarget.get());
                scheduler.unscheduleAllEvents(alienTarget.get());
                Alien alien = Factory.createAlien(this.getId(), tgtPos, this.getActionPeriod(), this.getAnimationPeriod(), this.getImages());

                world.addEntity(alien);
                nextPeriod += this.getActionPeriod();
                alien.scheduleActions(scheduler, world, imageStore);


            }
        }
        else{
            Optional<Entity> ufoTarget =world.findNearest(this.getPosition(),UFO.class);
            if (ufoTarget.isPresent()) {
                Point tgtPos = ufoTarget.get().getPosition();
                if(this.moveToAlien(world, ufoTarget.get(), scheduler)){
                    world.removeEntity(this);
                    scheduler.unscheduleAllEvents(this);
                    nextPeriod += this.getActionPeriod();
                }
            }
        }

        scheduleActions(scheduler, world, imageStore);
    }

    private boolean moveToAlien(

            WorldModel world,
            Entity target,
            EventScheduler scheduler) {

        if (this.getPosition().adjacent(target.getPosition())) {

            return true;

        } else {
            Point nextPos = this.nextPositionAlien(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    protected Point nextPositionAlien(WorldModel world, Point destPos)
    {
        PathingStrategy strategy = new AStarPathingStrategy();

        List<Point> points = strategy.computePath(this.getPosition(), destPos,
                p -> world.withinBounds(p) && !world.isOccupied(p)/*||world.getOccupant(p).getClass().equals(Ore.class)*/,
                Functions::neighbors,
                PathingStrategy.CARDINAL_NEIGHBORS);
        if(points.size()>0)
            return points.get(0);
        return this.getPosition();
    }
}
