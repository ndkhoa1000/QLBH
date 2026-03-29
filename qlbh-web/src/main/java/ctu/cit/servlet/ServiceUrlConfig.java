package ctu.cit.servlet;

public class ServiceUrlConfig {
    public static String getProductServiceUrl() {
        return getEnvOrDefault("PRODUCT_SERVICE_URL", "http://localhost:8081/QLBH/rest/sanpham");
    }

    public static String getCustomerServiceUrl() {
        return getEnvOrDefault("CUSTOMER_SERVICE_URL", "http://localhost:8082/QLBH/rest/khachhang");
    }

    public static String getSupplierServiceUrl() {
        return getEnvOrDefault("SUPPLIER_SERVICE_URL", "http://localhost:8083/QLBH/rest/nhacungcap");
    }

    public static String getInvoiceServiceUrl() {
        return getEnvOrDefault("INVOICE_SERVICE_URL", "http://localhost:8084/QLBH/rest/hoadon");
    }

    public static String getKhuyenMaiServiceUrl() {
        return getEnvOrDefault("KHUYENMAI_SERVICE_URL", "http://localhost:8084/QLBH/rest/khuyenmai");
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}
