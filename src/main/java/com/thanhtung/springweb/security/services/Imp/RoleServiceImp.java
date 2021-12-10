package com.thanhtung.springweb.security.services.Imp;

import com.thanhtung.springweb.Entity.RoleEntity;
import com.thanhtung.springweb.repository.RoleRepository;
import com.thanhtung.springweb.security.services.RoleService;
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

