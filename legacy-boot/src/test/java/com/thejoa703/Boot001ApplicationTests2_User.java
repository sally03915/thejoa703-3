package com.thejoa703;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import com.thejoa703.dao.AppUserDao;
import com.thejoa703.dto.AppUserAuthDto;
import com.thejoa703.dto.AppUserDto;
import com.thejoa703.dto.AuthDto;
import com.thejoa703.service.AppUserService;

@SpringBootTest
@Transactional   // 테스트 실행중 발행 db변경 자동 롤백
class Boot001ApplicationTests2_User {  
	 @Autowired  AppUserDao  dao;
	 @Autowired  AppUserService service;
	 
	 @Test 
	 public void testService() {
		 String email = UUID.randomUUID().toString() + "@test.com";
		 //1. 회원가입
		 AppUserDto user = new AppUserDto();
		 user.setEmail(email);
		 user.setPassword("pw123");
		 user.setMobile("0101234567");
		 user.setNickname("nickname");
		 user.setProvider("local");
		 user.setProviderId("local_" + UUID.randomUUID());
		 
		 MockMultipartFile file = new  MockMultipartFile("file" , "test.txt" , "text/plain" , "data".getBytes());
		 // import org.springframework.mock.web.MockMultipartFile;
		 int  insertResult = service.insert(file, user);
		 assertEquals(1,insertResult); 
		 
		 //2.  로그인테스트 (readAuth)  + 마이페이지
		 //2-1. 로그인
		 AppUserAuthDto login =  service.readAuth(email, "local"); 
		 assertNotNull(login);  //예상결과값 Null이 아님.
		 assertEquals(email, login.getEmail());
		 assertTrue( login.getAuthList().stream().anyMatch(a -> "ROLE_MEMBER".equals(a.getAuth())) );
		 
		 //2-2. 마이페이지
		 AppUserDto  mypage = service.selectEmail(email, "local");
		 assertNotNull(mypage);  //예상결과값 Null이 아님.
		 assertEquals("nickname", mypage.getNickname());
		 
		 //3.  업데이트
		 AppUserDto updateDto = new AppUserDto();
		 updateDto.setEmail(email);
		 updateDto.setPassword("pw123");
		 updateDto.setNickname("newNickname");
		 updateDto.setProvider("local");
		 
		 int updateResult = service.update(null, updateDto);
		 assertEquals(1 , updateResult );
		 
		 AppUserDto updatedUser = service.selectEmail(email, "local");
		 assertEquals("newNickname" , updatedUser.getNickname());
		 
		 //4.  유저삭제
	     AppUserDto deleteDto = new AppUserDto();
	     deleteDto.setEmail(email);
	     deleteDto.setPassword("pw123");
	     deleteDto.setProvider("local");

         int deleteResult = service.delete(deleteDto, true);
         assertEquals(1, deleteResult);
         assertNull(service.selectEmail(email, "local"));
	 }
	 
	 
	 @Disabled @Test
	 public void testCrudAppUserAuth() {
		 //1. 회원가입 - 사용자입력
		 AppUserDto  user = new AppUserDto();
		 user.setEmail("1@1");   user.setPassword("1");  user.setMbtiTypeId(1);
		 user.setUfile("1.png"); user.setMobile("01011");user.setProvider("local");
		 user.setProviderId("local_001");
		 int result    = dao.insertAppUser(user); 
		 System.out.println("....... 1. " + result );  // 수동확인
		 assertEquals(1 , result);  // 회원가입성공확인 (자동확인)
		 ///// import static org.junit.jupiter.api.Assertions.assertEquals;
		 
		 //2.        - 권한입력
		 AuthDto  auth  =  new AuthDto();
		 auth.setEmail("1@1");
		 auth.setAuth("ROLE_USER");
		 int result_auth    = dao.insertAuth(auth);
		 System.out.println("....... 2. " + result_auth );  //1
		 assertEquals(1 , result_auth); // 권한입력 (자동확인)  값이 같은지확인
		 
		 //3. 로그인
		 AppUserDto  login = new AppUserDto();
		 login.setEmail("1@1");  login.setProvider("local");
		 System.out.println("....... 3. " + dao.readAuthByEmail(login) ); 
		 assertNotNull(dao.readAuthByEmail(login));  // NotNull    null 확인
		 //import static org.junit.jupiter.api.Assertions.assertNotNull;
		 
		 
		 //4. 아이디중복
		 AppUserDto  iddouble = new AppUserDto();
		 iddouble.setEmail("1@1");  iddouble.setProvider("local");
		 System.out.println("....... 4. " + dao.iddoubleByEmail(iddouble) ); 
		 assertTrue(   dao.iddoubleByEmail(iddouble)  > 0 ); // 0보다 크다  조건식테스트가능
		 //import static org.junit.jupiter.api.Assertions.assertTrue;
		 
		 /////////////////////////////////////////
		 //5. 마이페이지
		 AppUserDto  mypage = new AppUserDto();
		 mypage.setEmail("1@1");  mypage.setProvider("local");
		 AppUserDto  findUser = dao.findByEmail(mypage);
		 int         id       = findUser.getAppUserId();
		 System.out.println("....... 5. " + findUser ); 
		 assertNotNull( findUser ); //## return - 객체  예상되는 결과를 자동테스트
		 
		 //6. 수정
		 AppUserDto  update = new AppUserDto(); 
		 update.setNickname("nickname001");  update.setAppUserId(id); 
		 int updateResult = dao.updateAppUser(update);
		 System.out.println("....... 6. " + updateResult ); // ##  int
		 assertEquals(1 , updateResult);
		 
		 //7. 사용자삭제
		 AppUserDto  delete = new AppUserDto();   delete.setAppUserId(id);  //##
		 int deleteResult =dao.deleteAppUser(delete);
		 System.out.println("....... 7. " + deleteResult ); 
		 assertEquals(1 , deleteResult); // 삭제성공확인
		 assertNull(dao.findByEmail(mypage));  //삭제후 조회불가 확인
		 
		 //8. 권한삭제
		 AuthDto    dauth = new AuthDto();   dauth.setEmail("1@1");
		 int deleteAuthResult =dao.deleteAuth(dauth);
		 System.out.println("....... 8. " + deleteAuthResult);   //1
		 assertEquals(1 , deleteAuthResult); 
		 
	 }
}

