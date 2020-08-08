package SDMSystem.exceptions;

public class LocationNotInRangeException extends RuntimeException{
    private int minRow;
    private int maxRow;
    private int minCol;
    private int maxCol;
    private final String EXCEPTION_MESSAGE = "The location row must be between %d to %d" +
                                            "\nand the location col must be between %d to %d ";

    public LocationNotInRangeException(int minRow, int maxRow, int minCol, int maxCol) {
        this.minRow = minRow;
        this.maxRow = maxRow;
        this.minCol = minCol;
        this.maxCol = maxCol;
    }


    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE,minRow,maxRow,minCol,maxCol);
    }
}
