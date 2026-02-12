package com.thejoa703.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BasicController {
    // http://localhost:8484/boot001/basic1
	//@RequestMapping("/basic1")
	@GetMapping("/basic1")
	@ResponseBody
	public String basic1() { return "basic1"; }

	//http://localhost:8484/boot001/basic2
	@GetMapping("/basic2")
	public String basic2( Model model ) { 
		model.addAttribute("greeting", "hello");
		return "basic2"; 
	}
	
//	@GetMapping("/")
//	public String index() {  
//		//return "index"; 
//		return "redirect:/board/list";
//	}
}
