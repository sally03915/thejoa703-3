package com.thejoa703.service;

import com.thejoa703.domain.DeptUser;
import com.thejoa703.repository.DeptUserRepository;
import com.thejoa703.mapper.DeptUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DeptUserService {

    private final DeptUserRepository deptUserRepository;
    private final DeptUserMapper deptUserMapper;

    public DeptUserService(DeptUserRepository deptUserRepository, DeptUserMapper deptUserMapper) {
        this.deptUserRepository = deptUserRepository;
        this.deptUserMapper = deptUserMapper;
    }
 
    public DeptUser create(DeptUser deptUser) {
        return deptUserRepository.save(deptUser);
    }

    public List<DeptUser> findAll() {
        return deptUserRepository.findAll();
    }

    public DeptUser findById(Long deptno) {
        return deptUserRepository.findById(deptno).orElse(null);
    }

    public DeptUser update(DeptUser deptUser) {
        return deptUserRepository.save(deptUser);
    }

    public void delete(Long deptno) {
        deptUserRepository.deleteById(deptno);
    }
 

    public List<DeptUser> findByNameKeyword(String keyword) {
        return deptUserMapper.findByNameKeyword(keyword);
    }
}
