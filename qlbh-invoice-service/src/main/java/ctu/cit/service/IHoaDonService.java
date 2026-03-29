package ctu.cit.service;

import java.util.List;

import ctu.cit.model.ChiTietHD;
import ctu.cit.model.HoaDon;
import ctu.cit.model.KhuyenMai;

public interface IHoaDonService {
	boolean taoHoaDon(HoaDon hd);
	boolean muaHang(String maHD, ChiTietHD ct);
	boolean apDungKhuyenMai(String maHD, KhuyenMai km);
	double tinhTongTien(String maHD);
	List<HoaDon> getAll();
	HoaDon getById(String maHD);
	String nextId();
	boolean xoaHoaDon(String maHD);
	boolean capNhatHoaDon(String maHD, HoaDon hd);
	boolean xoaChiTiet(String maHD, String maSP);
}