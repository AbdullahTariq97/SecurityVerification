package com.sky.security.service.models;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Person {

    private String firstName;
    private String lastName;
    private int age;
    private long nationalInsuranceNumber;

}
