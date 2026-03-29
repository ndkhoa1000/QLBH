package ctu.cit.service;

import ctu.cit.model.KhuyenMai;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/khuyenmai")
@Produces(MediaType.APPLICATION_JSON)
public class KhuyenMaiService {

    private static final IKhuyenMaiService service = new KhuyenMaiServiceImpl();

    @GET
    public List<KhuyenMai> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{maKM}")
    public Response getById(@PathParam("maKM") String maKM) {
        KhuyenMai km = service.getById(maKM);
        return km != null ? Response.ok(km).build() : Response.status(404).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(KhuyenMai km) {
        if (km == null) return Response.status(400).entity("Du lieu null").build();
        String maKM = service.create(km);
        if (maKM != null) {
            KhuyenMai created = service.getById(maKM);
            return Response.status(201).entity(created).build();
        }
        return Response.status(500).entity("Tao that bai").build();
    }

    @PUT
    @Path("/{maKM}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String update(@PathParam("maKM") String maKM, KhuyenMai km) {
        if (km == null) return "Du lieu null";
        return service.update(maKM, km) ? "Cap nhat thanh cong" : "Cap nhat that bai";
    }

    @DELETE
    @Path("/{maKM}")
    @Produces(MediaType.TEXT_PLAIN)
    public String delete(@PathParam("maKM") String maKM) {
        return service.delete(maKM) ? "Xoa thanh cong" : "Khong tim thay";
    }
}
