package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(EmployeeService employeeService,ReportService reportService) {
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

    //日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report, Model model,@AuthenticationPrincipal UserDetail userDetail) {
        //ログインユーザーの情報をVIEWに渡す
        model.addAttribute("user",reportService.getReport(Integer.parseInt(userDetail.getEmployee().getCode())).getEmployee());
        return "reports/new";
    }

    //日報新規登録処理
    @PostMapping(value ="/add")
    public String add(@Validated Report report , BindingResult res, Model model,@AuthenticationPrincipal UserDetail userDetail) {
        // 入力チェック
        if (res.hasErrors()) {
            System.out.println(res.getAllErrors());
            return create(report,model,userDetail);
        }

        return "redirect:/reports";
    }


}
