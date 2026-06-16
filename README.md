# 🏆 Bolão Copa do Mundo 2026

Aplicativo de bolão para a Copa do Mundo FIFA 2026. Usuários cadastrados fazem
palpites sobre os placares das partidas e competem em um ranking geral. Os
administradores gerenciam o campeonato por um painel web.

Projeto desenvolvido para o **Hackathon — 5º Período (Frameworks Java e
Desenvolvimento Mobile)** da Faculdade Alfa Umuarama (UniALFA).

---

## 🧱 Arquitetura

A solução tem dois artefatos que se comunicam por uma **API REST**, mais o banco:

| Artefato | Pasta | Stack |
|----------|-------|-------|
| **1 — Painel Admin Web + API REST** | [`backend/`](backend) | Java 21, Spring Boot 3.3, Spring Web, Spring Data JPA, Spring Security (JWT + login por sessão), Thymeleaf + Bootstrap 5 |
| **2 — App Mobile** | [`mobile/`](mobile) | React Native (Expo), Expo Router, TypeScript, Axios |
| **3 — Banco de Dados** | MySQL 8 | Schema documentado em [`backend/src/main/resources/db/modelo.sql`](backend/src/main/resources/db/modelo.sql) |

```
┌────────────────┐     REST/JSON + JWT     ┌──────────────────────────┐
│  App Mobile    │ ──────────────────────► │  Backend Spring Boot     │
│  (React Native)│ ◄────────────────────── │  • API REST  /api/**     │
└────────────────┘                         │  • Painel Admin /admin/**│
                                           └────────────┬─────────────┘
┌────────────────┐      HTTP (Thymeleaf)                │ JPA
│  Navegador     │ ───────────────────────────────────►│
│  (Admin)       │                                      ▼
└────────────────┘                            ┌──────────────────┐
                                              │   MySQL 8        │
                                              └──────────────────┘
```

---

## ✅ Requisitos atendidos

- **Autenticação/Conta (RF-001 a RF-006):** cadastro, login com JWT, recuperação de senha, edição de perfil, logout, exclusão de conta (LGPD).
- **Partidas (RF-010 a RF-013):** listagem por fase/data, detalhe, filtros, próximas partidas.
- **Palpites (RF-020 a RF-024):** registrar/editar, bloqueio após o início (HTTP 422), listar com pontuação e critério.
- **Pontuação e Ranking (RF-030 a RF-034):** cálculo automático ao encerrar a partida, regra 10/5/0, ranking geral paginado (≥50/página) com posição do usuário destacada e desempate por placares exatos.
- **Painel Admin (RF-040 a RF-047):** login ADMIN, CRUD de seleções e partidas, lançamento/correção de resultado com recálculo, listagem e bloqueio de usuários, dashboard com indicadores.
- **Regras de negócio (4.1 a 4.4):** regra de pontuação, bloqueio de palpite, recálculo transacional, critério de desempate.

---

## 🚀 Como executar

### Pré-requisitos
- Java 21+ e Maven 3.9+
- MySQL 8 em execução
- Node.js 18+ e Expo (`npm install -g expo-cli` opcional)

### 1. Backend (API + Painel Admin)

```bash
cd backend

# Configure o acesso ao MySQL via variáveis de ambiente (ou edite application.yml):
#   DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD
# O banco é criado automaticamente (createDatabaseIfNotExist=true).

./mvnw spring-boot:run        # Linux/Mac
mvnw.cmd spring-boot:run      # Windows
# ou:  mvn spring-boot:run
```

- API REST: `http://localhost:8080/api`
- Painel Admin: `http://localhost:8080/admin`
- **Admin inicial** (criado no primeiro boot): `admin@bolao.com` / `admin123`
  (altere via `ADMIN_EMAIL` / `ADMIN_SENHA`).
- Na primeira execução são criadas seleções e partidas de exemplo (`APP_SEED=true`).

### 2. App Mobile

```bash
cd mobile
npm install
npm start            # abre o Expo; use o app Expo Go ou um emulador
```

> **Endereço da API:** o app usa `extra.apiBaseUrl` de [`app.json`](mobile/app.json).
> O padrão `http://10.0.2.2:8080` aponta para o `localhost` do PC a partir do
> **emulador Android**. Para dispositivo físico, troque pelo IP da sua máquina
> (ex.: `http://192.168.0.10:8080`).

---

## 🔌 Endpoints da API (resumo)

| Método | Rota | Descrição | Auth |
|--------|------|-----------|------|
| POST | `/api/auth/cadastro` | Cadastro (RF-001) | — |
| POST | `/api/auth/login` | Login → JWT (RF-002) | — |
| POST | `/api/auth/recuperar-senha` | Recuperação (RF-003) | — |
| GET/PUT/DELETE | `/api/usuarios/me` | Perfil / editar / excluir (RF-004/5/6) | JWT |
| GET | `/api/partidas` `?fase=&status=` | Listar/filtrar (RF-010/12) | — |
| GET | `/api/partidas/proximas` | Próximas (RF-013) | — |
| GET | `/api/partidas/{id}` | Detalhe (RF-011) | — |
| GET | `/api/palpites/meus` | Meus palpites (RF-023/24) | JWT |
| POST | `/api/palpites` | Registrar/editar (RF-020/21/22) | JWT |
| GET | `/api/ranking` `?pagina=` | Ranking geral (RF-032/34) | opcional |

---

## 🧮 Regra de pontuação (seção 4.1)

Palpite `P (pm × pv)` vs. resultado `R (rm × rv)`:

| Condição | Pontos | Critério |
|----------|:------:|----------|
| `pm = rm` **e** `pv = rv` | **10** | Placar exato |
| `sign(pm−pv) = sign(rm−rv)` | **5** | Acerto do vencedor/empate |
| caso contrário | **0** | Erro total |

Implementada em [`PontuacaoService`](backend/src/main/java/com/unialfa/bolao/service/PontuacaoService.java)
e validada por testes em [`PontuacaoServiceTest`](backend/src/test/java/com/unialfa/bolao/service/PontuacaoServiceTest.java).

---

## 📁 Estrutura

```
HACKATHON-BOLAO-COPA-DO-MUNDO/
├── backend/                       # Artefato 1 + 3 (Spring Boot)
│   ├── src/main/java/com/unialfa/bolao/
│   │   ├── domain/                # Entidades JPA e enums
│   │   ├── repository/            # Spring Data repositories
│   │   ├── service/               # Regras de negócio (pontuação, ranking...)
│   │   ├── api/                   # Controllers REST + DTOs
│   │   ├── web/                   # Controllers Thymeleaf (painel admin)
│   │   ├── security/              # JWT, filtros, UserDetails
│   │   ├── config/                # SecurityConfig, DataSeeder
│   │   └── exception/             # Tratamento de erros da API
│   └── src/main/resources/
│       ├── templates/admin/       # Páginas do painel (Thymeleaf)
│       ├── static/css/            # Estilos do painel
│       ├── db/modelo.sql          # Modelo de dados (referência)
│       └── application.yml
└── mobile/                        # Artefato 2 (React Native / Expo)
    ├── app/                       # Telas (expo-router)
    │   ├── (tabs)/                # Partidas, Palpites, Ranking, Perfil
    │   ├── partida/[id].tsx       # Detalhe + palpite
    │   ├── login.tsx / cadastro.tsx
    │   └── _layout.tsx / index.tsx
    ├── componentes/               # PartidaCard, EstadoVazio...
    ├── contexts/AuthContext.tsx   # Sessão/JWT
    ├── services/                  # Camada de acesso à API (axios)
    ├── types/ utils/ theme/
    └── app.json
```
