package org.alessio.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message="cannot be blank")
    private String firstName;

    @NotBlank(message="cannot be blank")
    private String lastName;

    private LocalDate dob;

    private String nationality;

    @Column(length = 2048)
    private String biography;

    private String imageUrl;

    @ManyToMany(mappedBy = "authors", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("authors")
    private Set<Book> books = new HashSet<>();
}
