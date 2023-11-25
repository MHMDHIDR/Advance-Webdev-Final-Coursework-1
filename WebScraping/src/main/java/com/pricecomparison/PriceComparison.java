package com.pricecomparison;

import jakarta.persistence.*;

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

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private String price;

    @Column(name = "url")
    private String url;

    public void setCaseVariant(PhoneCaseVariation caseVariant) {
        this.caseVariant = caseVariant;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setPrice(String price) {
        this.price = price;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
