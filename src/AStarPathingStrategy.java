import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy implements PathingStrategy
{
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        Hashtable<Point, Node> open = new Hashtable<>();    //initialize open hash table
        open.put(start,new Node(start,0,0));          //add starting node to open
        List<Node> path = new LinkedList<>();               //define closed list
        Node current = open.get(start);
        while(open.size() > 0) {                            //until the closed list is empty

            if(withinReach.test(current.pos, end)){         //check if reached destination
                List<Point> ans= new ArrayList<>();         //generate list
                Node node = current;
                while(node.parent!= null){
                    ans.add(0,node.pos);
                    node = node.parent;
                }
                return ans;
            }

            Point parent =new Point(-1,-1);               //generate parent pos
            if(current.parent != null)                          //stops backtracking
                parent = current.parent.pos;

            Point finalParent = parent;
            List<Point> neighbors = potentialNeighbors.apply(current.pos)   //generate neighbors
                    .filter(canPassThrough)
                    .filter(s->!finalParent.equals(s))
                    .filter(s->!path.contains(new Node(s,0,0)))
                    .collect(Collectors.toList());

            for(Point neighbor: neighbors){                     //check neighbor for new pos
                if(!open.containsKey(neighbor)){                //generate new entry to open list
                    open.put(neighbor,new Node(neighbor,heuristicDistance(neighbor,end),current));
                }
                if(current.g+1 < open.get(neighbor).g) {        //check g val
                    Node prior = open.get(neighbor).parent;     //assign prior
                    open.replace(neighbor, new Node(neighbor, heuristicDistance(neighbor, end), current));
                    current = prior;                            //set new current
                }
            }

            path.add(current);                                  //add to closed list
            open.remove(current.pos);                           //remove from open
            if(open.size()>0)   current= Collections.min(open.entrySet(), (Map.Entry<Point, Node> n1, Map.Entry<Point, Node> n2)-> {return n1.getValue().g-n2.getValue().g;} ).getValue();
        }

        return new LinkedList<>();
    }

    private static int heuristicDistance(Point pos, Point end){
        return (Math.abs(pos.x-end.x)+Math.abs(pos.y-end.x));
    }

    private static int getH(Map.Entry<Point, Node> entry, Point end){
        return heuristicDistance(entry.getKey(),end);
    }

    private static int getF(Map.Entry<Point, Node> entry, Point end){
        return getH(entry,end) + entry.getValue().g;
    }

}


class Node{
    public Point pos;
    public Node parent;
    public int h;
    public int g;
    public int f;

    public Node(Point pos,int h,int g){
        this.pos= pos;this.h=h;this.g=g;this.f=g+h;parent=null;
    }
    public Node(Point pos,int h,Node parent){
        this.pos= pos;this.h=h;this.g= parent.g+1;this.f=g+h;this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(pos, node.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos);
    }
}
