# Documentação TRAVO Mobile

## SUMÁRIO
1. [INTRODUÇÃO](#1-introdução)  
   1.1. [Contexto e Justificativa](#11-contexto-e-justificativa)  
   1.2. [Objetivos](#12-objetivos)  
   1.3. [Escopo e Delimitação](#13-escopo-e-delimitação)  
2. [ENGENHARIA DE REQUISITOS](#2-engenharia-de-requisitos)  
   2.1. [Requisitos Funcionais (RFs)](#21-requisitos-funcionais-rfs)  
   2.2. [Requisitos Não Funcionais (RNFs)](#22-requisitos-não-funcionais-rnfs)  
3. [PROJETO E ARQUITETURA DO SOFTWARE](#3-projeto-e-arquitetura-do-software)  
   3.1. [Arquitetura Geral](#31-arquitetura-geral)  
   3.2. [Projeto do Banco de Dados](#32-projeto-do-banco-de-dados)  
   3.3. [Projeto de API](#33-projeto-de-api)  
4. [TECNOLOGIAS E FERRAMENTAS](#4-tecnologias-e-ferramentas)  
   4.1. [Stack de Tecnologias](#41-stack-de-tecnologias)  
   4.2. [Ferramentas de Desenvolvimento](#42-ferramentas-de-desenvolvimento)  
5. [IMPLEMENTAÇÃO E RESULTADOS](#5-implementação-e-resultados)  
   5.1. [Telas do Sistema](#51-telas-do-sistema)  
6. [AMBIENTE E GUIA DE IMPLANTAÇÃO](#6-ambiente-e-guia-de-implantação)  
   6.1. [Requisitos do Ambiente](#61-requisitos-do-ambiente)  
   6.2. [Processo de Implantação](#62-processo-de-implantação)  
   6.3. [Acesso à Aplicação Implantada](#63-acesso-à-aplicação-implantada)  
7. [CONCLUSÃO](#7-conclusão)  
   7.1. [Trabalhos Futuros](#71-trabalhos-futuros)  
   7.2. [Lições Aprendidas](#72-lições-aprendidas)


## 1. INTRODUÇÃO

### 1.1 Contexto e Justificativa

O turismo urbano e a participação em eventos culturais, gastronômicos e de entretenimento movimentam significativamente a economia local. No entanto, turistas e moradores frequentemente enfrentam dificuldades para descobrir eventos próximos, além de encontrar informações centralizadas sobre locais de interesse, cardápios, avaliações e benefícios promocionais.

Atualmente, essas informações estão fragmentadas entre redes sociais, sites e recomendações informais, resultando em perda de oportunidades tanto para os usuários quanto para os estabelecimentos.

O **TRAVO** surge como uma solução mobile que centraliza todas essas informações em um único aplicativo. Com ele, o usuário pode visualizar eventos ao seu redor, explorar locais próximos, consultar cardápios, avaliações e acessar cupons exclusivos.

O projeto atende:
- **Usuários finais:** que buscam lazer de forma prática e personalizada.
- **Estabelecimentos:** que desejam ampliar visibilidade e atrair novos consumidores.

### 1.2 Objetivos

Desenvolver um aplicativo mobile que centralize informações sobre eventos e locais turísticos próximos ao usuário, oferecendo experiência personalizada que integre avaliações, cardápios, favoritos e cupons de desconto.

### Objetivos Específicos

- Implementar sistema de autenticação (login, cadastro, recuperação de senha).
- Criar dashboard com mapa interativo, locais próximos e eventos em destaque.
- Desenvolver tela de visualização de locais com cardápio, avaliações e cupons.
- Implementar área de cupons com listagem e resgate.
- Permitir favoritar locais para acesso rápido.
- Criar tela de perfil para edição de informações pessoais.
- Garantir feedback em tempo real ao usuário.
- Preparar integração futura com clube de benefícios (versão premium).

### 1.3 Escopo e Delimitação

| Tela / Módulo | Descrição |
|---|---|
| Login | Autenticação com validação e tratamento de erros. |
| Cadastro | Criação de conta com dados essenciais. |
| Recuperar Senha | Fluxo via e-mail cadastrado. |
| Dashboard | Mapa interativo + eventos em destaque + locais próximos. |
| Tela de Local | Resumo, cardápio, avaliações, cupons do estabelecimento. |
| Tela de Cupons | Listagem geral + resgate. |
| Tela de Favoritos | Acesso rápido aos locais salvos. |
| Tela de Perfil | Edição de dados pessoais e configurações básicas. |
| Configurações | Preferências gerais e logout. |

### Fora do Escopo (Versão Atual)

- Gestão financeira ou notas fiscais.
- Cadastro direto de estabelecimentos via app.
- Integração com pagamentos ou reserva online.
- Logística ou controle de entregas.

## 2. ENGENHARIA DE REQUISITOS

### 2.1 Requisitos Funcionais (RFs)

| ID | Requisito | Descrição |
|---|---|---|
| RF01 | Autenticação | Login com e-mail e senha, manter sessão. |
| RF02 | Cadastro | Criar conta com nome, e-mail e senha. |
| RF03 | Recuperação de Senha | Redefinição via e-mail. |
| RF04 | Dashboard | Exibição de mapa, eventos e recomendação personalizada. |
| RF05 | Localização | Utilizar GPS para sugerir locais próximos. |
| RF06 | Visualização de Locais | Resumo, cardápio, avaliações e cupons. |
| RF07 | Favoritar Locais | Salvar locais para acesso rápido. |
| RF08 | Exibir Favoritos | Listagem dos locais salvos. |
| RF09 | Listagem de Cupons | Catálogo com busca e filtros. |
| RF10 | Resgate de Cupom | Possibilidade de resgatar um cupom. |
| RF11 | Avaliações | Exibir avaliações de usuários. |
| RF12 | Perfil | Editar informações pessoais e foto. |
| RF13 | Logout | Encerrar sessão manualmente. |
| RF14 | Configurações | Preferências gerais e notificações. |
| RF15 | Busca e Filtros | Filtragem e pesquisa no mapa. |
| RF16 | Notificações Push | Alertas sobre eventos e cupons. |

### 2.2 Requisitos Não Funcionais (RNFs)

| Categoria | RNF | Descrição |
|---|---|---|
| Desempenho | RNF01 | App deve iniciar em até 3s. |
|  | RNF02 | Mapa deve carregar em até 2s. |
|  | RNF03 | Troca entre telas < 1s. |
| Usabilidade | RNF04 | Interface seguindo Material Design 3. |
|  | RNF05 | Feedback imediato para ações do usuário. |
| Compatibilidade | RNF06 | Compatível com Android 9+ (API 28+). |
|  | RNF07 | Responsivo para diferentes tamanhos de tela. |
| Segurança | RNF08 | Tokens armazenados com EncryptedSharedPreferences. |
|  | RNF09 | Comunicação via HTTPS. |
|  | RNF10 | Senhas criptografadas no servidor (bcrypt). |
| Recursos | RNF11 | Uso de cache para economia de dados. |

## 3. PROJETO E ARQUITETURA DO SOFTWARE

### 3.1 Arquitetura Geral

- **Clean Architecture + MVVM**

### 3.2 Projeto do Banco de Dados

| Camada | Responsabilidade | Tecnologias |
|---|---|---|
| Presentation | UI e interação do usuário | **Jetpack Compose**, ViewModel |
| Domain | Regras de negócio | UseCases, Data Classes |
| Data | Repositórios e fontes de dados | Retrofit, Room, DTOs, Mapppers |
| External | Serviços externos | Supabase, Coil, WorkManager |

### Estratégia de Dados
- Fonte principal: API Supabase (REST).
- Cache local com **Room**.
- Padrão: **Single Source of Truth**.
- Atualização automática da UI via Flow/StateFlow.

### 3.3 Projeto de API

POST /auth/login
POST /auth/register
POST /auth/refresh
GET /users/{id}
GET /places?lat=&lng=&radius=
GET /places/{id}
GET /places/{id}/menu
GET /places/{id}/coupons
GET /events?lat=&lng=&from=&to=
POST /coupons/{id}/redeem
POST /favorites
DELETE /favorites/{id}

## 4. TECNOLOGIAS E FERRAMENTAS

### 4.1 Stack de Tecnologias

| Camada | Tecnologia |
|---|---|
| Mobile | Kotlin, Jetpack Compose, ViewModel, StateFlow |
| Backend | Node.js (em desenvolvimento futuro) |
| Banco / BaaS | Supabase (PostgreSQL, Auth, Storage) |
| API Client | Retrofit + OkHttp |
| Imagens | Coil |
| Persistência Local | Room |
| Notificações | Firebase Cloud Messaging (futuro) |

### 4.2 Ferramentas de Desenvolvimento

| Finalidade | Ferramenta |
|---|---|
| IDE Mobile | Android Studio |
| IDE Backend | VS Code |
| Versionamento | Git + GitHub (GitFlow) |
| Gestão do Projeto | Trello (Kanban) |
| Testes de API | Postman / Insomnia |


## 5. IMPLEMENTAÇÃO E RESULTADOS

### 5.1 Telas do Sistema

### Figura 1: Tela de Login  
**Legenda:** A tela de login é o ponto de entrada do sistema TRAVO Mobile.  
Ela permite que o usuário acesse o aplicativo informando seu e-mail e senha.  
Além disso, disponibiliza a opção **“Esqueceu sua senha?”** para recuperação de acesso e dois botões de ação:  
- **Cadastrar:** direciona o usuário para a tela de cadastro de nova conta.  
- **Entrar:** valida as credenciais e autentica o usuário no sistema.  

![Figura 1 - Tela de Login](/imagens-travo/tela_login_travo.png)

### Figura 2: Tela de Cadastro  
**Legenda:** A tela de cadastro permite que novos usuários criem uma conta no TRAVO Mobile.  
Ela solicita informações essenciais para o registro, incluindo **nome completo**, **e-mail**, **telefone (opcional)** e **senha**.  
Após o preenchimento, o usuário deve clicar em **Cadastrar** para concluir o processo.  
Também há a opção **“Já tem conta? Entrar”**, que redireciona o usuário para a tela de login.  

![Figura 2 - Tela de Cadastro](/imagens-travo/tela_cadastro_travo.png)

### Figura 3: Tela de Recuperação de Senha  
**Legenda:** A tela de recuperação de senha permite que o usuário redefina o acesso à sua conta TRAVO Mobile.  
Para isso, basta inserir o **e-mail cadastrado** e clicar no botão **Recuperar**, que enviará instruções de redefinição.  
A interface também oferece a opção **“Voltar para Login”**, facilitando o retorno à tela inicial de autenticação.  

![Figura 3 - Tela de Recuperação de Senha](/imagens-travo/tela_recuperarSenha_travo.png)

### Figura 4: Tela de Configurações  
**Legenda:** A tela de configurações centraliza as principais opções de navegação e personalização do usuário no TRAVO Mobile.  
Por meio desta interface, o usuário pode acessar suas listas de **Favoritos**, **Cupons** e **Serviços**, além de visualizar ou editar informações do **Perfil**.  
O botão **Sair**, destacado em vermelho, encerra a sessão atual e redireciona o usuário para a tela de login.  

![Figura 4 - Tela de Configurações](/imagens-travo/tela_configuracao_travo.png)

### Figura 5: Tela de Detalhe do Local  
**Legenda:** A tela de detalhe do local apresenta informações completas sobre um estabelecimento específico no TRAVO Mobile.  
Exibe dados como **nome**, **endereço**, **tipo**, **horário de funcionamento** e um **resumo descritivo** do local.  
Além disso, disponibiliza **cupons de desconto** ativos e uma seção de **avaliações de usuários**, que inclui comentários, notas e percentuais de satisfação.  
A navegação inferior permite alternar rapidamente entre as seções **Descontos**, **Home** e **Configurações**, garantindo uma experiência fluida dentro do aplicativo.  

![Figura 5 - Tela de Detalhe do Local](/imagens-travo/tela_detalhesLocal_travo.png)

### Figura 6: Tela de Favoritos 
**Legenda:** A tela de favoritos apresenta informações completas sobre um estabelecimento específico que o usuário marcou como favorito.  
A navegação inferior permite alternar rapidamente entre as seções **Descontos**, **Home** e **Configurações**, garantindo uma experiência fluida dentro do aplicativo.

![Figura 6 - Tela de Favoritos](/imagens-travo/tela_favoritos_travo.png)

### Figura 7: Tela de Listagem de serviços
**Legenda:** A tela de listagem de serviços permite que o usuário veja de forma rápida e em forma de listagem os estabelecimentos que estão cadastrados no app. 
A navegação inferior permite alternar rapidamente entre as seções **Descontos**, **Home** e **Configurações**, garantindo uma experiência fluida dentro do aplicativo. 

![Figura 7 - Tela de Listagem de Serviços](/imagens-travo/tela_listagemLocais_travo.png) 

### Figura 8: Tela de Listagem de Cupons
**Legenda:** A tela de listagem de cupons apresenta os cupons que estão disponíveis para os usuários e a quais estabacimentos cadastrados no app os cupons se referem.  

![Figura 8 - Tela de Listagem de Serviços](/imagens-travo/tela_favoritos_travo.png) **Imagem pendente

### Figura 9: Tela de Perfil do Usuário  
**Legenda:** A tela de perfil do usuário exibe as principais informações pessoais cadastradas no TRAVO Mobile.  
Apresenta **foto do usuário**, **nome**, **biografia curta** e **e-mail**. Também são mostrados dados complementares como **data de nascimento** e **telefone**.  
O botão **Editar** permite que o usuário acesse a tela de edição de perfil para atualizar seus dados.  
Na parte inferior, há uma lista de **locais visitados**, representada por miniaturas e nomes dos locais, reforçando a interação do usuário com o aplicativo.  
A **barra de navegação inferior** possibilita o acesso rápido às seções **Descontos**, **Home** e **Configurações**, mantendo a consistência visual entre as telas do aplicativo.  

![Figura 9 - Tela de Perfil do Usuário](/imagens-travo/tela_perfil_travo.png)

### Figura 10: Tela de Edição de Perfil  
**Legenda:** A tela de edição de perfil oferece ao usuário a possibilidade de atualizar suas informações pessoais de forma simples e intuitiva.  
Contém campos para **nome**, **bio** e **telefone**, além de permitir a **alteração da foto de perfil**.  
O botão **Salvar** garante que as mudanças realizadas sejam registradas.  
A seção “**Alterar senha**” possibilita redefinir a senha, exigindo o preenchimento da **senha antiga**, **nova senha** e **confirmação da nova senha**, reforçando a segurança dos dados.  

![Figura 10 - Tela de Edição de Perfil](/imagens-travo/tela_editarPerfil_travo.png)

### Figura 11: Tela de Home  
**Legenda:**


## 6. AMBIENTE E GUIA DE IMPLANTAÇÃO

Esta seção descreve o ambiente necessário e os passos para compilar o código-fonte e gerar o arquivo de instalação (.apk) do aplicativo **TRAVO Mobile**.

### 6.1 Requisitos do Ambiente

Para garantir a compilação correta do projeto, recomenda-se utilizar as seguintes versões e ferramentas:

- **IDE:** Android Studio Narwhal 4 Feature Drop 2025.1.4 
- **Gradle:** a versão que já vem no app quando baixado
- **JDK:** embutido no Android Studio
- **Sistema Operacional:** Windows 10/11, macOS Monterey ou Linux Ubuntu 22.04  
- **Emulador/Dispositivo:** Android 10 ou superior  

### 6.2 Processo de Implantação

Para gerar a versão **release** do aplicativo **TRAVO Mobile**, siga as instruções abaixo conforme seu sistema operacional:

1. Clone o repositório do projeto:

   git clone https://github.com/medinodev/travo-mobile

2. Acesse o diretório do projeto:

   cd travo-mobile

3. Gere o arquivo de instalação (.apk):

   No Windows: 
   gradlew.bat assembleRelease

   No Mac:
   ./gradlew assembleRelease

4. Após a conclusão, o arquivo de instalação será criado em:

   app/build/outputs/apk/release/app-release.apk

## 7. CONCLUSÃO

### 7.1 Trabalhos Futuros

### 7.2 Lições Aprendidas

