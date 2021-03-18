import processing.core.PImage;

import java.util.List;
import java.util.Optional;



public class UFO extends AnimatedEntity {
    private Point dest;
    private boolean hasSmith;

    public UFO(String id,
                   Point position,
                   List<PImage> images,
                   int actionPeriod,
                   int animationPeriod){

        super(id, position, images, actionPeriod, animationPeriod);
        newDest();

    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if(!world.getBackgroundCell(this.getPosition()).id.equals("scorched")){
            world.setBackgroundCell(this.getPosition(),new Background("scorched", imageStore.getImageList("scorched")));
        }

        Optional<Entity> ufoTarget =world.findNearest(dest,Blacksmith.class);
        long nextPeriod = this.getActionPeriod();

        if (ufoTarget.isPresent()&&!hasSmith) {
            Point tgtPos = ufoTarget.get().getPosition();
            if (this.moveToUFO(world, ufoTarget.get(), scheduler)) {
                Quake quake = Factory.createQuake(tgtPos, imageStore.getImageList(Functions.QUAKE_KEY));
                world.addEntity(quake);
                nextPeriod += this.getActionPeriod();
                quake.scheduleActions(scheduler, world, imageStore);
            }
        }
        else if(hasSmith){
            if(this.getPosition().adjacent(dest)){
                world.removeEntityAt(dest);
                world.addEntity(Factory.createBlacksmith("id", dest, imageStore.getImageList("blacksmith")));
                hasSmith =false;
                newDest();
            } else{
                if(nextPositionUFO(world, dest).equals(this.getPosition())){
                    newDest();
                }else{
                    world.moveEntity(this, nextPositionUFO(world,dest));
                }
            }
        }

        Optional<Entity> alien =world.findNearest(this.getPosition(),Alien.class);
        if(alien.isEmpty()) {
            Quake quake = Factory.createQuake(this.getPosition(), imageStore.getImageList(Functions.QUAKE_KEY));
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);
            world.addEntity(quake);
            quake.scheduleActions(scheduler, world, imageStore);
        }else
            scheduleActions(scheduler, world, imageStore);
    }

    private void newDest(){
        dest = new Point((int)(Math.random()*VirtualWorld.WORLD_COLS), (int)(Math.random()*VirtualWorld.WORLD_ROWS));
    }

    private boolean moveToUFO(
            WorldModel world,
            Entity target,
            EventScheduler scheduler) {


        if (this.getPosition().adjacent(target.getPosition())) {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            hasSmith = true;
            newDest();
            return true;
        } else {
            Point nextPos = this.nextPositionUFO(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {

                world.moveEntity(this, nextPos);
            }
            return false;
        }
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