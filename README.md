# 🤖 Bot de Saúde para Telegram

Um bot em **Java 21** e **Spring Boot** focado em ajudar o usuário a manter uma rotina saudável através do registro de hábitos, treinos e alimentação com o mínimo de atrito (uma mensagem ou um toque), fornecendo resumos diários e semanais de evolução.

---

## 📌 Princípios do Projeto

- **Organiza e informa:** O bot atua como um assistente de rotina e organização. **Não diagnostica nem prescreve.**
- **Privacidade e LGPD:** Dados de saúde são sensíveis. O sistema coleta o mínimo necessário, exige consentimento explícito no primeiro acesso e permite a remoção completa de todos os dados a qualquer momento via comando `/apagar`.
- **Arquitetura limpa:** A lógica de negócio reside nos *services*; o handler do Telegram atua estritamente como tradutor de mensagens para chamadas de serviço.
- **IA desacoplada:** A camada de inteligência artificial é isolada por trás de uma interface (`AiService`) e introduzida apenas após a estabilização do MVP funcional.

---

## 🛠️ Stack Tecnológica

- **Linguagem:** Java 21
- **Framework:** Spring Boot, Spring Data JPA
- **Banco de Dados:** PostgreSQL
- **Migrações:** Flyway
- **Infraestrutura:** Docker & Docker Compose
- **Biblioteca Telegram:** `telegrambots`
- **Testes:** JUnit 5, Testcontainers

---

## 📁 Estrutura de Pacotes

O projeto segue uma divisão modular por domínio:

```text
src/main/java/com/seuusuario/saudebot
├── ai          # Integrações com IA (pós-MVP)
├── bot         # Handlers, rotas e interações com a API do Telegram
├── config      # Configurações globais (Beans, segurança, timezone)
├── habit       # Domínio de Hábitos e Registros
├── meal        # Domínio de Alimentação
├── report      # Lógica de resumos diários e semanais
├── user        # Gerenciamento de contas e consentimento
└── workout     # Domínio de Treinos
```

## ⚙️ Como Rodar o Projeto Localmente

Pré-requisitos

    Java 21 instalado

    Maven (ou Gradle)

    Docker e Docker Compose

### 1. Clonar o repositório
 
```
git clone [https://github.com/seu-usuario/seu-repositorio.git](https://github.com/seu-usuario/seu-repositorio.git)
cd seu-repositorio
```

### 2. Configurar as Variáveis de Ambiente

Crie um arquivo .env na raiz do projeto baseando-se no .env.example:

    # Configurações do Bot do Telegram
    TELEGRAM_BOT_TOKEN=seu_token_aqui
    TELEGRAM_BOT_USERNAME=nome_do_seu_bot
    
    # Configurações do Banco de Dados PostgreSQL
    SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/saudebot
    SPRING_DATASOURCE_USERNAME=postgres
    SPRING_DATASOURCE_PASSWORD=postgres

### 3. Subir o Banco de Dados via Docker Compose

Na raiz do projeto, suba o container do PostgreSQL:
```
docker compose up -d
```

### 4. Executar a Aplicação

Com o banco rodando e as variáveis configuradas, execute a aplicação Spring Boot:
```
mvn spring-boot:run
```

O Flyway rodará automaticamente as migrações pendentes na inicialização e o banco estará pronto para uso.

---

## 🧪 Testes

Para rodar os testes unitários e de integração (que utilizam Testcontainers para subir instâncias efêmeras do PostgreSQL):
```
mvn test
```