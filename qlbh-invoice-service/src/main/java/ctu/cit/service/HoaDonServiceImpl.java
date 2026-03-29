package ctu.cit.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ctu.cit.model.ChiTietHD;
import ctu.cit.model.HoaDon;
import ctu.cit.model.KhachHang;
import ctu.cit.model.KhuyenMai;
import ctu.cit.model.SanPham;
import ctu.cit.util.DatabaseUtil;

public class HoaDonServiceImpl implements IHoaDonService {

    public HoaDonServiceImpl() {
        // Shared database approach - no need for ISanPhamService injection here
    }

    @Override
    public boolean taoHoaDon(HoaDon hd) {
        if (hd == null || hd.getMaHD() == null) return false;
        String sql = "INSERT INTO hoadon (mahd, ngaylap, vat, makh) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hd.getMaHD());
            ps.setDate(2, Date.valueOf(hd.getNgayLap()));
            ps.setDouble(3, hd.getVAT());
            ps.setString(4, hd.getKhachHang() != null ? hd.getKhachHang().getMaKH() : null);
            
            boolean ok = ps.executeUpdate() > 0;
            if (ok && hd.getDsChiTiet() != null) {
                for (ChiTietHD ct : hd.getDsChiTiet()) {
                    muaHang(hd.getMaHD(), ct);
                }
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean muaHang(String maHD, ChiTietHD ct) {
        if (ct == null || ct.getSoLuong() <= 0) return false;

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. Kiểm tra tồn kho và giảm số lượng
            String checkSP = "SELECT gia, soluongton FROM sanpham WHERE masp = ? FOR UPDATE";
            double currentGia = 0;
            try (PreparedStatement ps = conn.prepareStatement(checkSP)) {
                ps.setString(1, ct.getMaSP());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int stock = rs.getInt("soluongton");
                        currentGia = rs.getDouble("gia");
                        if (stock < ct.getSoLuong()) {
                            conn.rollback();
                            return false;
                        }
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            String updateSP = "UPDATE sanpham SET soluongton = soluongton - ? WHERE masp = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSP)) {
                ps.setInt(1, ct.getSoLuong());
                ps.setString(2, ct.getMaSP());
                ps.executeUpdate();
            }

            // 2. Thêm chi tiết hóa đơn
            String insertCT = "INSERT INTO chitiethd (mahd, masp, soluong, dongia) VALUES (?, ?, ?, ?) " +
                              "ON DUPLICATE KEY UPDATE soluong = soluong + ?, dongia = ?";
            try (PreparedStatement ps = conn.prepareStatement(insertCT)) {
                ps.setString(1, maHD);
                ps.setString(2, ct.getMaSP());
                ps.setInt(3, ct.getSoLuong());
                ps.setDouble(4, currentGia);
                ps.setInt(5, ct.getSoLuong());
                ps.setDouble(6, currentGia);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
    }

    @Override
    public boolean apDungKhuyenMai(String maHD, KhuyenMai km) {
        if (km == null || km.getMaKM() == null) return false;

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Insert/Update KhuyenMai
            String sqlKM = "INSERT INTO khuyenmai (makm, ngayapdung, ngayketthuc, phantramgiam) VALUES (?, ?, ?, ?) " +
                           "ON DUPLICATE KEY UPDATE ngayapdung = ?, ngayketthuc = ?, phantramgiam = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlKM)) {
                ps.setString(1, km.getMaKM());
                ps.setDate(2, Date.valueOf(km.getNgayApDung()));
                ps.setDate(3, Date.valueOf(km.getNgayKetThuc()));
                ps.setDouble(4, km.getPhanTramGiam());
                ps.setDate(5, Date.valueOf(km.getNgayApDung()));
                ps.setDate(6, Date.valueOf(km.getNgayKetThuc()));
                ps.setDouble(7, km.getPhanTramGiam());
                ps.executeUpdate();
            }

            // 2. Link KM to HD
            String sqlHD = "UPDATE hoadon SET makm = ? WHERE mahd = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlHD)) {
                ps.setString(1, km.getMaKM());
                ps.setString(2, maHD);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    conn.commit();
                    return true;
                }
            }
            conn.rollback();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public double tinhTongTien(String maHD) {
        HoaDon hd = findHoaDonById(maHD);
        return hd != null ? hd.tinhTongTien() : 0;
    }

    @Override
    public List<HoaDon> getAll() {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT mahd FROM hoadon";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                HoaDon hd = findHoaDonById(rs.getString("mahd"));
                if (hd != null) list.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean xoaHoaDon(String maHD) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            // Delete details first
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM chitiethd WHERE mahd = ?")) {
                ps.setString(1, maHD);
                ps.executeUpdate();
            }
            // Delete header
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM hoadon WHERE mahd = ?")) {
                ps.setString(1, maHD);
                int rows = ps.executeUpdate();
                conn.commit();
                return rows > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private HoaDon findHoaDonById(String maHD) {
        String sql = "SELECT h.*, k.hoten, k.diachi, k.cccd, km.ngayapdung, km.ngayketthuc, km.phantramgiam " +
                     "FROM hoadon h " +
                     "LEFT JOIN khachhang k ON h.makh = k.makh " +
                     "LEFT JOIN khuyenmai km ON h.makm = km.makm " +
                     "WHERE h.mahd = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHD);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HoaDon hd = new HoaDon();
                    hd.setMaHD(rs.getString("mahd"));
                    hd.setNgayLap(rs.getDate("ngaylap").toLocalDate());
                    hd.setVAT(rs.getDouble("vat"));
                    
                    String maKH = rs.getString("makh");
                    if (maKH != null) {
                        KhachHang kh = new KhachHang(maKH, rs.getString("hoten"), rs.getString("diachi"), rs.getString("cccd"));
                        hd.setKhachHang(kh);
                    }
                    
                    String maKM = rs.getString("makm");
                    if (maKM != null) {
                        KhuyenMai km = new KhuyenMai(maKM, rs.getDate("ngayapdung").toLocalDate(), rs.getDate("ngayketthuc").toLocalDate(), rs.getDouble("phantramgiam"));
                        hd.setKhuyenMai(km);
                    }
                    
                    // Load details
                    loadDetails(hd);
                    return hd;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadDetails(HoaDon hd) throws SQLException {
        String sql = "SELECT c.*, s.tensp, s.gia as sp_gia, s.mancc FROM chitiethd c " +
                     "JOIN sanpham s ON c.masp = s.masp WHERE c.mahd = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hd.getMaHD());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SanPham sp = new SanPham();
                    sp.setMaSP(rs.getString("masp"));
                    sp.setTenSP(rs.getString("tensp"));
                    sp.setGia(rs.getDouble("sp_gia"));
                    
                    ChiTietHD ct = new ChiTietHD();
                    ct.setSanPham(sp);
                    ct.setMaSP(sp.getMaSP());
                    ct.setSoLuong(rs.getInt("soluong"));
                    ct.setDonGia(rs.getDouble("dongia"));
                    hd.themChiTiet(ct);
                }
            }
        }
    }
}