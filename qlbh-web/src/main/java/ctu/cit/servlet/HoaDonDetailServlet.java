package ctu.cit.servlet;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ctu.cit.model.ChiTietHD;
import ctu.cit.model.HoaDon;
import ctu.cit.model.SanPham;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/hoadon-detail")
public class HoaDonDetailServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(HoaDonDetailServlet.class.getName());

    private Client createClient() {
        return ClientBuilder.newClient(new ClientConfig()
            .register(JacksonFeature.class)
            .register((ContextResolver<ObjectMapper>) type -> new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)));
    }

    private String safe(String v) { return v == null ? "" : v; }
    private String htmlEsc(String v) {
        if (v == null) return "";
        return v.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
                .replace("\"","&quot;").replace("'","&#39;");
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

        if (request.getSession(false) == null || request.getSession(false).getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String maHD = request.getParameter("mahd");
        if (maHD == null || maHD.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/hoadon");
            return;
        }

        Client client = createClient();
        try {
            WebTarget invoiceTarget = client.target(ServiceUrlConfig.getInvoiceServiceUrl());
            WebTarget productTarget = client.target(ServiceUrlConfig.getProductServiceUrl());

            String action  = request.getParameter("action");
            String message = safe(request.getParameter("msg"));

            if ("addChiTiet".equals(action)) {
                String maSP    = request.getParameter("masp");
                String soLuong = request.getParameter("soluong");
                String donGia  = request.getParameter("dongia");
                ChiTietHD ct = new ChiTietHD();
                SanPham sp = new SanPham();
                sp.setMaSP(maSP);
                ct.setSanPham(sp);
                ct.setMaSP(maSP);
                ct.setSoLuong(soLuong != null ? Integer.parseInt(soLuong) : 1);
                if (donGia != null && !donGia.isBlank()) ct.setDonGia(Double.parseDouble(donGia));
                try {
                    message = invoiceTarget.path(maHD).path("muahang")
                            .request(MediaType.TEXT_PLAIN)
                            .post(Entity.entity(ct, MediaType.APPLICATION_JSON), String.class);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "addChiTiet failed", e);
                    message = "L\u1ed7i th\u00eam chi ti\u1ebft: " + e.getMessage();
                }
            } else if ("deleteChiTiet".equals(action)) {
                String maSP = request.getParameter("masp");
                try {
                    message = invoiceTarget.path(maHD).path("chitiet").path(maSP)
                            .request(MediaType.TEXT_PLAIN).delete(String.class);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "deleteChiTiet failed", e);
                    message = "L\u1ed7i x\u00f3a chi ti\u1ebft: " + e.getMessage();
                }
            }

            // Load invoice with details
            HoaDon hd = null;
            try {
                javax.ws.rs.core.Response r = invoiceTarget.path(maHD)
                        .request(MediaType.APPLICATION_JSON).get();
                if (r.getStatus() == 200) hd = r.readEntity(HoaDon.class);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load invoice " + maHD, e);
            }

            if (hd == null) {
                response.sendRedirect(request.getContextPath() + "/hoadon");
                return;
            }

            // Load product list for dropdown
            List<SanPham> dsSP;
            try {
                SanPham[] arr = productTarget.request(MediaType.APPLICATION_JSON).get(SanPham[].class);
                dsSP = arr != null ? Arrays.asList(arr) : Collections.emptyList();
            } catch (Exception e) {
                dsSP = Collections.emptyList();
            }

            // Build chi tiet rows
            StringBuilder ctRows = new StringBuilder();
            List<ChiTietHD> dsCT = hd.getDsChiTiet() != null ? hd.getDsChiTiet() : Collections.emptyList();
            double subtotalSum = 0;
            for (ChiTietHD ct : dsCT) {
                String maSP = ct.getSanPham() != null ? safe(ct.getSanPham().getMaSP()) : "";
                String tenSP = ct.getSanPham() != null ? safe(ct.getSanPham().getTenSP()) : maSP;
                double sub = ct.getDonGia() * ct.getSoLuong();
                subtotalSum += sub;
                ctRows.append("<tr>");
                ctRows.append("<td>").append(htmlEsc(maSP)).append("</td>");
                ctRows.append("<td>").append(htmlEsc(tenSP)).append("</td>");
                ctRows.append("<td class='num'>").append(ct.getSoLuong()).append("</td>");
                ctRows.append("<td class='num'>").append(String.format("%,.0f", ct.getDonGia())).append("</td>");
                ctRows.append("<td class='num'>").append(String.format("%,.0f", sub)).append("</td>");
                ctRows.append("<td><form method='post' style='display:inline'>");
                ctRows.append("<input type='hidden' name='mahd' value='").append(htmlEsc(maHD)).append("'>");
                ctRows.append("<input type='hidden' name='masp' value='").append(htmlEsc(maSP)).append("'>");
                ctRows.append("<button class='table-action table-action-danger' type='submit' name='action' value='deleteChiTiet'");
                ctRows.append(" onclick=\"return confirm('X\\u00f3a chi ti\\u1ebft n\\u00e0y?')\">X\u00f3a</button>");
                ctRows.append("</form></td>");
                ctRows.append("</tr>");
            }
            if (ctRows.length() == 0) {
                ctRows.append("<tr class='table-empty'><td colspan='6'>Ch\u01b0a c\u00f3 s\u1ea3n ph\u1ea9m n\u00e0o.</td></tr>");
            }

            // Build product options for add-row form
            StringBuilder spOpts = new StringBuilder("<option value=''>-- Ch\u1ecdn s\u1ea3n ph\u1ea9m --</option>");
            for (SanPham sp : dsSP) {
                spOpts.append("<option value='").append(htmlEsc(safe(sp.getMaSP()))).append("'")
                      .append(" data-gia='").append(sp.getGia()).append("'>")
                      .append(htmlEsc(safe(sp.getMaSP()))).append(" - ").append(htmlEsc(safe(sp.getTenSP())))
                      .append(" (").append(String.format("%,.0f", sp.getGia())).append(")</option>");
            }

            double vatRate = hd.getVAT() > 1 ? hd.getVAT() / 100.0 : hd.getVAT();
            double vatAmt  = subtotalSum * vatRate;
            double discPct = 0;
            if (hd.getKhuyenMai() != null) {
                discPct = hd.getKhuyenMai().getPhanTramGiam() > 1
                        ? hd.getKhuyenMai().getPhanTramGiam() / 100.0
                        : hd.getKhuyenMai().getPhanTramGiam();
            }
            double discAmt = subtotalSum * discPct;
            double total   = subtotalSum + vatAmt - discAmt;

            request.setAttribute("hd",          hd);
            request.setAttribute("maHD",         maHD);
            request.setAttribute("chiTietRows",  ctRows.toString());
            request.setAttribute("spOpts",       spOpts.toString());
            request.setAttribute("subtotal",     String.format("%,.0f", subtotalSum));
            request.setAttribute("vatAmt",       String.format("%,.0f", vatAmt));
            request.setAttribute("discAmt",      String.format("%,.0f", discAmt));
            request.setAttribute("total",        String.format("%,.0f", total));
            request.setAttribute("message",      message);
            request.getRequestDispatcher("HoadonDetail.jsp").forward(request, response);
        } finally {
            client.close();
        }
    }
}
