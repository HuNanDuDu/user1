package com.example.user1.controller;

import com.example.user1.dto.UserDto;
import com.example.user1.mq.JuseMqSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private JuseMqSender juseMqSender;
    @GetMapping("/getUserName")
    public String getUserName(String name, HttpServletRequest request){
        return request.getServerPort()+ name;
    }

    @PostMapping("/sendUser")
    public String senderUser(@RequestBody UserDto userDto){
        juseMqSender.send(null,null,userDto);
        return "ok";
    }
}
