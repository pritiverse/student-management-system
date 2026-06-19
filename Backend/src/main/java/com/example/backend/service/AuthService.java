package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entity.User;
import com.example.backend.repository.userRepository;

@Service
public class AuthService {
    @Autowired
    private userRepository userRepository;

    public boolean Login(User request){
        User u=userRepository.findByEmail(request.getEmail()).orElse(null);
        return u!=null && u.getPassword().equals(request.getPassword());
    }
    

}
