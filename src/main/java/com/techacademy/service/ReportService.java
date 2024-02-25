package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

import jakarta.transaction.Transactional;

@Service
public class ReportService {
    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 日報1件を検索
    public Report findByCode(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    //従業員の日報をすべて取得
    public List<Report> findByEmployee(Employee employee){
        return reportRepository.findByEmployee(employee);
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report, UserDetail userDetail) {

        // ログインユーザーの日報をすべて取得
        List<Report> loginUserReport = new ArrayList<>();
        loginUserReport = findByEmployee(userDetail.getEmployee());


        // 日付重複チェック
        // ログインユーザーの日報の日付と登録しようとしている日報の日付が重複した場合エラーとする
        for (int i = 0; i < (int) loginUserReport.size(); i++) {
            if (loginUserReport.get(i).getReportDate().equals(report.getReportDate())) {
                return ErrorKinds.DATECHECK_ERROR;
            }
        }

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
        report.setEmployee(userDetail.getEmployee());

        reportRepository.save(report);

        return ErrorKinds.SUCCESS;

    }

    // 日報削除
    @Transactional
    public void delete(Integer id) {

        Report report = findByCode(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

    }

    // 日報更新
    @Transactional
    public ErrorKinds update(Report report, Employee employee, UserDetail userDetail) {

        // 従業員の日報をすべて取得
        List<Report> employeeReport = new ArrayList<>();
        employeeReport = findByEmployee(employee);

        // 日付重複チェック
        for (int i = 0; i < (int) employeeReport.size(); i++) {
            if (employeeReport.get(i).getReportDate().equals(report.getReportDate())) {
                // 更新しようとしている日報の日付が変わらない場合はチェックしない
                if (employeeReport.get(i).getId().equals(report.getId())) {
                    break;
                } else {
                    // 更新しようとしている日付が既に登録されている場合エラーとする
                    return ErrorKinds.DATECHECK_ERROR;
                }
            }
        }

        report.setEmployee(employee);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        reportRepository.save(report);

        return ErrorKinds.SUCCESS;

    }

}
