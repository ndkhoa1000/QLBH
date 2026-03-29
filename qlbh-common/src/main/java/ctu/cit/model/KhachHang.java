package ctu.cit.model;

public class KhachHang {
	private String maKH;
	private String hoTen;
	private String diaChi;
	private String cccd;

	public KhachHang() {
	}

	public KhachHang(String maKH, String hoTen, String diaChi, String cccd) {
		this.maKH = maKH;
		this.hoTen = hoTen;
		this.diaChi = diaChi;
		setCccd(cccd);
	}

	public void setCccd(String cccd) {
		this.cccd = cccd;
	}

	public String getMaKH() {
		return maKH;
	}

	public void setMaKH(String maKH) {
		this.maKH = maKH;
	}

	public String getHoTen() {
		return hoTen;
	}

	public void setHoTen(String hoTen) {
		this.hoTen = hoTen;
	}

	public String getDiaChi() {
		return diaChi;
	}

	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}

	public String getCccd() {
		return cccd;
	}

	@Override
	public String toString() {
		return hoTen + " (" + maKH + ")";
	}
}