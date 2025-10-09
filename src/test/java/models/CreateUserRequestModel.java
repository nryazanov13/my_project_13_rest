package models;

import lombok.Data;

@Data
public class CreateUserRequestModel {
    private String name;
    private String job;
}
