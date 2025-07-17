package poly.quanlythuvien.controller;

import poly.quanlythuvien.entity.ThongKe;
import java.util.List;

public interface ThongKeController {
    void loadMostBorrowedBooks(); // Tải danh sách sách mượn nhiều nhất lên bảng
    void loadLateReturns(); // Tải danh sách trả trễ lên bảng
    void loadBookInventory(); // Tải tình trạng kho sách lên bảng
    void loadUserActivity(); // Tải hoạt động người dùng lên bảng
    void deleteLateReturn(String maDocGia, String tenSach); // Xóa bản ghi trả trễ
    List<ThongKe> getMostBorrowedBooks(); // Lấy danh sách sách mượn nhiều nhất
    List<ThongKe> getLateReturns(); // Lấy danh sách trả trễ
    List<ThongKe> getBookInventory(); // Lấy tình trạng kho sách
    List<ThongKe> getUserActivity(); // Lấy hoạt động người dùng
}