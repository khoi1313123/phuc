package poly.quanlythuvien.daoimpl;

import poly.quanlythuvien.dao.QuanLyNVDAO;
import poly.quanlythuvien.entity.QuanLyNV;
import poly.quanlythuvien.util.XJdbc;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuanLyNVDAOImpl implements QuanLyNVDAO {

    private final String INSERT_SQL = "INSERT INTO NhanVien (MaNhanVien, HoTen, GioiTinh, NgaySinh, SoDienThoai, Email, ChucVu, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private final String UPDATE_SQL = "UPDATE NhanVien SET HoTen = ?, GioiTinh = ?, NgaySinh = ?, SoDienThoai = ?, Email = ?, ChucVu = ?, TrangThai = ? WHERE MaNhanVien = ?";
    private final String DELETE_SQL = "DELETE FROM NhanVien WHERE MaNhanVien = ?";
    private final String SELECT_ALL_SQL = "SELECT * FROM NhanVien";
    private final String SELECT_BY_ID_SQL = "SELECT * FROM NhanVien WHERE MaNhanVien = ?";

    @Override
    public QuanLyNV create(QuanLyNV entity) {
        XJdbc.executeUpdate(INSERT_SQL, 
            entity.getMaNhanVien(), 
            entity.getHoTen(), 
            entity.isGioiTinh(), 
            new java.sql.Date(entity.getNgaySinh().getTime()), 
            entity.getSoDienThoai(), 
            entity.getEmail(), 
            entity.getChucVu(), 
            entity.getTrangThai()
        );
        return entity;
    }

    @Override
    public void update(QuanLyNV entity) {
        XJdbc.executeUpdate(UPDATE_SQL, 
            entity.getHoTen(), 
            entity.isGioiTinh(), 
            new java.sql.Date(entity.getNgaySinh().getTime()), 
            entity.getSoDienThoai(), 
            entity.getEmail(), 
            entity.getChucVu(), 
            entity.getTrangThai(), 
            entity.getMaNhanVien()
        );
    }

    @Override
    public void deleteById(String id) {
        XJdbc.executeUpdate(DELETE_SQL, id);
    }

    @Override
    public List<QuanLyNV> findAll() {
        List<QuanLyNV> list = new ArrayList<>();
        try (ResultSet rs = XJdbc.executeQuery(SELECT_ALL_SQL)) {
            while (rs.next()) {
                list.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return list;
    }

    @Override
    public QuanLyNV findById(String id) {
        try (ResultSet rs = XJdbc.executeQuery(SELECT_BY_ID_SQL, id)) {
            if (rs.next()) {
                return mapResultSetToEntity(rs);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }

    private QuanLyNV mapResultSetToEntity(ResultSet rs) throws SQLException {
        QuanLyNV nv = new QuanLyNV();
        nv.setMaNhanVien(rs.getString("MaNhanVien"));
        nv.setHoTen(rs.getString("HoTen"));
        nv.setGioiTinh(rs.getBoolean("GioiTinh"));
        nv.setNgaySinh(rs.getDate("NgaySinh"));
        nv.setSoDienThoai(rs.getString("SoDienThoai"));
        nv.setEmail(rs.getString("Email"));
        nv.setChucVu(rs.getString("ChucVu"));
        nv.setTrangThai(rs.getString("TrangThai"));
        return nv;
    }
}