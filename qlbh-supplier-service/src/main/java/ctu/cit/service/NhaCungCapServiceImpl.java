package ctu.cit.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ctu.cit.model.NhaCungCap;
import ctu.cit.util.DatabaseUtil;

public class NhaCungCapServiceImpl implements INhaCungCapService {

    @Override
    public boolean them(NhaCungCap ncc) {
        if (ncc == null || ncc.getMaNCC() == null) return false;
        String sql = "INSERT INTO nhacungcap (mancc, tenncc, diachi, sodienthoai) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ncc.getMaNCC());
            ps.setString(2, ncc.getTenNCC());
            ps.setString(3, ncc.getDiaChi());
            ps.setString(4, ncc.getSoDienThoai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<NhaCungCap> getAll() {
        List<NhaCungCap> ds = new ArrayList<>();
        String sql = "SELECT * FROM nhacungcap";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                NhaCungCap n = new NhaCungCap();
                n.setMaNCC(rs.getString("mancc"));
                n.setTenNCC(rs.getString("tenncc"));
                n.setDiaChi(rs.getString("diachi"));
                n.setSoDienThoai(rs.getString("sodienthoai"));
                ds.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    @Override
    public NhaCungCap tim(String ma) {
        String sql = "SELECT * FROM nhacungcap WHERE mancc = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    NhaCungCap n = new NhaCungCap();
                    n.setMaNCC(rs.getString("mancc"));
                    n.setTenNCC(rs.getString("tenncc"));
                    n.setDiaChi(rs.getString("diachi"));
                    n.setSoDienThoai(rs.getString("sodienthoai"));
                    return n;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean capNhat(String ma, NhaCungCap newNCC) {
        if (newNCC == null) return false;
        String sql = "UPDATE nhacungcap SET tenncc = ?, diachi = ?, sodienthoai = ? WHERE mancc = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newNCC.getTenNCC());
            ps.setString(2, newNCC.getDiaChi());
            ps.setString(3, newNCC.getSoDienThoai());
            ps.setString(4, ma);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean xoa(String ma) {
        String sql = "DELETE FROM nhacungcap WHERE mancc = ?";
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