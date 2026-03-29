package ctu.cit.servlet;

import ctu.cit.model.NhaCungCap;
import org.glassfish.jersey.client.ClientConfig;

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
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@WebServlet("/nhacungcap")
public class NhaCungCapServlet extends HttpServlet {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        Client client = ClientBuilder.newClient(new ClientConfig());
        WebTarget target = client.target(ServiceUrlConfig.getSupplierServiceUrl());

        String action = request.getParameter("action");
        String message = "";

        if (action != null) {
            if ("add".equals(action)) {
                NhaCungCap ncc = new NhaCungCap();
                ncc.setMaNCC(request.getParameter("mancc"));
                ncc.setTenNCC(request.getParameter("tenncc"));
                ncc.setDiaChi(request.getParameter("dcncc"));
                ncc.setSoDienThoai(request.getParameter("sdtncc"));

                message = target.path("nhacungcap").request(MediaType.TEXT_PLAIN).post(Entity.entity(ncc, MediaType.APPLICATION_JSON), String.class);
            } else if ("update".equals(action)) {
                String maNCC = request.getParameter("mancc");

                NhaCungCap ncc = new NhaCungCap();
                ncc.setMaNCC(maNCC);
                ncc.setTenNCC(request.getParameter("tenncc"));
                ncc.setDiaChi(request.getParameter("dcncc"));
                ncc.setSoDienThoai(request.getParameter("sdtncc"));

                message = target.path("nhacungcap").path(maNCC).request(MediaType.TEXT_PLAIN).put(Entity.entity(ncc, MediaType.APPLICATION_JSON), String.class);
            } else if ("delete".equals(action)) {
                String maNCC = request.getParameter("mancc");
                message = target.path("nhacungcap").path(maNCC).request(MediaType.TEXT_PLAIN).delete(String.class);
            }
        }

        // =========Lay nha cung cap ============
        List<NhaCungCap> dsNCC = Collections.emptyList();
        try {
            NhaCungCap[] array = target.path("nhacungcap").request(MediaType.APPLICATION_JSON).get(NhaCungCap[].class);
            if (array != null) {
                dsNCC = Arrays.asList(array);
            }
        } catch (ProcessingException | WebApplicationException e) {}

        StringBuilder inDS = new StringBuilder();
        for (NhaCungCap ncc : dsNCC) {

            inDS.append("<tr>");
            inDS.append("<td>").append(safe(ncc.getMaNCC())).append("</td>");
            inDS.append("<td>").append(safe(ncc.getTenNCC())).append("</td>");
            inDS.append("<td>").append(safe(ncc.getDiaChi())).append("</td>");
            inDS.append("<td>").append(safe(ncc.getSoDienThoai())).append("</td>");
            inDS.append("<td><div class='table-actions'>");
            inDS.append("<button class='table-inline-button' type='button' onclick=\"chinhSuaNhaCungCap('")
                    .append(escapeJs(ncc.getMaNCC()))
                    .append("','")
                    .append(escapeJs(ncc.getTenNCC()))
                    .append("','")
                    .append(escapeJs(ncc.getDiaChi()))
                    .append("','")
                    .append(escapeJs(ncc.getSoDienThoai()))
                    .append("')\">Sửa</button>");
            inDS.append("<a class='table-action table-action-danger' onclick=\"return moXacNhanXoa(this.href, 'B\\u1ea1n c\\u00f3 ch\\u1eafc mu\\u1ed1n x\\u00f3a nh\\u00e0 cung c\\u1ea5p n\\u00e0y?');\" href='nhacungcap?action=delete&mancc=")
                    .append(safe(ncc.getMaNCC()))
                    .append("'>Xóa</a></div></td>");
            inDS.append("</tr>");
        }
        if (inDS.length() == 0) {
            inDS.append("<tr class='table-empty'><td colspan='5'>Chưa có nhà cung cấp nào.</td></tr>");
        }
        request.setAttribute("dsNCC", inDS.toString());
        request.setAttribute("message", message);
        request.getRequestDispatcher("Nhacungcap.jsp").forward(request, response);
        client.close();
    }


}
