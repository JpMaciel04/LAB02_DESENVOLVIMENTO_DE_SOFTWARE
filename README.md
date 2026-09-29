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

> **Nota de modelagem:** o enunciado atribui o número de créditos ao *curso*. Mantivemos essa leitura literal no modelo de análise e no protótipo (Sprint 03): `Curso.numeroCreditos`; a implementação não exigiu créditos por disciplina.

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
| UC07 | Definir e Abrir Período de Matrículas | Secretaria |
| UC08 | Matricular-se em Disciplinas | Aluno |
| UC09 | Cancelar Matrícula | Aluno |
| UC10 | Consultar Disciplinas Disponíveis | Aluno |
| UC11 | Consultar Alunos Matriculados | Professor |
| UC12 | Encerrar Período de Matrículas | Secretaria |
| UC13 | Notificar Sistema de Cobrança | Sistema de Cobrança (secundário) |
| UC14 | Consultar Minhas Matrículas *(Sprint 03)* | Aluno |
| UC15 | Confirmar Inscrição do Semestre *(Sprint 03)* | Aluno |

**Relacionamentos:**

- Todos os casos de uso dos atores **incluem** UC01 (Efetuar Login).
- UC15 **inclui** UC13 (Notificar Sistema de Cobrança): a cobrança é notificada quando o aluno confirma a inscrição do semestre, não a cada matrícula isolada.
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

## 6. Arquitetura e Diagrama de Classes

- Diagrama de classes (revisado na Sprint 03): [`docs/diagrama-classes.puml`](docs/diagrama-classes.puml) → [`docs/diagrama-classes.png`](docs/diagrama-classes.png)
- Diagrama de arquitetura (novo na Sprint 03): [`docs/diagrama-arquitetura.puml`](docs/diagrama-arquitetura.puml) → [`docs/diagrama-arquitetura.png`](docs/diagrama-arquitetura.png)

### 6.1 Arquitetura em Camadas

O núcleo do projeto continua sendo **controller → service → model**. Para entregar o protótipo usável (interface + persistência), a Sprint 03 acrescentou uma camada acima (`view`) e uma abaixo (`repository`), sem alterar as responsabilidades das três originais. Cada camada só depende da camada imediatamente abaixo; o `model` é compartilhado por todas.

| Camada | Pacote | Responsabilidade |
|---|---|---|
| **View** *(Sprint 03)* | `matriculas.view` | Interface de linha de comando: menus por perfil, leitura/validação de entrada e exibição de mensagens. Não contém regra de negócio. |
| **Controller** | `matriculas.controller` | Uma fachada por ator (login, secretaria, aluno, professor). Recebe identificadores vindos da tela e delega aos services. |
| **Service** | `matriculas.service` | Regras de negócio (RN01–RN07) e integração com o `SistemaCobranca`. Violações geram `RegraNegocioException`, cuja mensagem é exibida pela view. |
| **Repository** *(Sprint 03)* | `matriculas.repository` | Persistência em arquivos texto. Um repositório por entidade, todos derivados de `RepositorioArquivo<T>`. |
| **Model** | `matriculas.model` | Entidades de domínio (dados e relacionamentos), sem lógica de negócio. |

`Main` cria a `Aplicacao`, que monta as camadas na ordem repository → service → controller, e depois inicia o `MenuPrincipal`.

### 6.2 Principais Classes do Model

| Classe | Responsabilidade |
|---|---|
| **Usuario** (abstrata) | Concentra `id`, `nome` e `senha`; base de `Aluno`, `Professor` e `Secretaria` (RN06). |
| **Curso** | Código, nome e número de créditos; agrega as `Disciplina`s do curso (RN07). |
| **Disciplina** | Código, nome, tipo (obrigatória/optativa), situação, professor responsável, limite de 60 alunos (RN03) e mínimo de 3 para ativação (RN02). |
| **Curriculo** | Disciplinas ofertadas em um semestre (UC06) e o seu `PeriodoMatricula`. |
| **PeriodoMatricula** | Datas de início/fim e indicador de abertura (RN04, UC07/UC12). |
| **Inscricao** | Agrupa as matrículas de um aluno no semestre e indica se a inscrição foi confirmada (RN05, UC15). |
| **Matricula** | Associação entre `Aluno` e `Disciplina` em um semestre, com data e situação. |

