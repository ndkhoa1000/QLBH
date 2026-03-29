package ctu.cit.servlet;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ctu.cit.model.KhuyenMai;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/khuyenmai")
public class KhuyenMaiServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(KhuyenMaiServlet.class.getName());

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

        Client client = createClient();
        try {
            WebTarget kmTarget = client.target(ServiceUrlConfig.getKhuyenMaiServiceUrl());
            String action = request.getParameter("action");
            String message = "";

            if ("add".equals(action)) {
                KhuyenMai km = buildFromRequest(request);
                String maKMInput = request.getParameter("makm");
                // Use user-provided code if given, otherwise let server generate
                km.setMaKM(maKMInput != null && !maKMInput.isBlank() ? maKMInput : null);
                Response r = kmTarget.request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(km, MediaType.APPLICATION_JSON));
                message = r.getStatus() == 201 ? "T\u1ea1o kh\u00e0m m\u00e3i th\u00e0nh c\u00f4ng" : "L\u1ed7i t\u1ea1o (HTTP " + r.getStatus() + ")";
            } else if ("update".equals(action)) {
                String maKM = request.getParameter("makm");
                KhuyenMai km = buildFromRequest(request);
                km.setMaKM(maKM);
                String r = kmTarget.path(maKM).request(MediaType.TEXT_PLAIN)
                        .put(Entity.entity(km, MediaType.APPLICATION_JSON), String.class);
                message = r;
            } else if ("delete".equals(action)) {
                String maKM = request.getParameter("makm");
                message = kmTarget.path(maKM).request(MediaType.TEXT_PLAIN).delete(String.class);
            }

            List<KhuyenMai> dsKM = loadList(kmTarget);

            StringBuilder rows = new StringBuilder();
            for (KhuyenMai km : dsKM) {
                double pct = km.getPhanTramGiam() <= 1
                        ? Math.round(km.getPhanTramGiam() * 100) : km.getPhanTramGiam();
                rows.append("<tr>");
                rows.append("<td>").append(htmlEsc(safe(km.getMaKM()))).append("</td>");
                rows.append("<td>").append(km.getNgayApDung()).append("</td>");
                rows.append("<td>").append(km.getNgayKetThuc()).append("</td>");
                rows.append("<td>").append(pct).append("%</td>");
                rows.append("<td><div class='table-actions'>");
                rows.append("<button class='table-inline-button' type='button' onclick=\"suaKM('")
                    .append(htmlEsc(safe(km.getMaKM()))).append("','")
                    .append(km.getNgayApDung()).append("','")
                    .append(km.getNgayKetThuc()).append("',").append(pct).append(")\">S\u1eeda</button>");
                rows.append("<a class='table-action table-action-danger' href='khuyenmai?action=delete&makm=")
                    .append(htmlEsc(safe(km.getMaKM())))
                    .append("' onclick=\"return moXacNhanXoa(this.href,'X\u00f3a kh\u00e0m m\u00e3i n\u00e0y?')\">X\u00f3a</a>");
                rows.append("</div></td>");
                rows.append("</tr>");
            }
            if (rows.length() == 0) {
                rows.append("<tr class='table-empty'><td colspan='5'>Ch\u01b0a c\u00f3 ch\u01b0\u01a1ng tr\u00ecnh kh\u00e0m m\u00e3i n\u00e0o.</td></tr>");
            }

            request.setAttribute("dsKhuyenMai", rows.toString());
            request.setAttribute("message", message);
            request.getRequestDispatcher("KhuyenMai.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "KhuyenMaiServlet error", e);
            request.setAttribute("message", "L\u1ed7i h\u1ec7 th\u1ed1ng: " + e.getMessage());
            request.setAttribute("dsKhuyenMai", "");
            request.getRequestDispatcher("KhuyenMai.jsp").forward(request, response);
        } finally {
            client.close();
        }
    }

    private KhuyenMai buildFromRequest(HttpServletRequest req) {
        KhuyenMai km = new KhuyenMai();
        String ngayAp = req.getParameter("ngayapdung");
        String ngayKT = req.getParameter("ngayketthuc");
        String pct    = req.getParameter("phantramgiam");
        if (ngayAp != null && !ngayAp.isBlank()) km.setNgayApDung(LocalDate.parse(ngayAp));
        if (ngayKT != null && !ngayKT.isBlank()) km.setNgayKetThuc(LocalDate.parse(ngayKT));
        if (pct != null && !pct.isBlank()) {
            double v = Double.parseDouble(pct);
            km.setPhanTramGiam(v > 1 && v <= 100 ? v / 100.0 : v);
        }
        return km;
    }

    private List<KhuyenMai> loadList(WebTarget kmTarget) {
        try {
            KhuyenMai[] arr = kmTarget.request(MediaType.APPLICATION_JSON).get(KhuyenMai[].class);
            return arr != null ? Arrays.asList(arr) : Collections.emptyList();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load khuyenmai", e);
            return Collections.emptyList();
        }
    }
}
