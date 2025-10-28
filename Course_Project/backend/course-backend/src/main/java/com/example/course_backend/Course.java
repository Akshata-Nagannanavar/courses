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
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @NotBlank(message = "Name cannot be null or blank")
    private String name;

    @NotBlank(message = "Description cannot be null or blank")
    private String description;

    @NotBlank(message = "Board cannot be null or blank")
    private String board;

    @Column(columnDefinition = "jsonb")
    @Convert(converter = StringListConverter.class)
    @NotEmpty(message = "Subject list cannot be null or empty")
    private List<String> subject = new ArrayList<>();

    @Column(columnDefinition = "jsonb")
    @Convert(converter = StringListConverter.class)
    @NotEmpty(message = "Medium list cannot be null or empty")
    private List<String> medium = new ArrayList<>();

    @Column(columnDefinition = "jsonb")
    @Convert(converter = StringListConverter.class)
    @NotEmpty(message = "Grade list cannot be null or empty")
    private List<String> grade = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.PERSIST, orphanRemoval = false)
    @JsonManagedReference
    private List<Unit> units = new ArrayList<>();
}
