import processing.core.PImage;

import java.util.List;

public abstract class Entity {
    private final String id;
    private final List<PImage> images;
    private Point position;

    protected Entity(String id, Point position, List<PImage> images){
        this.id=id;
        this.position = position;
        this.images = images;
    }

    protected PImage getCurrentImage() {
        return this.images.get(0);
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    protected List<PImage>getImages(){
        return images;
    }

    protected String getId(){
        return id;
    }
}
