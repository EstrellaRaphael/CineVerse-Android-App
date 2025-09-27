# Camada de Dados - Consumindo a API

## Objetivo

Nesta etapa, o foco foi construir o "braço" do aplicativo que busca informações no mundo
externo. O objetivo foi configurar e implementar toda a lógica de comunicação com a API do TMDb,
tratando desde a autenticação segura até o recebimento e parseamento dos dados brutos. Esta camada é
a ponte entre o app e a fonte de dados remota.

---

### 1. Obtenção e Armazenamento Seguro da API Key

* **O que foi feito?**
  Foi obtida uma chave de API (v3 auth) junto ao TMDb e configurada no projeto de uma maneira
  segura, sem expô-la diretamente no código-fonte.

* **Como foi feito?**
    1. A chave foi adicionada ao arquivo `local.properties`, que é um arquivo não versionado pelo
       Git (geralmente incluído no `.gitignore`).
       ```properties
       TMDB_API_KEY="CHAVE_API"
       ```
    2. No arquivo `build.gradle.kts`, foi adicionada uma instrução para ler essa chave e torná-la
       disponível no código através da classe `BuildConfig`.
       ```kotlin
       buildConfigField("String", "TMDB_API_KEY", project.properties["TMDB_API_KEY"].toString())
       ```

* **Por que foi feito?**
  Esta é uma prática de segurança fundamental. Colocar chaves, senhas ou tokens diretamente no
  código é um risco de segurança enorme, pois qualquer pessoa com acesso ao repositório (mesmo que
  público no GitHub) poderia vê-los. Ao usar o `local.properties`, garantimos que as credenciais
  fiquem apenas no ambiente local do desenvolvedor. O `BuildConfig` é a forma padrão e segura de
  acessar essas variáveis de configuração de forma type-safe dentro do aplicativo.

---

### 2. Criação dos DTOs (Data Transfer Objects)

* **O que foi feito?**
  Foram criadas as classes `MovieDto` e `MovieListResponseDto` no pacote `data.remote.dto`.

* **Como foi feito?**
  Utilizando `data class` do Kotlin, os modelos foram criados para espelhar exatamente a estrutura
  do JSON retornado pela API. A anotação `@SerializedName` foi usada para mapear os nomes dos
  campos.
    ```kotlin
    data class MovieDto(
        @SerializedName("id")
        val id: Int,
        @SerializedName("poster_path")
        val posterPath: String?,
        // ...
    )
    ```

* **Por que foi feito?**
  DTOs são um pilar da Clean Architecture. A responsabilidade deles é única e exclusiva: *
  *representar o dado como ele vem da fonte externa**. Isso desacopla nosso aplicativo da API. Se a
  API mudar um nome de campo no futuro, só precisamos ajustar a anotação `@SerializedName` no DTO,
  sem quebrar outras partes do código. Essa separação é crucial para que a camada de domínio
  trabalhe com modelos de negócio limpos e independentes de como a API estrutura
  suas respostas.

---

### 3. Definição da Interface da API com Retrofit

* **O que foi feito?**
  Foi criada a interface `TmdbApi.kt`, que define os endpoints que o aplicativo irá consumir.

* **Como foi feito?**
  A interface foi anotada com as anotações do Retrofit (`@GET`, `@Query`) e suas funções foram
  marcadas como `suspend` para integração com Coroutines.
    ```kotlin
    interface TmdbApi {
        @GET("movie/popular")
        suspend fun getPopularMovies(
            @Query("page") page: Int = 1,
            @Query("language") language: String = "pt-BR"
        ): MovieListResponseDto
    }
    ```

* **Por que foi feito?**
  O Retrofit usa essa interface para gerar em tempo de execução uma implementação concreta que
  realiza as chamadas de rede. Isso é um exemplo de código declarativo: dizemos **o que**
  queremos fazer (buscar filmes populares), e o Retrofit se preocupa em **como** fazer. O uso de
  `suspend` é a forma moderna no Android de lidar com operações de longa duração sem bloquear a
  thread principal, garantindo que a UI do app continue sempre responsiva.

---

### 4. Implementação do Cliente Retrofit

* **O que foi feito?**
  Foi criado um objeto singleton `RetrofitClient` para construir e configurar a instância do
  Retrofit que será usada em todo o aplicativo.

* **Como foi feito?**
  O objeto configura um `OkHttpClient` com dois interceptors (um para logs e outro para
  autenticação) e o utiliza para construir a instância do Retrofit, que por sua vez implementa a
  interface `TmdbApi`.
    ```kotlin
    private val authInterceptor = Interceptor { chain ->
        val newUrl = chain.request().url.newBuilder()
            .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
            .build()
        // ...
    }
    ```

* **Por que foi feito?**
    * **Centralização:** Ter um único objeto para configurar o cliente de rede garante consistência
      e reaproveitamento de recursos.
    * **Interceptor de Autenticação:** Esta é a forma mais limpa e robusta de lidar com a
      autenticação. Ao invés de adicionar a API Key como parâmetro em cada função da interface
      `TmdbApi`, o interceptor faz isso automaticamente para **todas** as requisições. Isso segue o
      princípio **DRY (Don't Repeat Yourself)** e torna o código mais limpo e menos propenso a
      erros.
    * **Interceptor de Logging:** Essencial para a fase de desenvolvimento, pois nos permite
      inspecionar no Logcat as requisições enviadas e as respostas recebidas, facilitando a
      depuração de problemas de comunicação com a API.

---

## Conclusão da Etapa

Com a camada de dados configurada, o aplicativo agora é capaz de se conectar à internet, fazer
uma requisição autenticada à API do TMDb e converter a resposta JSON em objetos Kotlin. Temos os "
tijolos" de informação, mas eles ainda estão no formato "bruto" da API. O próximo passo será
construir a camada de domínio para transformar esses tijolos em modelos de negócio úteis para o
nosso aplicativo.
