package com.example.booklibrary.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.catalog.Catalog;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Catalogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Catalog parent;

    @OneToMany(mappedBy = "parent")
    private List<Catalog> children;

    @ManyToMany(mappedBy = "catalogs")
    private List<Book> books;

}
