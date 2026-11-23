# Brasa PDV

Sistema web para pequenos negócios acompanharem o que entra e o que sai, dia a dia.
Foi desenvolvido como atividade extensionista de Engenharia de Software.

## Funcionalidades

- Cadastro e login de usuários
- Lançamentos de entradas e saídas, com categoria e fornecedor
- Saldo do período e gráfico "Para onde vai o dinheiro"
- Cadastro de categorias e fornecedores
- Dicas de economia geradas por IA a partir dos totais do período

## Tecnologias

- Java e Spring Boot
- Maven
- PostgreSQL (via Docker)
- Front-end em HTML, servido pelo próprio Spring

## Pré-requisitos

- Java (JDK)
- Maven
- Docker e Docker Compose

## Como rodar

1. Suba o banco de dados:

   ```bash
   docker compose up -d
   ```

2. (Opcional) Configure a chave da IA para receber a análise completa.
   Copie o arquivo de exemplo e preencha a sua chave:

   ```bash
   cp segredos.properties.example segredos.properties
   ```

   Sem a chave, o sistema funciona normalmente e mostra dicas básicas.

3. Inicie a aplicação:

   ```bash
   mvn spring-boot:run
   ```

4. Acesse http://localhost:8080 e crie sua conta.

## Configuração

As configurações ficam em `src/main/resources/application.properties`.
A chave da IA é lida da variável `ANTHROPIC_API_KEY`, definida no
`segredos.properties` (que não é versionado) ou no ambiente.

## Autora

Leticia Caroline Rodrigues De Arruda Lima
