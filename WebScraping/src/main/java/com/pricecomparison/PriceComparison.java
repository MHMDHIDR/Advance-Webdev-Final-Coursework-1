package com.pricecomparison;

import jakarta.persistence.*;

/**
 * PriceComparison
 * This class is used to store the price comparison data for a phone case variation.
 * I used the @OneToOne annotation to create a one-to-one relationship between the
 * caseVariant field and the PhoneCaseVariation class.
 * I used the @JoinColumn annotation to specify `case_variant_id` as the foreign key
 * column name in the price comparison table.
 */
@Entity
@Table(name = "comparison")
public class PriceComparison {
    /**
     * This is the constructor of the ExtractProductPrice class.
     */
    public PriceComparison() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @OneToOne
    @JoinColumn(name = "case_variant_id")
    private PhoneCaseVariation caseVariant;

    @Column(name = "name")
    private String name;

    @Column(name = "website")
    private String website;

    @Column(name = "price")
    private String price;

    @Column(name = "url")
    private String url;

    /**
     * Constructor
     * @param caseVariant Sets The phone case variation that this price comparison is for.
     */
    public void setCaseVariant(PhoneCaseVariation caseVariant) {
        this.caseVariant = caseVariant;
    }

    /**
     * Constructor
     * takes parameter name of type String and sets the name of the price comparison.
     * @param name Sets the name of the price comparison.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Constructor
     * @param website Sets the website of the price comparison.
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * Constructor
     * @param price Sets the price of the price comparison.
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * Constructor
     * @param url Sets the url of the price comparison.
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
