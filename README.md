                                                                                                          bootEco - Assistente Financeiro Inteligente 🚀

O bootEco é um assistente financeiro pessoal baseado em conversação que utiliza Inteligência Artificial para simplificar o controlo de gastos e receitas. Em vez de preencher formulários complexos, o utilizador simplesmente envia mensagens de texto natural (como se estivesse a falar com um amigo) e o bot processa, categoriza e regista os dados automaticamente.

📋 Sobre o Projeto
Este projeto foi desenvolvido como um MVP para demonstrar a viabilidade de uma ferramenta de gestão financeira com "fricção zero". O foco principal foi a criação de um backend robusto em Java que orquestra a comunicação entre uma interface de chat (simulada via Webhook), a API do Google Gemini e uma base de dados relacional.

✨ Funcionalidades
Processamento de Linguagem Natural (NLP): Integração com a IA do Gemini para extrair valores, tipos de transação e categorias de mensagens de texto livre.

Máquina de Estados de Conversa: Gestão do fluxo de diálogo (Início, Registo de Email, Objetivo e modo Livre) através de um sistema de estados persistido na base de dados.

Relatórios Automatizados: Geração de resumos mensais com cálculo de saldo e agrupamento de gastos por categoria.

Gestão de Transações: Funcionalidades de cancelamento via código curto (shortcode) e fecho de ciclo mensal.

Tratamento Global de Erros: Implementação de um interceptor global para garantir respostas amigáveis ao utilizador em caso de falhas técnicas ou de validação.

🛠️ Tecnologias Utilizadas
Linguagem: Java 21 (Long-Term Support)

Framework: Spring Boot 3.4

Base de Dados: PostgreSQL

IA Generativa: Google Gemini API (via RestClient)

Mapeamento: MapStruct (Desacoplamento de Entidades e DTOs)

Migrações: Flyway DB

Produtividade: Lombok e Java Records

🏗️ Arquitetura e Boas Práticas
O projeto segue os princípios da Clean Architecture e S.O.L.I.D., garantindo facilidade de manutenção e escalabilidade:

Camada de Domínio: Entidades ricas com validações de estado e integridade.

DTO Pattern: Uso extensivo de Records para garantir a imutabilidade dos dados durante o transporte entre camadas.

Global Exception Handling: Uso de @RestControllerAdvice para capturar exceções de domínio e devolvê-las de forma padronizada.

Segurança de Credenciais: Configuração de variáveis de ambiente para proteção de chaves de API e credenciais de base de dados.

🚀 Como Rodar o Projeto
Pré-requisitos
JDK 21

Docker (para o PostgreSQL via Docker Compose)

Chave de API do Google Gemini

Configuração
Clone o repositório:

Bash
git clone https://github.com/seu-usuario/boot_eco.git
Configure as variáveis de ambiente no seu sistema ou IDE:

GEMINI_API_KEY: Sua chave do Google Gemini.

DB_URL: jdbc:postgresql://localhost:5432/BotEco

DB_USER: Seu utilizador do Postgres.

DB_PASS: Sua senha do Postgres.

Execute a aplicação:

Bash
./mvnw spring-boot:run
🛣️ Roadmap
[ ] Integração oficial com a API do WhatsApp (Meta Cloud API).

[ ] Exportação automatizada de dados para Google Sheets via Google Drive API.

[ ] Implementação de Testes de Integração com Testcontainers.

[ ] Deploy em nuvem utilizando Docker e Railway/Render.

👤 Autor
Anthonny de Freitas Tauchen

Estudante de Sistemas de Informação - UNISINOS

SDR na SKA | Transição de Carreira para Software Engineering

LinkedIn
