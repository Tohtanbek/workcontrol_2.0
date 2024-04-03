package com.tosDev.spring.jpa.entity.client_orders;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity(name = "\"order\"")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float subTotal;
    private Float total;
    private LocalDateTime orderDateTime;
    private Integer orderOffset;
    private LocalDateTime dateTime;
    private Float area;
    private Long phoneNumber;
    private String clientName;
    private String email;
    private String address;
    private String promoCode;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderService> orderServices;
}
