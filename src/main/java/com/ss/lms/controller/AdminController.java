package com.ss.lms.controller;

import com.ss.lms.services.AdminServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lms/admin/")
public class AdminController {
    @Autowired
    private AdminServices adminServices;


}
