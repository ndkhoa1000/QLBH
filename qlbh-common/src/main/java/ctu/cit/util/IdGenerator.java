package ctu.cit.util;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Centralized, DB-backed ID generator.
 * Uses the `sequences` table with atomic SELECT ... FOR UPDATE + UPDATE.
 *
 * Supported entity names and their output patterns:
 *   HOADON     -> HD-YYYYMMDD-0001
 *   KHUYENMAI  -> KM-YYYYMM-0001
 *   KHACHHANG  -> KH-000001
 *   SANPHAM    -> SP-00001
 *   NHACUNGCAP -> NCC-00001
 */
public class IdGenerator {

    /**
     * Returns the next ID for the given entity name.
     * Thread- and cross-process-safe via DB transaction lock.
     *
     * @param entity one of: HOADON, KHUYENMAI, KHACHHANG, SANPHAM, NHACUNGCAP
     * @return formatted ID string
     */
    public static String next(String entity) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            long val;
            try (PreparedStatement sel = conn.prepareStatement(
                    "SELECT next_val FROM sequences WHERE name = ? FOR UPDATE")) {
                sel.setString(1, entity);
                try (ResultSet rs = sel.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        throw new RuntimeException("Unknown sequence entity: " + entity);
                    }
                    val = rs.getLong(1);
                }
            }

            try (PreparedStatement upd = conn.prepareStatement(
                    "UPDATE sequences SET next_val = next_val + 1 WHERE name = ?")) {
                upd.setString(1, entity);
                upd.executeUpdate();
            }

            conn.commit();
            return format(entity, val);

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("IdGenerator.next failed for entity=" + entity, e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    private static String format(String entity, long val) {
        LocalDate today = LocalDate.now();
        String yyyymmdd = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String yyyymm   = today.format(DateTimeFormatter.ofPattern("yyyyMM"));
        switch (entity) {
            case "HOADON":     return String.format("HD-%s-%04d",  yyyymmdd, val);
            case "KHUYENMAI":  return String.format("KM-%s-%04d",  yyyymm,   val);
            case "KHACHHANG":  return String.format("KH-%06d",               val);
            case "SANPHAM":    return String.format("SP-%05d",               val);
            case "NHACUNGCAP": return String.format("NCC-%05d",              val);
            default:           return entity + "-" + String.format("%06d",   val);
        }
    }
}
