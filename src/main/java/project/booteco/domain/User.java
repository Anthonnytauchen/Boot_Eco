package project.booteco.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tb_usuario")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
//id UUID PRIMARY KEY,
//    telefone_whatsapp VARCHAR(20) NOT NULL UNIQUE,
//    email_google VARCHAR(100),
//    url_planilha VARCHAR(255),
//    estado_conversa VARCHAR(50) NOT NULL,
//    objetivo_texto_livre TEXT,
//    data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name="telefone_whatsapp", unique = true)
    private String phoneWhatsapp;

    @Column(name="email_google")
    private String emailGoogle;

    @Column(name="url_planilha")
    private String urlGraphic;

    @Column(name="estado_conversa")
    @Enumerated(EnumType.STRING)
    private StateConversation stateCoversation;

    @Column(name="objetivo_texto_livre")
    private String objectiveTextFree;

    @Column(name="data_cadastro" )
    @CreationTimestamp
    private LocalDateTime dateCreated;

}
