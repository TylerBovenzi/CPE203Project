import processing.core.PImage;

import java.util.List;

public abstract class AnimatedEntity extends ActiveEntity{

    private final int animationPeriod;
    private int imageIndex;

    protected AnimatedEntity(String id,Point position,List<PImage> images,int actionPeriod, int animationPeriod){
        super(id, position, images, actionPeriod);
        this.animationPeriod =animationPeriod;
    }

    public void nextImage() {
        this.imageIndex = (this.imageIndex + 1) % this.getImages().size();
    }

    @Override
    protected PImage getCurrentImage() {
        return this.getImages().get(imageIndex);
    }

    protected int getAnimationPeriod() {
        return animationPeriod;
    }

    protected void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        super.scheduleActions(scheduler, world,imageStore);
        scheduler.scheduleEvent( this,this.createAnimationAction( 0), this.getAnimationPeriod());
    }

    protected Action createAnimationAction(int repeatCount) {
        return new Animation( this, null, null,repeatCount);
    }
}
