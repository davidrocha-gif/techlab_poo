# TechLab Inventory & Service Desk

Sistema de console em Java para controlar os ativos de TI de uma empresa (servidores, estações de trabalho e equipamentos de rede) e registrar os chamados de suporte abertos para cada um deles. Projeto da disciplina de Programação Orientada a Objetos e Persistência, Faculdade Senac Ceará.

Os dados ficam salvos em um banco SQLite (arquivo `techlab.db`, criado sozinho na primeira execução).

## O que ele faz

- Cadastrar, listar, buscar, atualizar e remover ativos (Servidor, Estação de Trabalho, Dispositivo de Rede)
- Abrir chamados ligados a um ativo, com prioridade (Baixa, Média, Alta, Crítica)
- Acompanhar o status do chamado (Aberto, Em andamento, Resolvido, Fechado)
- Gerar notificações para chamados e alertas de rede
- Relatório com o custo mensal de manutenção de todos os ativos e o resumo dos chamados
- Carregar dados de exemplo para testar rápido

## Como rodar

Precisa de JDK 17 ou mais novo e do `make`, em Linux.

```
make roda
```

Outros comandos:

```
make compila   # só compila para a pasta bin
make limpa     # apaga bin e o banco techlab.db
```

O driver do SQLite já está na pasta `lib`. São dois jars e os dois precisam ficar juntos: o `without-natives` tem as classes do driver e o `natives-linux` tem as bibliotecas nativas.

Se o `techlab.db` for de uma versão antiga do projeto, rode `make limpa` antes para ele ser recriado com as colunas novas.

## Estrutura

```
src/Techlab/
  Main.java              menu e entrada de dados
  interfaces/            IConexaoBD, IPersistencia<T>, INotificavel
  db/                    ConexaoBanco
  negocio/               AtivoTI, Servidor, EstacaoTrabalho, DispositivoRede, ChamadoSuporte
  dao/                   AtivoDAO, ChamadoDAO
lib/                     driver SQLite
Makefile
```

A explicação completa de cada classe e variável está no documento do projeto.
