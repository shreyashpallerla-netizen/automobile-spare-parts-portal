package com.autoparts.config;

import com.autoparts.entity.Product;
import com.autoparts.entity.Role;
import com.autoparts.entity.User;
import com.autoparts.repository.ProductRepository;
import com.autoparts.repository.RoleRepository;
import com.autoparts.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Create roles if not exist
        Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN)
            .orElseGet(() -> roleRepository.save(Role.builder().name(Role.RoleName.ROLE_ADMIN).build()));

        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
            .orElseGet(() -> roleRepository.save(Role.builder().name(Role.RoleName.ROLE_USER).build()));

        // Create admin user if not exist
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("Admin@123"))
                .email("admin@autoparts.com")
                .fullName("System Administrator")
                .enabled(true)
                .roles(Set.of(adminRole, userRole))
                .build();
            userRepository.save(admin);
            log.info("Admin user created: admin / Admin@123");
        }

        // Create demo user if not exist
        if (!userRepository.existsByUsername("user1")) {
            User demoUser = User.builder()
                .username("user1")
                .password(passwordEncoder.encode("User@123"))
                .email("user1@autoparts.com")
                .fullName("Demo User")
                .enabled(true)
                .roles(Set.of(userRole))
                .build();
            userRepository.save(demoUser);
            log.info("Demo user created: user1 / User@123");
        }

        // Seed sample spare parts if catalogue is empty
        if (productRepository.count() == 0) {
            List<Product> seedProducts = List.of(
                Product.builder().name("Bosch Spark Plug Iridium").description("High-performance iridium spark plug for petrol engines, long life 100k km")
                    .sku("BSP-001").price(new BigDecimal("299.99")).stockQuantity(50).category("Engine").brand("Bosch").active(true).build(),
                Product.builder().name("Denso Air Filter").description("Premium air filter for engine protection and improved airflow")
                    .sku("DAF-001").price(new BigDecimal("450.00")).stockQuantity(35).category("Filters").brand("Denso").active(true).build(),
                Product.builder().name("MRF Tyre 185/65R15").description("Passenger radial tyre, excellent wet and dry grip")
                    .sku("MRF-001").price(new BigDecimal("3500.00")).stockQuantity(20).category("Tyres").brand("MRF").active(true).build(),
                Product.builder().name("Valeo Brake Pad Set").description("Ceramic brake pads for front axle, low dust and noise")
                    .sku("VBP-001").price(new BigDecimal("1200.00")).stockQuantity(15).category("Brakes").brand("Valeo").active(true).build(),
                Product.builder().name("ACDelco Engine Oil 5W30").description("Fully synthetic engine oil, 1 litre, API SN rated")
                    .sku("ACO-001").price(new BigDecimal("350.00")).stockQuantity(100).category("Lubricants").brand("ACDelco").active(true).build(),
                Product.builder().name("NGK O2 Oxygen Sensor").description("Wideband oxygen sensor for automotive fuel injection system")
                    .sku("NGK-001").price(new BigDecimal("1850.00")).stockQuantity(12).category("Engine").brand("NGK").active(true).build(),
                Product.builder().name("Exide Battery 55Ah").description("55Ah maintenance-free car battery, 18-month warranty")
                    .sku("EXB-001").price(new BigDecimal("4200.00")).stockQuantity(8).category("Electrical").brand("Exide").active(true).build(),
                Product.builder().name("Minda Headlight Bulb H4").description("H4 halogen headlight bulb 60/55W for most passenger cars")
                    .sku("MHB-001").price(new BigDecimal("180.00")).stockQuantity(75).category("Electrical").brand("Minda").active(true).build(),
                Product.builder().name("Gabriel Shock Absorber").description("Front hydraulic shock absorber with gas damping")
                    .sku("GSA-001").price(new BigDecimal("1650.00")).stockQuantity(18).category("Suspension").brand("Gabriel").active(true).build(),
                Product.builder().name("Schaeffler Timing Belt Kit").description("Complete timing belt kit with tensioner and idler pulley")
                    .sku("STB-001").price(new BigDecimal("2800.00")).stockQuantity(10).category("Engine").brand("Schaeffler").active(true).build(),
                Product.builder().name("Mahle Oil Filter").description("High efficiency oil filter for extended oil change intervals")
                    .sku("MOF-001").price(new BigDecimal("280.00")).stockQuantity(60).category("Filters").brand("Mahle").active(true).build(),
                Product.builder().name("Bosch Wiper Blade 21\"").description("Aerotwin flat wiper blade 21 inch, streak-free performance")
                    .sku("BWB-001").price(new BigDecimal("420.00")).stockQuantity(40).category("Exterior").brand("Bosch").active(true).build(),
                Product.builder().name("LUK Clutch Pressure Plate (Low Stock)").description("Heavy-duty clutch assembly plate for hatchback vehicles")
                    .sku("LUK-001").price(new BigDecimal("2499.00")).stockQuantity(2).category("Transmission").brand("LUK").active(true).build()
            );
            productRepository.saveAll(seedProducts);
            log.info("Seeded {} sample spare parts into catalogue", seedProducts.size());
        }
    }
}
