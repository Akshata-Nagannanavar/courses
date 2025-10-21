
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
    private String board;   // e.g., CBSE, State Board
    private String medium;  // e.g., English, Kannada
    private String grade;   // e.g., Class 1, Class 2
    private String subject; // e.g., English, Hindi

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "course_id")
    @JsonManagedReference  // <- add this
    private List<Unit> units = new ArrayList<>();


}

