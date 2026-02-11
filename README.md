# 🎟️ TicketFlow API

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green?style=for-the-badge&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=for-the-badge&logo=docker)

Uma API robusta de venda de ingressos projetada para lidar com **alta concorrência** e garantir a integridade de dados em cenários de "Race Condition".

Este projeto simula um desafio real de engenharia de software: impedir que o mesmo assento seja vendido para múltiplos usuários simultâneos durante picos de acesso (ex: Black Friday ou estreia de shows).

---

## 🚀 Tecnologias & Arquitetura

O projeto foi construído seguindo as melhores práticas de mercado para 2025/2026:

* **Java 21 (LTS):** Utilizando recursos modernos da linguagem (Records, Virtual Threads ready).
* **Spring Boot 3:** Framework base para desenvolvimento ágil.
* **PostgreSQL:** Banco de dados relacional robusto.
* **Flyway:** Versionamento de banco de dados (Migrations) para garantir consistência entre ambientes de Dev, Test e Prod.
* **Docker & Docker Compose:** Containerização do ambiente de desenvolvimento e banco de dados.
* **Testcontainers:** Testes de integração reais subindo containers do banco de dados (sem mocks para a camada de persistência).
* **RFC 7807 (Problem Details):** Padronização de respostas de erro da API.

---

## 🧠 Destaques Técnicos (The "Why")

### 1. Controle de Concorrência (Pessimistic Locking)
O maior desafio de sistemas de ingressos é o **Overselling** (vender mais do que a capacidade).
* **Solução:** Implementação de `PESSIMISTIC_WRITE` (Select for Update) no repositório JPA.
* **Resultado:** Quando uma transação de compra inicia, a linha do evento no banco de dados é **travada**. Outras requisições simultâneas aguardam na fila do banco até a liberação, garantindo atomicidade e consistência estrita do estoque.

### 2. Domain-Driven Design (DDD) - Rich Model
* Evitamos o anti-pattern de "Entidades Anêmicas".
* A entidade `Event` possui **regras de negócio encapsuladas** e protege seu próprio estado (ex: não permite estoque negativo através de métodos de negócio).
* Uso de **Imutabilidade** e **Builders** para construção segura de objetos.

### 3. Testes de Integração com Testcontainers
* Não confiamos apenas em Mocks.
* Os testes sobem um container Docker do PostgreSQL real para validar se o Lock Pessimista e as constraints do banco estão funcionando sob estresse.

---

## 🛠️ Pré-requisitos
* Java 21 JDK
* Docker & Docker Compose
* Maven Wrapper (incluso no projeto)
