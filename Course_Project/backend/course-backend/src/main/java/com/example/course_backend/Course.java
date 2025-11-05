
package com.example.course_backend;
import com.example.course_backend.*;
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


    @NotNull(message = "Board is required")
    @Enumerated(EnumType.STRING)
    private Board board;


    @ElementCollection(targetClass = Subject.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "subject")
    @NotEmpty(message = "Subject list cannot be null or empty")
    private List<Subject> subject = new ArrayList<>();

    @ElementCollection(targetClass = Medium.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "medium")
    @NotEmpty(message = "Medium list cannot be null or empty")
    private List<Medium> medium = new ArrayList<>();

    @ElementCollection(targetClass = Grade.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "grade")
    @NotEmpty(message = "Grade list cannot be null or empty")
    private List<Grade> grade = new ArrayList<>();



    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Unit> units = new ArrayList<>();
}
