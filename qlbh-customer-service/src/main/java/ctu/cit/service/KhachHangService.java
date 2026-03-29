package ctu.cit.service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ctu.cit.model.KhachHang;

@Path("/khachhang")
public class KhachHangService {

    private static IKhachHangService service = new KhachHangServiceImpl();

    @GET
    public List<KhachHang> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{ma}")
    public KhachHang getById(@PathParam("ma") String ma) {
        return service.timKhachHang(ma);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String add(KhachHang kh) {
        if (kh == null || kh.getMaKH() == null) return "Fail";
        return service.themKhachHang(kh) ? "OK" : "Ma da ton tai";
    }

    @PUT
    @Path("/{ma}")
    @Consumes(MediaType.APPLICATION_JSON)
    public String update(@PathParam("ma") String ma, KhachHang kh) {
        if (kh == null) return "Fail";
        return service.capNhat(ma, kh) ? "OK" : "Khong tim thay";
    }

    @DELETE
    @Path("/{ma}")
    public String delete(@PathParam("ma") String ma) {
        return service.xoa(ma) ? "OK" : "Khong tim thay";
    }
}