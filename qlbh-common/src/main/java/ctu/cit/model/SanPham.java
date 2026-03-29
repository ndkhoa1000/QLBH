package ctu.cit.model;

public class SanPham {
	private String maSP;
	private String tenSP;
	private double gia;
	private int soLuongTon;
	private NhaCungCap nhaCungCap;

	public SanPham() {
	}

	public SanPham(String maSP, String tenSP, double gia, int soLuongTon, NhaCungCap nhaCungCap) {
		this.maSP = maSP;
		this.tenSP = tenSP;
		this.gia = gia;
		this.soLuongTon = soLuongTon;
		this.nhaCungCap = nhaCungCap;
	}

	public void giamSoLuong(int soLuongBan) {
		if (soLuongBan > 0 && soLuongTon >= soLuongBan) {
			soLuongTon -= soLuongBan;
		} else {
			throw new IllegalArgumentException("Khong du hang");
		}
	}

	public String getMaSP() {
		return maSP;
	}

	public void setMaSP(String maSP) {
		this.maSP = maSP;
	}

	public String getTenSP() {
		return tenSP;
	}

	public void setTenSP(String tenSP) {
		this.tenSP = tenSP;
	}

	public double getGia() {
		return gia;
	}

	public void setGia(double gia) {
		if (gia >= 0)
			this.gia = gia;
	}

	public int getSoLuongTon() {
		return soLuongTon;
	}

	public void setSoLuongTon(int soLuongTon) {
		if (soLuongTon >= 0)
			this.soLuongTon = soLuongTon;
	}

	public NhaCungCap getNhaCungCap() {
		return nhaCungCap;
	}

	public void setNhaCungCap(NhaCungCap nhaCungCap) {
		this.nhaCungCap = nhaCungCap;
	}

	@Override
	public String toString() {
		return tenSP + " (" + maSP + ")";
	}
}