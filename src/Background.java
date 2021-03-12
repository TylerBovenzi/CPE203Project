import java.util.List;

import processing.core.PImage;

public final class Background
{
    public String id;
    public List<PImage> images;
    public int imageIndex;

    public Background(String id, List<PImage> images) {
        this.id = id;
        this.images = images;
    }

    public PImage getCurrentImage() {
            return this.images.get(this.imageIndex);
    }
}
