package com.bezkoder.springjwt.security.services.Imp;

import com.bezkoder.springjwt.Entity.RoleEntity;
import com.bezkoder.springjwt.repository.RoleRepository;
import com.bezkoder.springjwt.security.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImp implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public RoleEntity Save(RoleEntity roleEntity) {
        try {

            return roleRepository.save(roleEntity);
        } catch (Exception ex) {
            return null;
        }

    }
}

