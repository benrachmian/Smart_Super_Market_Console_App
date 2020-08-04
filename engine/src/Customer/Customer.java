package Customer;

public class Customer {

    private static int generatedSerialNumber = 1000;
    private final String customerName;
    private final int serialNumber;

    public Customer(String customerName) {
        this.customerName = customerName;
        this.serialNumber = generatedSerialNumber++;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getSerialNumber() {
        return serialNumber;
    }
}
