# Sistema de Matrículas — Projeto de Software

**PUC Minas — Engenharia de Software**
Laboratório 1 — Segundo Semestre/2026
Disciplina: Projeto de Software — Profa. Milena Menezes Adão
Aluno: João Pedro Maciel de Oliveira

---

## 1. Visão Geral

Sistema para informatizar o processo de matrículas de uma universidade. A secretaria gera o currículo de cada semestre e mantém os dados de disciplinas, professores e alunos. Os alunos se matriculam em disciplinas durante um período determinado, e o sistema notifica o sistema de cobranças ao final da inscrição de cada aluno.

**Tecnologia:** Java
**Interface:** linha de comando 
**Persistência de dados:** via arquivos

---

## 2. Atores

| Ator | Tipo | Descrição |
|---|---|---|
| **Aluno** | Primário | Matricula-se e cancela matrículas em disciplinas durante o período de matrículas. |
| **Professor** | Primário | Consulta os alunos matriculados nas disciplinas que leciona. |
| **Secretaria** | Primário | Mantém cursos, disciplinas, professores e alunos; gera o currículo do semestre e controla o período de matrículas. |
| **Sistema de Cobrança** | Secundário (externo) | Recebe a notificação de matrícula para faturar o aluno pelas disciplinas do semestre. |

> Aluno, Professor e Secretaria são especializações de **Usuário**, que concentra o login e a senha.

---

## 3. Regras de Negócio

| ID | Regra |
|---|---|
| **RN01** | O aluno deve se matricular em 4 disciplinas obrigatórias (1ª opção) e em até 2 disciplinas optativas (alternativas). |
| **RN02** | Uma disciplina só é ativada para o semestre seguinte se tiver, no mínimo, **3 alunos matriculados** ao final do período de matrículas. Caso contrário, é cancelada. |
| **RN03** | O número máximo de alunos por disciplina é **60**. Ao atingir esse número, as inscrições para a disciplina são encerradas. |
| **RN04** | Matrículas e cancelamentos só podem ser realizados enquanto o período de matrículas estiver aberto. |
| **RN05** | Após o aluno concluir sua inscrição no semestre, o sistema de matrículas notifica o sistema de cobranças. |
| **RN06** | Todos os usuários possuem senha, utilizada para validação do login. |
| **RN07** | Um curso possui nome e número de créditos, e é constituído por diversas disciplinas. |

> **Nota de modelagem:** o enunciado atribui o número de créditos ao *curso*. Mantivemos essa leitura literal no modelo de análise. Caso a implementação exija carga horária por disciplina, a alteração será registrada na Sprint 02 e justificada na apresentação final.

---

## 4. Casos de Uso

### 4.1 Diagrama

O diagrama de casos de uso está em [`docs/casos-de-uso.puml`](docs/casos-de-uso.puml) (fonte PlantUML) e exportado em [`docs/casos-de-uso.png`](docs/casos-de-uso.png).

### 4.2 Lista de Casos de Uso

| ID | Caso de Uso | Ator Principal |
|---|---|---|
| UC01 | Efetuar Login | Usuário (Aluno, Professor, Secretaria) |
| UC02 | Manter Curso | Secretaria |
| UC03 | Manter Disciplina | Secretaria |
| UC04 | Manter Professor | Secretaria |
| UC05 | Manter Aluno | Secretaria |
| UC06 | Gerar Currículo do Semestre | Secretaria |
| UC07 | Definir Período de Matrículas | Secretaria |
| UC08 | Matricular-se em Disciplinas | Aluno |
| UC09 | Cancelar Matrícula | Aluno |
| UC10 | Consultar Disciplinas Disponíveis | Aluno |
| UC11 | Consultar Alunos Matriculados | Professor |
| UC12 | Encerrar Período de Matrículas | Secretaria |
| UC13 | Notificar Sistema de Cobrança | Sistema de Cobrança (secundário) |

**Relacionamentos:**

- UC08, UC09, UC10, UC11 e todos os UC de manutenção **incluem** UC01 (Efetuar Login).
- UC08 **inclui** UC13 (Notificar Sistema de Cobrança) ao concluir a inscrição do semestre.
- UC12 **inclui** a avaliação de RN02 — ativação ou cancelamento de cada disciplina.

---

## 5. Histórias de Usuário

### 5.1 Autenticação

#### US01 — Efetuar login
> **Como** usuário do sistema,
> **quero** acessar o sistema informando meu identificador e minha senha,
> **para que** apenas usuários autorizados manipulem os dados de matrícula.

**Critérios de aceitação**
- Dado que informo credenciais válidas, então acesso o menu correspondente ao meu perfil (aluno, professor ou secretaria).
- Dado que informo senha incorreta, então recebo mensagem de erro e permaneço na tela de login.
- Nenhuma funcionalidade do sistema é acessível sem autenticação prévia.

---

### 5.2 Secretaria

#### US02 — Manter cursos
> **Como** secretaria,
> **quero** cadastrar, consultar, alterar e remover cursos com nome e número de créditos,
> **para que** a estrutura acadêmica da universidade fique registrada no sistema.

**Critérios de aceitação**
- É possível associar disciplinas a um curso.
- Não é permitido remover um curso que possua disciplinas com alunos matriculados.

