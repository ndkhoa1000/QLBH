package ctu.cit.service;
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

import ctu.cit.model.ChiTietHD;
import ctu.cit.model.HoaDon;
import ctu.cit.model.KhuyenMai;

@Path("/hoadon")
public class HoaDonService {

    private static IHoaDonService service = new HoaDonServiceImpl();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String taoHoaDon(HoaDon hd) {
        if (hd == null) return "That bai";
        return service.taoHoaDon(hd) ? "Tao thanh cong" : "That bai";
    }

    @POST
    @Path("/{maHD}/muahang")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String muaHang(@PathParam("maHD") String maHD, ChiTietHD ct) {
        if (ct == null) return "Loi du lieu";
        return service.muaHang(maHD, ct)
            ? "Mua hang thanh cong"
            : "Khong du hang hoac loi";
    }

    @PUT
    @Path("/{maHD}/khuyenmai")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String apDungKM(@PathParam("maHD") String maHD, KhuyenMai km) {
        if (km == null) return "Khong hop le";
        return service.apDungKhuyenMai(maHD, km)
            ? "Ap dung thanh cong"
            : "Khuyen mai khong hop le";
    }

    @GET
    @Path("/{maHD}/tongtien")
    @Produces(MediaType.TEXT_PLAIN)
    public double tongTien(@PathParam("maHD") String maHD) {
        return service.tinhTongTien(maHD);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<HoaDon> getAll() {
        return service.getAll();
    }

    @DELETE
    @Path("/{maHD}")
    @Produces(MediaType.TEXT_PLAIN)
    public String delete(@PathParam("maHD") String maHD) {
        return service.xoaHoaDon(maHD)
            ? "Xoa thanh cong"
            : "Khong tim thay";
    }
}
