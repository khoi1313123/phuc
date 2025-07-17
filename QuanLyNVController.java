package poly.quanlythuvien.controller;

import poly.quanlythuvien.entity.QuanLyNV;
import java.util.List;

public interface QuanLyNVController {
    void loadTableData();
    void createEmployee();
    void updateEmployee();
    void deleteEmployee();
    void clearForm();
    void fillForm(QuanLyNV employee);
    List<QuanLyNV> getAllEmployees();
}