package com.thejoa703.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "DEPTUSER")  // 테이블 이름지정
@Getter  @Setter
public class DeptUser {
	
	@Id
	private Long deptno;    
	private String dname;
	private String loc;
	
}
 