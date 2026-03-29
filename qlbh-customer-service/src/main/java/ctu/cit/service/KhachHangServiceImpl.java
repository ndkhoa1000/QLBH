package ctu.cit.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ctu.cit.model.KhachHang;
import ctu.cit.util.DatabaseUtil;

public class KhachHangServiceImpl implements IKhachHangService {

    @Override
    public boolean themKhachHang(KhachHang kh) {
        if (kh == null || kh.getMaKH() == null) return false;
        String sql = "INSERT INTO khachhang (makh, hoten, diachi, cccd) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getHoTen());
            ps.setString(3, kh.getDiaChi());
            ps.setString(4, kh.getCccd());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public KhachHang timKhachHang(String ma) {
        String sql = "SELECT * FROM khachhang WHERE makh = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new KhachHang(
                        rs.getString("makh"),
                        rs.getString("hoten"),
                        rs.getString("diachi"),
                        rs.getString("cccd")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<KhachHang> getAll() {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM khachhang";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ds.add(new KhachHang(
                    rs.getString("makh"),
                    rs.getString("hoten"),
                    rs.getString("diachi"),
                    rs.getString("cccd")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    @Override
    public boolean capNhat(String ma, KhachHang newKH) {
        if (newKH == null) return false;
        String sql = "UPDATE khachhang SET hoten = ?, diachi = ?, cccd = ? WHERE makh = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newKH.getHoTen());
            ps.setString(2, newKH.getDiaChi());
            ps.setString(3, newKH.getCccd());
            ps.setString(4, ma);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean xoa(String ma) {
        String sql = "DELETE FROM khachhang WHERE makh = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}