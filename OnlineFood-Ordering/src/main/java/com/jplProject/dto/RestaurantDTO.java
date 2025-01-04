package com.jplProject.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data // Lombok annotation
@Embeddable // Marks this class as embeddable in other entity classes, meaning its fields will be stored in the parent entity's table
public class RestaurantDTO {

    private String title;

    @Column(length = 1000) // Sets a maximum length of 1000 characters for this column to store image URLs or paths
    private List<String> images;

    private String description;

    private Long id;
}
