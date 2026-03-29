package ctu.cit.model;

import java.time.LocalDate;

public class KhuyenMai {
	private String maKM;
	private LocalDate ngayApDung;
	private LocalDate ngayKetThuc;
	private double phanTramGiam;

	public KhuyenMai() {
	}

	public KhuyenMai(String maKM, LocalDate ngayApDung, LocalDate ngayKetThuc, double phanTramGiam) {
		this.maKM = maKM;
		this.ngayApDung = ngayApDung;
		this.ngayKetThuc = ngayKetThuc;
		this.phanTramGiam = phanTramGiam;
	}

	public boolean conHieuLuc() {
		LocalDate today = LocalDate.now();
		return !today.isBefore(ngayApDung) && !today.isAfter(ngayKetThuc);
	}

	public double apDung(double tongTien) {
		if (conHieuLuc()) {
			return tongTien * (1 - phanTramGiam);
		}
		return tongTien;
	}

	public String getMaKM() {
		return maKM;
	}

	public void setMaKM(String maKM) {
		this.maKM = maKM;
	}

	public LocalDate getNgayApDung() {
		return ngayApDung;
	}

	public void setNgayApDung(LocalDate ngayApDung) {
		this.ngayApDung = ngayApDung;
	}

	public LocalDate getNgayKetThuc() {
		return ngayKetThuc;
	}

	public void setNgayKetThuc(LocalDate ngayKetThuc) {
		this.ngayKetThuc = ngayKetThuc;
	}

	public double getPhanTramGiam() {
		return phanTramGiam;
	}

	public void setPhanTramGiam(double phanTramGiam) {
		this.phanTramGiam = phanTramGiam;
	}
}
