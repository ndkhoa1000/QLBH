package ctu.cit.model;

public class NhaCungCap {
	private String maNCC;
	private String tenNCC;
	private String diaChi;
	private String soDienThoai;

	public NhaCungCap() {
	}

	public NhaCungCap(String maNCC, String tenNCC) {
		this.maNCC = maNCC;
		this.tenNCC = tenNCC;
	}

	public NhaCungCap(String maNCC, String tenNCC, String diaChi, String soDienThoai) {
		this.maNCC = maNCC;
		this.tenNCC = tenNCC;
		this.diaChi = diaChi;
		this.soDienThoai = soDienThoai;
	}

	public String getMaNCC() {
		return maNCC;
	}

	public void setMaNCC(String maNCC) {
		this.maNCC = maNCC;
	}

	public String getTenNCC() {
		return tenNCC;
	}

	public void setTenNCC(String tenNCC) {
		if (tenNCC != null && !tenNCC.trim().isEmpty())
			this.tenNCC = tenNCC;
	}

	public String getDiaChi() {
		return diaChi;
	}

	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}

	public String getSoDienThoai() {
		return soDienThoai;
	}

	public void setSoDienThoai(String soDienThoai) {
		this.soDienThoai = soDienThoai;
	}

	@Override
	public String toString() {
		return tenNCC + " (" + maNCC + ")";
	}
}