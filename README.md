# app-crud — Cadastro de produtos em Java

Aplicação de console para **cadastrar, consultar, atualizar e excluir produtos**, com persistência em MySQL por meio de JDBC. O projeto permite estudar orientação a objetos, organização de responsabilidades e acesso a um banco relacional.

O usuário interage com um menu no terminal. A aplicação Java é executada na máquina local; o Docker Compose fornece o MySQL e o phpMyAdmin para administrar os dados pelo navegador.

## Sumário

- [Funcionalidades](#funcionalidades)
- [Tecnologias](#tecnologias)
- [Estrutura e arquitetura](#estrutura-e-arquitetura)
- [Regras e comportamento atual](#regras-e-comportamento-atual)
- [Banco de dados](#banco-de-dados)
- [Como executar](#como-executar)
- [Exemplo de utilização](#exemplo-de-utilização)
- [Ambiente Docker](#ambiente-docker)
- [Problemas comuns](#problemas-comuns)
- [Limitações e evolução](#limitações-e-evolução)
- [Imagens do projeto](#imagens-do-projeto)
- [Fontes e referências](#fontes-e-referências)

## Funcionalidades

CRUD corresponde às operações de criação, leitura, atualização e exclusão de registros.

| Opção | Funcionalidade | Entrada | Comportamento |
| --- | --- | --- | --- |
| `1` | Cadastrar produto | Nome, preço e quantidade | Insere um produto; o banco gera o ID e a data de cadastro. |
| `2` | Atualizar produto | ID, nome, preço e quantidade | Substitui os três campos do registro identificado pelo ID. |
| `3` | Excluir produto | ID | Remove fisicamente o registro do banco. |
| `4` | Consultar produtos | Nenhuma entrada adicional | Lista os produtos em ordem crescente de ID. |

Depois de cada operação, o programa pergunta `DESEJA FAZER OUTRA OPERAÇÃO? (S/N):`. Digitar `S` ou `s` abre o menu novamente; qualquer outra resposta encerra a execução. Atualmente, essa repetição usa uma chamada recursiva ao método `main`.

## Tecnologias

As versões abaixo são as declaradas nos arquivos do projeto.

| Tecnologia | Versão/configuração | Papel |
| --- | --- | --- |
| Java | Source e target `21` | Linguagem e execução da aplicação. |
| Maven | `cotiinformatica:app-crud:1.0-SNAPSHOT` | Compilação e resolução de dependências. |
| JDBC | API `java.sql` | Conexões, comandos SQL e leitura de resultados. |
| MySQL Connector/J | `26.7.0` no `pom.xml` | Driver JDBC para acesso ao MySQL. |
| MySQL | Imagem `mysql:8.4` | Armazenamento dos produtos. |
| phpMyAdmin | Imagem `phpmyadmin:5.2` | Administração do banco pelo navegador. |
| Docker Compose | Projeto `app-crud` | Serviços, rede, volumes e health checks. |

O ponto de entrada é `cotiinformatica.Main`. A interface de cadastro é o terminal. O phpMyAdmin é uma ferramenta de administração do banco. O projeto não inclui API HTTP, interface web de produtos ou framework ORM.

## Estrutura e arquitetura

| Caminho | Responsabilidade |
| --- | --- |
| [pom.xml](pom.xml) | Define o projeto Maven, a versão Java e a dependência do driver. |
| [docker-compose.yml](docker-compose.yml) | Configura serviços, portas, rede e persistência. |
| [Main.java](src/main/java/cotiinformatica/Main.java) | Exibe o menu e direciona a operação escolhida. |
| [entities/Produto.java](src/main/java/cotiinformatica/entities/Produto.java) | Representa os dados do produto com atributos privados, getters e setters. |
| [services/ProdutoService.java](src/main/java/cotiinformatica/services/ProdutoService.java) | Lê entradas, converte valores, chama o repositório e apresenta resultados ou erros. |
| [repositories/ProdutoRepository.java](src/main/java/cotiinformatica/repositories/ProdutoRepository.java) | Executa SQL e transforma os resultados da consulta em objetos `Produto`. |
| [factories/ConnectionFactory.java](src/main/java/cotiinformatica/factories/ConnectionFactory.java) | Centraliza a abertura de conexões JDBC. |
| [resources/scripts/schema.sql](src/main/resources/scripts/schema.sql) | Define a tabela `produtos`. |

### Fluxo de uma operação

1. `Main` recebe a opção do menu e chama um método de `ProdutoService`.
2. `ProdutoService` solicita os dados e prepara o objeto ou ID necessário.
3. `ProdutoRepository` solicita uma conexão a `ConnectionFactory` e executa o SQL.
4. O resultado retorna ao serviço, que apresenta a mensagem ou os produtos no terminal.

Essa organização separa a representação dos dados, o acesso ao banco e a interação com o usuário. Na implementação atual, `ProdutoService` também concentra a entrada e a saída do console; não existe uma camada separada de apresentação nem uma interface para o repositório.

### Métodos do repositório

| Método | Operação SQL | Retorno |
| --- | --- | --- |
| `inserir(Produto)` | `INSERT INTO produtos(nome, preco, quantidade) VALUES (?, ?, ?)` | `void`; o ID gerado não é recuperado pelo método. |
| `atualizar(Produto)` | `UPDATE produtos SET nome=?, preco=?, quantidade=? WHERE id=?` | `boolean`, conforme a quantidade de linhas afetadas. |
| `excluir(Integer)` | `DELETE FROM produtos WHERE id=?` | `boolean`, conforme a quantidade de linhas afetadas. |
| `consultar()` | `SELECT * FROM produtos ORDER BY id` | `List<Produto>`. |

Os comandos de escrita usam `PreparedStatement`, com valores vinculados pelos métodos `setString`, `setDouble` e `setInt`. A consulta percorre um `ResultSet` para montar a lista.

Cada operação abre uma conexão própria e utiliza `try-with-resources` para fechá-la. As exceções são propagadas pelo repositório e capturadas no serviço, que imprime `ERRO:` seguido da mensagem recebida.

## Regras e comportamento atual

Esta seção descreve o que o código e o esquema SQL efetivamente implementam. Regras adicionais estão indicadas como possibilidades de evolução ao final.

| Assunto | Regra ou comportamento implementado |
| --- | --- |
| Identificação | O ID é uma chave primária inteira gerada com `AUTO_INCREMENT`. Não é solicitado no cadastro. |
| Nome | A coluna aceita até 150 caracteres e não aceita `NULL`. Não há validação Java para nome vazio, espaços em branco ou tamanho máximo. |
| Preço | A entrada é convertida com `Double.parseDouble`. Utilize ponto em valores decimais usuais, como `49.90`, sem `R$`. |
| Quantidade | A entrada é convertida com `Integer.parseInt`; valores fracionários, como `2.5`, causam erro de conversão. |
| Valores negativos | Não há validação na aplicação nem restrição `CHECK` no esquema que impeça preço ou quantidade negativos. |
| Duplicidade | Não há restrição de unicidade para o nome; produtos com o mesmo nome podem existir. |
| Atualização | Solicita novamente nome, preço e quantidade. Não há atualização parcial. ID e data de cadastro não são alterados pelo SQL. |
| Exclusão | É definitiva, pelo ID, sem confirmação adicional nem exclusão lógica. |
| Consulta | Lista todos os produtos, sem filtros ou paginação, mostrando ID, nome, preço e quantidade. Uma lista vazia não gera mensagem específica. |
| Data de cadastro | É gerada pelo banco. Embora exista na entidade, não é carregada pelo repositório na consulta nem exibida no terminal. |
| Opção do menu | Um número fora de `1` a `4` gera `OPÇÃO INVÁLIDA!`. Texto não numérico causa exceção antes de entrar nos métodos do serviço. |

Atualização e exclusão exibem sucesso quando `executeUpdate() > 0`; caso contrário, exibem `NENHUM PRODUTO ENCONTRADO.`. Erros de conversão ou acesso ao banco nas operações são apresentados pelo serviço.

`NOT NULL` impede um valor nulo no banco, mas não equivale a validar um texto em branco ou um número positivo.

## Banco de dados

### Configuração de acesso

| Item | Valor configurado |
| --- | --- |
| Banco | `app_crud` |
| Usuário da aplicação | `coti` |
| Senha da aplicação | `coti` |
| Senha do usuário `root` | `root` |
| Endereço para a aplicação local | `localhost:3307` |
| URL JDBC | `jdbc:mysql://localhost:3307/app_crud` |
| Endereço usado pelo phpMyAdmin na rede Docker | `mysql:3306` |
| phpMyAdmin | [http://localhost:5052](http://localhost:5052) |

As credenciais estão definidas no Compose e diretamente em `ConnectionFactory.java`. São configurações simples para desenvolvimento local; ao preparar outro ambiente, substitua-as e adapte a conexão.

A porta `3307` da máquina é encaminhada à porta `3306` do container. O phpMyAdmin usa o nome do serviço `mysql` dentro da rede compartilhada.

### Tabela produtos

| Coluna | Tipo SQL | Restrições/padrão | Campo Java |
| --- | --- | --- | --- |
| `id` | `INTEGER` | Chave primária, `AUTO_INCREMENT` | `Integer` |
| `nome` | `VARCHAR(150)` | `NOT NULL` | `String` |
| `preco` | `DOUBLE` | `NOT NULL` | `Double` |
| `quantidade` | `INTEGER` | `NOT NULL` | `Integer` |
| `dataCadastro` | `DATETIME` | `DEFAULT NOW()`; sem `NOT NULL` explícito | `LocalDateTime` |

O esquema contém uma única tabela, sem relacionamentos ou chaves estrangeiras:

```sql
CREATE TABLE produtos (
    id           INTEGER AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(150) NOT NULL,
    preco        DOUBLE NOT NULL,
    quantidade   INTEGER NOT NULL,
    dataCadastro DATETIME DEFAULT NOW()
);
```

O Compose configura a criação do banco `app_crud`, mas **não executa o arquivo schema.sql automaticamente**. A tabela deve ser criada manualmente. O script não contém `IF NOT EXISTS`: executá-lo novamente sobre uma tabela existente gera erro.

## Como executar

### 1. Preparar os requisitos

- JDK 21, conforme o `pom.xml`.
- Maven instalado e disponível no terminal; o repositório não inclui Maven Wrapper.
- Docker Engine com o plugin Docker Compose ou Docker Desktop com Compose.
- Git para clonar o repositório.
- Portas locais `3307` e `5052` disponíveis.

Verifique as ferramentas:

```bash
java -version
mvn -version
docker version
docker compose version
```

Confirme que o Maven está utilizando o JDK 21. Na primeira execução, é necessário acesso aos repositórios de dependências Maven e ao registro de imagens Docker.

### 2. Clonar o projeto

```bash
git clone https://github.com/Safforcks/app-crud.git
cd app-crud
```

Execute os comandos seguintes na raiz do projeto.

### 3. Iniciar o MySQL e o phpMyAdmin

```bash
docker compose up -d
docker compose ps
```

Aguarde os serviços ficarem saudáveis (`healthy`). O phpMyAdmin depende do health check do MySQL antes de iniciar.

### 4. Criar a tabela

Pelo navegador:

1. Abra [http://localhost:5052](http://localhost:5052).
2. Entre com usuário `coti` e senha `coti`.
3. Selecione o banco `app_crud`.
4. Na aba **Importar**, selecione `src/main/resources/scripts/schema.sql` e execute a importação. Como alternativa, cole o conteúdo do arquivo na aba **SQL**.

Também é possível abrir o terminal interativo do MySQL:

```bash
docker compose exec mysql mysql -u coti -p app_crud
```

Digite a senha `coti` quando solicitada e execute o `CREATE TABLE` apresentado na seção de banco de dados. Para conferir a estrutura e sair:

```sql
SHOW TABLES;
DESCRIBE produtos;
EXIT;
```

### 5. Compilar e iniciar a aplicação

**Pela IDE:** abra o projeto como Maven, selecione o JDK 21, aguarde a resolução das dependências e execute `src/main/java/cotiinformatica/Main.java`.

**Pelo terminal com Maven:**

```bash
mvn clean compile
mvn org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass=cotiinformatica.Main
```

O segundo comando invoca explicitamente o Exec Maven Plugin, sem exigir alteração no `pom.xml`, e inicia a classe principal com as dependências do projeto. Utilize um terminal interativo para responder às perguntas.

O Compose inicia os dois serviços de infraestrutura. A aplicação Java deve ser executada separadamente e, com a configuração atual, conectar-se a partir da máquina local.

## Exemplo de utilização

Ao iniciar, o programa apresenta:

```text
MENU DE OPÇÕES:

(1) CADASTRAR PRODUTO
(2) ATUALIZAR PRODUTO
(3) EXCLUIR PRODUTO
(4) CONSULTAR PRODUTOS

INFORME A OPÇÃO DESEJADA:
```

Para verificar o fluxo completo manualmente:

1. Escolha `1` e cadastre `Teclado USB`, com preço `89.90` e quantidade `10`.
2. Responda `S` e escolha `4` para consultar os dados e obter o ID gerado.
3. Responda `S`, escolha `2`, informe esse ID e preencha novamente os campos, por exemplo `Teclado USB`, `79.90` e `8`.
4. Consulte novamente para conferir a alteração.
5. Para remover o produto de teste, escolha `3` e informe o mesmo ID. A remoção é definitiva.
6. Responda `N` para encerrar.

O preço é exibido pela conversão padrão de `Double`, sem formatação monetária brasileira. Consulte o ID em vez de presumir um valor, pois ele depende do histórico do banco.

## Ambiente Docker

| Recurso | Configuração e finalidade |
| --- | --- |
| Serviço `mysql` | Container `app-crud-mysql`, imagem `mysql:8.4`, porta `3307:3306`. |
| Serviço `phpmyadmin` | Container `app-crud-phpmyadmin`, imagem `phpmyadmin:5.2`, porta `5052:80`. |
| Volume `mysql_data` | Armazena os arquivos do MySQL em `/var/lib/mysql`. |
| Volume `phpmyadmin_sessions` | Preserva as sessões do phpMyAdmin em `/sessions`. |
| Rede `app-crud-network` | Rede `bridge` compartilhada pelos serviços. |
| Reinício | Ambos usam a política `unless-stopped`. |

Os nomes de volumes e rede acima são as chaves declaradas no Compose; os nomes efetivos dos recursos podem receber o prefixo do projeto `app-crud`.

O health check do MySQL executa `SELECT 1` com o usuário e banco configurados. Ele verifica acesso ao banco, mas não garante que a tabela `produtos` exista. O health check do phpMyAdmin verifica se é possível obter uma resposta HTTP local.

Comandos úteis:

```bash
# Consultar os serviços
docker compose ps

# Acompanhar os logs do banco
docker compose logs -f mysql

# Remover os containers, preservando os volumes
docker compose down

# Iniciar novamente o ambiente
docker compose up -d
```

**Atenção:** `docker compose down -v` também remove os volumes e apaga os dados persistidos do ambiente, incluindo os produtos cadastrados. Utilize apenas quando quiser descartar o banco local.

## Problemas comuns

| Sintoma | O que verificar |
| --- | --- |
| Falha de conexão ou `Communications link failure` | Execute `docker compose ps`, aguarde o MySQL ficar saudável e confira a porta `3307` em `ConnectionFactory.java`. |
| `Access denied` | Confira usuário e senha. Alterar variáveis do Compose não atualiza automaticamente usuários de um banco já inicializado em volume existente. |
| `Unknown database 'app_crud'` | Verifique a instância utilizada e a existência do banco, especialmente ao reutilizar um volume antigo. |
| Tabela `produtos` inexistente | Importe `schema.sql` dentro de `app_crud`; o Compose não importa esse script. |
| Tabela já existe ao importar | Confira a estrutura com `DESCRIBE produtos`; o script atual não é idempotente. |
| Erro ao digitar preço | Use `89.90`, sem vírgula ou símbolo de moeda. |
| Erro ao digitar ID ou quantidade | Informe um número inteiro dentro do intervalo aceito por `Integer`. |
| `NENHUM PRODUTO ENCONTRADO.` | Confira o ID pela consulta. A mensagem aparece quando a atualização ou exclusão retorna zero linhas afetadas. |
| Porta ocupada | Libere `3307` ou `5052`. Se alterar a porta publicada do MySQL, ajuste também a URL JDBC. |
| `invalid target release: 21` | Configure o JDK 21 no terminal/Maven e na IDE. |
| Maven não encontra o driver | Verifique acesso ao repositório de dependências e a disponibilidade da versão `26.7.0` declarada no `pom.xml`. |
| `No suitable driver` | Execute com o Connector/J no classpath, pela IDE com Maven ou pelo comando de execução indicado. |

## Limitações e evolução

O escopo atual é um CRUD de produtos pelo terminal. Os itens abaixo são melhorias possíveis e **não representam funcionalidades implementadas**:

- Validar nome obrigatório, tamanho do nome, preço e quantidade antes de acessar o banco.
- Definir regras explícitas para valores negativos, preço zero e nomes duplicados.
- Utilizar `BigDecimal` em Java e `DECIMAL` no MySQL para valores monetários que exijam precisão decimal.
- Carregar e exibir `dataCadastro` nas consultas.
- Separar a interação de console das regras de negócio e adotar interfaces para desacoplar o repositório.
- Substituir a recursão do menu por um laço e tratar entradas não numéricas já na escolha da opção.
- Adicionar confirmação de exclusão, busca por ID ou nome e paginação.
- Externalizar credenciais, adicionar gerenciamento de conexões e refinar o tratamento de exceções.
- Automatizar a criação e a evolução do esquema com scripts de inicialização ou migrações.
- Adicionar testes automatizados, integração contínua e configuração reproduzível dos plugins Maven.

O repositório ainda não contém testes automatizados. Para verificar o comportamento manualmente, siga o exemplo de utilização e confira os registros pelo phpMyAdmin.

## Imagens do projeto

Imagens preservadas da documentação original:

### Entidade Produto

<img width="396" height="318" alt="Produto" src="https://github.com/user-attachments/assets/c4aefe47-a44d-44dc-9b3a-5ced131b8665" />

### Conexão com o banco

<img width="280" height="300" alt="Connection" src="https://github.com/user-attachments/assets/615cc3af-8733-4fc9-a862-05e8e02f0440" />

### Repositório de produtos

<img width="603" height="303" alt="Repository" src="https://github.com/user-attachments/assets/b0e31e1a-d35e-4cf5-95c1-ff9f93785dea" />

## Fontes e referências

As funcionalidades e configurações documentadas foram extraídas do próprio código: [Main](src/main/java/cotiinformatica/Main.java), [Produto](src/main/java/cotiinformatica/entities/Produto.java), [ProdutoService](src/main/java/cotiinformatica/services/ProdutoService.java), [ProdutoRepository](src/main/java/cotiinformatica/repositories/ProdutoRepository.java), [ConnectionFactory](src/main/java/cotiinformatica/factories/ConnectionFactory.java), [schema.sql](src/main/resources/scripts/schema.sql), [pom.xml](pom.xml) e [docker-compose.yml](docker-compose.yml).

Documentação oficial para aprofundamento:

- [Java 21 — API JDBC](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/package-summary.html)
- [Apache Maven — Introdução](https://maven.apache.org/guides/getting-started/)
- [MojoHaus — Exec Maven Plugin](https://www.mojohaus.org/exec-maven-plugin/)
- [MySQL — Connector/J](https://dev.mysql.com/doc/connector-j/en/)
- [MySQL 8.4 — CREATE TABLE](https://dev.mysql.com/doc/refman/8.4/en/create-table.html)
- [Docker — Referência do Compose](https://docs.docker.com/reference/compose-file/)
- [Docker — Imagem oficial do MySQL](https://hub.docker.com/_/mysql)
- [phpMyAdmin — Documentação](https://docs.phpmyadmin.net/en/latest/)
