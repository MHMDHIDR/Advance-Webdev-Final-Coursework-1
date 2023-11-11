package com.pricecomparison;

import javax.persistence.*;

@Entity
@Table(name = "comparison")
public class PriceComparison {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @OneToOne
    @JoinColumn(name = "case_variant_id")
    private PhoneCaseVariation caseVariant;

    @Column(name = "price")
    private double price;

    @Column(name = "url")
    private String url;

    // Constructors, getters, setters, and other methods...

    public void setCaseVariant(PhoneCaseVariation caseVariant) {
        this.caseVariant = caseVariant;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
