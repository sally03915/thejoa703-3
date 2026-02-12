package com.thejoa703.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.thejoa703.service.Sboard2Service;
import com.thejoa703.util.UtilPaging;

@Controller
public class MainController {
	//내가 필요로하는 서비스들- index구성 (서비스들)
	@Autowired private Sboard2Service service;
	
	@GetMapping("/")
	public String list(Model model , @RequestParam(value="pageNo" , defaultValue="1")  int pageNo) {
		model.addAttribute("paging" , new UtilPaging( service.selectTotalCnt()  , pageNo));  // 화면용계산 이전-1,2,3-다음
		model.addAttribute("list"   , service.select10(pageNo));  //처리- 게시글10개 가져오기 
		model.addAttribute("imglist", service.select10(pageNo)); 
		//return "redirect:/index"; // 화면 - list 값못가져옴
		//				    static/index.html    (정적페이지 - 미리올라가있는데이터)
		return "index";  // templates/index.html (동적페이지 - 서비스와 관련된내용들)
	}
}
