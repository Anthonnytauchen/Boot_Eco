package project.booteco.domain;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tb_transacao")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;


    @Column(name = "usuario_id", nullable = false)
    private UUID userId;

    @Column(name = "codigo_curto", nullable = false, unique = true, length = 10)
    private String shortCode;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    @Column(name = "tipo", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TypeTransaction type;

    @Column(name = "categoria", length = 50)
    @Enumerated(EnumType.STRING)
    private CategoryTransaction categoryTransaction;

    @Column(name = "subcategoria", length = 50)
    private String subcategory;

    @Column(name = "data", nullable = false)
    @CreationTimestamp
    private LocalDateTime date;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StatusTransaction status= StatusTransaction.ATIVA;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = StatusTransaction.ATIVA;
        }
    }
}

