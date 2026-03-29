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

import ctu.cit.model.NhaCungCap;

@Path("/nhacungcap")
public class NhaCungCapService {

    private static INhaCungCapService service = new NhaCungCapServiceImpl();

    @GET
    public List<NhaCungCap> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{ma}")
    public NhaCungCap getById(@PathParam("ma") String ma) {
        return service.tim(ma);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String add(NhaCungCap ncc) {
        if (ncc == null || ncc.getMaNCC() == null) return "Fail";
        return service.them(ncc) ? "OK" : "Fail";
    }

    @PUT
    @Path("/{ma}")
    @Consumes(MediaType.APPLICATION_JSON)
    public String update(@PathParam("ma") String ma, NhaCungCap ncc) {
        return service.capNhat(ma, ncc) ? "OK" : "Fail";
    }

    @DELETE
    @Path("/{ma}")
    public String delete(@PathParam("ma") String ma) {
        return service.xoa(ma) ? "OK" : "Fail";
    }

}