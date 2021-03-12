public final class Activity implements Action{
    private ActiveEntity entity;
    private WorldModel world;
    private ImageStore imageStore;
    private int repeatCount;

    public Activity(
            ActiveEntity entity,
            WorldModel world,
            ImageStore imageStore,
            int repeatCount)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }


    public void executeAction( EventScheduler scheduler)
    {
        this.entity.executeActivity(this.world,this.imageStore,scheduler);
    }

}
