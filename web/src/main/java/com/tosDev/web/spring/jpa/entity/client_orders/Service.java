package com.tosDev.web.spring.jpa.entity.client_orders;

import com.tosDev.web.enums.ServiceCategory;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Float price;
    @Enumerated
    private ServiceCategory category;
    private Float minimalPrice;
    private String promoCode;
    private Integer promoCodeDiscount;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "service")
    List<OrderService> orderServiceList = new ArrayList<>();
}
