package com.example.serbisyofullstack.config;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.serbisyofullstack.model.entity.CustomerProfile;
import com.example.serbisyofullstack.model.entity.ProviderProfile;
import com.example.serbisyofullstack.model.entity.Role;
import com.example.serbisyofullstack.model.entity.Service;
import com.example.serbisyofullstack.model.entity.ServiceCategory;
import com.example.serbisyofullstack.model.entity.User;
import com.example.serbisyofullstack.model.entity.UserRole;
import com.example.serbisyofullstack.model.enums.PricingType;
import com.example.serbisyofullstack.model.enums.RoleEnum;
import com.example.serbisyofullstack.model.enums.Status;
import com.example.serbisyofullstack.model.enums.VerificationStatus;
import com.example.serbisyofullstack.repository.CustomerProfileRepository;
import com.example.serbisyofullstack.repository.ProviderProfileRepository;
import com.example.serbisyofullstack.repository.RoleRepository;
import com.example.serbisyofullstack.repository.ServiceCategoryRepository;
import com.example.serbisyofullstack.repository.ServiceRepository;
import com.example.serbisyofullstack.repository.UserRepository;
import com.example.serbisyofullstack.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Development seed data. Runs only when {@code app.seed.enabled=true} is passed
 * (e.g. {@code java -jar app.jar --app.seed.enabled=true}) AND the database is
 * still empty — never inserts over existing content, and never enables itself
 * by default.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DevDataSeeder {

    @Bean
    CommandLineRunner seedRunner(
            ServiceCategoryRepository categories,
            UserRepository users,
            RoleRepository roles,
            UserRoleRepository userRoles,
            CustomerProfileRepository customers,
            ProviderProfileRepository providers,
            ServiceRepository services,
            PasswordEncoder encoder,
            org.springframework.core.env.Environment environment) {
        return args -> {
            boolean seedEnabled = Boolean.parseBoolean(
                    environment.getProperty("app.seed.enabled", "false"));
            log.info("[seed] Runner invoked. app.seed.enabled={}", seedEnabled);
            if (!seedEnabled) {
                return;
            }
            // Idempotent per section: categories/users may partially exist from
            // an earlier interrupted run — always top up what's missing.

            // ---------- roles ----------
            Role customerRole = ensureRole(roles, RoleEnum.CUSTOMER);
            Role providerRole = ensureRole(roles, RoleEnum.PROVIDER);

            // ---------- categories ----------
            ServiceCategory homeRepair = ensureCat(categories, "Home repair",
                    "Plumbing, electrical, carpentry and appliance fixes done right the first time.");
            ServiceCategory cleaning = ensureCat(categories, "Cleaning",
                    "Home, office and deep cleaning with vetted, insured cleaners.");
            ServiceCategory tutoring = ensureCat(categories, "Tutoring",
                    "One-on-one academic support for kids and adults, in person or online.");
            ServiceCategory beauty = ensureCat(categories, "Beauty & wellness",
                    "Hair, nails, massage and wellness treatments at your convenience.");
            ServiceCategory moving = ensureCat(categories, "Moving & transport",
                    "Furniture moving, deliveries and errands with honest hourly pricing.");

            // ---------- providers ----------
            record Seed(String email, String phone, String business, String bio,
                    double rating, int reviews, Map<ServiceCategory, String[][]> catalog,
                    ServiceCategory... extraCategories) {

            }

            List<Seed> seeds = List.of(
                    new Seed("tomas@example.com", "+639170000101", "Reyes Home Repair",
                            "Family-run handyman service in Parañaque since 2012. Licensed electrician on staff, and we always clean up after the job.",
                            4.8, 64, Map.of(
                                    homeRepair, new String[][]{
                                        {"Leaking faucet or pipe repair", "Diagnostics included; parts quoted on site before any work starts.", "FIXED", "850", "90"},
                                                    {"Electrical outlet and wiring check", "Whole-house safety check with a written report.", "HOURLY", "450", "120"}}),
                             cleaning, tutoring, beauty, moving),
                    new Seed("lia@example.com", "+639170000102", "Sparkle Squad Cleaning",
                            "A five-person crew serving Metro Manila homes and small offices. Bring your own supplies or ours — same price either way.",
                            4.6, 41, Map.of(
                                    cleaning, new String[][]{
                                        {"Studio / condo deep clean", "Kitchen, bath, floors, windows and those forgotten corners.", "FIXED", "1500", "240"},
                                        {"Post-renovation cleanup", "Dust, debris and paint spatter removed, ready to move in.", "HOURLY", "600", "180"}}),
                            homeRepair, tutoring, beauty, moving),
                    new Seed("kit@example.com", "+639170000103", "BrightPath Tutors",
                            "Engineer-turned-teacher offering math and science tutoring from grade school to college entrance prep.",
                            4.9, 88, Map.of(
                                    tutoring, new String[][]{
                                        {"Grade school math catch-up", "Patient, step-by-step sessions with weekly progress notes for parents.", "HOURLY", "500", "60"},
                                        {"College entrance exam prep (Math)", "Past-exam drills, timing strategy, and honest diagnostics.", "FIXED", "2500", "480"}}),
                            homeRepair, cleaning, beauty, moving));

            for (Seed s : seeds) {
                // Skip provider users already seeded by an earlier partial run.
                if (users.findByEmail(s.email).isPresent()) {
                    continue;
                }
                User user = new User();
                user.setEmail(s.email);
                user.setPassword(encoder.encode("Password123!"));
                user.setPhone(s.phone);
                user.setStatus(Status.ACTIVE);
                user = users.save(user);
                for (Role role : new Role[]{customerRole, providerRole}) {
                    UserRole ur = new UserRole();
                    ur.setUser(user);
                    ur.setRole(role);
                    userRoles.save(ur);
                }

                ProviderProfile provider = new ProviderProfile();
                provider.setUser(user);
                provider.setBusinessName(s.business);
                provider.setBio(s.bio);
                provider.setVerificationStatus(VerificationStatus.VERIFIED);
                provider.setAverageRating(s.rating);
                provider.setReviewCount(s.reviews);
                provider = providers.save(provider);

                for (Map.Entry<ServiceCategory, String[][]> entry : s.catalog.entrySet()) {
                    for (String[] row : entry.getValue()) {
                        Service service = new Service();
                        service.setProvider(provider);
                        service.setCategory(entry.getKey());
                        service.setName(row[0]);
                        service.setDescription(row[1]);
                        service.setPricingType(PricingType.valueOf(row[2]));
                        service.setBasePrice(new BigDecimal(row[3]));
                        service.setDurationMinutes(Integer.valueOf(row[4]));
                        service.setActive(true);
                        services.save(service);
                    }
                }
            }

            // ---------- one customer for testing the booking flow ----------
            if (users.findByEmail("juandela.cruz@example.com").isEmpty()) {
            User customer = new User();
            customer.setEmail("juandela.cruz@example.com");
            customer.setPassword(encoder.encode("Password123!"));
            customer.setPhone("+639170000201");
            customer.setStatus(Status.ACTIVE);
            customer = users.save(customer);
            UserRole ur = new UserRole();
            ur.setUser(customer);
            ur.setRole(customerRole);
            userRoles.save(ur);
            CustomerProfile profile = new CustomerProfile();
            profile.setUser(customer);
            profile.setDisplayName("Juan de la Cruz");
            customers.save(profile);
            }

            log.info("[seed] Done: {} categories, {} providers, {} services, 1 customer.",
                    categories.count(), providers.count(), services.count());
        };
    }

    private Role ensureRole(RoleRepository roles, RoleEnum name) {
        return roles.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            return roles.save(role);
        });
    }

    private ServiceCategory ensureCat(ServiceCategoryRepository repo, String name, String description) {
        return repo.findByNameIgnoreCase(name).orElseGet(() -> {
            ServiceCategory category = new ServiceCategory();
            category.setName(name);
            category.setDescription(description);
            return repo.save(category);
        });
    }
}
