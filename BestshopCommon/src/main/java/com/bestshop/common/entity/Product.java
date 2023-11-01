package com.bestshop.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "products")
@NoArgsConstructor
@Getter
@Setter
public class Product{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, length = 256, nullable = false)
    private String name;
    @Column(unique = true, length = 256)
    private String alias;
    @Column(name = "short_description", length = 512)
    private String shortDescription;
    @Column(name = "full_description", length = 4096)
    private String fullDescription;

    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "updated_time")
    private Date updatedTime;
    private boolean enabled;
    @Column(name = "in_stock")
    private boolean inStock;

    private BigDecimal cost;
    private BigDecimal price;
    @Column(name = "discount_percent")
    private BigDecimal discountPercent;

    private float length;
    private float width;
    private float heigth;
    private float weigth;

    @Column(name = "main_image", nullable = false)
    private String mainImage;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<ProductImage> images = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductDetail> details = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        if(cost == null || cost.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Cost cannot be null or negative");
        }
        this.cost = cost;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if(price == null || price.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Price cannot be null or negative");
        }
        this.price = price;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        if(discountPercent == null || discountPercent.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Discount Percent cannot be null or negative");
        }
        this.discountPercent = discountPercent;
    }

    public void addExtraImage(String name){
        this.images.add(new ProductImage(name, this));
    }

    public void addDetail(String name, String value) {
        this.details.add(new ProductDetail(name, value, this));
    }

    @Transient
    public String getMainImagePath(){
        if(this.id == null || this.mainImage == null || this.mainImage.isEmpty()) return "/images/image-thumbnail.png";
        return "/product-images/" + this.id + "/" + this.mainImage;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", brand=" + brand +
                '}';
    }

    public boolean containsImageName(String imageName) {
        return images.stream()
                .anyMatch(imagem -> Objects.equals(imagem.getName(), imageName));
    }
}
