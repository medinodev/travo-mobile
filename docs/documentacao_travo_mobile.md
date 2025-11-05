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

### 1.2 Objetivos

### 1.3 Escopo e Delimitação

## 2. ENGENHARIA DE REQUISITOS

### 2.1 Requisitos Funcionais (RFs)

### 2.2 Requisitos Não Funcionais (RNFs)

## 3. PROJETO E ARQUITETURA DO SOFTWARE

### 3.1 Arquitetura Geral

### 3.2 Projeto do Banco de Dados

### 3.3 Projeto de API

## 4. TECNOLOGIAS E FERRAMENTAS

### 4.1 Stack de Tecnologias

### 4.2 Ferramentas de Desenvolvimento


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

![Figura 6 - Tela de Perfil do Usuário](/imagens-travo/tela_perfil_travo.png)

---

### Figura 7: Tela de Edição de Perfil  
**Legenda:** A tela de edição de perfil oferece ao usuário a possibilidade de atualizar suas informações pessoais de forma simples e intuitiva.  
Contém campos para **nome**, **bio** e **telefone**, além de permitir a **alteração da foto de perfil**.  
O botão **Salvar** garante que as mudanças realizadas sejam registradas.  
A seção “**Alterar senha**” possibilita redefinir a senha, exigindo o preenchimento da **senha antiga**, **nova senha** e **confirmação da nova senha**, reforçando a segurança dos dados.  
O design segue o padrão visual do TRAVO, com **botões arredondados em laranja**, **fundo rosado** e layout limpo, priorizando a experiência do usuário.  

![Figura 7 - Tela de Edição de Perfil](/imagens-travo/tela_editarPerfil_travo.png)







## 6. AMBIENTE E GUIA DE IMPLANTAÇÃO

### 6.1 Requisitos do Ambiente

### 6.2 Processo de Implantação

### 6.3 Acesso à Aplicação Implantada

## 7. CONCLUSÃO

### 7.1 Trabalhos Futuros

### 7.2 Lições Aprendidas

