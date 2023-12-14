package com.pricecomparison;

import com.pricecomparison.webscraping.CaseDao;
import jakarta.persistence.*;
import java.util.List;

/**
 * The PhoneCase class represents a phone case model.
 * It contains a list of PhoneCaseVariation objects.
 * Each PhoneCaseVariation object contains a list of PriceComparison objects.
 * It is a one-to-many relationship between PhoneCase and PhoneCaseVariation.
 */
@Entity
@Table(name = "`case`")
public class PhoneCase {
    /**
     * This is the constructor of the ExtractProductPrice class.
     */
    public PhoneCase() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "phone_model")
    private String phoneModel;

    @OneToMany(mappedBy = "phoneCase", cascade = CascadeType.ALL)
    private List<PhoneCaseVariation> variations;


    /**
     * This method is used to set the phone model of the PhoneCase object.
     * It is used in the CaseDao class.
     * @param phoneModel The phone model of the PhoneCase object.
     *
     * @see CaseDao#saveCase(List, String)
     */
    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    /**
     * This method is used to get the id of the PhoneCase object.
     * It is used in the CaseDao class.
     * @return id The id of the PhoneCase object.
     *
     * @see CaseDao#saveCase(List, String)
     */
    public int getId() {
        return id;
    }

    /**
     * This method is used to set the id of the PhoneCase object.
     * @param id The id of the PhoneCase object.
     *
     * @see CaseDao#saveCase(List, String)
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * This method is used to get the color of the PhoneCase object.
     * It is used in the CaseDao class.
     * @return color The color of the PhoneCase object.
     *
     * @see CaseDao#saveCase(List, String)
     */
    public String getColor() {
        return variations.get(0).getColor();
    }

    /**
     * This method is used to get the phone model of the PhoneCase object.
     * It is used in the CaseDao class.
     * @return phoneModel The phone model of the PhoneCase object.
     *
     * @see CaseDao#saveCase(List, String)
     */
    public String getPhoneModel() {
        return phoneModel;
    }

    /**
     * This method is used to get the list of PhoneCaseVariation objects of the PhoneCase object.
     * It is used in the CaseDao class.
     * @return variations The list of PhoneCaseVariation objects of the PhoneCase object.
     *
     * @see CaseDao#saveCase(List, String)
     */
    public List<PhoneCaseVariation> getVariations() {
        return variations;
    }

    /**
     * This method is used to set the list of PhoneCaseVariation objects of the PhoneCase object.
     * @param variations The list of PhoneCaseVariation objects of the PhoneCase object.
     *
     * @see CaseDao#saveCase(List, String)
     */
    public void setVariations(List<PhoneCaseVariation> variations) {
        this.variations = variations;
    }
}