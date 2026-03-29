package ctu.cit.service;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import ctu.cit.model.SanPham;

@Path("/sanpham")
@Produces(MediaType.APPLICATION_JSON)
public class SanPhamService {

	private static ISanPhamService service = SanPhamServiceImpl.getInstance();

    // ================= GET ALL =================
    @GET
    public List<SanPham> getAll() {
        return service.getAll();
    }

    // ================= GET BY ID =================
    @GET
    @Path("/{ma}")
    public SanPham getById(@PathParam("ma") String ma) {
        return service.timSanPham(ma);
    }

    // ================= ADD =================
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String add(SanPham sp) {
        if (sp == null || sp.getMaSP() == null) {
            return "Fail";
        }
        return service.themSanPham(sp) ? "OK" : "Fail";
    }

    // ================= UPDATE =================
    @PUT
    @Path("/{ma}")
    @Consumes(MediaType.APPLICATION_JSON)
    public String update(@PathParam("ma") String ma, SanPham spMoi) {
        SanPham sp = service.timSanPham(ma);

        if (sp != null && spMoi != null) {
            sp.setTenSP(spMoi.getTenSP());
            sp.setGia(spMoi.getGia());
            sp.setSoLuongTon(spMoi.getSoLuongTon());
            return "OK";
        }
        return "Fail";
    }

    // ================= DELETE =================
    @DELETE
    @Path("/{ma}")
    public String delete(@PathParam("ma") String ma) {
        return service.xoaSanPham(ma) ? "OK" : "Fail";
    }

    // ================= GIẢM SỐ LƯỢNG =================
    @PUT
    @Path("/giam/{ma}/{sl}")
    public String giam(@PathParam("ma") String ma, @PathParam("sl") int sl) {
        if (sl <= 0) return "Fail";
        return service.giamSoLuong(ma, sl) ? "OK" : "Fail";
    }

    // ================= TĂNG SỐ LƯỢNG =================
    @PUT
    @Path("/tang/{ma}/{sl}")
    public String tang(@PathParam("ma") String ma, @PathParam("sl") int sl) {
        if (sl <= 0) return "Fail";
        return service.tangSoLuong(ma, sl) ? "OK" : "Fail";
    }
}