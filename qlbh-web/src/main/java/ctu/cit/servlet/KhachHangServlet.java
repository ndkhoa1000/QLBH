package ctu.cit.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import javax.ws.rs.client.*;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import ctu.cit.model.KhachHang;
import org.glassfish.jersey.client.ClientConfig;

@WebServlet("/khachhang")
public class KhachHangServlet extends HttpServlet  {
    private static final Logger LOGGER = Logger.getLogger(KhachHangServlet.class.getName());

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String escapeJs(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("'", "\\'");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String message = "";
        String action = request.getParameter("action");
        WebTarget customerTarget = ClientBuilder.newClient(new ClientConfig()).target(ServiceUrlConfig.getCustomerServiceUrl());

        if (action != null) {
            try {
                if ("add".equals(action)) {
                    String makh = request.getParameter("makh");
                    String tenkh = request.getParameter("tenkh");
                    String dc = request.getParameter("dc");
                    String cccd = request.getParameter("cccd");
                    KhachHang kh = new KhachHang(makh, tenkh, dc, cccd);
                    message = customerTarget.request(MediaType.TEXT_PLAIN)
                            .post(Entity.entity(kh, MediaType.APPLICATION_JSON), String.class);

                } else if ("update".equals(action)) {
                    String makh = request.getParameter("makh");
                    String tenkh = request.getParameter("tenkh");
                    String dc = request.getParameter("dc");
                    String cccd = request.getParameter("cccd");
                    KhachHang kh = new KhachHang(makh, tenkh, dc, cccd);

                    message = customerTarget.path(makh).request(MediaType.TEXT_PLAIN).put(Entity.entity(kh, MediaType.APPLICATION_JSON), String.class);
                } else if ("delete".equals(action)) {
                    String makh = request.getParameter("makh");
                    message = customerTarget.path(makh).request(MediaType.TEXT_PLAIN).delete(String.class);
                }
            } catch (ProcessingException | WebApplicationException e) {
                LOGGER.log(Level.WARNING, "Failed to process customer action", e);
                message = "Khong thuc hien duoc thao tac khach hang.";
            }
        }

        List<KhachHang> dsKhachHang = Collections.emptyList();
        try {
            KhachHang[] array = customerTarget.request(MediaType.APPLICATION_JSON).get(KhachHang[].class);
            if (array != null) {
                dsKhachHang = Arrays.asList(array);
            }
        } catch (ProcessingException | WebApplicationException e) {
            LOGGER.log(Level.WARNING, "Failed to load customer list from REST API", e);
            if (message == null || message.isEmpty()) {
                message = "Khong tai duoc danh sach khach hang.";
            }
        } finally {
            customerTarget.getUriBuilder().build(); // Just to keep the pattern, but client should be closed
        }

        StringBuilder inDS = new StringBuilder();
        for (KhachHang kh : dsKhachHang) {
            inDS.append("<tr>");
            inDS.append("<td>").append(safe(kh.getMaKH())).append("</td>");
            inDS.append("<td>").append(safe(kh.getHoTen())).append("</td>");
            inDS.append("<td>").append(safe(kh.getDiaChi())).append("</td>");
            inDS.append("<td>").append(safe(kh.getCccd())).append("</td>");
            inDS.append("<td><div class='table-actions'>");
            inDS.append("<button class='table-inline-button' type='button' onclick=\"chinhSuaKhachHang('")
                    .append(escapeJs(kh.getMaKH()))
                    .append("','")
                    .append(escapeJs(kh.getHoTen()))
                    .append("','")
                    .append(escapeJs(kh.getDiaChi()))
                    .append("','")
                    .append(escapeJs(kh.getCccd()))
                    .append("')\">Sửa</button>");
            inDS.append("<a class='table-action table-action-danger' onclick=\"return moXacNhanXoa(this.href, 'B\\u1ea1n c\\u00f3 ch\\u1eafc mu\\u1ed1n x\\u00f3a kh\\u00e1ch h\\u00e0ng n\\u00e0y?');\" href='khachhang?action=delete&makh=")
                    .append(safe(kh.getMaKH()))
                    .append("'>Xóa</a></div></td>");
            inDS.append("</tr>");
        }
        if (inDS.length() == 0) {
            inDS.append("<tr class='table-empty'><td colspan='5'>Chưa có khách hàng nào.</td></tr>");
        }
        request.setAttribute("dsKhachHang", inDS.toString());

        request.setAttribute("message", message);
        request.getRequestDispatcher("Khachhang.jsp").forward(request, response);
    }

}
