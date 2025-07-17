package poly.quanlythuvien.daoimpl;

import poly.quanlythuvien.dao.ThongKeDAO;
import poly.quanlythuvien.entity.ThongKe;
import poly.quanlythuvien.util.XJdbc;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAOlmpl implements ThongKeDAO {

    private final String SELECT_MOST_BORROWED_BOOKS_SQL = "SELECT s.TenSach, s.MaSach, s.TacGia, COUNT(pm.MaSach) as SoLuotMuon " +
                                                          "FROM Sach s JOIN PhieuMuon pm ON s.MaSach = pm.MaSach " +
                                                          "GROUP BY s.MaSach, s.TenSach, s.TacGia " +
                                                          "ORDER BY SoLuotMuon DESC";
    
    private final String SELECT_LATE_RETURNS_SQL = "SELECT ROW_NUMBER() OVER (ORDER BY pm.MaDocGia) as STT, pm.MaDocGia, dg.HoTen, s.TenSach, " +
                                                  "pm.NgayTraDuKien, pm.NgayTraThucTe, DATEDIFF(DAY, pm.NgayTraDuKien, pm.NgayTraThucTe) as SoNgayTre " +
                                                  "FROM PhieuMuon pm JOIN DocGia dg ON pm.MaDocGia = dg.MaDocGia " +
                                                  "JOIN Sach s ON pm.MaSach = s.MaSach " +
                                                  "WHERE pm.NgayTraThucTe > pm.NgayTraDuKien";
    
    private final String SELECT_BOOK_INVENTORY_SQL = "SELECT ROW_NUMBER() OVER (ORDER BY s.MaSach) as STT, s.MaSach, s.TenSach, tl.TenTheLoai, " +
                                                    "s.TongSo, COUNT(pm.MaSach) as DangMuon, (s.TongSo - COUNT(pm.MaSach)) as ConLai " +
                                                    "FROM Sach s LEFT JOIN PhieuMuon pm ON s.MaSach = pm.MaSach AND pm.NgayTraThucTe IS NULL " +
                                                    "JOIN TheLoai tl ON s.MaTheLoai = tl.MaTheLoai " +
                                                    "GROUP BY s.MaSach, s.TenSach, tl.TenTheLoai, s.TongSo";
    
    private final String SELECT_USER_ACTIVITY_SQL = "SELECT ROW_NUMBER() OVER (ORDER BY dg.MaDocGia) as STT, dg.MaDocGia, dg.HoTen, " +
                                                   "COUNT(pm.MaDocGia) as TongSoLuotMuon, " +
                                                   "SUM(CASE WHEN pm.NgayTraThucTe > pm.NgayTraDuKien THEN 1 ELSE 0 END) as TongSoSachTraTre " +
                                                   "FROM DocGia dg LEFT JOIN PhieuMuon pm ON dg.MaDocGia = dg.MaDocGia " +
                                                   "GROUP BY dg.MaDocGia, dg.HoTen";
    
    private final String DELETE_LATE_RETURN_SQL = "DELETE FROM PhieuMuon WHERE MaDocGia = ? AND MaSach = (SELECT MaSach FROM Sach WHERE TenSach = ?)";

    @Override
    public List<ThongKe> getMostBorrowedBooks() {
        List<ThongKe> list = new ArrayList<>();
        try (ResultSet rs = XJdbc.executeQuery(SELECT_MOST_BORROWED_BOOKS_SQL)) {
            while (rs.next()) {
                list.add(ThongKe.builder()
                    .maSach(rs.getString("MaSach"))
                    .tenSach(rs.getString("TenSach"))
                    .tacGia(rs.getString("TacGia"))
                    .soLuotMuon(rs.getInt("SoLuotMuon"))
                    .build());
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi lấy danh sách sách mượn nhiều nhất: " + ex.getMessage());
        }
        return list;
    }

    @Override
    public List<ThongKe> getLateReturns() {
        List<ThongKe> list = new ArrayList<>();
        try (ResultSet rs = XJdbc.executeQuery(SELECT_LATE_RETURNS_SQL)) {
            while (rs.next()) {
                list.add(ThongKe.builder()
                    .stt(rs.getInt("STT"))
                    .maDocGia(rs.getString("MaDocGia"))
                    .hoTen(rs.getString("HoTen"))
                    .tenSach(rs.getString("TenSach"))
                    .ngayTraDuKien(rs.getString("NgayTraDuKien"))
                    .ngayTraThucTe(rs.getString("NgayTraThucTe"))
                    .soNgayTre(rs.getInt("SoNgayTre"))
                    .build());
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi lấy danh sách độc giả trả sách trễ: " + ex.getMessage());
        }
        return list;
    }

    @Override
    public List<ThongKe> getBookInventory() {
        List<ThongKe> list = new ArrayList<>();
        try (ResultSet rs = XJdbc.executeQuery(SELECT_BOOK_INVENTORY_SQL)) {
            while (rs.next()) {
                list.add(ThongKe.builder()
                    .stt(rs.getInt("STT"))
                    .maSach(rs.getString("MaSach"))
                    .tenSach(rs.getString("TenSach"))
                    .theLoai(rs.getString("TenTheLoai"))
                    .tongSo(rs.getInt("TongSo"))
                    .dangMuon(rs.getInt("DangMuon"))
                    .conLai(rs.getInt("ConLai"))
                    .build());
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi lấy tình hình kho sách: " + ex.getMessage());
        }
        return list;
    }

    @Override
    public List<ThongKe> getUserActivity() {
        List<ThongKe> list = new ArrayList<>();
        try (ResultSet rs = XJdbc.executeQuery(SELECT_USER_ACTIVITY_SQL)) {
            while (rs.next()) {
                list.add(ThongKe.builder()
                    .stt(rs.getInt("STT"))
                    .maDocGia(rs.getString("MaDocGia"))
                    .hoTen(rs.getString("HoTen"))
                    .tongSoLuotMuon(rs.getInt("TongSoLuotMuon"))
                    .tongSoSachTraTre(rs.getInt("TongSoSachTraTre"))
                    .build());
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi lấy hoạt động mượn/trả theo người dùng: " + ex.getMessage());
        }
        return list;
    }

    @Override
    public void deleteLateReturn(String maDocGia, String tenSach) {
        XJdbc.executeUpdate(DELETE_LATE_RETURN_SQL, maDocGia, tenSach);
    }
}