package ctu.cit.service;

import ctu.cit.model.KhuyenMai;
import java.util.List;

public interface IKhuyenMaiService {
    List<KhuyenMai> getAll();
    KhuyenMai getById(String maKM);
    String create(KhuyenMai km);   // returns generated maKM
    boolean update(String maKM, KhuyenMai km);
    boolean delete(String maKM);
}
