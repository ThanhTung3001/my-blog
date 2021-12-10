package com.bezkoder.springjwt.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;


@MappedSuperclass
@Setter
@Getter
public class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;
    public Date createDate = new Date();

    public Date modifyDate =new Date();

    public UUID createBy;

    public UUID modifyBy;

}
