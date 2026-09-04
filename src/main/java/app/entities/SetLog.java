package app.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class SetLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_log_id", nullable = false)
    @ToString.Exclude
    private ExerciseLog exerciseLog;

    @Column(nullable = false)
    private int setNumber;

    private Double weight;

    private Integer reps;

    private Integer rir;

    @Column(nullable = false)
    private boolean completed;

    public SetLog(
            ExerciseLog exerciseLog,
            int setNumber,
            Double weight,
            Integer reps,
            Integer rir
    ) {
        this.exerciseLog = exerciseLog;
        this.setNumber = setNumber;
        this.weight = weight;
        this.reps = reps;
        this.rir = rir;
        this.completed = false;
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

        SetLog setLog = (SetLog) o;
        return getId() != null && Objects.equals(getId(), setLog.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
