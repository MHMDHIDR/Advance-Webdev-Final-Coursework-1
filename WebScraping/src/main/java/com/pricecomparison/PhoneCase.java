package com.pricecomparison;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "`case`")
public class PhoneCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "phone_model")
    private String phoneModel;

    @OneToMany(mappedBy = "phoneCase", cascade = CascadeType.ALL)
    private List<PhoneCaseVariation> variations;

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getColor() {
        return variations.get(0).getColor();
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public List<PhoneCaseVariation> getVariations() {
        return variations;
    }

    public void setVariations(List<PhoneCaseVariation> variations) {
        this.variations = variations;
    }


}