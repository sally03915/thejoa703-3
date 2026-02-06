package com.thejoa703.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thejoa703.domain.DeptUser;

@Repository
public interface DeptUserRepository  extends JpaRepository<DeptUser, Long>{  // Entity, PK

}

/*
CREATE : save     -   INSERT INTO  테이블명 (컬럼1,컬럼2,,) values (?,?,,)
READ   : findAll  -   SELECT  * from 테이블명  
         findById -   SELECT  * from 테이블명   where deptno=? 
UPDATE : save     -   update  테이블명   set 컬럼1=? ,컬럼2=?  where   deptno=? 
DELETE : deleteById - delete from 테이블명   where deptno=?
*/