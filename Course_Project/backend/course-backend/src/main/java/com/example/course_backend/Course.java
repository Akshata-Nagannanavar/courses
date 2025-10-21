package com.example.course_backend;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

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
    private String board;   // CBSE, State Board
    private String medium;  // English, Kannada
    private String grade;   // Class 1, Class 2
    private String subject; // English, Hindi

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "course_id") // adds foreign key in Unit table
    private List<Unit> units = new ArrayList<>();
}
