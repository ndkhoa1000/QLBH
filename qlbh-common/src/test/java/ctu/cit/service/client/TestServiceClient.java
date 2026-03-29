package ctu.cit.service.client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.glassfish.jersey.jackson.JacksonFeature;

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

    public static void main(String[] args) {
        client = ClientBuilder.newClient().register(JacksonFeature.class);
        
        try {
            System.out.println("=== BAT DAU KICH BAN TEST MICROSERVICES ===");

            // 1. Them Nha Cung Cap
            NhaCungCap ncc = new NhaCungCap("NCC001", "Cong ty Giai Phap So", "Ha Noi", "0901234567");
            post(SUPPLIER_URL, ncc, "Them Nha Cung Cap");

            // 2. Them San Pham
            SanPham sp1 = new SanPham("SP001", "Laptop Dell XPS", 25000000, 10, ncc);
            SanPham sp2 = new SanPham("SP002", "Chuot Logitech", 500000, 50, ncc);
            SanPham sp3 = new SanPham("SP003", "Ban phim Co", 1200000, 20, ncc);
            post(PRODUCT_URL, sp1, "Them San Pham 1");
            post(PRODUCT_URL, sp2, "Them San Pham 2");
            post(PRODUCT_URL, sp3, "Them San Pham 3");

            // 3. Them Khach Hang
            KhachHang kh1 = new KhachHang("KH001", "Nguyen Van A", "Can Tho", "123456789");
            KhachHang kh2 = new KhachHang("KH002", "Tran Thi B", "Vinh Long", "987654321");
            post(CUSTOMER_URL, kh1, "Them Khach Hang 1");
            post(CUSTOMER_URL, kh2, "Them Khach Hang 2");

            // 4. Tao Hoa Don
            HoaDon hd = new HoaDon("HD001", 0.1); // VAT 10%
            hd.setKhachHang(kh1);
            hd.setNgayLap(LocalDate.now());
            post(INVOICE_URL, hd, "Tao Hoa Don HD001");

            // 5. Mua hang (Them chi tiet)
            // Gia su API mua hang la POST /hoadon/{maHD}/muahang
            ChiTietHD ct1 = new ChiTietHD(sp1, sp1.getMaSP(), 1, sp1.getGia());
            ChiTietHD ct2 = new ChiTietHD(sp2, sp2.getMaSP(), 2, sp2.getGia());
            post(INVOICE_URL + "/HD001/muahang", ct1, "Mua Laptop Dell (1)");
            post(INVOICE_URL + "/HD001/muahang", ct2, "Mua Chuot Logitech (2)");

            // 6. Ap dung khuyen mai
            KhuyenMai km = new KhuyenMai("KM_HE_2024", LocalDate.now().minusDays(1), LocalDate.now().plusMonths(1), 0.15);
            put(INVOICE_URL + "/HD001/khuyenmai", km, "Ap dung KM 15%");

            // 7. Kiem tra tong tien
            Response resp = client.target(INVOICE_URL + "/HD001/tongtien").request().get();
            System.out.println("Tong tien HD001: " + resp.readEntity(Double.class));

            System.out.println("=== KET THUC KICH BAN TEST THANH CONG ===");
        } catch (Exception e) {
            System.err.println("LOI TRONG QUA TRINH TEST: " + e.getMessage());
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    private static void post(String url, Object data, String label) {
        Response resp = client.target(url).request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(data, MediaType.APPLICATION_JSON));
        System.out.println(label + ": " + resp.getStatus() + " - " + resp.readEntity(String.class));
    }

    private static void put(String url, Object data, String label) {
        Response resp = client.target(url).request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(data, MediaType.APPLICATION_JSON));
        System.out.println(label + ": " + resp.getStatus() + " - " + resp.readEntity(String.class));
    }
}
