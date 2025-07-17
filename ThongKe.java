package poly.quanlythuvien.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThongKe {
    private Integer stt; // Số thứ tự (cho trả trễ, kho sách, hoạt động người dùng)
    private String maSach; // Mã sách (cho sách mượn nhiều, kho sách)
    private String tenSach; // Tên sách (cho sách mượn nhiều, trả trễ, kho sách)
    private String tacGia; // Tác giả (cho sách mượn nhiều)
    private String theLoai; // Thể loại (cho kho sách)
    private Integer soLuotMuon; // Số lượt mượn (cho sách mượn nhiều)
    private Integer tongSo; // Tổng số sách (cho kho sách)
    private Integer dangMuon; // Số sách đang mượn (cho kho sách)
    private Integer conLai; // Số sách còn lại (cho kho sách)
    private String maDocGia; // Mã độc giả (cho trả trễ, hoạt động người dùng)
    private String hoTen; // Họ tên độc giả (cho trả trễ, hoạt động người dùng)
    private String ngayTraDuKien; // Ngày trả dự kiến (cho trả trễ)
    private String ngayTraThucTe; // Ngày trả thực tế (cho trả trễ)
    private Integer soNgayTre; // Số ngày trễ (cho trả trễ)
    private Integer tongSoLuotMuon; // Tổng số lượt mượn (cho hoạt động người dùng)
    private Integer tongSoSachTraTre; // Tổng số sách trả trễ (cho hoạt động người dùng)
}