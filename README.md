# 📋 Personal Tasks

Um app Android simples e funcional para gerenciamento de tarefas pessoais, desenvolvido em Kotlin com persistência local via SQLite.

## ✨ Funcionalidades

- 📌 Adicionar novas tarefas com título, descrição e data limite
- 📝 Editar tarefas existentes
- 👀 Visualizar detalhes de uma tarefa
- 🗑️ Remover tarefas com menu de contexto
- 📅 Ordenação automática por data limite
- 💾 Salvamento local usando SQLite
- ✅ Suporte a view binding e parcelable

## 🧱 Arquitetura

O projeto segue uma separação básica de responsabilidades:

- **Model**: `Task`, `TaskDAO`, `TaskSqlite`, `Constant`
- **Controller**: `MainController` — camada de acesso ao DAO
- **View/UI**: `MainActivity`, `TaskAdapter`, `TaskActivity` (não incluída acima mas presumida)
- **Adapter**: `TaskAdapter` para gerenciamento de lista de tarefas com `RecyclerView`

## 📲 Como usar


### Video da execução do aplicativo

https://github.com/user-attachments/assets/bbe29584-9f2d-4f6e-81b2-92ef8221f1ea

##

### Pré-requisitos

- Android Studio (recomendado Arctic Fox ou superior)
- SDK mínimo: API 24 (Android 7.0)
- Kotlin ativado no projeto

### Clonando o repositório

```bash
git clone https://github.com/seu-usuario/personal-tasks.git
cd personal-tasks
``` 

### Rodando o projeto

1. Abra o Android Studio
2. Vá em File > Open e selecione a pasta do projeto
3. Sincronize o Gradle
4. Rode o app em um emulador ou dispositivo físico

### ▶️ Usando o app

- Clique no ícone ➕ da barra superior para adicionar uma nova tarefa
- Segure uma tarefa para abrir o menu de contexto:
  - Visualizar mostra os detalhes
  - Editar permite alterar a tarefa
  - Remover apaga a tarefa
- Se não houver tarefas, um texto "Nenhuma tarefa" será exibido

### 🛠️ Tecnologias

- Kotlin
- SQLite
- View Binding
- RecyclerView
- Parcelize
- AndroidX
