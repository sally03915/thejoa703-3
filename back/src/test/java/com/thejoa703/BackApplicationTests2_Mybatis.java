package com.thejoa703;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.thejoa703.domain.DeptUser;
import com.thejoa703.mapper.DeptUserMapper;
import com.thejoa703.repository.DeptUserRepository;

@SpringBootTest
class BackApplicationTests2_Mybatis {

	@Autowired private DeptUserRepository deptUserRepository;  // crud
	@Autowired private DeptUserMapper     deptUserMapper;      // sql
	
	@Test
	@DisplayName("■ DeptUserRepository-CRUD ")
	void testDeptUserRepository() {
		//1. 저장
		DeptUser deptUser = new DeptUser();
		deptUser.setDeptno(10L);
		deptUser.setDname("TheJoa703");
		deptUser.setLoc("Incheon");
		deptUserRepository.save(deptUser);
		
		//2. 단건조회
		Optional<DeptUser> found = 	deptUserRepository.findById(10L);
		assertThat(found).isPresent();
		
		//3. Mapper 사용
		List<DeptUser> result =  deptUserMapper.findByNameKeyword("The");
		assertThat(result).isNotEmpty();
		assertThat(result.get(0).getDname()).contains("The");
	}

}

