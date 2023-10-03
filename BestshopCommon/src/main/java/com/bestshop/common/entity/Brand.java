package com.bestshop.common.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "brands")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 45, nullable = false, unique = true)
    private String name;

    @Column(length = 128, nullable = false)
    private String logo;

    @ManyToMany
    @JoinTable(name = "brands_categories",
                    joinColumns = @JoinColumn(name = "brand_id"),
                    inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    public Brand() {
    }

    public Brand(String name, String logo) {
        this.name = name;
        this.logo = logo;
    }

    public Brand(String name) {
        this.name = name;
    }

    public Brand(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public Integer getId() {
        return id;
    }

    @Transient
    public String getLogoPath(){
        if(this.id == null || this.logo == null || this.logo.isEmpty()) return "/images/image-thumbnail.png";
        return "/brand-logos/" + this.id + "/" + this.logo;
    }

    @Override
    public String toString() {
        return "Brand{" +
                "name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                ", categories=" + categories +
                '}';
    }
}
