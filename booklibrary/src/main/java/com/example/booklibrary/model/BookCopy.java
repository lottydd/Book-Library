package com.example.booklibrary.model;

import com.example.booklibrary.util.CopyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BookCopies")
public class BookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "copy_id")
    private int copyId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "copy_status")
    @Enumerated(EnumType.STRING)
    private CopyStatus status;

    @OneToMany(mappedBy = "copy_id")
    private List<Rental> rentals = new ArrayList<>();

}
