

package com.example.course_backend;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Unit {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank(message = "Unit title is required")
    private String title;

    @NotBlank(message = "Unit content is required")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = true)
    @JsonBackReference
    private Course course;
}
