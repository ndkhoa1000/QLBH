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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import ctu.cit.model.NhaCungCap;
import ctu.cit.model.SanPham;
import org.glassfish.jersey.client.ClientConfig;

@WebServlet("/sanpham")
public class SanPhamServlet extends HttpServlet  {
    private static final Logger LOGGER = Logger.getLogger(SanPhamServlet.class.getName());

    private NhaCungCap timNhaCungCap(List<NhaCungCap> dsNCC, String maNCC) {
        for (NhaCungCap ncc : dsNCC) {
            if (ncc != null && ncc.getMaNCC() != null && ncc.getMaNCC().equals(maNCC)) {
                return ncc;
            }
        }
        return null;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String escapeJs(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("'", "\\'");
    }

    private String buildClientErrorMessage(String fallback, Exception e) {
        if (e instanceof WebApplicationException) {
            WebApplicationException webEx = (WebApplicationException) e;
            Response response = webEx.getResponse();
            if (response != null) {
                String body = "";
                try {
                    body = response.readEntity(String.class);
                } catch (IllegalStateException ignored) {
                    body = "";
                }

                StringBuilder message = new StringBuilder(fallback)
                        .append(" (HTTP ")
                        .append(response.getStatus())
                        .append(")");
                if (body != null && !body.isBlank()) {
                    message.append(": ").append(body);
                }
                return message.toString();
            }
        }
        if (e instanceof ProcessingException) {
            return fallback + ": Khong ket noi duoc toi REST API Backend qua mang Docker.";
        }
        return fallback;
    }

    private String extractResponseMessage(Response response, String successFallback) {
        if (response == null) {
            return successFallback;
        }

        String body = "";
        if (response.hasEntity()) {
            try {
                body = response.readEntity(String.class);
            } catch (IllegalStateException ignored) {
                body = "";
            }
        }

        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            return body != null && !body.isBlank() ? body : successFallback;
        }

        StringBuilder message = new StringBuilder("REST API loi (HTTP ")
                .append(response.getStatus())
                .append(")");
        if (body != null && !body.isBlank()) {
            message.append(": ").append(body);
        }
        return message.toString();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        Client client = ClientBuilder.newClient(new ClientConfig());
        try {
            // Target cho từng service
            WebTarget productTarget = client.target(ServiceUrlConfig.getProductServiceUrl());
            WebTarget supplierTarget = client.target(ServiceUrlConfig.getSupplierServiceUrl());

            // =========Lay nha cung cap ============
            List<NhaCungCap> dsNCC = Collections.emptyList();
            try {
                NhaCungCap[] array = supplierTarget.request(MediaType.APPLICATION_JSON).get(NhaCungCap[].class);
                if (array != null) {
                    dsNCC = Arrays.asList(array);
                }
            } catch (ProcessingException | WebApplicationException e) {
                LOGGER.log(Level.WARNING, "Failed to load supplier list", e);
            }

            // ========== Thao tac form =============
            String action = request.getParameter("action");
            String message = "";

            if (action != null) {
                try {
                    if ("add".equals(action)) {
                        String maSP = request.getParameter("masp");
                        String tenSP = request.getParameter("tensp");
                        String giaSP = request.getParameter("gia");
                        String soLuongTonSP = request.getParameter("slton");
                        String nhaCungCapSP = request.getParameter("nhacungcap");

                        SanPham sp = new SanPham();
                        sp.setMaSP(maSP);
                        sp.setTenSP(tenSP);
                        sp.setGia(Double.parseDouble(giaSP));
                        sp.setSoLuongTon(Integer.parseInt(soLuongTonSP));
                        NhaCungCap ncc = timNhaCungCap(dsNCC, nhaCungCapSP);
                        if (ncc == null) {
                            message = "Khong tim thay nha cung cap.";
                        } else {
                            sp.setNhaCungCap(ncc);
                            Response restResponse = productTarget
                                    .request()
                                    .post(Entity.entity(sp, MediaType.APPLICATION_JSON));
                            message = extractResponseMessage(restResponse, "Them san pham thanh cong.");
                        }
                    } else if ("update".equals(action)) {
                        String maSP = request.getParameter("masp");
                        String tenSP = request.getParameter("tensp");
                        String giaSP = request.getParameter("gia");
                        String soLuongTonSP = request.getParameter("slton");
                        String nhaCungCapSP = request.getParameter("nhacungcap");

                        SanPham sp = new SanPham();
                        sp.setMaSP(maSP);
                        sp.setTenSP(tenSP);
                        sp.setGia(Double.parseDouble(giaSP));
                        sp.setSoLuongTon(Integer.parseInt(soLuongTonSP));
                        NhaCungCap ncc = timNhaCungCap(dsNCC, nhaCungCapSP);
                        if (ncc == null) {
                            message = "Khong tim thay nha cung cap.";
                        } else {
                            sp.setNhaCungCap(ncc);
                            Response restResponse = productTarget.path(maSP)
                                    .request()
                                    .put(Entity.entity(sp, MediaType.APPLICATION_JSON));
                            message = extractResponseMessage(restResponse, "Cap nhat san pham thanh cong.");
                        }
                    } else if ("delete".equals(action)) {
                        String masp = request.getParameter("masp");
                        if (masp != null && !masp.isBlank()) {
                            Response restResponse = productTarget.path(masp)
                                    .request()
                                    .delete();
                            message = extractResponseMessage(restResponse, "Xoa san pham thanh cong.");
                        } else {
                            message = "Ma san pham khong hop le.";
                        }
                    }
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "Invalid number format in product form", e);
                    message = "Du lieu gia hoac so luong ton khong hop le.";
                } catch (ProcessingException | WebApplicationException e) {
                    LOGGER.log(Level.WARNING, "Failed to process product action", e);
                    message = buildClientErrorMessage("Khong thuc hien duoc thao tac san pham.", e);
                }
            }

            // ========== Lay ds san pham ==============
            List<SanPham> dsSP = Collections.emptyList();
            try {
                SanPham[] array = productTarget.request(MediaType.APPLICATION_JSON).get(SanPham[].class);
                if (array != null) {
                    dsSP = Arrays.asList(array);
                }
            } catch (ProcessingException | WebApplicationException e) {
                LOGGER.log(Level.WARNING, "Failed to load product list", e);
                if (message.isEmpty()) {
                    message = buildClientErrorMessage("Khong tai duoc danh sach san pham.", e);
                }
            }

            StringBuilder inDS = new StringBuilder();
            for (SanPham sp : dsSP) {
                if (sp == null) {
                    continue;
                }
                String tenNCC = "";
                if (sp.getNhaCungCap() != null) {
                    tenNCC = safe(sp.getNhaCungCap().getTenNCC());
                }
                String maNCC = sp.getNhaCungCap() != null ? safe(sp.getNhaCungCap().getMaNCC()) : "";

                inDS.append("<tr>");
                inDS.append("<td>").append(safe(sp.getMaSP())).append("</td>");
                inDS.append("<td>").append(safe(sp.getTenSP())).append("</td>");
                inDS.append("<td>").append(sp.getGia()).append("</td>");
                inDS.append("<td>").append(tenNCC).append("</td>");
                inDS.append("<td>").append(sp.getSoLuongTon()).append("</td>");
                inDS.append("<td><div class='table-actions'>");
                inDS.append("<button class='table-inline-button' type='button' onclick=\"chinhSuaSanPham('")
                        .append(escapeJs(safe(sp.getMaSP())))
                        .append("','")
                        .append(escapeJs(safe(sp.getTenSP())))
                        .append("','")
                        .append(sp.getGia())
                        .append("','")
                        .append(escapeJs(maNCC))
                        .append("','")
                        .append(sp.getSoLuongTon())
                        .append("')\">Sửa</button>");
                inDS.append("<a class='table-action table-action-danger' onclick=\"return moXacNhanXoa(this.href, 'B\\u1ea1n c\\u00f3 ch\\u1eafc mu\\u1ed1n x\\u00f3a s\\u1ea3n ph\\u1ea9m n\\u00e0y?');\" href='sanpham?action=delete&masp=")
                        .append(safe(sp.getMaSP()))
                        .append("'>Xóa</a></div></td>");
                inDS.append("</tr>");
            }
            if (inDS.length() == 0) {
                inDS.append("<tr class='table-empty'><td colspan='6'>Chưa có sản phẩm nào.</td></tr>");
            }
            request.setAttribute("dsSP", inDS.toString());
            request.setAttribute("message", message);
            request.getRequestDispatcher("Sanpham.jsp").forward(request, response);
        } finally {
            client.close();
        }
    }

}
