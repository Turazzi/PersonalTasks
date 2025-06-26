# PersonalTasks - Gerenciador de Tarefas Pessoal

## Descrição do Projeto

**PersonalTasks** é um aplicativo Android nativo, desenvolvido em Kotlin, que funciona como um assistente pessoal para o gerenciamento de tarefas do dia a dia. O app permite que os usuários criem, editem, concluam e organizem suas atividades de forma simples e intuitiva, com todos os dados sincronizados em tempo real com a nuvem através do Firebase.

Além disso, o aplicativo conta com um robusto suporte offline, garantindo que o usuário possa continuar a gerenciar suas tarefas mesmo sem conexão com a internet.

## Funcionalidades Implementadas

* **Autenticação de Usuários:** Sistema seguro de Login e Registro utilizando o Firebase Authentication.
* **CRUD de Tarefas:** Funcionalidade completa para Criar, Ler, Atualizar e Excluir tarefas.
* **Lixeira:** As tarefas removidas são movidas para uma lixeira, de onde podem ser restauradas ou visualizadas.
* **Busca Inteligente:**
    * Filtro dinâmico por texto para encontrar tarefas pelo título ou descrição.
    * Filtro por data específica para visualizar tarefas de um dia.
* **Suporte Offline Completo:** Utilizando o banco de dados local Room, o aplicativo permite o gerenciamento total das tarefas sem conexão com a internet. As alterações são salvas localmente e sincronizadas com o Firebase assim que a conexão é restabelecida.

## Instruções de Execução

Para compilar e executar o projeto, siga os passos abaixo.

### Pré-requisitos
* Android Studio instalado
* Emulador Android ou um dispositivo físico com depuração USB habilitada

### Passos

**1. Configuração do Firebase:**
O projeto necessita do arquivo de configuração do Firebase para se conectar aos serviços de nuvem.

* Vá ao seu [Console do Firebase](https://console.firebase.google.com/).
* Selecione o seu projeto.
* Navegue até **Configurações do projeto** (ícone de engrenagem).
* Na aba **Geral**, faça o download do seu arquivo `google-services.json`.
* Copie o arquivo `google-services.json` baixado e cole-o dentro da pasta `app/` do projeto.

**2. Compilar e Executar:**
* Abra o projeto no Android Studio.
* Aguarde o Gradle sincronizar todas as dependências.
* Clique no botão **Run 'app'** (ícone de play verde) na barra de ferramentas superior.
* Selecione o emulador ou dispositivo físico onde deseja instalar e executar o aplicativo.

## Vídeo de Demonstração

Assista a um vídeo de 1 minuto demonstrando as principais funcionalidades do aplicativo em execução.

**(COLOQUE O LINK PARA O SEU VÍDEO AQUI)**
