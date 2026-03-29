package ctu.cit.servlet;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ctu.cit.model.HoaDon;
import ctu.cit.model.KhachHang;
import ctu.cit.model.KhuyenMai;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages invoice *headers* only (list, create, update, delete).
 * Invoice detail management is handled by HoaDonDetailServlet (/hoadon-detail).
 */
@WebServlet("/hoadon")
public class HoaDonServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(HoaDonServlet.class.getName());

    private String safe(String v) { return v == null ? "" : v; }

    private String htmlEsc(String v) {
        if (v == null) return "";
        return v.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
                .replace("\"","&quot;").replace("'","&#39;");
    }

    private String escapeJs(String v) {
        if (v == null) return "";
        return v.replace("\\","\\\\").replace("'","\\'");
    }

    private double normPct(double v) { return (v > 1 && v <= 100) ? v / 100.0 : v; }

    private String clientError(String fallback, Exception e) {
        if (e instanceof WebApplicationException) {
            WebApplicationException w = (WebApplicationException) e;
            if (w.getResponse() != null) return fallback + " (HTTP " + w.getResponse().getStatus() + ")";
        }
        return fallback;
    }

    private Client createClient() {
        return ClientBuilder.newClient(new ClientConfig()
            .register(JacksonFeature.class)
            .register((ContextResolver<ObjectMapper>) type -> new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        Client client = createClient();
        try {
            WebTarget customerTarget = client.target(ServiceUrlConfig.getCustomerServiceUrl());
            WebTarget invoiceTarget  = client.target(ServiceUrlConfig.getInvoiceServiceUrl());
            WebTarget kmTarget       = client.target(ServiceUrlConfig.getKhuyenMaiServiceUrl());

            List<KhachHang> dsKH = loadList(customerTarget, KhachHang[].class, "khachhang");
            List<KhuyenMai> dsKM = loadList(kmTarget,       KhuyenMai[].class, "khuyenmai");

            String action  = request.getParameter("action");
            String message = "";

            if ("add".equals(action)) {
                message = handleAdd(request, invoiceTarget, dsKH, dsKM, response);
                if (message == null) return; // redirect done
            } else if ("update".equals(action)) {
                message = handleUpdate(request, invoiceTarget, dsKH, dsKM);
            } else if ("delete".equals(action)) {
                message = handleDelete(request, invoiceTarget);
            }

            List<HoaDon> dsHD = loadList(invoiceTarget, HoaDon[].class, "invoices");

            StringBuilder rows = new StringBuilder();
            for (HoaDon hd : dsHD) {
                String ngayText = hd.getNgayLap() != null ? hd.getNgayLap().toString() : "";
                String khText   = hd.getKhachHang() != null ? safe(hd.getKhachHang().getMaKH()) : "";
                String kmText   = hd.getKhuyenMai()  != null ? safe(hd.getKhuyenMai().getMaKM())  : "";
                double vatDisp  = hd.getVAT() > 0 && hd.getVAT() <= 1 ? Math.round(hd.getVAT() * 100) : hd.getVAT();

                rows.append("<tr>");
                rows.append("<td><a href='").append(request.getContextPath())
                    .append("/hoadon-detail?mahd=").append(htmlEsc(hd.getMaHD())).append("'>")
                    .append(htmlEsc(hd.getMaHD())).append("</a></td>");
                rows.append("<td>").append(ngayText).append("</td>");
                rows.append("<td>").append(vatDisp).append("%</td>");
                rows.append("<td>").append(htmlEsc(khText)).append("</td>");
                rows.append("<td>").append(kmText.isEmpty() ? "\u2014" : htmlEsc(kmText)).append("</td>");
                rows.append("<td>").append(hd.tinhTongTien()).append("</td>");
                rows.append("<td><div class='table-actions'>");
                rows.append("<button class='table-inline-button' type='button' onclick=\"suaHoaDon('")
                    .append(escapeJs(hd.getMaHD())).append("','").append(escapeJs(ngayText)).append("',")
                    .append(vatDisp).append(",'").append(escapeJs(khText)).append("','")
                    .append(escapeJs(kmText)).append("')\">S\u1eeda</button>");
                rows.append("<a class='table-action table-action-danger' href='hoadon?action=delete&mahd=")
                    .append(htmlEsc(hd.getMaHD()))
                    .append("' onclick=\"return moXacNhanXoa(this.href,'X\u00f3a h\u00f3a \u0111\u01a1n n\u00e0y?')\">X\u00f3a</a>");
                rows.append("<a class='table-inline-button table-inline-button-info' href='")
                    .append(request.getContextPath()).append("/hoadon-detail?mahd=")
                    .append(htmlEsc(hd.getMaHD())).append("'>Chi ti\u1ebft &rarr;</a>");
                rows.append("</div></td>");
                rows.append("</tr>");
            }
            if (rows.length() == 0) {
                rows.append("<tr class='table-empty'><td colspan='7'>Ch\u01b0a c\u00f3 h\u00f3a \u0111\u01a1n n\u00e0o.</td></tr>");
            }

            StringBuilder khOpts = new StringBuilder("<option value=''>-- Ch\u1ecdn kh\u00e1ch h\u00e0ng --</option>");
            for (KhachHang kh : dsKH) {
                if (kh == null) continue;
                khOpts.append("<option value='").append(htmlEsc(safe(kh.getMaKH()))).append("'>")
                      .append(htmlEsc(safe(kh.getMaKH()))).append(" - ").append(htmlEsc(safe(kh.getHoTen())))
                      .append("</option>");
            }

            StringBuilder kmOpts = new StringBuilder("<option value=''>-- Kh\u00f4ng \u00e1p d\u1ee5ng --</option>");
            for (KhuyenMai km : dsKM) {
                if (km == null) continue;
                double pct = km.getPhanTramGiam() <= 1 ? Math.round(km.getPhanTramGiam() * 100) : km.getPhanTramGiam();
                kmOpts.append("<option value='").append(htmlEsc(safe(km.getMaKM()))).append("'>")
                      .append(htmlEsc(safe(km.getMaKM()))).append(" (").append(pct).append("% \u2013 ")
                      .append(km.getNgayApDung()).append(" \u2192 ").append(km.getNgayKetThuc())
                      .append(")</option>");
            }

            request.setAttribute("dsHoaDon",       rows.toString());
            request.setAttribute("message",         message);
            request.setAttribute("dsKhachHangOpts", khOpts.toString());
            request.setAttribute("dsKhuyenMaiOpts", kmOpts.toString());
            request.getRequestDispatcher("Hoadon.jsp").forward(request, response);
        } finally {
            client.close();
        }
    }

    // ---- action handlers ----

    private String handleAdd(HttpServletRequest request, WebTarget invoiceTarget,
                              List<KhachHang> dsKH, List<KhuyenMai> dsKM,
                              HttpServletResponse response) throws IOException {
        try {
            HoaDon hd = buildHeaderFromRequest(request, dsKH, dsKM);
            Response resp = invoiceTarget.request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(hd, MediaType.APPLICATION_JSON));
            if (resp.getStatus() == 201) {
                HoaDon created = resp.readEntity(HoaDon.class);
                String maHD = created != null ? created.getMaHD() : "";
                response.sendRedirect(request.getContextPath()
                        + "/hoadon-detail?mahd=" + maHD + "&msg=T%E1%BA%A1o+ho%C3%A1+%C4%91%C6%A1n+th%C3%A0nh+c%C3%B4ng");
                return null;
            }
            return "Kh\u00f4ng t\u1ea1o \u0111\u01b0\u1ee3c h\u00f3a \u0111\u01a1n (server tr\u1ea3 " + resp.getStatus() + ")";
        } catch (NumberFormatException | DateTimeParseException e) {
            LOGGER.log(Level.WARNING, "Invalid invoice form data", e);
            return "D\u1eef li\u1ec7u h\u00f3a \u0111\u01a1n kh\u00f4ng h\u1ee3p l\u1ec7.";
        } catch (ProcessingException | WebApplicationException e) {
            LOGGER.log(Level.WARNING, "Failed to create invoice", e);
            return clientError("Kh\u00f4ng t\u1ea1o \u0111\u01b0\u1ee3c h\u00f3a \u0111\u01a1n.", e);
        }
    }

    private String handleUpdate(HttpServletRequest request, WebTarget invoiceTarget,
                                 List<KhachHang> dsKH, List<KhuyenMai> dsKM) {
        try {
            String maHD = request.getParameter("mahd");
            HoaDon hd = buildHeaderFromRequest(request, dsKH, dsKM);
            hd.setMaHD(maHD);
            return invoiceTarget.path(maHD).request(MediaType.TEXT_PLAIN)
                    .put(Entity.entity(hd, MediaType.APPLICATION_JSON), String.class);
        } catch (NumberFormatException | DateTimeParseException e) {
            LOGGER.log(Level.WARNING, "Invalid invoice update data", e);
            return "D\u1eef li\u1ec7u kh\u00f4ng h\u1ee3p l\u1ec7.";
        } catch (ProcessingException | WebApplicationException e) {
            LOGGER.log(Level.WARNING, "Failed to update invoice", e);
            return clientError("Kh\u00f4ng c\u1eadp nh\u1eadt \u0111\u01b0\u1ee3c h\u00f3a \u0111\u01a1n.", e);
        }
    }

    private String handleDelete(HttpServletRequest request, WebTarget invoiceTarget) {
        String maHD = request.getParameter("mahd");
        if (maHD == null || maHD.isBlank()) return "Thi\u1ebfu m\u00e3 h\u00f3a \u0111\u01a1n.";
        try {
            return invoiceTarget.path(maHD).request(MediaType.TEXT_PLAIN).delete(String.class);
        } catch (ProcessingException | WebApplicationException e) {
            LOGGER.log(Level.WARNING, "Failed to delete invoice", e);
            return clientError("Kh\u00f4ng x\u00f3a \u0111\u01b0\u1ee3c h\u00f3a \u0111\u01a1n.", e);
        }
    }

    private HoaDon buildHeaderFromRequest(HttpServletRequest request,
                                           List<KhachHang> dsKH, List<KhuyenMai> dsKM) {
        HoaDon hd = new HoaDon();
        String ngayLap = request.getParameter("ngaylap");
        if (ngayLap != null && !ngayLap.isBlank()) hd.setNgayLap(LocalDate.parse(ngayLap.substring(0, 10)));
        String vat = request.getParameter("vat");
        if (vat != null && !vat.isBlank()) hd.setVAT(normPct(Double.parseDouble(vat)));
        String maKH = request.getParameter("makh");
        if (maKH != null && !maKH.isBlank()) {
            KhachHang kh = dsKH.stream().filter(k -> maKH.equals(k.getMaKH())).findFirst().orElseGet(() -> {
                KhachHang k = new KhachHang(); k.setMaKH(maKH); return k;
            });
            hd.setKhachHang(kh);
        }
        String maKM = request.getParameter("makm");
        if (maKM != null && !maKM.isBlank()) {
            KhuyenMai km = dsKM.stream().filter(k -> maKM.equals(k.getMaKM())).findFirst().orElseGet(() -> {
                KhuyenMai k = new KhuyenMai(); k.setMaKM(maKM); return k;
            });
            hd.setKhuyenMai(km);
        }
        return hd;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> loadList(WebTarget target, Class<T[]> arrClass, String label) {
        try {
            T[] arr = target.request(MediaType.APPLICATION_JSON).get(arrClass);
            return arr != null ? Arrays.asList(arr) : Collections.emptyList();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load " + label, e);
            return Collections.emptyList();
        }
    }
}
