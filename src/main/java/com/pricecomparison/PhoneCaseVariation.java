package com.pricecomparison;

import javax.persistence.*;

@Entity
@Table(name = "phone_case_variation")
public class PhoneCaseVariation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "product_id") // Make sure it matches the column name in the PhoneCase table
    private PhoneCase phoneCase;

    @Column(name = "color")
    private String color;

    @Column(name = "material")
    private String material;

    @Column(name = "model")
    private String model;

    // Constructors, getters, setters, and other methods
}
