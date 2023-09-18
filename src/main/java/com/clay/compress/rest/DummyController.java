package com.clay.compress.rest;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DummyController {

        @RequestMapping("/")
        public String getHtml() {
            return "Hello World";
        }
}
