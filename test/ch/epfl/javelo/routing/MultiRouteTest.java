package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.DoubleUnaryOperator;
import static org.junit.jupiter.api.Assertions.*;

class MultiRouteTest {

    DoubleUnaryOperator profile = Functions.constant(0);
    Edge edge1 = new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N), 500, profile);
    Edge edge2 = new Edge(1, 2, new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N), 500, profile);
    Edge edge3 = new Edge(2, 3, new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 1500, SwissBounds.MIN_N), 500, profile);
    Edge edge4 = new Edge(3, 4, new PointCh(SwissBounds.MIN_E + 1500, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N), 500, profile);
    Edge edge5 = new Edge(4, 5, new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 2500, SwissBounds.MIN_N), 500, profile);
    Edge edge6 = new Edge(5, 6, new PointCh(SwissBounds.MIN_E + 2500, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N), 500, profile);
    Edge edge7 = new Edge(6, 7, new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 3500, SwissBounds.MIN_N), 500, profile);
    Edge edge8 = new Edge(7, 8, new PointCh(SwissBounds.MIN_E + 3500, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 4000, SwissBounds.MIN_N), 500, profile);
    Edge edge9 = new Edge(8, 9, new PointCh(SwissBounds.MIN_E + 4000, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 4500, SwissBounds.MIN_N), 500, profile);
    Edge edge10 = new Edge(9, 10, new PointCh(SwissBounds.MIN_E + 4500, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N), 500, profile);
    Edge edge11 = new Edge(10, 11, new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 5500, SwissBounds.MIN_N), 500, profile);
    Edge edge12 = new Edge(11, 12, new PointCh(SwissBounds.MIN_E + 5500, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N), 500, profile);
    Edge edge13 = new Edge(11, 12, new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 6500, SwissBounds.MIN_N), 500, profile);
    Edge edge14 = new Edge(11, 12, new PointCh(SwissBounds.MIN_E + 6500, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 7000, SwissBounds.MIN_N), 500, profile);
    Edge edge15 = new Edge(11, 12, new PointCh(SwissBounds.MIN_E + 7000, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 7500, SwissBounds.MIN_N), 500, profile);
    Edge edge16 = new Edge(11, 12, new PointCh(SwissBounds.MIN_E + 7500, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 8000, SwissBounds.MIN_N), 500, profile);

    SingleRoute route1 = new SingleRoute(List.of(edge1, edge2));
    SingleRoute route2 = new SingleRoute(List.of(edge3, edge4));
    SingleRoute route3 = new SingleRoute(List.of(edge5, edge6));
    SingleRoute route4 = new SingleRoute(List.of(edge7, edge8));
    SingleRoute route5 = new SingleRoute(List.of(edge9, edge10));
    SingleRoute route6 = new SingleRoute(List.of(edge11, edge12));

    SingleRoute route7 = new SingleRoute(List.of(edge13, edge14));
    SingleRoute route8 = new SingleRoute(List.of(edge15, edge16));

    MultiRoute multiroute1 = new MultiRoute(List.of(route1, route2, route3));
    MultiRoute multiroute2 = new MultiRoute(List.of(route4, route5, route6));

    MultiRoute globalRoute1 = new MultiRoute(List.of(multiroute1, multiroute2));
    MultiRoute globalRoute2 = new MultiRoute(List.of(multiroute1, multiroute2, route7, route8));


    @Test
    void indexOfSegmentAtWorksOnSubjectExample(){
        assertEquals(5, globalRoute1.indexOfSegmentAt(5400));
    }

    @Test
    void indexOfSegmentAtWorksOnSimpleMultiRoute(){
        assertEquals(2, multiroute1.indexOfSegmentAt(2400));
    }

    @Test
    void indexOfSegmentAtWorksOnComplexMultiRoute(){
        assertEquals(7, globalRoute2.indexOfSegmentAt(7001));
    }

    @Test
    void lengthWorksProperly(){
        assertEquals(8000, globalRoute2.length());
    }

    @Test
    void edgesWorksProperly(){
        assertEquals(List.of(edge1, edge2, edge3, edge4, edge5, edge6), multiroute1.edges());
    }

    @Test
    void pointsWorksProperly(){
        assertEquals(List.of(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N),
                             new PointCh(SwissBounds.MIN_E + 1000,  SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 1500, SwissBounds.MIN_N),
                             new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 2500, SwissBounds.MIN_N),
                             new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N)),  multiroute1.points());
    }

    @Test
    void pointAtWorksProperly(){
        assertEquals(new PointCh(SwissBounds.MIN_E + 4787, SwissBounds.MIN_N), globalRoute2.pointAt(4787));
        assertEquals(new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N), globalRoute2.pointAt(3000));
        assertEquals(new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N), globalRoute2.pointAt(6000));
        assertEquals(new PointCh(SwissBounds.MIN_E + 8000, SwissBounds.MIN_N), globalRoute2.pointAt(8000));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), globalRoute2.pointAt(0));
        assertEquals(new PointCh(SwissBounds.MIN_E + 8000, SwissBounds.MIN_N), globalRoute2.pointAt(10000));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), globalRoute2.pointAt(-3));


    }



}