# Java Starts Here — Companion Code Repository

Official companion code repository for the book:
**"Java Starts Here: Java, Software Engineering, and the Things They Don't Teach You"**  
by **Mothusi Molorane**

---

## 🛠️ Architecture & Technology Stack

* **Language Runtime:** Java 25 (LTS)
* **Framework:** Spring Boot 4.1.1
* **Build System:** Apache Maven (Multi-Module Reactor)
* **Contract Specification:** OpenAPI 3.0 / 3.1 (Single-file Specification-First)
* **Quality & Enforcement:** Maven Enforcer Plugin (`extra-enforcer-rules`), Checkstyle, ArchUnit

---

## 📦 Chapter Modules Blueprint

This repository is organized into dedicated Maven modules corresponding to the book chapters, eliminating dependency leakage and demonstrating real enterprise reactor architecture:

| Module | Chapter Focus | Key Concepts & Runnable Code |
| :--- | :--- | :--- |
| **`ch03-engine`** | The Java Engine | Project Loom Virtual Threads, Tiered JIT compilation, `invokedynamic` |
| **`ch04-core`** | Core Types & Mechanics | Compact Strings, Exception stack trace costs, Covariant returns, Access widening |
| **`ch05-design-principles`**| Real-World Design | Overload resolution ladder, Domain invariants, Dynamic dispatch (`vtable`) |
| **`ch06-code-hygiene`** | Software Engineering Hygiene | Checkstyle rules, Maven Enforcer, Cyclomatic vs cognitive complexity |
| **`ch07-gotchas`** | Gotchas & Puzzles | Integer cache trap, Floating-point precision, `Spliterator.SIZED` stream elision |
| **`ch08-real-world`** | The Enterprise Ecosystem | **Single-file OpenAPI 3.1 API-First**, Spring Boot 4.1.1 delegate controller |
| **`ch09-observability`** | Production Investigations | Structured JSON logs, MDC context propagation, Micrometer Prometheus metrics |
| **`ch10-enterprise-toolkit`**| Cloud & Containers | Multi-stage Dockerfile, cgroups memory ergonomics, Kubernetes probes |
| **`ch11-interview`** | The Java Interview | HashMap collision treeification, `equals`/`hashCode`, Declarative Comparators |
| **`ch12-beyond-the-code`**| Craft & Architecture | ArchUnit architecture boundary enforcement, Defending domain models |

---

## 🚀 Getting Started

Ensure you have **Java 25** installed. No local Maven installation is required; use the bundled Maven Wrapper (`./mvnw`).

### 1. Build and Run All Tests Across the Reactor
```bash
./mvnw clean test
```

### 2. Run a Targeted Chapter Module
To run tests for a specific chapter without compiling the entire project, use Maven's `-pl` (project list) flag:

```bash
# Run Chapter 7 gotcha puzzles:
./mvnw test -pl ch07-gotchas

# Run Chapter 8 contract-driven API tests:
./mvnw test -pl ch08-real-world

# Run Chapter 12 architectural invariant tests:
./mvnw test -pl ch12-beyond-the-code
```

### 3. Generate OpenAPI Sources (Chapter 8)
Chapter 8 demonstrates **API-First (Specification-Driven)** architecture using a clean, single-file contract located at:
`ch08-real-world/src/main/resources/api/orders-api.yml`

To trigger code generation of the Spring MVC interface and models:
```bash
./mvnw generate-sources -pl ch08-real-world
```

---

## 🛡️ Enterprise Build Governance

This repository includes real-world build governance configurations:
* **Maven Enforcer:** Enforces Java version bounds, bans duplicate dependency declarations, and verifies build environment reproducibility.
* **Checkstyle:** Enforces formatting, import hygiene, and complexity thresholds defined in `config/checkstyle/checkstyle.xml`.

---

## 📖 Book Information
* **Author:** Mothusi Molorane
* **Repository:** [github.com/molorane/java-starts-here-code](https://github.com/molorane/java-starts-here-code)
* **Book Source:** [github.com/molorane/java-starts-here](https://github.com/molorane/java-starts-here)
