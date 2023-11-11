package com.pricecomparison;

import javax.persistence.*;

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

    // Constructors, getters, setters, and other methods...

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
}
