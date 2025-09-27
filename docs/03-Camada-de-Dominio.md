# Passo 3: Camada de Domínio - O Coração da Aplicação

## Objetivo

O objetivo desta etapa foi construir o núcleo da aplicação: a camada de Domínio. Esta camada é
a mais importante dentro da Clean Architecture, pois ela contém as regras de negócio e os modelos de
dados essenciais, sendo completamente independente de frameworks externos como o Android, de
detalhes de UI ou da origem dos dados. Ela define **o que** o aplicativo faz, não **como** os dados
são obtidos ou exibidos.

---

### 1. Model de Domínio (`Movie.kt`)

* **O que foi feito?**
  Foi criada a data class `Movie`, que representa um filme dentro do nosso aplicativo.

* **Como foi feito?**
  A classe foi definida no pacote `domain.model` contendo apenas os campos estritamente necessários
  para a aplicação.
    ```kotlin
    data class Movie(
        val id: Int,
        val title: String,
        val overview: String,
        val posterUrl: String, 
        val releaseDate: String,
        val voteAverage: Double
    )
    ```

* **Por que foi feito?**
  Este passo é fundamental para **desacoplar** a lógica de negócio da fonte de dados externa.
    * **Diferença entre Model e DTO:** Enquanto o `MovieDto` (camada de dados) é um espelho da
      resposta da API, com todos os seus campos e convenções de nomenclatura, o `Movie` (camada de
      domínio) é um modelo limpo e adaptado para as necessidades do *aplicativo*. Por exemplo,
      ele já prevê uma `posterUrl` completa, abstraindo da UI a responsabilidade de saber como
      construir essa URL.
    * **Independência:** Se a API mudar no futuro (ex: o campo `title` passa a se chamar `name`),
      apenas o mapeamento na camada de dados precisará ser ajustado. Nosso modelo `Movie` e toda a
      lógica de negócio que o utiliza permanecerão intactos.

---

### 2. Interface do Repositório (`MovieRepository.kt`)

* **O que foi feito?**
  Foi definida a interface `MovieRepository` no pacote `domain.repository`.

* **Como foi feito?**
  A interface estabelece um "contrato" que define os métodos que a camada de domínio precisa para
  acessar os dados dos filmes.
    ```kotlin
    interface MovieRepository {
        suspend fun getPopularMovies(): List<Movie>
    }
    ```

* **Por que foi feito?**
  Esta é a aplicação direta do **Princípio da Inversão de Dependência (a letra 'D' do SOLID)**.
    * **O Contrato:** A camada de domínio não sabe (e não deve saber) de onde os dados vêm. Ela
      apenas define um contrato: "Preciso de um objeto que saiba como me entregar uma lista de
      `Movie`".
    * **Invertendo a Flecha:** Normalmente, as camadas de alto nível (domínio) dependeriam das de
      baixo nível (dados). Aqui, nós invertemos isso. A camada de dados é quem vai depender da
      abstração (a interface) definida pelo domínio. A implementação concreta do `MovieRepository`
      ficará na camada de dados, "obedecendo" ao contrato do domínio.
    * **Testabilidade e Flexibilidade:** Isso torna nosso código extremamente flexível. Para testes,
      podemos criar uma implementação falsa (`FakeMovieRepository`) que retorna dados em memória,
      sem precisar de uma conexão de rede. Se no futuro decidirmos buscar dados de um banco de dados
      local, podemos criar um `LocalMovieRepository` que implementa a mesma interface, sem alterar
      nenhuma linha de código no domínio ou na apresentação.

---

### 3. Use Case (`GetPopularMoviesUseCase.kt`)

* **O que foi feito?**
  Foi criada a classe `GetPopularMoviesUseCase`, que encapsula a lógica para a funcionalidade de
  buscar filmes populares.

* **Como foi feito?**
  A classe recebe o `MovieRepository` como dependência via construtor e possui um único método
  público, `invoke`, que executa a ação.
    ```kotlin
    class GetPopularMoviesUseCase(
        private val movieRepository: MovieRepository
    ) {
        suspend operator fun invoke(): List<Movie> {
            return movieRepository.getPopularMovies()
        }
    }
    ```

* **Por que foi feito?**
    * **Princípio da Responsabilidade Única (a letra 'S' do SOLID):** Cada classe de Use Case tem
      uma, e somente uma, responsabilidade: executar uma regra de negócio específica. Isso mantém
      nosso código altamente organizado. Se tivéssemos uma busca de filmes, criaríamos um
      `SearchMoviesUseCase`.
    * **Orquestração:** Os Use Cases são os orquestradores da camada de domínio. Eles utilizam um ou
      mais repositórios para executar a lógica necessária. Neste caso simples, ele apenas repassa a
      chamada, mas em cenários complexos, um Use Case poderia combinar dados de diferentes
      repositórios, aplicar filtros, etc.
    * **ViewModels Limpos:** Ao encapsular a lógica aqui, nossos `ViewModels` (que criaremos no
      próximo passo) se tornam muito mais simples. A responsabilidade do ViewModel será apenas
      chamar o Use Case apropriado e gerenciar o estado da UI, sem conter lógica de negócio.

---

## Conclusão da Etapa

Com a camada de domínio pronta, temos um núcleo de negócio robusto, testável e totalmente isolado de
detalhes de implementação. Definimos as entidades principais (`Movie`), os contratos de dados (
`MovieRepository`) e as ações que o sistema pode realizar (`GetPopularMoviesUseCase`). Agora,
estamos prontos para a etapa final: conectar essa lógica a uma interface de usuário na **camada de
Apresentação**.