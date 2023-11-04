package com.pricecomparison;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "retailer")
public class Retailer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;

    @OneToMany(mappedBy = "retailer", cascade = CascadeType.ALL)
    private List<PhoneCase> phoneCases;

    // Constructors, getters, setters, and other methods
}
