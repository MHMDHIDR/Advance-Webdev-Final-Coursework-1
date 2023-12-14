package com.pricecomparison;

import jakarta.persistence.*;

/**
 * This class represents a variation of a phone case. For example, a phone case
 * may come in multiple colors, so each color would be a variation of the phone
 * case.
 * I used the @OneToOne annotation to create a one-to-one relationship between
 * this class and the PriceComparison class. This means that each phone case
 * variation has one price comparison, and each price comparison has one phone
 * case variation.
 */
@Entity
@Table(name = "cases_variants")
public class PhoneCaseVariation {
    /**
     * This is the constructor of the ExtractProductPrice class.
     */
    public PhoneCaseVariation() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "case_id")
    private PhoneCase phoneCase;

    @Column(name = "color")
    private String color;

    @Column(name = "image_url")
    private String imageUrl;

    /**
     * This field is used to create a one-to-one relationship between this class
     * and the PriceComparison class.
     */
    @OneToOne(mappedBy = "caseVariant", cascade = CascadeType.ALL)
    private PriceComparison priceComparison;

    /**
     * This constructor is used to create a new PhoneCaseVariation object.
     * @param color The color of the phone case.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * This constructor is used to create a new PhoneCaseVariation object.
     * @param imageUrl The URL of the image of the phone case.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * This constructor is used to create a new PhoneCaseVariation object.
     * @param phoneCase The phone case that this variation belongs to.
     */
    public void setPhoneCase(PhoneCase phoneCase) {
        this.phoneCase = phoneCase;
    }

    /**
     * This constructor is used to create a new PhoneCaseVariation object.
     * @param priceComparison The price comparison of this variation.
     */
    public void setPriceComparison(PriceComparison priceComparison) {
        this.priceComparison = priceComparison;
    }

    /**
     * This constructor is used to create a new PhoneCaseVariation object.
     * Gets The ID of the phone case variation.
     *
     * @return id The ID of the phone case variation.
     */
    public String getId() {
        return String.valueOf(id);
    }

    /**
     * This constructor is used to create a new PhoneCaseVariation object.
     * Gets The phone case that this variation belongs to.
     *
     * @return color The color of the phone case.
     */
    public String getColor() {
        return color;
    }

    /**
     * This constructor is used to create a new PhoneCaseVariation object.
     * Gets The phone case that this variation belongs to.
     *
     * @return imageUrl The URL of the image of the phone case.
     */
    public String getImageUrl() {
        return imageUrl;
    }
}
