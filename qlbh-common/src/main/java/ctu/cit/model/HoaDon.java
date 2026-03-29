package ctu.cit.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.*;

public class HoaDon {
	private String maHD;
	private LocalDate ngayLap;
	private double VAT;
	private KhachHang khachHang;
	private KhuyenMai khuyenMai;
	private List<ChiTietHD> dsChiTiet;

	public HoaDon() {
		dsChiTiet = new ArrayList<>();
		ngayLap = LocalDate.now();
	}

	public HoaDon(String maHD, double VAT) {
		this();
		this.maHD = maHD;
		this.VAT = VAT;
	}

	public void themChiTiet(ChiTietHD ct) {
		if (ct != null) {
			dsChiTiet.add(ct);
			ct.getSanPham().giamSoLuong(ct.getSoLuong());
		}
	}

	public double tinhTienTruocVAT() {
		return dsChiTiet.stream().mapToDouble(ChiTietHD::tinhTien).sum();
	}

	public double tinhTienVAT() {
		return tinhTienTruocVAT() * VAT;
	}

	public double tinhTongTien() {
		double tong = tinhTienTruocVAT() + tinhTienVAT();

		if (khuyenMai != null) {
			tong = khuyenMai.apDung(tong);
		}

		return tong;
	}

	// Getter Setter
	public String getMaHD() {
		return maHD;
	}

	public void setMaHD(String maHD) {
		this.maHD = maHD;
	}

	public LocalDate getNgayLap() {
		return ngayLap;
	}
	
	public void setNgayLap(LocalDate ngayLap) {
	    this.ngayLap = ngayLap;
	}

	@JsonProperty("VAT")
	public double getVAT() {
		return VAT;
	}

	@JsonProperty("VAT")
	@JsonAlias("vat")
	public void setVAT(double VAT) {
		this.VAT = VAT;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public KhuyenMai getKhuyenMai() {
		return khuyenMai;
	}

	public void setKhuyenMai(KhuyenMai khuyenMai) {
		this.khuyenMai = khuyenMai;
	}

	public List<ChiTietHD> getDsChiTiet() {
		return dsChiTiet;
	}
}
