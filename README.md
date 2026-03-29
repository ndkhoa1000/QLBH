QLBH — Simple Java EE Microservices (QLBH)

Overview

- A Maven multi-module project implementing a simple retail (QLBH) microservices stack: product, customer, supplier, invoice services, and a web UI. Services are implemented with Jersey/JAX-RS and use MariaDB as the database. The project contains a shared `qlbh-common` module with model classes.

Modules

- `qlbh-common` — shared models and utilities (e.g., `ctu.cit.model` classes, `DatabaseUtil`).
- `qlbh-product-service` — product REST service.
- `qlbh-customer-service` — customer REST service.
- `qlbh-supplier-service` — supplier REST service.
- `qlbh-invoice-service` — invoice/order service and business logic.
- `qlbh-web` — JSP/Tomcat web UI and servlets.

Prerequisites

- Java 17
- Maven
- Docker & Docker Compose (for running the full stack easily)

Build (local)

From the repository root run:

```bash
mvn clean package -DskipTests
```

This builds all modules and produces WARs under each module's `target` directory.

Run with Docker Compose (recommended)

1. Build and start the stack:

```bash
docker compose up -d --build
```

2. Open the web UI in your browser:

- http://localhost:8080/QLBH/

Notes: the stack expects a MariaDB instance named `db` (configured in `docker-compose.yml`). Services are reachable on the internal Docker network by their service names (e.g., `product-service`, `invoice-service`).

Running tests

- Unit / integration tests are module-scoped. From the repo root run:

```bash
mvn -pl qlbh-common test
```

- For the end-to-end regression, the repository includes a JUnit harness in `qlbh-common` that is designed to run from a container attached to the Docker network. See `qlbh-common/src/test/java/ctu/cit/service/client/TestServiceClient.java` for details; you can run it inside a Maven container:

```bash
docker run --rm --network qlbh_qlbh-network -v "$PWD":/workspace -w /workspace maven:3.8.4-openjdk-17-slim \
  mvn -pl qlbh-common -Dtest=TestServiceClient test -Dproduct.url=http://product-service:8080/QLBH/rest/sanpham -Dcustomer.url=http://customer-service:8080/QLBH/rest/khachhang -Dsupplier.url=http://supplier-service:8080/QLBH/rest/nhacungcap -Dinvoice.url=http://invoice-service:8080/QLBH/rest/hoadon
```

Development notes & recent fixes

- The project uses Jersey for REST and Jackson for JSON. Some content-negotiation issues were fixed by adding `@Produces(MediaType.APPLICATION_JSON)` to resource classes.
- Invoice flow: the invoice service (`qlbh-invoice-service`) handles stock checks and updates inside a transaction. A bug where invoice detail addition didn't update stock was fixed by ensuring invoice details are deserialized and processed server-side. See `qlbh-common/src/main/java/ctu/cit/model/HoaDon.java` for model changes and `qlbh-invoice-service/src/main/java/ctu/cit/service/HoaDonServiceImpl.java` for the transactional `muaHang` implementation.
- Web UI improvements: `qlbh-web` contains servlets such as `ctu.cit.servlet.HoaDonServlet` and JSP pages like `Hoadon.jsp`. The UI now includes a modal for editing invoice details and uses JSON-embedded data to populate it.

Useful file references

- `qlbh-web/src/main/webapp/Hoadon.jsp` — invoice UI and client-side logic
- `qlbh-web/src/main/java/ctu/cit/servlet/HoaDonServlet.java` — servlet coordinating invoice UI and REST calls
- `qlbh-invoice-service/src/main/java/ctu/cit/service/HoaDonServiceImpl.java` — invoice business logic and DB interactions
- `qlbh-common/src/main/java/ctu/cit/model` — domain models

Troubleshooting

- If services fail to start during Docker image build, run `mvn -pl <module> compile` locally to see compilation errors.
- If an end-to-end test fails with a 405 on POST, ensure the `Accept` header is set to `application/json` or the service `@Produces` matches the request expectation.

Contributing

- Follow the existing multi-module structure. Make small, focused changes per module and run `mvn -pl <module> test` as you go.
- When modifying REST endpoints, update both the service and any servlets/JSPs that call them.

Contact

- For questions about the code or environment, open an issue in the repository or ask the project maintainer.

