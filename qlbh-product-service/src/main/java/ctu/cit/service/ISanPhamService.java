package ctu.cit.service;

import java.util.List;

import ctu.cit.model.SanPham;

public interface ISanPhamService {
    boolean themSanPham(SanPham sp);
    boolean xoaSanPham(String ma);
    SanPham timSanPham(String ma);
    List<SanPham> getAll();
    boolean giamSoLuong(String maSP, int soLuong);
    boolean tangSoLuong(String maSP, int soLuong);
}