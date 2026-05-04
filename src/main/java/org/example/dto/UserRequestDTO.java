package org.example.dto;

public class UserRequestDTO {
    private String name;
    private String email;
    private Integer age;

    public UserRequestDTO() {}

    public UserRequestDTO(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge()
    {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}

