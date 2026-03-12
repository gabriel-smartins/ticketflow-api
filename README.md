<div align="center">

# 🎟️ TicketFlow

**Uma API RESTful robusta para plataformas de venda de ingressos com alta concorrência**

[![Java](https://img.shields.io/badge/Java_21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat-square&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)](https://www.docker.com/)
[![Testcontainers](https://img.shields.io/badge/Testcontainers-25A162?style=flat-square&logo=testing-library&logoColor=white)](https://testcontainers.com/)

</div>

---

## Sobre o Projeto

O **TicketFlow** é uma API desenvolvida para resolver um dos desafios mais críticos em sistemas de e-commerce e bilheteria: a alta concorrência. O objetivo central é gerenciar picos de tráfego durante o lançamento de eventos populares, lidando com *race conditions* (condições de corrida) e garantindo a integridade absoluta dos dados.

A aplicação utiliza o mecanismo de **Pessimistic Locking** a nível de banco de dados para prevenir o *overselling* (venda além da capacidade física do evento), tudo isso apoiado por uma arquitetura bem estruturada baseada em pacotes por funcionalidade (*Package-by-Feature*).

---

## Índice

- [Funcionalidades](#funcionalidades)
- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Endpoints da API](#endpoints-da-api)
- [Como Executar](#como-executar)
- [Testes](#testes)
- [Estrutura de Pastas](#estrutura-de-pastas)
- [Variáveis de Ambiente](#variáveis-de-ambiente)

---

## Funcionalidades

### Autenticação e Segurança

- Registro de usuários com encriptação segura de senhas
- Login com geração de token **JWT** para autenticação *stateless*
- Proteção de rotas sensíveis via **Spring Security**
- Tratamento de erros padronizado e amigável seguindo a RFC 7807 (**ProblemDetail**)

### Gestão de Eventos e Concorrência

- Criação e consulta de eventos com detalhes de lotes e capacidades
- **Controle de Concorrência Rigoroso:** Implementação de bloqueio `PESSIMISTIC_WRITE` para garantir atualizações atômicas na disponibilidade de ingressos
- Listagem de eventos com suporte a **paginação** e otimização de leitura usando **Cache no Redis**
- Processamento seguro de compras de ingressos e reembolsos integrados ao ciclo de vida do evento
- Gestão automatizada do esquema de banco de dados usando **Flyway**

---

## Arquitetura

O projeto adota o padrão de organização **Package-by-Feature** (Pacotes por Funcionalidade). Em vez de agrupar classes por seus tipos (ex: todos os controllers juntos, todos os services juntos), o código é estruturado ao redor de domínios de negócio (`event`, `ticket`, `user`). Isso aumenta a coesão, facilita a manutenção e torna a base de código muito mais intuitiva para navegar.

Internamente, cada pacote de funcionalidade segue a clássica arquitetura em camadas (MVC/Layered) do Spring Boot:

~~~text
┌──────────────────────────────────────────────────────────┐
│                 Arquitetura Base (Camadas)               │
│                                                          │
│   [ Controllers ] ──> [ Services ] ──> [ Repositories ]  │
│    (Rotas REST)     (Regras de Negócio)  (Spring Data)   │
│                                                          │
│                 Organização do Código                    │
│                (Package by Feature)                      │
│                                                          │
│   📂 event/    (Tudo relacionado a Eventos)              │
│   📂 ticket/   (Tudo relacionado a Ingressos)            │
│   📂 user/     (Tudo relacionado a Usuários/Auth)        │
└──────────────────────────────────────────────────────────┘
~~~

**Controllers** — Gerenciam o tráfego HTTP, validações de entrada (via DTOs) e retornos de respostas (status codes e *ProblemDetails*).

**Services** — Concentram a lógica de negócio e as regras transacionais da aplicação (`@Transactional`). É aqui que a mágica da compra de ingressos e do tratamento de concorrência acontece.

**Repositories / Entities** — O mapeamento objeto-relacional (JPA/Hibernate) e o acesso direto aos dados via interfaces do Spring Data JPA.

---

## Tecnologias

| Categoria       | Tecnologia                 |
| --------------- | -------------------------- |
| Linguagem       | Java 21                    |
| Framework       | Spring Boot 3              |
| Banco de Dados  | PostgreSQL 15              |
| Cache           | Redis                      |
| ORM             | Spring Data JPA / Hibernate|
| Migrations      | Flyway                     |
| Autenticação    | Spring Security · JWT      |
| Documentação    | SpringDoc (OpenAPI 3)      |
| Testes          | JUnit 5 · Mockito          |
| Integração      | Testcontainers             |
| Containerização | Docker · Docker Compose    |

---

## Endpoints da API

> **Autenticação:** Bearer Token JWT no header `Authorization`.  
> **Documentação Completa:** Disponível via Swagger UI em `/swagger-ui.html`.

### Autenticação

| Método | Rota             | Descrição                      | Auth |
| ------ | ---------------- | ------------------------------ | ---- |
| POST   | `/auth/register` | Criação de conta de usuário    | ❌   |
| POST   | `/auth/login`    | Login e geração do token JWT   | ❌   |

### Eventos e Ingressos

| Método | Rota                           | Descrição                                    | Auth |
| ------ | ------------------------------ | -------------------------------------------- | ---- |
| GET    | `/events`                      | Listar eventos disponíveis (Paginado/Cache)  | ❌   |
| GET    | `/events/{eventId}`            | Buscar detalhes de um evento específico      | ❌   |
| POST   | `/events`                      | Criar um novo evento                         | ✅   |
| POST   | `/events/{eventId}/purchase`   | Processar a compra de um ingresso (Com Lock) | ✅   |
| POST   | `/events/{eventId}/refund`     | Solicitar reembolso de um ingresso           | ✅   |

---

## Como Executar

### Pré-requisitos

- [Java 21 JDK](https://adoptium.net/)
- [Docker & Docker Compose](https://www.docker.com/)
- Maven (O *wrapper* `./mvnw` já está incluso no projeto)

### Passo a passo

**1. Clone o repositório**

~~~bash
git clone [https://github.com/gabriel-smartins/ticketflow-api.git](https://github.com/gabriel-smartins/ticketflow-api.git)
cd ticketflow-api
~~~

**2. Configure as variáveis de ambiente**

Crie um arquivo `.env` na raiz do projeto:

~~~bash
cp .env.example .env
~~~

Preencha o `.env` com os valores do seu ambiente local (veja a seção [Variáveis de Ambiente](#variáveis-de-ambiente)).

**3. Suba a infraestrutura de dados**

Inicie os containers do PostgreSQL e do Redis em segundo plano:

~~~bash
docker-compose up -d
~~~

**4. Inicie o servidor**

Execute a aplicação via Maven Wrapper. O Flyway executará as migrations automaticamente.

~~~bash
./mvnw spring-boot:run
~~~

A API estará disponível em `http://localhost:8080`.

---

## Testes

O projeto adota uma abordagem sólida de testes para garantir confiabilidade em cenários de estresse.

~~~bash
# Executar toda a suíte de testes
./mvnw test
~~~

**Testes Unitários** Focados em validar a lógica de negócio contida nos `Services` utilizando o Mockito para isolar e simular as dependências.

**Testes de Integração (Testcontainers)** Essenciais para validar o controle de concorrência deste projeto. Eles utilizam a biblioteca **Testcontainers** para subir containers efêmeros do PostgreSQL durante a execução. Isso permite simular dezenas de requisições paralelas tentando comprar o mesmo ingresso ao mesmo tempo contra um banco de dados real, validando se o *Pessimistic Locking* está realmente impedindo o *overselling*.

---

## Estrutura de Pastas

~~~text
ticketflow-api/
│
├── src/main/java/com/ticketflow/api/
│   ├── config/                # Configurações globais (Security, Redis, Swagger)
│   ├── event/                 # Contexto de Eventos
│   │   ├── dto/               # Objetos de transferência de dados (Records)
│   │   ├── exception/         # Tratamento de exceções específicas do domínio
│   │   ├── Event.java         # Entidade JPA
│   │   ├── EventController.java # Endpoints HTTP de eventos
│   │   ├── EventRepository.java # Acesso aos dados do PostgreSQL
│   │   └── EventService.java    # Lógica de negócio e transações de eventos
│   │
│   ├── ticket/                # Contexto de Ingressos
│   │   ├── dto/
│   │   ├── enums/
│   │   ├── exception/
│   │   ├── Ticket.java
│   │   └── TicketRepository.java
│   │
│   └── user/                  # Contexto de Usuários e Autenticação
│       ├── auth/              # Lógica e filtros JWT
│       ├── dto/
│       ├── enums/
│       ├── exception/
│       ├── User.java
│       ├── UserRepository.java
│       └── UserService.java
│
├── src/main/resources/
│   ├── db/migration/          # Scripts SQL do Flyway
│   └── application.yml        # Configurações do Spring
│
├── src/test/                  # Testes unitários e integração
├── docker-compose.yaml        # Serviços de infraestrutura
├── .env                       # Variáveis de ambiente configuradas
└── pom.xml                    # Dependências do projeto (Maven)
~~~

---

## Variáveis de Ambiente

Configure as variáveis abaixo no seu arquivo `.env`:

| Variável           | Descrição                                                               |
| ------------------ | ----------------------------------------------------------------------- |
| `POSTGRES_USER`    | Nome de usuário do banco (usado pelo Docker e na string de conexão)     |
| `POSTGRES_PASSWORD`| Senha do banco de dados (usada pelo Docker e na string de conexão)      |
| `POSTGRES_DB`      | Nome do banco PostgreSQL (usado pelo Docker e na string de conexão)     |
| `POSTGRES_PORT`    | Porta de exposição do banco (Padrão: 5432)                              |
| `REDIS_PASSWORD`   | Senha para conexão com o cache Redis                                    |
| `REDIS_PORT`       | Porta de exposição do Redis (Padrão: 6379)                              |
| `JWT_SECRET`       | Chave secreta longa para assinatura HMAC dos tokens JWT                 |

---

<div align="center">

Desenvolvido por Gabriel. Focado em alta performance, resiliência e boas práticas Spring Boot.

</div>
