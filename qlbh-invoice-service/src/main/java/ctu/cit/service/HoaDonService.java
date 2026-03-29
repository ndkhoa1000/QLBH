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

import javax.ws.rs.core.Response;
import ctu.cit.model.HoaDon;
import ctu.cit.model.KhuyenMai;
import ctu.cit.model.ChiTietHD;

import javax.ws.rs.core.Response;

@Path("/hoadon")
public class HoaDonService {

    private static IHoaDonService service = new HoaDonServiceImpl();

    /** Generate next invoice ID without creating the invoice. */
    @GET
    @Path("/nextid")
    @Produces(MediaType.TEXT_PLAIN)
    public String nextId() {
        return service.nextId();
    }

    /** Create invoice header (dsChiTiet optional). Returns 201 + created HoaDon JSON. */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response taoHoaDon(HoaDon hd) {
        if (hd == null) return Response.status(400).entity(new HoaDon()).build();
        boolean ok = service.taoHoaDon(hd);
        if (ok) {
            HoaDon created = service.getById(hd.getMaHD());
            return Response.status(201).entity(created != null ? created : hd).build();
        }
        return Response.status(500).build();
    }

    @GET
    @Path("/{maHD}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("maHD") String maHD) {
        HoaDon hd = service.getById(maHD);
        return hd != null ? Response.ok(hd).build() : Response.status(404).build();
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

    @PUT
    @Path("/{maHD}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String capNhatHoaDon(@PathParam("maHD") String maHD, HoaDon hd) {
        if (hd == null) return "Du lieu khong hop le";
        return service.capNhatHoaDon(maHD, hd) ? "Cap nhat thanh cong" : "Cap nhat that bai";
    }

    @DELETE
    @Path("/{maHD}/chitiet/{maSP}")
    @Produces(MediaType.TEXT_PLAIN)
    public String xoaChiTiet(@PathParam("maHD") String maHD, @PathParam("maSP") String maSP) {
        return service.xoaChiTiet(maHD, maSP) ? "Xoa chi tiet thanh cong" : "Khong tim thay chi tiet";
    }
}
