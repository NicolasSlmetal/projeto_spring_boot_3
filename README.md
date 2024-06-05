# slgames-api

API RESTFul de uma loja de jogos desenvolvida em Java utilizando Spring Boot 3, Lombok, MySQL e Swagger para documentação.

# Como executar com o Docker
# Requisitos: 
 - JDK 21 instalada;
 - Maven instalado;
 - Docker e docker-compose instalado.
## 1 - Executando mvn clean install
Abra o terminal na raíz do projeto e execute o comando: <br>
`mvn clean install` <br>
Aguarde o termíno do comando e ele gerará um arquivo `.jar` na pasta `target`.
## 2 - Criar container docker
No terminal, execute o comando: <br>
`docker-compose up --build` <br>
Pronto. O container com a aplicação e o banco de dados MySQL estará criado.
# Documentação da API
Ao executar a API, seja pelo docker ou não, é possível acessar a documentação pela URL `localhost:8080/swagger-ui/index.html`