#### US03 — Manter disciplinas
> **Como** secretaria,
> **quero** cadastrar, consultar, alterar e remover disciplinas, indicando se são obrigatórias ou optativas e qual professor as leciona,
> **para que** os alunos possam se matricular nas ofertas do semestre.

**Critérios de aceitação**
- Cada disciplina pertence a um curso e possui um professor responsável.
- Cada disciplina possui limite máximo de 60 alunos (RN03).
- Não é permitido excluir disciplina com matrículas ativas.

#### US04 — Manter professores e alunos
> **Como** secretaria,
> **quero** cadastrar, consultar, alterar e remover professores e alunos,
> **para que** os usuários possam acessar o sistema e participar do processo de matrícula.

**Critérios de aceitação**
- Todo professor e aluno cadastrado recebe credenciais de acesso (RN06).
- Não é permitido cadastrar dois usuários com o mesmo identificador.

#### US05 — Gerar currículo do semestre
> **Como** secretaria,
> **quero** gerar o currículo de um semestre com as disciplinas ofertadas,
> **para que** os alunos saibam quais disciplinas estão disponíveis para matrícula.

**Critérios de aceitação**
- O currículo relaciona o semestre às disciplinas ofertadas e seus professores.
- Apenas disciplinas do currículo vigente aparecem para matrícula.

#### US06 — Definir período de matrículas
> **Como** secretaria,
> **quero** definir a data de início e de fim do período de matrículas,
> **para que** as operações de matrícula e cancelamento sejam restritas a esse intervalo (RN04).

**Critérios de aceitação**
- Fora do período, tentativas de matrícula e cancelamento são recusadas com mensagem explicativa.
- A data de fim deve ser posterior à data de início.

#### US07 — Encerrar período de matrículas
> **Como** secretaria,
> **quero** encerrar o período de matrículas e processar a situação de cada disciplina,
> **para que** disciplinas com menos de 3 alunos sejam canceladas e as demais ativadas (RN02).

**Critérios de aceitação**
- Ao encerrar, cada disciplina com ≥ 3 matriculados passa ao estado **ativa**.
- Cada disciplina com < 3 matriculados passa ao estado **cancelada**.
- É emitido um relatório com a situação final de todas as disciplinas do semestre.

---

### 5.3 Aluno

#### US08 — Consultar disciplinas disponíveis
> **Como** aluno,
> **quero** visualizar as disciplinas do currículo do semestre com suas vagas restantes,
> **para que** eu possa escolher em quais me matricular.

**Critérios de aceitação**
- A listagem distingue disciplinas obrigatórias de optativas.
- Disciplinas com 60 matriculados aparecem sinalizadas como sem vagas (RN03).

#### US09 — Matricular-se em disciplinas
> **Como** aluno,
> **quero** me matricular em 4 disciplinas obrigatórias e em até 2 optativas,
> **para que** eu curse o semestre seguinte.

**Critérios de aceitação**
- O sistema recusa a matrícula em mais de 4 obrigatórias ou mais de 2 optativas (RN01).
- O sistema recusa a matrícula em disciplina que já atingiu 60 alunos (RN03).
- O sistema recusa matrícula fora do período (RN04).
- Não é possível se matricular duas vezes na mesma disciplina.
- Ao concluir a inscrição do semestre, o sistema de cobranças é notificado (RN05).

#### US10 — Cancelar matrícula
> **Como** aluno,
> **quero** cancelar uma matrícula realizada anteriormente,
> **para que** eu possa reorganizar minhas escolhas durante o período de matrículas.

**Critérios de aceitação**
- O cancelamento só é permitido com o período aberto (RN04).
- Após o cancelamento, a vaga é liberada e volta a contar para o limite de 60.

#### US11 — Consultar minhas matrículas
> **Como** aluno,
> **quero** consultar as disciplinas em que estou matriculado e a situação de cada uma,
> **para que** eu acompanhe minha inscrição no semestre.

**Critérios de aceitação**
- A consulta exibe disciplina, professor, tipo (obrigatória/optativa) e situação.

---

### 5.4 Professor

#### US12 — Consultar alunos matriculados
> **Como** professor,
> **quero** consultar a lista de alunos matriculados em cada disciplina que leciono,
> **para que** eu possa me preparar para o semestre.

**Critérios de aceitação**
- O professor visualiza apenas as disciplinas sob sua responsabilidade.
- A lista exibe o total de matriculados e indica se a disciplina atingiu o mínimo de 3 alunos.

---

### 5.5 Integração

#### US13 — Notificar sistema de cobrança
> **Como** sistema de matrículas,
> **quero** notificar o sistema de cobranças após a inscrição de um aluno no semestre,
> **para que** o aluno seja cobrado pelas disciplinas em que se matriculou (RN05).

**Critérios de aceitação**
- A notificação contém a identificação do aluno, o semestre e as disciplinas matriculadas.
- Falha na notificação é registrada em log e não impede a persistência da matrícula.

---

## 6. Estrutura do Repositório

```
.
├── README.md
├── docs/
│   ├── casos-de-uso.puml
│   └── casos-de-uso.png
└── code/
│   ├── src/
```

---


