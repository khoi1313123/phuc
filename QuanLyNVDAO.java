package poly.quanlythuvien.dao;

import poly.quanlythuvien.entity.QuanLyNV;
import java.util.List;

public interface QuanLyNVDAO {
    QuanLyNV create(QuanLyNV entity);
    void update(QuanLyNV entity);
    void deleteById(String id);
    List<QuanLyNV> findAll();
    QuanLyNV findById(String id);
}