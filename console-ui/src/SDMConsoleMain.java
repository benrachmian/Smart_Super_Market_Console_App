import Product.*;

public class SDMConsoleMain {
    public static void main(String[] args){
        Product pro1 = new Product("Brn", Product.WayOfBuying.BYQUANTITY);
        ProductInStore pro2 = new ProductInStore("Mike", Product.WayOfBuying.BYWEIGHT,50);
        System.out.println(pro1.getWayOfBuying());
        System.out.println(pro2.getSerialNumber());
        System.out.println(pro2.getPrice());
    }
}
