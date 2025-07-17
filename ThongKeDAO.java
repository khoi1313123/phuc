package poly.quanlythuvien.dao;

import poly.quanlythuvien.entity.ThongKe;
import java.util.List;

public interface ThongKeDAO {
    List<ThongKe> getMostBorrowedBooks();
    List<ThongKe> getLateReturns();
    List<ThongKe> getBookInventory();
    List<ThongKe> getUserActivity();
    void deleteLateReturn(String maDocGia, String tenSach);
}