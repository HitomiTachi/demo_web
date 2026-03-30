package com.example.demo.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "books")
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Column(nullable = false, length = 500)
    private String title;

    @NotBlank(message = "Tác giả không được để trống")
    @Column(nullable = false, length = 255)
    private String author;
}
