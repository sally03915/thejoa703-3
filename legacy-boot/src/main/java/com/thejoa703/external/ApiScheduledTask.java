package com.thejoa703.external;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ApiScheduledTask {
	/*****
	@Scheduled(fixedRate = 2000)  // 2초마다    1000이 1초
	public void runTask1() {
		System.out.println(".......스케쥴러 실행중 : " + System.currentTimeMillis());
	}
	*/
	//	@Scheduled(fixedDelay = )  어떤작업이 끝난후에 지정된 시간에 시행
	//  2025-12-17 12:41:00
	//  2025-12-18 12:41:00
	//  2025-12-19 12:41:00
	@Scheduled(cron="0 41 12 * * ?")   // 0초  41분  12시  일  월 요일( ?특정하지 않음)
	public void runTesk2() {
		System.out.println(".......스케쥴러 실행중 : " + System.currentTimeMillis());
	}
	
}
/*****************
1. 스케쥴링구동
@SpringBootApplication
@EnableScheduling
public class Boot001Application { }

2. 스케쥴부품
@Component
public class ApiScheduledTask { 
	@Scheduled(fixedRate = 2000){}   2초마다 실행 
	@Scheduled(fixedDelay = )  어떤작업이 끝난후에 지정된 시간에 시행
	@Scheduled(cron="0 0 10 * * ?")
	public void runTesk2() {
		System.out.println(".......스케쥴러 실행중 : " + System.currentTimeMillis());
	}
	cron="0 0  0  * * ?"    // 0초  0분  0시   일  월 요일- 매일자정
	cron="0 0  12 * * ?"    // 0초  0분  12시  일  월 요일- 매일정오
	cron="0 30 18 * * ?"    // 매일 오후 6시 30분
	cron="0 0  0  1 * ?"    // 매월 1일 자정
	cron="0 0  0  ? * MON"  // 매주 월요일 자정
	
	* 제한없는 모든값
	? 특정값 없음
	SUN 일, MON 월, TUE 화, WED 수, THU 목, FRI 금, SAT토

******************/
/* 
- 스케쥴링 
- naver 개발자 (api 보기 기본)
- mail
- 크롤링
- map
- chatgpt ★ 
- kma
- coolsms
- kakaopay
.....
*/
