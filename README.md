# 📐 TriggerIQ ERP – Design Branch

This branch contains the **architectural design, UML models, and data specifications** for the TriggerIQ ERP system with
AI and blockchain integration. It serves as the **blueprint** for implementation and collaboration.

---

## 📂 Structure

- `diagrams/uml/triggeriq-erp-datamodel.puml` → UML class diagram for ERP core entities (Products, Orders, Customers, Inventory,
  Finance, Blockchain Ledger).
- `diagrams/uml/triggeriq-erp-sequence.puml` → Business process flow (Sales → Inventory → Finance).
- `diagrams/uml/triggeriq-erp-deployment.puml` → System deployment diagram (Backend, Frontend, Database, Blockchain nodes, AI
  services).
- `docs/` → Supporting documentation, architecture notes, API specifications.
```
triggeriq-erp/
├── pom.xml                  # Parent POM (dependency + plugin mgmt)
│
├── erp-core/                # Domain + Application layer (business logic)
│   ├── pom.xml
│   └── src/main/java/com/triggeriq/erp/core/
│       ├── application/
│       │   ├── service/     # Application services
│       │   ├── dto/         # Internal DTOs
│       │   └── port/        # Ports (in/out)
│       │       ├── in/
│       │       └── out/
│       └── domain/
│           ├── model/       # Entities/Aggregates
│           ├── repository/  # Repository interfaces
│           └── service/     # Pure domain services
│
├── erp-infra/               # Infrastructure implementations
│   ├── pom.xml
│   └── src/main/java/com/triggeriq/erp/infra/
│       ├── persistence/     # JPA/Hibernate impls
│       ├── ai/              # AI integrations
│       ├── blockchain/      # Blockchain ledger adapters
│       ├── messaging/       # Kafka/RabbitMQ adapters
│       └── config/          # Spring/Data configs
│
├── erp-adapters/            # Driving adapters (REST, CLI, UI)
│   ├── pom.xml
│   └── src/main/java/com/triggeriq/erp/adapters/
│       ├── web/             # REST/GraphQL controllers
│       ├── cli/             # CLI commands
│       └── ui/              # UI connectors
│
├── erp-web/                 # Main Spring Boot entrypoint
│   ├── pom.xml
│   └── src/main/java/com/triggeriq/erp/web/
│       └── TriggerIqErpApplication.java
│
└── docs/                    # Documentation
├── design/              # UML/PlantUML diagrams
├── architecture.md
└── api-spec.md
```

---

## 🏗️ Architecture

- **Hexagonal Architecture** for maintainability and domain-driven design.
- **ERP Database + Blockchain Ledger** = Core Data Hub (ensuring both operational efficiency and immutable audit
  trails).
- **AI Assistant** modules for forecasting, recommendations, and chatbot interfaces.

---

## 🚀 Goals

1. Provide a clear and extensible **data model** for ERP operations.
2. Define **integration points** for blockchain and AI.
3. Support **business processes** (inventory, sales, finance, HR).
4. Serve as the foundation for **collaborative development** (issues, milestones, features).

---

## 🔧 Tools

- [PlantUML](https://plantuml.com/) → UML diagrams in `.puml` format.
- [GitHub Projects](https://github.com/features/issues) → Agile issue tracking.
- [JHipster](https://www.jhipster.tech/) → Code generation and scaffolding.

---

## 📜 How to Use

1. Clone the repo and switch to the `design` branch:
   ```bash
   git checkout design
   ```
2. Render UML diagrams:
   ```bash
   plantuml triggeriq_erp_datamodel.puml
   ```
3. Discuss, review, and open issues for refinements.

---

📌 Next Steps
 * Finalize core data model (triggeriq_erp_datamodel.puml).
 * Align blockchain ledger schema with ERP events.
 * Expand business process coverage (Procurement, HR, Finance).
 * Sync with implementation branch for MVP.

---

👉 This branch is not production code – it is the design workspace for TriggerIQ ERP.

---