package com.pricecomparison;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "product")
public class PhoneCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "retailer_id") // Adjust the column name if necessary
    private Retailer retailer;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private String price;

    @Column(name = "description")
    private String description;

    @Column(name = "website_link")
    private String websiteLink;

    @Column(name = "product_image_url")
    private String productImageUrl;

    @OneToMany(mappedBy = "phoneCase", cascade = CascadeType.ALL)
    private List<PhoneCaseVariation> variations;

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDescription(String description) { // Corrected method name
        this.description = description;
    }

    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    public void setProductImageUrl(String productImageUrl) { // New method for product image URL
        this.productImageUrl = productImageUrl;
    }

    // Constructors, getters, setters, and other methods
}
