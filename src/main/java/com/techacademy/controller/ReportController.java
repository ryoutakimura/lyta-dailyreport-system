package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Report;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(EmployeeService employeeService, ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧画面
    @GetMapping("")
    public String list(Model model, @AuthenticationPrincipal UserDetail userDetail) {

        model.addAttribute("user", userDetail);
        // ADMIN権限の場合すべての日報をVIEWに渡す
        if (userDetail.getEmployee().getRole().toString() == "ADMIN") {
            model.addAttribute("listSize", reportService.findAll().size());
            model.addAttribute("reportList", reportService.findAll());
        } else {
            // GENERAL権限の場合自身の日報をVIEWに渡す
            model.addAttribute("listSize",
                    reportService.findByUser(userDetail, (int) reportService.findAll().size()).size());
            model.addAttribute("reportList",
                    reportService.findByUser(userDetail, (int) reportService.findAll().size()));
        }

        return "reports/list";
    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report, Model model, @AuthenticationPrincipal UserDetail userDetail) {
        // ログインユーザーの情報をVIEWに渡す
        model.addAttribute("user",
                reportService.findByCode(Integer.parseInt(userDetail.getEmployee().getCode())).getEmployee());
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, Model model,
            @AuthenticationPrincipal UserDetail userDetail) {
        // 入力チェック
        if (res.hasErrors()) {
            return create(report, model, userDetail);
        }

        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = reportService.save(report, userDetail);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(report, model, userDetail);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(report, model, userDetail);
        }

        return "redirect:/reports";
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, Model model) {

        model.addAttribute("report", reportService.findByCode(id));
        return "reports/detail";
    }

    //日報更新画面
    @GetMapping(value = "/{id}/update")
    public String edit(@PathVariable(required = false) Integer id,Model model) {

        //日報詳細画面から来た場合DBから検索した情報を渡す
        if (id != null) {
            model.addAttribute("report", reportService.findByCode(id));
        }

        return "reports/update";
    }

}
