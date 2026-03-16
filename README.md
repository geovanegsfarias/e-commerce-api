# API de E-Commerce
![Java](https://img.shields.io/badge/Java-21-E67E22)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.11-85EA2D)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791)
![Flyway](https://img.shields.io/badge/Flyway-11.7-1A6E8A)
![Swagger](https://img.shields.io/badge/Swagger-3-27AE60)
![Docker](https://img.shields.io/badge/Docker-On-2980B9)

---

### Descrição

API REST para gerenciamento de e-commerce desenvolvida com Spring Boot. O projeto oferece autenticação stateless atráves de tokens JWT, controle de acesso a rotas baseado em cargos e integração com Stripe Checkout para processar o pagamento de pedidos.

---

### Tecnologias

- Java 21, Spring Boot 3.5
- Spring Security, JWT
- PostgreSQL, Flyway (Migrations)
- JUnit 5 e Mockito (Testes)
- Springdoc OpenAPI (Swagger)
- Docker

---

### Funcionalidades

- Criação, busca, atualização e remoção de produtos e categorias.
- Gerenciamento de carrinho de compras e pedidos.
- Autenticação e autorização com JWT e cargos (user e admin).
- Integração com Stripe Checkout e webhook para confirmação de pagamento.
- Validação de dados e paginação.
- Documentação completa via Swagger.

---

### Instalação
1. Pré-requisitos
<ul>

- Docker

</ul>

2. Clone o repositório
<ul>

```bash
git clone https://github.com/geovanegsfarias/e-commerce-api.git
cd e-commerce-api
```

</ul>

3. Gere as chaves RSA
<ul>

```bash
openssl genrsa -out src/main/resources/app.key 2048
openssl rsa -in src/main/resources/app.key -pubout -out src/main/resources/app.pub
```

</ul>


4. Configure as variáveis de ambiente
<ul>
Crie um arquivo <code>.env</code> na raiz do projeto e preencha com suas credenciais:

```
POSTGRES_DB=e_commerce_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

STRIPE_API_KEY=
STRIPE_PUBLIC_KEY=
STRIPE_WEBHOOK_SECRET=
```
</ul>

5. Suba a aplicação
<ul>

```bash
docker compose up
```

</ul>

<ul>

- A API está disponível em http://localhost:8080

- Documentação Swagger em http://localhost:8080/swagger-ui.html

</ul>

---

### Endpoints

#### **Autenticação**

- **POST /auth/register:** Registra um novo usuário.
- **POST /auth/login:** Autentica um usuário cadastrado.

#### **Categorias**

- **GET /category:** Retorna uma lista de categorias.
- **GET /category/{id}:** Retorna uma categoria específica.
- **POST /category:** Cria uma nova categoria.
- **PUT /category/{id}:** Atualiza uma categoria.
- **DELETE /category/{id}:** Deleta uma categoria.

#### **Produtos**

- **GET /product:** Retorna uma lista de produtos.
- **GET /product/{id}:** Retorna um produto específico.
- **POST /product:** Cria um novo produto.
- **PUT /product/{id}:** Atualiza um produto.
- **DELETE /product/{id}:** Deleta um produto.

#### **Carrinho**

- **GET /cart:** Retorna um carrinho de compras.
- **POST /cart:** Cria um carrinho de compras.
- **DELETE /cart:** Deleta um carrinho de compras.
- **POST /cart/items:** Adiciona um produto ao carrinho de compras.
- **DELETE /cart/items/{id}:** Remove um produto do carrinho de compras.

#### **Pedidos**

- **GET /order:** Retorna o histórico de pedidos.
- **GET /order/{id}:** Retorna um pedido específico.
- **POST /order:** Cria um novo pedido.
- **DELETE /order/{id}:** Remove um pedido do histórico de pedidos.

#### **Pagamento**
- **POST /checkout:** Retorna dados para realizar o pagamento do pedido.

---