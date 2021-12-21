package com.sky.security.service.models;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WantedPerson {

    private String firstName;
    private String lastName;
    private int age;
    private List<Crime> crimes;

}



