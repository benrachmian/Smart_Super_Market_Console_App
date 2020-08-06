package SDMSystem.validation;

import java.awt.*;
import SDMSystem.exceptions.*;

public class LocationValidation {
    public static void checkLocationValidation2D(Point locationToCheck, int minRow, int maxRow, int minCol, int maxCol){
        if((locationToCheck.x <minRow && locationToCheck.x > maxRow) ||
                (locationToCheck.y < minCol && locationToCheck.y > maxCol)){
            throw new LocationNotInRangeException(minRow,maxRow,minCol,maxCol);
        }
    }

}
