

package com.example.course_backend;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private Enums.Board board;

    @Enumerated(EnumType.STRING)
    private Enums.Medium medium;

    @Enumerated(EnumType.STRING)
    private Enums.Grade grade;

    @Enumerated(EnumType.STRING)
    private Enums.Subject subject;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "course_id")
    @JsonManagedReference
    private List<Unit> units = new ArrayList<>();
}


