package ctu.cit.service;

import java.util.List;

import ctu.cit.model.KhachHang;

public interface IKhachHangService {
    boolean themKhachHang(KhachHang kh);
    KhachHang timKhachHang(String ma);
    List<KhachHang> getAll();
    boolean capNhat(String ma, KhachHang kh);
    boolean xoa(String ma);
}
