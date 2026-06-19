package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.User;
import com.example.backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")


public class authController {
    @Autowired
    private AuthService authservice;

    @PostMapping("/login")
    public ResponseEntity<String> getEmail(@RequestBody User user) {
        boolean service=authservice.Login(user);
        if(service==true){
            return ResponseEntity.ok("Success");
        }
        else{
           return ResponseEntity.badRequest().body("Invalid Credentials");
        }

    }
    

    
}
