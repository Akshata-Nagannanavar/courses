
package com.example.course_backend;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Unit {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;
    private String content;


    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference  // prevents infinite JSON recursion
    private Course course;



}
