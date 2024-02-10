package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.service.EmployeeService;

@Controller
@RequestMapping("reports")
public class ReportsController {

    private final EmployeeService employeeService;

    @Autowired
    public ReportsController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    //日報一覧画面
    @GetMapping("")
    public String list(Model model) {
        return "reports/list";
    }

}