### 6.3 Principais Classes do Service e Controller

| Classe | Responsabilidade |
|---|---|
| **AutenticacaoService** | Valida usuário e senha (RN06, UC01). |
| **CursoService / ProfessorService / AlunoService** | Cadastro, alteração, remoção e consulta (UC02, UC04, UC05). Impedem identificadores duplicados e a remoção de registros em uso. |
| **DisciplinaService** | CRUD de disciplinas (UC03), `temVagas`, `vagasRestantes`, `totalMatriculados` e `avaliarSituacao` (RN02/RN03). |
| **CurriculoService** | Gera o currículo do semestre, mantém o currículo vigente e as disciplinas ofertadas (UC06, UC10). |
| **PeriodoMatriculaService** | Define, abre e encerra o período; o encerramento aplica RN02 a cada disciplina (UC07, UC12, RN04). |
| **MatriculaService** | `matricular` e `cancelar`, aplicando RN01, RN03 e RN04 (UC08, UC09). |
| **InscricaoService** | `concluir`, que notifica o `SistemaCobranca`. Uma falha na notificação vai para o log e não desfaz a inscrição (RN05, UC13, UC15). |
| **SistemaCobranca / SistemaCobrancaService** | Integração com o ator externo. O protótipo registra cada notificação em `dados/cobrancas.log`. |
| **LoginController** | UC01. |
| **SecretariaController** | UC02–UC07 e UC12. |
| **AlunoController** | UC08–UC10, UC14 e UC15. |
| **ProfessorController** | UC11. |

### 6.4 Persistência em Arquivos

Os dados ficam na pasta `dados/` (ou na pasta passada como argumento), em arquivos texto UTF-8 com uma linha por registro e campos separados por `;`. Referências entre entidades são gravadas pelo identificador e reconstruídas na carga.

| Arquivo | Repositório | Campos |
|---|---|---|
| `usuarios.csv` | `UsuarioRepository` | tipo; id; nome; senha |
| `cursos.csv` | `CursoRepository` | código; nome; créditos |
| `disciplinas.csv` | `DisciplinaRepository` | código; nome; tipo; situação; curso; professor |
| `curriculos.csv` | `CurriculoRepository` | semestre; vigente; início; fim; aberto; disciplinas |
| `inscricoes.csv` | `InscricaoRepository` | aluno; semestre; concluída |
| `matriculas.csv` | `MatriculaRepository` | aluno; disciplina; semestre; data; situação |
| `cobrancas.log` | `SistemaCobrancaService` | data/hora; aluno; nome; semestre; disciplinas |

Cada alteração regrava o arquivo da entidade de forma atômica (grava em `.tmp` e depois move).

---

## 7. Como Executar (Sprint 03)

Requisitos: **Java 17+** e **Maven 3.9+**.

```bash
cd code
mvn package              # compila, roda os testes e gera target/sistema-matriculas.jar
java -jar target/sistema-matriculas.jar            # dados em ./dados
java -jar target/sistema-matriculas.jar outra/pasta  # pasta de dados alternativa
```

> No Windows, rode `chcp 65001` antes para os acentos aparecerem corretamente no terminal.

Na primeira execução (pasta de dados vazia), o sistema carrega **dados de demonstração** criados pelos próprios services, ou seja, validados pelas mesmas regras de negócio da interface:

| Perfil | Login / senha |
|---|---|
| Secretaria | `admin` / `admin` |
| Professores | `p1` (Ana Souza), `p2` (Carlos Lima) / `123` |
| Alunos | `a1` … `a5` / `123` |

Também são criados: o curso ES, 5 disciplinas obrigatórias e 3 optativas, o currículo **2026/2** com o período de matrículas aberto, e matrículas de `a1`–`a3`. ES101 e ES103 já têm 3 alunos; as demais ficam abaixo do mínimo, o que permite demonstrar a RN02 ao encerrar o período.

**Roteiro sugerido para a apresentação**

