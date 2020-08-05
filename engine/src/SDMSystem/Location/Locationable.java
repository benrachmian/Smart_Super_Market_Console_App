package SDMSystem.Location;

import java.awt.*;

public interface Locationable {
    Point getLocation();
    double getDistanceFrom(Point target);
}
