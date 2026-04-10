package com.perfectahr.customer_support_hub.customer.dto;

public class CustomerResponse {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;

    public CustomerResponse() {
    }

    public CustomerResponse(Long id, String username, String firstName, String lastName, String email) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}
