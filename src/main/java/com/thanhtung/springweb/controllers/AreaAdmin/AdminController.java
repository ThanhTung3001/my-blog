package com.thanhtung.springweb.controllers.AreaAdmin;

import com.thanhtung.springweb.Entity.RoleEntity;
import com.thanhtung.springweb.controllers.BaseController;
import com.thanhtung.springweb.security.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;


@PreAuthorize("hasRole('ADMIN')")
@RequestMapping(value = {"/admin"})
public class AdminController extends BaseController {
@Autowired
public RoleService roleService;
@PostMapping("/Roles")
    public ResponseEntity<?>AddRole(@RequestBody List<RoleEntity> roles){
        List<RoleEntity>roleList = new ArrayList<>();
        try{
            roles.forEach(e->{
                roleList.add(roleService.Save(e));
            });
            return ResponseEntity.ok(BuildSuccess("Add Success",200,roleList));
        }catch (Exception ex){
           return ResponseEntity.badRequest().body(BuildFail("Can't Save Roles",404));
        }
    }
}
