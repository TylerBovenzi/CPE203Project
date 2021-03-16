import processing.core.PApplet;
import processing.core.PImage;

import java.util.*;

public final class Functions
{

    private static final int PROPERTY_KEY = 0;

    private static final String BGND_KEY = "background";
    private static final int BGND_NUM_PROPERTIES = 4;
    private static final int BGND_ID = 1;
    private static final int BGND_COL = 2;
    private static final int BGND_ROW = 3;

    public static final String QUAKE_KEY = "quake";
    private static final String BLOB_KEY = "blob";
    private static final String BLOB_ID_SUFFIX = " -- blob";
    private static final int BLOB_PERIOD_SCALE = 4;
    private static final int BLOB_ANIMATION_MIN = 50;
    private static final int BLOB_ANIMATION_MAX = 150;

    private static final String MINER_KEY = "miner";
    private static final int MINER_NUM_PROPERTIES = 7;
    private static final int MINER_ID = 1;
    private static final int MINER_COL = 2;
    private static final int MINER_ROW = 3;
    private static final int MINER_LIMIT = 4;
    private static final int MINER_ACTION_PERIOD = 5;
    private static final int MINER_ANIMATION_PERIOD = 6;

    private static final String OBSTACLE_KEY = "obstacle";
    private static final int OBSTACLE_NUM_PROPERTIES = 4;
    private static final int OBSTACLE_ID = 1;
    private static final int OBSTACLE_COL = 2;
    private static final int OBSTACLE_ROW = 3;

    private static final String ORE_KEY = "ore";
    private static final int ORE_NUM_PROPERTIES = 5;
    private static final int ORE_ID = 1;
    private static final int ORE_COL = 2;
    private static final int ORE_ROW = 3;
    private static final int ORE_ACTION_PERIOD = 4;

    private static final String SMITH_KEY = "blacksmith";
    private static final int SMITH_NUM_PROPERTIES = 4;
    private static final int SMITH_ID = 1;
    private static final int SMITH_COL = 2;
    private static final int SMITH_ROW = 3;

    private static final String VEIN_KEY = "vein";
    private static final int VEIN_NUM_PROPERTIES = 5;
    private static final int VEIN_ID = 1;
    private static final int VEIN_COL = 2;
    private static final int VEIN_ROW = 3;
    private static final int VEIN_ACTION_PERIOD = 4;

    public static final Random rand = new Random();

    public static final String ORE_ID_PREFIX = "ore -- ";
    public static final int ORE_CORRUPT_MIN = 20000;
    public static final int ORE_CORRUPT_MAX = 30000;
    public static final int ORE_REACH = 1;

    public static final int COLOR_MASK = 0xffffff;
    public static final int KEYED_IMAGE_MIN = 5;
    private static final int KEYED_RED_IDX = 2;
    private static final int KEYED_GREEN_IDX = 3;
    private static final int KEYED_BLUE_IDX = 4;

    private static final String ALIEN_KEY = "alien";
    private static final int ALIEN_ID = 1;


    public static void processImageLine(
            Map<String, List<PImage>> images, String line, PApplet screen)
    {
        String[] attrs = line.split("\\s");
        if (attrs.length >= 2) {
            String key = attrs[0];
            PImage img = screen.loadImage(attrs[1]);
            if (img != null && img.width != -1) {
                List<PImage> imgs = getImages(images, key);
                imgs.add(img);

                if (attrs.length >= KEYED_IMAGE_MIN) {
                    int r = Integer.parseInt(attrs[KEYED_RED_IDX]);
                    int g = Integer.parseInt(attrs[KEYED_GREEN_IDX]);
                    int b = Integer.parseInt(attrs[KEYED_BLUE_IDX]);
                    setAlpha(img, screen.color(r, g, b), 0);
                }
            }
        }
    }

    public static List<PImage> getImages(
            Map<String, List<PImage>> images, String key)
    {
        List<PImage> imgs = images.get(key);
        if (imgs == null) {
            imgs = new LinkedList<>();
            images.put(key, imgs);
        }
        return imgs;
    }

