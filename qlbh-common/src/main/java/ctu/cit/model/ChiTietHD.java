package ctu.cit.model;

public class ChiTietHD {
	private String maSP;
	private SanPham sanPham;
	private int soLuong;
	private double donGia;

	public ChiTietHD() {
	}
	
	public ChiTietHD(SanPham sanPham,String maSP, int soLuong, double donGia) {
		this.maSP = maSP;
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.sanPham = sanPham;
	}

	public double tinhTien() {
		return soLuong * donGia;
	}

	public SanPham getSanPham() {
		return sanPham;
	}

	public void setSanPham(SanPham sanPham) {
		this.sanPham = sanPham;
	}
	
	public void setMaSP(String maSP) {
	    this.maSP = maSP;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		if (soLuong > 0)
			this.soLuong = soLuong;
	}

	public double getDonGia() {
		return donGia;
	}

	public void setDonGia(double donGia) {
		if (donGia >= 0)
			this.donGia = donGia;
	}

	public String getMaSP() {
		return maSP;
	}
}