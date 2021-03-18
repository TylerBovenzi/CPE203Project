import processing.core.PImage;

import java.util.List;
import java.util.Optional;



public class OreBlob extends AnimatedEntity {

    public OreBlob(String id,
                   Point position,
                   List<PImage> images,
                   int actionPeriod,
                   int animationPeriod){
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> blobTarget =world.findNearest(this.getPosition(),Vein.class);
        long nextPeriod = this.getActionPeriod();

        if (blobTarget.isPresent()) {
            Point tgtPos = blobTarget.get().getPosition();

            if (this.moveToOreBlob(world, blobTarget.get(), scheduler)) {
                Quake quake = Factory.createQuake(tgtPos, imageStore.getImageList(Functions.QUAKE_KEY));

                world.addEntity(quake);
                nextPeriod += this.getActionPeriod();
                quake.scheduleActions(scheduler, world, imageStore);
            }
            scheduleActions(scheduler, world, imageStore);
        }
        else{
            Optional<Entity> blobTarget2 =world.findNearest(this.getPosition(),Fire.class);
            if(blobTarget2.isPresent()){
                world.moveEntity(this,nextPositionOreBlob(world,blobTarget2.get().getPosition()));
                if(this.getPosition().adjacent(blobTarget2.get().getPosition())){
                    Fire fire = new Fire(this.getPosition(),imageStore.getImageList("fire"));
                    world.removeEntity(this);
                    world.addEntity(fire);
                    fire.scheduleActions(scheduler,world,imageStore);
                    scheduler.unscheduleAllEvents(this);
                }else
                    scheduleActions(scheduler, world, imageStore);

                nextPeriod += this.getActionPeriod();
            }

        }




    }

    private boolean moveToOreBlob(
            WorldModel world,
            Entity target,
            EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        } else {
            Point nextPos = this.nextPositionOreBlob(world, target.getPosition());

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
    /*
    private Point nextPositionOreBlob(WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - this.getPosition().x);
        Point newPos = new Point(this.getPosition().x + horiz, this.getPosition().y);

        Optional<Entity> occupant = world.getOccupant(newPos);

        if (horiz == 0 || (occupant.isPresent() && !(occupant.get().getClass()
                == Ore.class))) {
            int vert = Integer.signum(destPos.y - this.getPosition().y);
            newPos = new Point(this.getPosition().x, this.getPosition().y + vert);
            occupant = world.getOccupant(newPos);

            if (vert == 0 || (occupant.isPresent() && !(occupant.get().getClass()
                    == Ore.class))) {
                newPos = this.getPosition();
            }
        }

        return newPos;
    }
    */
    protected Point nextPositionOreBlob( WorldModel world, Point destPos)
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