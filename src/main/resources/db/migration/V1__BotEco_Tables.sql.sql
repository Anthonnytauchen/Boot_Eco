CREATE TABLE tb_usuario (
                            id UUID PRIMARY KEY,
                            telefone_whatsapp VARCHAR(20) NOT NULL UNIQUE,
                            email_google VARCHAR(100),
                            url_planilha VARCHAR(255),
                            estado_conversa VARCHAR(50) NOT NULL,
                            objetivo_texto_livre TEXT,
                            data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tb_transacao (
                              id UUID PRIMARY KEY,
                              usuario_id UUID NOT NULL,
                              codigo_curto VARCHAR(10) NOT NULL UNIQUE,
                              valor NUMERIC(10, 2) NOT NULL,
                              tipo VARCHAR(20) NOT NULL,
                              categoria VARCHAR(50),
                              subcategoria VARCHAR(50),
                              data TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              status VARCHAR(20) NOT NULL,

                              CONSTRAINT fk_transacao_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario (id) ON DELETE CASCADE
);