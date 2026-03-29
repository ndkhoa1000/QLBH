package ctu.cit.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ctu.cit.model.NhaCungCap;
import ctu.cit.model.SanPham;
import ctu.cit.util.DatabaseUtil;

public class SanPhamServiceImpl implements ISanPhamService {

    private static SanPhamServiceImpl instance = new SanPhamServiceImpl();
    public static SanPhamServiceImpl getInstance() {
        return instance;
    }

    @Override
    public boolean themSanPham(SanPham sp) {
        if (sp == null || sp.getMaSP() == null) return false;
        String sql = "INSERT INTO sanpham (masp, tensp, gia, soluongton, mancc) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sp.getMaSP());
            ps.setString(2, sp.getTenSP());
            ps.setDouble(3, sp.getGia());
            ps.setInt(4, sp.getSoLuongTon());
            ps.setString(5, sp.getNhaCungCap() != null ? sp.getNhaCungCap().getMaNCC() : null);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean xoaSanPham(String ma) {
        String sql = "DELETE FROM sanpham WHERE masp = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public SanPham timSanPham(String ma) {
        String sql = "SELECT * FROM sanpham WHERE masp = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToSanPham(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<SanPham> getAll() {
        List<SanPham> ds = new ArrayList<>();
        String sql = "SELECT * FROM sanpham";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(mapRowToSanPham(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    @Override
    public boolean giamSoLuong(String maSP, int soLuong) {
        String sql = "UPDATE sanpham SET soluongton = soluongton - ? WHERE masp = ? AND soluongton >= ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soLuong);
            ps.setString(2, maSP);
            ps.setInt(3, soLuong);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean tangSoLuong(String maSP, int soLuong) {
        String sql = "UPDATE sanpham SET soluongton = soluongton + ? WHERE masp = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, soLuong);
            ps.setString(2, maSP);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private SanPham mapRowToSanPham(ResultSet rs) throws SQLException {
        SanPham sp = new SanPham();
        sp.setMaSP(rs.getString("masp"));
        sp.setTenSP(rs.getString("tensp"));
        sp.setGia(rs.getDouble("gia"));
        sp.setSoLuongTon(rs.getInt("soluongton"));
        String maNCC = rs.getString("mancc");
        if (maNCC != null) {
            NhaCungCap ncc = new NhaCungCap();
            ncc.setMaNCC(maNCC);
            sp.setNhaCungCap(ncc);
        }
        return sp;
    }
}
