# 📰 DevNews

Aplicativo Android em **Kotlin + Jetpack Compose** para autenticação de usuários, listagem de notícias, favoritos em cache local e histórico de notificações.

![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple)
![Compose](https://img.shields.io/badge/Jetpack-Compose-green)
![Firebase](https://img.shields.io/badge/Firebase-Enabled-orange)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20Architecture-blue)
## ✨ Visão geral

O **DevNews** permite que usuários se autentiquem, explorem notícias por paginação e filtros, abram detalhes com WebView e salvem notícias favoritas localmente com **Room**, separando os dados por usuário logado (UUID do Firebase Auth).

---

## 🧱 Tecnologias utilizadas

| Categoria | Tecnologias |
|---|---|
| Linguagem | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Arquitetura | MVVM (ViewModel + StateFlow), organização por camadas (`feature`, `domain`, `data`) |
| Injeção de dependência | Hilt |
| Rede | Retrofit + Gson |
| Persistência local | Room |
| Assíncrono/Reatividade | Kotlin Coroutines + Flow |
| Imagens | Coil |
| Firebase | Auth, Analytics, Crashlytics, Messaging |
| Build | Gradle Kotlin DSL, Product Flavors (`dev`, `hml`, `prod`) |

---

## 🚀 Funcionalidades

| Funcionalidade | Descrição |
|---|---|
| Autenticação | Login, cadastro, recuperação de senha e logout via Firebase Auth |
| Sessão | Controle de sessão e fluxo de entrada por estado autenticado |
| Feed de notícias | Lista paginada com carregamento progressivo |
| Filtros e busca | Busca textual e filtros por categoria/autor |
| Detalhe da notícia | Tela de detalhe com botão para abrir notícia completa |
| WebView | Abertura da notícia completa sem sair do app |
| Favoritos | Persistência em Room por usuário (`userId + articleUrl`) |
| Ícone de favorito | Coração nos cards (estado visual sincronizado com Room) |
| Aba Favoritos | Lista somente favoritos do usuário logado |
| Notificações locais | Disparo de notificação ao favoritar notícia |
| Histórico de notificações | Tela dedicada no app |
| Navegação inferior | Bottom Nav fixa no fluxo autenticado |

---

## 📂 Estrutura do Projeto (resumo)

```text
app/src/main/java/com/avanade/devnews
│
├── core
│   └── notifications
│
├── data
│   ├── local
│   │   ├── dao
│   │   ├── database
│   │   └── entity
│   │
│   ├── mapper
│   │
│   ├── remote
│   │   ├── api
│   │   └── dto
│   │
│   └── repository
│
├── di
│
├── domain
│   ├── model
│   ├── repository
│   └── usecase
│
├── feature
│   ├── auth
│   ├── favorites
│   ├── news
│   ├── notifications
│   └── profile
│
└── ui
    ├── designsystem
    ├── login
    └── cadastro
```

### Organização dos Pacotes

| Pacote | Responsabilidade |
|---------|-----------------|
| `core` | Funcionalidades compartilhadas e infraestrutura da aplicação |
| `data` | Implementações de repositórios, fontes de dados remotas e locais |
| `domain` | Regras de negócio, modelos e casos de uso |
| `feature` | Módulos funcionais da aplicação |
| `ui` | Componentes visuais, telas e design system |
| `di` | Configuração de Injeção de Dependência com Hilt |



---

## 🧠 Fluxo de favoritos (Room)

1. Usuário clica no coração do card.
2. App identifica o usuário logado (`FirebaseAuth.currentUser.uid`).
3. Salva/remove no Room usando chave composta `userId + articleUrl`.
4. DAO emite atualização com `Flow`.
5. ViewModel atualiza estado da tela.
6. UI renderiza coração preenchido e aba Favoritos atualizada automaticamente.

---

## 🔔 Notificações

- Há suporte a canal de notificação e permissão para Android 13+.
- Ao favoritar uma notícia, o app envia uma notificação local usando `PushNotificationManager`.

---

## ⚙️ Pré-requisitos

- Android Studio atualizado
- JDK 11
- SDK Android conforme projeto
- Conta/configuração Firebase
- Chave da News API

---

## ▶️ Como rodar

1. Clone o repositório.
2. Configure o arquivo `local.properties` e insira `newsApiKey` com a chave obtida na [NewsAPI](https://newsapi.org/).
3. Sincronize o Gradle.
4. Execute o app por um flavor (ex.: `prodDebug`).

---

## 🧪 Build variants / Flavors

| Flavor | Uso |
|---|---|
| `dev` | Ambiente de desenvolvimento |
| `hml` | Homologação |
| `prod` | Produção |

---

## 📌 Pontos importantes

- Favoritos ficam em cache local e separados por usuário.
- Navegação mantém contexto de origem (ex.: voltar do detalhe para Favoritos quando veio de Favoritos).
- Estado de telas principais foi ajustado para melhor comportamento em rotação.

---

## 👥 Autores

| Nome | GitHub |
|-------|--------|
| Rafael Fratini | [@Rfratini](https://github.com/Rfratini) |
| Leonardo Monteiro | [@LeoMont200](https://github.com/LeoMont200) |
---

Projeto desenvolvido para estudos e aplicação prática de tecnologias modernas do ecossistema Android, com foco em Jetpack Compose, Clean Architecture, MVVM, Room, Firebase, Hilt, Retrofit e boas práticas de desenvolvimento mobile.
