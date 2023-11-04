package com.pricecomparison;

import javax.persistence.*;

@Entity
@Table(name = "price_comparison")
public class PriceComparison {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private PhoneCase phoneCase;

    @ManyToOne
    @JoinColumn(name = "retailer_id")
    private Retailer retailer;

    @Column(name = "price")
    private double price;

    @Column(name = "url")
    private String url;

    // Constructors, getters, setters, and other methods
}
