# Configuração do Projeto e Arquitetura

## Objetivo

O objetivo desta etapa inicial foi estabelecer a fundação sólida sobre a qual o aplicativo "
CineVerse" será construído. Isso envolve a criação do projeto no Android Studio, a configuração do
ambiente de desenvolvimento com as bibliotecas necessárias e, mais importante, a definição de uma
estrutura de pacotes que promova um código limpo, organizado e escalável, seguindo os preceitos da *
*Clean Architecture**.

---

### 1. Criação do Projeto no Android Studio

* **O que foi feito?**
  Foi criado um novo projeto Android utilizando o template "Empty Activity" com Jetpack Compose.

* **Como foi feito?**
  Através do assistente de criação de projetos do Android Studio (`File > New > New Project`), foram
  definidas as seguintes configurações:
    * **Template:** Empty Activity (Compose)
    * **Name:** CineVerse
    * **Package name:** `com.estrella.cineverse`
    * **Minimum SDK:** API 24 (Android 7.0)

* **Por que foi feito?**
  O template com Jetpack Compose foi escolhido por ser a stack principal definida nos requisitos,
  permitindo a construção de UI de forma declarativa e moderna em Kotlin. A escolha de um
  `Minimum SDK` como o 24 garante que o aplicativo seja compatível com a grande maioria dos
  dispositivos Android atualmente em uso.

---

### 2. Adição de Dependências

* **O que foi feito?**
  Foram adicionadas ao arquivo `build.gradle.kts` (nível do módulo `:app`) as bibliotecas (
  dependências) essenciais para o funcionamento do aplicativo.

* **Como foi feito?**
  O seguinte bloco de código foi adicionado à seção `dependencies`:
    ```kotlin
    // Retrofit (Networking)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // ViewModel e Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.4")

    // Coil (Image Loading)
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    ```

* **Por que foi feito?**
  Cada dependência tem um papel fundamental e foi escolhida para seguir as melhores práticas do
  mercado:
    * **Retrofit & Gson:** Facilitam a comunicação com a API. O Retrofit transforma a API REST em
      uma interface Kotlin, enquanto o Gson converte automaticamente a resposta JSON em objetos de
      dados (DTOs), abstraindo a complexidade do networking.
    * **Coroutines:** São a ferramenta padrão em Kotlin para gerenciar tarefas assíncronas. Como
      chamadas de rede não podem ser executadas na thread principal da UI, as coroutines nos
      permitem executá-las em background de forma eficiente e com um código legível.
    * **ViewModel & Lifecycle:** São componentes do Android Jetpack essenciais para a arquitetura
      MVVM. O `ViewModel` armazena e gerencia os dados relacionados à UI de forma consciente ao
      ciclo de vida, sobrevivendo a mudanças de configuração (como rotação de tela).
    * **Navigation Compose:** É a biblioteca recomendada para gerenciar a navegação entre as telas (
      Composables) de forma integrada ao Jetpack Compose.
    * **Coil:** É uma biblioteca moderna e eficiente para carregar imagens de URLs (como os pôsteres
      dos filmes) e exibi-las na UI. É otimizada para Kotlin e Compose.
    * **Hilt:** É uma biblioteca para injeção de dependência. Ela nos ajudará a "injetar" (fornecer)
      dependências (como um `Repository` para um `ViewModel`) automaticamente, o que reduz o
      acoplamento entre as classes, simplifica o código e facilita muito a criação de testes
      unitários.

---

### 3. Estrutura de Pacotes (Clean Architecture)

* **O que foi feito?**
  A estrutura de pacotes foi organizada em três camadas principais: `data`, `domain` e
  `presentation`, com seus respectivos subpacotes.

* **Como foi feito?**
  Foram criados os seguintes pacotes dentro do pacote principal do projeto:
    ```
    com.estrella.cineverse
    ├── data
    │   ├── mappers
    │   ├── remote
    │   │   └── dto
    │   └── repository
    ├── domain
    │   ├── model
    │   ├── repository
    │   └── usecase
    └── presentation
        ├── details
        └── movielist
    ```

* **Por que foi feito?**
  A principal razão é implementar a **Clean Architecture**. Esta arquitetura impõe uma regra de
  dependência estrita: as camadas internas (`domain`) não devem conhecer as camadas externas (
  `data`, `presentation`). Isso traz enormes benefícios:
    * **Separação de Responsabilidades (Princípio S do SOLID):** Cada camada tem um propósito único
      e bem definido.
        * `presentation`: Responsável exclusivamente pela UI e lógica de apresentação.
        * `domain`: Contém a lógica de negócio pura, sem depender de frameworks externos. É o
          coração da aplicação.
        * `data`: Gerencia a origem dos dados (API, banco de dados, etc.).
    * **Testabilidade:** A camada `domain` pode ser testada com testes unitários simples e rápidos (
      JVM tests), pois não tem dependências do Android.
    * **Manutenibilidade e Escalabilidade:** A baixa coesão entre as camadas permite que uma camada
      seja alterada sem impactar as outras. Por exemplo, podemos trocar a API (camada `data`) sem
      fazer nenhuma alteração na UI (camada `presentation`). Isso torna o projeto mais fácil de
      manter e expandir no futuro.

---

## Conclusão da Etapa

Com a fundação do projeto estabelecida, temos um ambiente de desenvolvimento robusto e uma
arquitetura escalável. Estamos prontos para começar a construir a funcionalidade do aplicativo,
começando pela camada responsável por buscar as informações: a **camada de dados**.