    /*
      Called with color for which alpha should be set and alpha value.
      setAlpha(img, color(255, 255, 255), 0));
    */
    public static void setAlpha(PImage img, int maskColor, int alpha) {
        int alphaValue = alpha << 24;
        int nonAlpha = maskColor & COLOR_MASK;
        img.format = PApplet.ARGB;
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            if ((img.pixels[i] & COLOR_MASK) == nonAlpha) {
                img.pixels[i] = alphaValue | nonAlpha;
            }
        }
        img.updatePixels();
    }


    public static String getBlobKey() {
        return BLOB_KEY;
    }

    public static String getBlobIdSuffix() {
        return BLOB_ID_SUFFIX;
    }

    public static int getBlobPeriodScale() {
        return BLOB_PERIOD_SCALE;
    }

    public static int getBlobAnimationMin() {
        return BLOB_ANIMATION_MIN;
    }

    public static int getBlobAnimationMax() {
        return BLOB_ANIMATION_MAX;
    }


    public static Optional<Entity> nearestEntity(
            List<Entity> entities, Point pos)
    {
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        else {
            Entity nearest = entities.get(0);
            int nearestDistance = nearest.getPosition().distanceSquared(pos);

            for (Entity other : entities) {
                int otherDistance = other.getPosition().distanceSquared(pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }



    public static int clamp(int value, int low, int high) {
        return Math.min(high, Math.max(value, low));
    }


    public static boolean neighbors(Point p1, Point p2)
    {
        return p1.x+1 == p2.x && p1.y == p2.y ||
                p1.x-1 == p2.x && p1.y == p2.y ||
                p1.x == p2.x && p1.y+1 == p2.y ||
                p1.x == p2.x && p1.y-1 == p2.y;
    }

    public static boolean processLine(String line, WorldModel world, ImageStore imageStore)
    {
        String[] properties = line.split("\\s");
        if (properties.length > 0) {
            switch (properties[PROPERTY_KEY]) {
                case BGND_KEY:
                    return parseBackground(properties, world, imageStore);
                case MINER_KEY:
                    return parseMiner(properties,world, imageStore);
                case OBSTACLE_KEY:
                    return parseObstacle(properties, world, imageStore);
                case ORE_KEY:
                    return parseOre(properties, world,imageStore);
                case SMITH_KEY:
                    return parseSmith(properties,  world, imageStore);
                case VEIN_KEY:
                    return parseVein(properties,  world,imageStore);
            }
        }

        return false;
    }

    public static boolean parseBackground(
            String[] properties, WorldModel world, ImageStore imageStore)
    {
        if (properties.length == BGND_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[BGND_COL]),
                    Integer.parseInt(properties[BGND_ROW]));
            String id = properties[BGND_ID];
            world.setBackground(pt, new Background(id, imageStore.getImageList( id)));
        }

        return properties.length == BGND_NUM_PROPERTIES;
    }

    public static boolean parseMiner(
            String[] properties, WorldModel world,ImageStore imageStore)
    {
        if (properties.length == MINER_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[MINER_COL]),
                    Integer.parseInt(properties[MINER_ROW]));
            Entity entity = Factory.createMinerNotFull(properties[MINER_ID],
                    Integer.parseInt(
                            properties[MINER_LIMIT]),
                    pt, Integer.parseInt(
                            properties[MINER_ACTION_PERIOD]), Integer.parseInt(
                            properties[MINER_ANIMATION_PERIOD]),
                    imageStore.getImageList(
                            MINER_KEY));
            world.tryAddEntity(entity);
        }

        return properties.length == MINER_NUM_PROPERTIES;
    }

    public static boolean parseObstacle(String[] properties,  WorldModel world,ImageStore imageStore)
    {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[OBSTACLE_COL]),Integer.parseInt(properties[OBSTACLE_ROW]));
            Entity entity = Factory.createObstacle(properties[OBSTACLE_ID], pt,imageStore.getImageList(OBSTACLE_KEY));
            world.tryAddEntity(entity);
        }
        return properties.length == OBSTACLE_NUM_PROPERTIES;
    }

    public static boolean parseOre(String[] properties,  WorldModel world,ImageStore imageStore) {
        if (properties.length == ORE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[ORE_COL]),Integer.parseInt(properties[ORE_ROW]));
            Entity entity = Factory.createOre(properties[ORE_ID], pt, Integer.parseInt(properties[ORE_ACTION_PERIOD]),imageStore.getImageList( ORE_KEY));
            world.tryAddEntity(entity);
        }
        return properties.length == ORE_NUM_PROPERTIES;
    }

    public static boolean parseSmith(String[] properties, WorldModel world,ImageStore imageStore) {
        if (properties.length == SMITH_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[SMITH_COL]),Integer.parseInt(properties[SMITH_ROW]));
            Entity entity = Factory.createBlacksmith(properties[SMITH_ID], pt, imageStore.getImageList(SMITH_KEY));
            world.tryAddEntity(entity);
        }

        return properties.length == SMITH_NUM_PROPERTIES;
    }

    public static boolean parseVein(String[] properties, WorldModel world,ImageStore imageStore) {
        if (properties.length == VEIN_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[VEIN_COL]),
                    Integer.parseInt(properties[VEIN_ROW]));
            Entity entity = Factory.createVein(properties[VEIN_ID], pt,
                    Integer.parseInt(
                            properties[VEIN_ACTION_PERIOD]),
                    imageStore.getImageList( VEIN_KEY));
            world.tryAddEntity(entity);
        }
        return properties.length == VEIN_NUM_PROPERTIES;
    }


    public static String getOreKey() {
        return ORE_KEY;
    }
    public static String getAlienKey() {return ALIEN_KEY;}
    public static int getAlienId() {return ALIEN_ID;}
}
