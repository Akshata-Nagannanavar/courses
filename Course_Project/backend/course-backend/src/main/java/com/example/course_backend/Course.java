package com.example.course_backend;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Board is required")
    private String board;

    // Existing column in DB: medium (stores comma-separated values)
    @Column(name = "medium")
    private String medium;

    @NotBlank(message = "Grade is required")
    private String grade;

    @NotBlank(message = "Subject is required")
    private String subject;

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "course_id")
//    @JsonManagedReference
//    private List<Unit> units = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.PERSIST, orphanRemoval = false)
    @JsonManagedReference
    private List<Unit> units = new ArrayList<>();



    // For JSON binding only; persisted via `medium` string column using converter in service
    @Size(min = 1, message = "At least one medium is required")
    @Transient
    private List<Enums.Medium> mediums = new ArrayList<>();
}
