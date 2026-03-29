package ctu.cit.service.client;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ctu.cit.model.ChiTietHD;
import ctu.cit.model.HoaDon;
import ctu.cit.model.KhachHang;
import ctu.cit.model.KhuyenMai;
import ctu.cit.model.NhaCungCap;
import ctu.cit.model.SanPham;

public class TestServiceClient {

    private static final String PRODUCT_URL = System.getProperty("product.url", "http://localhost:8081/QLBH/rest/sanpham");
    private static final String CUSTOMER_URL = System.getProperty("customer.url", "http://localhost:8082/QLBH/rest/khachhang");
    private static final String SUPPLIER_URL = System.getProperty("supplier.url", "http://localhost:8083/QLBH/rest/nhacungcap");
    private static final String INVOICE_URL = System.getProperty("invoice.url", "http://localhost:8084/QLBH/rest/hoadon");

    private static Client client;
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUpClient() {
        client = ClientBuilder.newClient();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @AfterAll
    static void tearDownClient() {
        if (client != null) {
            client.close();
        }
    }

    @Test
    void dockerComposeServiceRegressionSuite() throws Exception {
        List<String> failures = new ArrayList<>();
        String runId = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        String supplierId = "NCC" + runId;
        String customerId = "KH" + runId;
        String productId = "SP" + runId;
        String invoiceId = "HD" + runId;
        String promotionId = "KM" + runId;

        NhaCungCap supplier = new NhaCungCap(supplierId, "Cong ty Test " + runId, "Can Tho", "0901234567");
        KhachHang customer = new KhachHang(customerId, "Khach Hang " + runId, "Can Tho", "123456789012");
        SanPham product = new SanPham(productId, "San pham test " + runId, 1000, 5, supplier);

        System.out.println("=== BAT DAU KICH BAN TEST MICROSERVICES: " + runId + " ===");

        ResponseData supplierCreate = sendJson("POST", SUPPLIER_URL, supplier);
        expectStatus("Them nha cung cap", supplierCreate, 200, failures);
        expectBodyContains("Them nha cung cap", supplierCreate, "OK", failures);

        ResponseData duplicateSupplier = sendJson("POST", SUPPLIER_URL, supplier);
        expectBodyNotContains("Them trung nha cung cap", duplicateSupplier, "OK", failures);

        ResponseData supplierList = send("GET", SUPPLIER_URL, null, MediaType.APPLICATION_JSON);
        expectStatus("Lay danh sach nha cung cap", supplierList, 200, failures);
        expectJsonArray("Lay danh sach nha cung cap", supplierList, failures);
        expectBodyContains("Danh sach nha cung cap co ban ghi vua tao", supplierList, supplierId, failures);

        ResponseData customerCreate = sendJson("POST", CUSTOMER_URL, customer);
        expectStatus("Them khach hang", customerCreate, 200, failures);
        expectBodyContains("Them khach hang", customerCreate, "OK", failures);

        ResponseData duplicateCustomer = sendJson("POST", CUSTOMER_URL, customer);
        expectBodyNotContains("Them trung khach hang", duplicateCustomer, "OK", failures);

        ResponseData customerList = send("GET", CUSTOMER_URL, null, MediaType.APPLICATION_JSON);
        expectStatus("Lay danh sach khach hang", customerList, 200, failures);
        expectJsonArray("Lay danh sach khach hang", customerList, failures);
        expectBodyContains("Danh sach khach hang co ban ghi vua tao", customerList, customerId, failures);

        ResponseData productCreate = sendJson("POST", PRODUCT_URL, product);
        expectStatus("Them san pham", productCreate, 200, failures);
        expectBodyContains("Them san pham", productCreate, "OK", failures);

        ResponseData productList = send("GET", PRODUCT_URL, null, MediaType.APPLICATION_JSON);
        expectStatus("Lay danh sach san pham", productList, 200, failures);
        expectJsonArray("Lay danh sach san pham", productList, failures);
        expectBodyContains("Danh sach san pham co ban ghi vua tao", productList, productId, failures);

        HoaDon invoice = new HoaDon(invoiceId, 0.1);
        invoice.setKhachHang(customer);
        invoice.setNgayLap(LocalDate.now());

        ResponseData invoiceCreate = sendJson("POST", INVOICE_URL, invoice);
        expectStatus("Tao hoa don", invoiceCreate, 200, failures);
        expectBodyContains("Tao hoa don", invoiceCreate, "Tao thanh cong", failures);

        ResponseData duplicateInvoice = sendJson("POST", INVOICE_URL, invoice);
        expectBodyNotContains("Tao trung hoa don", duplicateInvoice, "Tao thanh cong", failures);

        ChiTietHD invalidQuantity = new ChiTietHD(product, productId, 0, product.getGia());
        ResponseData invalidPurchase = sendJson("POST", INVOICE_URL + "/" + invoiceId + "/muahang", invalidQuantity);
        expectStatus("Mua hang voi so luong khong hop le", invalidPurchase, 200, failures);
        expectBodyContains("Mua hang voi so luong khong hop le", invalidPurchase, "Loi", failures);

        ChiTietHD validPurchase = new ChiTietHD(product, productId, 2, product.getGia());
        ResponseData purchase = sendJson("POST", INVOICE_URL + "/" + invoiceId + "/muahang", validPurchase);
        expectStatus("Mua hang hop le", purchase, 200, failures);
        expectBodyContains("Mua hang hop le", purchase, "thanh cong", failures);

        ChiTietHD overPurchase = new ChiTietHD(product, productId, 100, product.getGia());
        ResponseData excessivePurchase = sendJson("POST", INVOICE_URL + "/" + invoiceId + "/muahang", overPurchase);
        expectStatus("Mua hang vuot ton kho", excessivePurchase, 200, failures);
        expectBodyContains("Mua hang vuot ton kho", excessivePurchase, "Khong du hang", failures);

        KhuyenMai promotion = new KhuyenMai(promotionId, LocalDate.now().minusDays(1), LocalDate.now().plusDays(7), 0.15);
        ResponseData promotionApply = sendJson("PUT", INVOICE_URL + "/" + invoiceId + "/khuyenmai", promotion);
        expectStatus("Ap dung khuyen mai", promotionApply, 200, failures);
        expectBodyContains("Ap dung khuyen mai", promotionApply, "thanh cong", failures);

        ResponseData promotionMissingInvoice = sendJson("PUT", INVOICE_URL + "/MISSING_" + runId + "/khuyenmai", promotion);
        expectStatus("Ap dung khuyen mai cho hoa don khong ton tai", promotionMissingInvoice, 200, failures);
        expectBodyContains("Ap dung khuyen mai cho hoa don khong ton tai", promotionMissingInvoice, "khong hop le", failures);

        ResponseData invoiceTotal = send("GET", INVOICE_URL + "/" + invoiceId + "/tongtien", null);
        expectStatus("Lay tong tien hoa don", invoiceTotal, 200, failures);
        expectPositiveNumber("Lay tong tien hoa don", invoiceTotal, failures);

        cleanup(invoiceId, productId, customerId, supplierId);

        if (!failures.isEmpty()) {
            fail(String.join(System.lineSeparator(), failures));
        }

        System.out.println("=== KET THUC KICH BAN TEST THANH CONG: " + runId + " ===");
    }

    private static void cleanup(String invoiceId, String productId, String customerId, String supplierId) {
        send("DELETE", INVOICE_URL + "/" + invoiceId, null);
        send("DELETE", PRODUCT_URL + "/" + productId, null);
        send("DELETE", CUSTOMER_URL + "/" + customerId, null);
        send("DELETE", SUPPLIER_URL + "/" + supplierId, null);
    }

    private static ResponseData sendJson(String method, String url, Object data) throws IOException {
        String payload = objectMapper.writeValueAsString(data);
        return send(method, url, payload);
    }

    private static ResponseData send(String method, String url, String payload) {
        return send(method, url, payload, null);
    }

    private static ResponseData send(String method, String url, String payload, String acceptType) {
        try (Response response = invoke(method, url, payload, acceptType)) {
            String body = response.hasEntity() ? response.readEntity(String.class) : "";
            String contentType = response.getHeaderString("Content-Type");
            System.out.println(method + " " + url + " => " + response.getStatus() + " | " + body);
            return new ResponseData(response.getStatus(), body, contentType);
        }
    }

    private static Response invoke(String method, String url, String payload, String acceptType) {
        switch (method) {
            case "GET":
                return request(url, acceptType).get();
            case "DELETE":
                return request(url, acceptType).delete();
            case "POST":
                return request(url, acceptType)
                        .post(Entity.entity(payload, MediaType.APPLICATION_JSON));
            case "PUT":
                return request(url, acceptType)
                        .put(Entity.entity(payload, MediaType.APPLICATION_JSON));
            default:
                throw new IllegalArgumentException("Unsupported method: " + method);
        }
    }

    private static javax.ws.rs.client.Invocation.Builder request(String url, String acceptType) {
        if (acceptType == null || acceptType.isBlank()) {
            return client.target(url).request();
        }
        return client.target(url).request(acceptType);
    }

    private static void expectStatus(String label, ResponseData response, int expected, List<String> failures) {
        if (response.status != expected) {
            failures.add(label + " expected HTTP " + expected + " but was HTTP " + response.status + ". Body: " + response.body);
        }
    }

    private static void expectBodyContains(String label, ResponseData response, String expectedFragment, List<String> failures) {
        if (response.body == null || !response.body.toLowerCase().contains(expectedFragment.toLowerCase())) {
            failures.add(label + " expected body to contain '" + expectedFragment + "' but was: " + response.body);
        }
    }

    private static void expectBodyNotContains(String label, ResponseData response, String unexpectedFragment, List<String> failures) {
        if (response.body != null && response.body.toLowerCase().contains(unexpectedFragment.toLowerCase())) {
            failures.add(label + " should not contain '" + unexpectedFragment + "' but was: " + response.body);
        }
    }

    private static void expectJsonArray(String label, ResponseData response, List<String> failures) {
        String contentType = response.contentType == null ? "" : response.contentType.toLowerCase();
        if (!contentType.contains("application/json")) {
            failures.add(label + " expected Content-Type application/json but was: " + response.contentType);
        }
        String body = response.body == null ? "" : response.body.trim();
        if (!(body.startsWith("[") && body.endsWith("]"))) {
            failures.add(label + " expected JSON array body but was: " + response.body);
        }
    }

    private static void expectPositiveNumber(String label, ResponseData response, List<String> failures) {
        try {
            double value = Double.parseDouble(response.body.trim());
            if (value <= 0) {
                failures.add(label + " expected a positive total but was: " + response.body);
            }
        } catch (RuntimeException ex) {
            failures.add(label + " expected numeric body but was: " + response.body);
        }
    }

    private static final class ResponseData {
        private final int status;
        private final String body;
        private final String contentType;

        private ResponseData(int status, String body, String contentType) {
            this.status = status;
            this.body = body == null ? "" : body;
            this.contentType = contentType;
        }
    }
}