1. `a4` → consultar disciplinas → matricular em ES104 e ES202 → confirmar a inscrição (grava a notificação em `dados/cobrancas.log`).
2. `a1` (já tem ES201) → matricular em ES202 e depois em ES203: a 3ª optativa é recusada (RN01).
3. `p1` → consultar os alunos de ES101: 3 alunos, mínimo atingido.
4. `admin` → Período de matrículas → Encerrar: o relatório mostra as disciplinas ATIVAS e CANCELADAS (RN02).
5. `a4` → tentar cancelar uma matrícula: o sistema recusa porque o período está fechado (RN04).
6. Fechar e reabrir o programa: todos os dados continuam lá (persistência).

### 7.1 Testes

`mvn test` executa `RegrasDeNegocioTest` (11 casos): RN01 (limites 4 + 2), matrícula duplicada, RN03 (60 alunos), RN04 (período fechado), cancelamento libera a vaga, RN02 (encerramento), RN05 (notificação e falha na cobrança), RN06 (login), remoção bloqueada e recarga dos dados a partir dos arquivos.

---

## 8. Alterações nos Modelos na Sprint 03

Mudanças em relação aos modelos das Sprints 01/02, feitas para o software funcionar como especificado:

| # | Alteração | Motivo |
|---|---|---|
| 1 | Novas camadas `view` e `repository` | A Sprint 03 exige interface e persistência. Mantê-las fora de controller/service preserva a separação de responsabilidades original. |
| 2 | `codigo` em `Curso` e `Disciplina` | O usuário precisa de um identificador curto para escolher registros na linha de comando; também é a chave nos arquivos. |
| 3 | `Date` → `LocalDate` | API moderna e imutável; facilita comparar datas do período (RN04). |
| 4 | `PeriodoMatricula` passa a pertencer ao `Curriculo` (antes estava associado a `Matricula`) | O período é do semestre; cada matrícula consulta o período do currículo vigente. |
| 5 | `Inscricao.concluida` e novo UC15 "Confirmar Inscrição do Semestre" (inclui UC13) | O enunciado diz que a cobrança é notificada *após o aluno se inscrever para o semestre*. Qualquer alteração posterior exige nova confirmação. |
| 6 | Novo UC14 "Consultar Minhas Matrículas" | A US11 existia, mas faltava o caso de uso correspondente. |
| 7 | Services recebem identificadores (`String`) em vez de entidades já montadas, e retornam as entidades | A view só conhece o que o usuário digitou; a busca e a validação ficam no service. |
| 8 | `totalMatriculados`, `temVagas` e `avaliarSituacao` recebem `semestre` | Evita que matrículas de semestres anteriores contem vagas no semestre atual. |
| 9 | `AutenticacaoService.autenticar(id, senha): Usuario` | O login parte do identificador digitado, não de um `Usuario` já carregado. |
| 10 | `RegraNegocioException` | Canal único para a view exibir violações de regra ao usuário. |
| 11 | Removidos `CursoService.adicionarDisciplina/removerDisciplina` | A disciplina é vinculada ao curso na criação (`DisciplinaService.criar`), o que evita duas formas de fazer a mesma coisa. |
| 12 | Nova matrícula na mesma disciplina após um cancelamento reativa o registro anterior | Mantém um único registro por aluno/disciplina/semestre no arquivo. |

---

## 9. Estrutura do Repositório

```
.
├── README.md
├── docs/
│   ├── casos-de-uso.puml / .png
│   ├── diagrama-classes.puml / .png
│   └── diagrama-arquitetura.puml / .png
└── code/
    ├── pom.xml
    └── src/
        ├── main/java/matriculas/
        │   ├── Main.java            # ponto de entrada
        │   ├── Aplicacao.java       # montagem das camadas
        │   ├── DadosIniciais.java   # carga de demonstração
        │   ├── view/                # interface de linha de comando
        │   ├── controller/          # orquestração dos casos de uso
        │   ├── service/             # regras de negócio
        │   ├── repository/          # persistência em arquivos
        │   └── model/               # entidades de domínio
        └── test/java/matriculas/service/
            └── RegrasDeNegocioTest.java
```
