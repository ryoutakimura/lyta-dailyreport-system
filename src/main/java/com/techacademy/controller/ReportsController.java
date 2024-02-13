package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportsController {

    private final ReportService reportService;

    @Autowired
    public ReportsController(EmployeeService employeeService,ReportService reportService) {
        this.reportService=reportService;
    }

    //日報一覧画面
    @GetMapping("")
    public String list(Model model,@AuthenticationPrincipal UserDetail userDetail) {

        //ADMIN権限の場合すべての日報をVIEWに渡す
        if(userDetail.getEmployee().getRole().toString() == "ADMIN") {
        model.addAttribute("listSize", reportService.findAll().size());
        model.addAttribute("reportList",reportService.findAll());
        }else {
        //GENERAL権限の場合自身の日報をVIEWに渡す
        model.addAttribute("report",reportService.getReport(Integer.parseInt(userDetail.getEmployee().getCode())));
        model.addAttribute("listSize", 1);
        }

        return "reports/list";
    }

}
