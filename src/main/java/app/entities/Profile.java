package app.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @OneToMany(
            mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    @Builder.Default
    private List<TrainingCalendar> calendars = new ArrayList<>();

    @OneToMany(
            mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    @Builder.Default
    private List<TrainingProgram> trainingPrograms = new ArrayList<>();

    @OneToMany(
            mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    @Builder.Default
    private List<Note> notes = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @PrePersist
    private void beforeCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        deletedAt = null;
    }

    @PreUpdate
    private void beforeUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Profile(String name, User user) {
        this.name = name;
        this.user = user;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;

        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();

        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();

        if (thisEffectiveClass != oEffectiveClass)
            return false;

        Profile profile = (Profile) o;
        return getId() != null && Objects.equals(getId(), profile.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}