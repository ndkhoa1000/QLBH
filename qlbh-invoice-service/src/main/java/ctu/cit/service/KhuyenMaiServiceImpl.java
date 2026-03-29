package ctu.cit.service;

import ctu.cit.model.KhuyenMai;
import ctu.cit.util.DatabaseUtil;
import ctu.cit.util.IdGenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiServiceImpl implements IKhuyenMaiService {

    @Override
    public List<KhuyenMai> getAll() {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT makm, ngayapdung, ngayketthuc, phantramgiam FROM khuyenmai ORDER BY ngayapdung DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public KhuyenMai getById(String maKM) {
        String sql = "SELECT makm, ngayapdung, ngayketthuc, phantramgiam FROM khuyenmai WHERE makm = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKM);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String create(KhuyenMai km) {
        if (km.getMaKM() == null || km.getMaKM().isBlank()) {
            km.setMaKM(IdGenerator.next("KHUYENMAI"));
        }
        String sql = "INSERT INTO khuyenmai (makm, ngayapdung, ngayketthuc, phantramgiam) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, km.getMaKM());
            ps.setDate(2, Date.valueOf(km.getNgayApDung()));
            ps.setDate(3, Date.valueOf(km.getNgayKetThuc()));
            ps.setDouble(4, km.getPhanTramGiam());
            ps.executeUpdate();
            return km.getMaKM();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(String maKM, KhuyenMai km) {
        String sql = "UPDATE khuyenmai SET ngayapdung = ?, ngayketthuc = ?, phantramgiam = ? WHERE makm = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(km.getNgayApDung()));
            ps.setDate(2, Date.valueOf(km.getNgayKetThuc()));
            ps.setDouble(3, km.getPhanTramGiam());
            ps.setString(4, maKM);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String maKM) {
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            // Unlink from invoices first to avoid FK constraint violation
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE hoadon SET makm = NULL WHERE makm = ?")) {
                ps.setString(1, maKM);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM khuyenmai WHERE makm = ?")) {
                ps.setString(1, maKM);
                int rows = ps.executeUpdate();
                conn.commit();
                return rows > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private KhuyenMai mapRow(ResultSet rs) throws SQLException {
        return new KhuyenMai(
            rs.getString("makm"),
            rs.getDate("ngayapdung").toLocalDate(),
            rs.getDate("ngayketthuc").toLocalDate(),
            rs.getDouble("phantramgiam")
        );
    }
}
