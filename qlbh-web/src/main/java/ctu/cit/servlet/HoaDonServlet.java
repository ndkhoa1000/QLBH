package ctu.cit.servlet;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ctu.cit.model.ChiTietHD;
import ctu.cit.model.HoaDon;
import ctu.cit.model.KhachHang;
import ctu.cit.model.KhuyenMai;
import ctu.cit.model.SanPham;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/hoadon")
public class HoaDonServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(HoaDonServlet.class.getName());

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String escapeJs(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("'", "\\'");
    }

    private Client createClient() {
        ClientConfig config = new ClientConfig()
                .register(JacksonFeature.class)
                .register(new ContextResolver<ObjectMapper>() {
                    private final ObjectMapper mapper = new ObjectMapper()
                            .registerModule(new JavaTimeModule())
                            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                    @Override
                    public ObjectMapper getContext(Class<?> type) {
                        return mapper;
                    }
                });
        return ClientBuilder.newClient(config);
    }

    private String buildClientErrorMessage(String fallback, Exception e) {
        if (e instanceof WebApplicationException) {
            WebApplicationException webEx = (WebApplicationException) e;
            if (webEx.getResponse() != null) {
                return fallback + " (HTTP " + webEx.getResponse().getStatus() + ")";
            }
        }
        return fallback;
    }

    private double normalizePercentValue(double value) {
        if (value > 1 && value <= 100) {
            return value / 100;
        }
        return value;
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // ============lay ds hoa don ================
        Client client = createClient();
        
        // Target cho từng service
        WebTarget customerTarget = client.target(ServiceUrlConfig.getCustomerServiceUrl());
        WebTarget productTarget = client.target(ServiceUrlConfig.getProductServiceUrl());
        WebTarget invoiceTarget = client.target(ServiceUrlConfig.getInvoiceServiceUrl());

        List<KhachHang> dsKhachHang = Collections.emptyList();
        try {
            KhachHang[] array = customerTarget.request(MediaType.APPLICATION_JSON).get(KhachHang[].class);
            if (array != null) {
                dsKhachHang = Arrays.asList(array);
            }
        } catch (ProcessingException | WebApplicationException e) {
            LOGGER.log(Level.WARNING, "Failed to load customer list", e);
        }

        // ===============  lay ds san pham =================
        List<SanPham> dsSP = Collections.emptyList();
        try {
            SanPham[] array = productTarget.request(MediaType.APPLICATION_JSON).get(SanPham[].class);
            if (array != null) {
                dsSP = Arrays.asList(array);
            }
        } catch (ProcessingException | WebApplicationException e) {
            LOGGER.log(Level.WARNING, "Failed to load product list", e);
        }

        String action = request.getParameter("action");
        String message = "";
        String messagekm = "";
        String activePromotionInvoiceId = "";


        // ========= thao tac form=================
        if (action != null) {
            if ("add".equals(action)) {
                try {
                    HoaDon hd = new HoaDon();
                    hd.setMaHD(request.getParameter("mahd"));

                    String ngayLap = request.getParameter("ngaylap");
                    if (ngayLap != null && !ngayLap.isBlank()) {
                        hd.setNgayLap(LocalDate.parse(ngayLap.substring(0, 10)));
                    }
                    String vat = request.getParameter("vat");
                    if (vat != null && !vat.isBlank()) {
                        hd.setVAT(normalizePercentValue(Double.parseDouble(vat)));
                    }
                    String maKH = request.getParameter("makh");
                    if (maKH != null && !maKH.isBlank()) {
                        KhachHang khachHang = null;
                        for (KhachHang kh : dsKhachHang) {
                            if (maKH.equals(kh.getMaKH())) {
                                khachHang = kh;
                                break;
                            }
                        }
                        if (khachHang == null) {
                            khachHang = new KhachHang();
                            khachHang.setMaKH(maKH);
                        }
                        hd.setKhachHang(khachHang);
                    }

                    String[] maSPs = request.getParameterValues("masp[]");
                    String[] soLuongs = request.getParameterValues("soluong[]");
                    String[] donGias = request.getParameterValues("dongia[]");
                    List<ChiTietHD> dsChiTiet = hd.getDsChiTiet();
                    if (dsChiTiet == null) {
                        dsChiTiet = new ArrayList<>();
                    }

                    if (maSPs != null && soLuongs != null && donGias != null) {
                        int count = Math.min(maSPs.length, Math.min(soLuongs.length, donGias.length));
                        for (int i = 0; i < count; i++) {
                            if (maSPs[i] == null || maSPs[i].isBlank()) {
                                continue;
                            }
                            if (soLuongs[i] == null || soLuongs[i].isBlank() || donGias[i] == null || donGias[i].isBlank()) {
                                message = "Chi tiet hoa don khong hop le.";
                                break;
                            }

                            ChiTietHD ct = new ChiTietHD();
                            ct.setMaSP(maSPs[i]);
                            ct.setSoLuong(Integer.parseInt(soLuongs[i]));
                            ct.setDonGia(Double.parseDouble(donGias[i]));

                            SanPham sanPham = null;
                            for (SanPham sp : dsSP) {
                                if (maSPs[i].equals(sp.getMaSP())) {
                                    sanPham = sp;
                                    break;
                                }
                            }
                            if (sanPham == null) {
                                sanPham = new SanPham();
                                sanPham.setMaSP(maSPs[i]);
                            }
                            ct.setSanPham(sanPham);
                            dsChiTiet.add(ct);
                        }
                    }

                    if (!message.isEmpty()) {
                        request.setAttribute("message", message);
                    } else {
                    message = invoiceTarget.request(MediaType.TEXT_PLAIN)
                            .post(Entity.entity(hd, MediaType.APPLICATION_JSON), String.class);
                    }
                } catch (NumberFormatException | DateTimeParseException e) {
                    LOGGER.log(Level.WARNING, "Invalid invoice form data", e);
                    message = "Du lieu hoa don khong hop le.";
                } catch (ProcessingException | WebApplicationException e) {
                    LOGGER.log(Level.WARNING, "Failed to create invoice", e);
                    message = buildClientErrorMessage("Khong tao duoc hoa don.", e);
                }
            } else if ("delete".equals(action)) {
                String maHD = request.getParameter("mahd");
                if (maHD != null && !maHD.isBlank()) {
                    try {
                        message = invoiceTarget.path(maHD).request(MediaType.TEXT_PLAIN).delete(String.class);
                    } catch (ProcessingException | WebApplicationException e) {
                        LOGGER.log(Level.WARNING, "Failed to delete invoice", e);
                        message = buildClientErrorMessage("Khong xoa duoc hoa don.", e);
                    }
                }
            } else if ("aplkm".equals(action)) {
                String maHD = request.getParameter("mahdkm");
                String maKM = request.getParameter("makmchohd");
                String ngayApDung = request.getParameter("ngayapdungkm");
                String ngayKetThuc = request.getParameter("ngayketthuckm");
                String phanTramGiam = request.getParameter("phantramgiamkm");
                activePromotionInvoiceId = maHD != null ? maHD : "";

                if (maHD != null && !maHD.isBlank() && maKM != null && !maKM.isBlank()
                        && ngayApDung != null && !ngayApDung.isBlank()
                        && ngayKetThuc != null && !ngayKetThuc.isBlank()
                        && phanTramGiam != null && !phanTramGiam.isBlank()) {
                    KhuyenMai khuyenMai = new KhuyenMai();
                    khuyenMai.setMaKM(maKM);
                    khuyenMai.setNgayApDung(LocalDate.parse(ngayApDung));
                    khuyenMai.setNgayKetThuc(LocalDate.parse(ngayKetThuc));
                    khuyenMai.setPhanTramGiam(normalizePercentValue(Double.parseDouble(phanTramGiam)));

                    try {
                        messagekm = invoiceTarget.path(maHD)
                                .path("khuyenmai")
                                .request(MediaType.TEXT_PLAIN)
                                .put(Entity.entity(khuyenMai, MediaType.APPLICATION_JSON), String.class);
                    } catch (ProcessingException | WebApplicationException e) {
                        LOGGER.log(Level.WARNING, "Failed to apply promotion", e);
                        messagekm = buildClientErrorMessage("Khong ap dung duoc khuyen mai tu server.", e);
                    }
                } else {
                    messagekm = "Khong hop le";
                }
            }
        }

        List<HoaDon> dsHD = Collections.emptyList();
        try {
            HoaDon[] array = invoiceTarget.request(MediaType.APPLICATION_JSON).get(HoaDon[].class);
            if (array != null) {
                dsHD = Arrays.asList(array);
            }
        } catch (ProcessingException | WebApplicationException e) {
            LOGGER.log(Level.WARNING, "Failed to load invoice list", e);
            if (message.isEmpty()) {
                message = "Khong tai duoc danh sach hoa don.";
            }
        }



        // ======== in bang hoa don =============
        StringBuilder inDS = new StringBuilder();
        for (HoaDon hd : dsHD) {
            String ngayLapText = hd.getNgayLap() != null ? hd.getNgayLap().toString() : "";
            String maKhText = hd.getKhachHang() != null && hd.getKhachHang().getMaKH() != null ? hd.getKhachHang().getMaKH() : "";
            List<ChiTietHD> dsCT = hd.getDsChiTiet() != null ? hd.getDsChiTiet() : Collections.emptyList();
            inDS.append("<tr>");
            inDS.append("<td>").append(hd.getMaHD()).append("</td>");
            inDS.append("<td>").append(ngayLapText).append("</td>");
            inDS.append("<td>").append(hd.getVAT()).append("</td>");
            inDS.append("<td>").append(maKhText).append("</td>");
            inDS.append("<td>").append(hd.tinhTongTien()).append("</td>");
            inDS.append("<td><div class='table-actions'><a class='table-action table-action-danger' onclick=\"return moXacNhanXoa(this.href, 'B\\u1ea1n c\\u00f3 ch\\u1eafc mu\\u1ed1n x\\u00f3a h\\u00f3a \\u0111\\u01a1n n\\u00e0y?');\" href='hoadon?action=delete&mahd=")
                    .append(hd.getMaHD())
                    .append("'>Xóa</a>");
            inDS.append("<button class='table-inline-button table-inline-button-accent' type='button' onclick=\"moKhuyenMai('")
                    .append(escapeJs(hd.getMaHD()))
                    .append("')\">Khuyến mãi</button></div></td>");
            inDS.append("<td><button class='table-inline-button table-inline-button-toggle' id='toggle-")
                    .append(hd.getMaHD())
                    .append("' aria-label='Xem chi tiết' type='button' onclick=\"xemCT('")
                    .append(hd.getMaHD())
                    .append("')\">&#8250;</button></td>");
            inDS.append("</tr>");

            inDS.append("<tr ").append("id='").append(hd.getMaHD()).append("' class='hide ct'>");
            inDS.append("<td colspan='7'>");
            inDS.append("<table>");
                inDS.append("<tr>");
                inDS.append("<th> Ma SP </th>");
                inDS.append("<th> San Pham </th>");
                inDS.append("<th> So luong </th>");
                inDS.append("<th> Don Gia </th>");
                inDS.append("</tr>");

            for(ChiTietHD ct : dsCT){
                String maSP = ct.getMaSP() != null ? ct.getMaSP() : "";
                String sanPhamText = ct.getSanPham() != null ? ct.getSanPham().toString() : "";
                inDS.append("<tr>");
                inDS.append("<td>").append(maSP).append("</td>");
                inDS.append("<td>").append(sanPhamText).append("</td>");
                inDS.append("<td>").append(ct.getSoLuong()).append("</td>");
                inDS.append("<td>").append(ct.getDonGia()).append("</td>");
                inDS.append("</tr>");
            }
            if (dsCT.isEmpty()) {
                inDS.append("<tr class='table-empty'><td colspan='4'>Chưa có chi tiết hóa đơn.</td></tr>");
            }

            inDS.append("</table>");
            inDS.append("</td>");
            inDS.append("</tr>");
        }
        if (inDS.length() == 0) {
            inDS.append("<tr class='table-empty'><td colspan='7'>Chưa có hóa đơn nào.</td></tr>");
        }
        request.setAttribute("dsHoaDon", inDS.toString());
        request.setAttribute("message", message);
        request.setAttribute("messagekm", messagekm);
        request.setAttribute("activePromotionInvoiceId", safe(activePromotionInvoiceId));
        request.getRequestDispatcher("Hoadon.jsp").forward(request, response);
        client.close();
    }
}
