package SDMSystem.Exceptions;

public class ProductAlreadyExistsException extends RuntimeException{

    private int productSerialNumber;
    private Enum {Store, System};
    private final String EXCEPTION_MESSAGE = "Product %s already exists in the %s...";

    public ProductAlreadyExistsException(int productSerialNumber) {
        this.productSerialNumber = productSerialNumber;
    }

    public int getProductSerialNumber(){
        return productSerialNumber;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, productSerialNumber);
    }
}
