import processing.core.PImage;

import java.util.List;
import java.util.Optional;



public class UFO extends AnimatedEntity {

    private Point randomTarget;
    private boolean hasSmith;

    public UFO(String id,
                   Point position,
                   List<PImage> images,
                   int actionPeriod,
                   int animationPeriod){
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> ufoTarget =world.findNearest(this.getPosition(),Blacksmith.class);
        long nextPeriod = this.getActionPeriod();

        if (ufoTarget.isPresent()) {
            Point tgtPos = ufoTarget.get().getPosition();

            if (this.moveToUFO(world, ufoTarget.get(), scheduler)) {
                Quake quake = Factory.createQuake(tgtPos, imageStore.getImageList(Functions.QUAKE_KEY));

                world.addEntity(quake);
                nextPeriod += this.getActionPeriod();
                quake.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduleActions(scheduler, world, imageStore);
    }

    private boolean moveToUFO(
            WorldModel world,
            Entity target,
            EventScheduler scheduler) {

        if(!hasSmith){
        if (this.getPosition().adjacent(target.getPosition())) {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            hasSmith = true;
            randomTarget = new Point((int)(VirtualWorld.WORLD_COLS*Math.random())-2,(int)(VirtualWorld.WORLD_ROWS*Math.random())-1);
            return true;
        } else {
            Point nextPos = this.nextPositionUFO(world, target.getPosition());

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
        else{
            Point nextPos = this.nextPositionUFO(world,randomTarget);
            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity(this,nextPos);
            }
        }
        return false;
    }

    protected Point nextPositionUFO( WorldModel world, Point destPos)
    {
        PathingStrategy strategy = new AStarPathingStrategy();

        List<Point> points = strategy.computePath(this.getPosition(), destPos,
                p -> world.withinBounds(p) && !world.isOccupied(p)||world.getOccupant(p).getClass().equals(Ore.class) ,
                Functions::neighbors,
                PathingStrategy.CARDINAL_NEIGHBORS);
        if(points.size()>0)
            return points.get(0);
        return this.getPosition();
    }

}