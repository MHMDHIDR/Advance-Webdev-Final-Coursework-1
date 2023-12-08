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

    @OneToOne(mappedBy = "caseVariant", cascade = CascadeType.ALL)
    private PriceComparison priceComparison;

    public void setColor(String color) {
        this.color = color;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPhoneCase(PhoneCase phoneCase) {
        this.phoneCase = phoneCase;
    }

    public void setPriceComparison(PriceComparison priceComparison) {
        this.priceComparison = priceComparison;
    }

    public String getId() {
        return String.valueOf(id);
    }

    public String getColor() {
        return color;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
