package app.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class GoogleCalendarConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @ToString.Exclude
    private User user;

    @Column(nullable = false)
    private String googleUserId;

    @Column(nullable = false, length = 4000)
    private String accessToken;

    @Column(nullable = false, length = 4000)
    private String refreshToken;

    private String calendarId;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    @PrePersist
    private void beforeCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    private void beforeUpdate() {
        updatedAt = LocalDate.now();
    }

    public GoogleCalendarConnection(
            User user,
            String googleUserId,
            String accessToken,
            String refreshToken
    ) {
        this.user = user;
        this.googleUserId = googleUserId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.calendarId = "primary";
    }

    public void updateTokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;

        if (refreshToken != null && !refreshToken.isBlank()) {
            this.refreshToken = refreshToken;
        }
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
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
                : getClass();

        if (thisEffectiveClass != oEffectiveClass)
            return false;

        GoogleCalendarConnection that = (GoogleCalendarConnection) o;

        return getId() != null &&
                Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}