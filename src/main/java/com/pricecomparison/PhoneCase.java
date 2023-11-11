package com.pricecomparison;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "`case`")
public class PhoneCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "website")
    private String website;

    @Column(name = "phone_model")
    private String phoneModel;

    @OneToMany(mappedBy = "phoneCase", cascade = CascadeType.ALL)
    private List<PhoneCaseVariation> variations;

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }


    // Constructors, getters, setters, and other methods
}
