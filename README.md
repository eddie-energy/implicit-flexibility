# EDDIE Implicit Flexibility

The **Implicit Flexibility Tariff Information Service** provides a unified framework for requesting and retrieving tariff information from various platforms in the EU Member States through a common REST API.

The framework supports country-specific data source integrations and provider-specific tariff discovery workflows. It uses Spring Boot and Spring HATEOAS to expose API endpoints and hypermedia links, allowing clients to discover available operations and retrieve tariff information.

## Features

- **Country-based tariff retrieval:** Resolve energy tariff information providers based on countries.
- **REST API:** Expose tariff information and discovery operations through HTTP endpoints.
- **Modular design:** Separate shared framework functionality from provider-specific implementations.
- **Extensible architecture:** Integrate additional energy providers through dedicated data source modules.
- **Tariff discovery:** Support provider-specific workflows for discovering grid operators, brands, and products.
- **HAL-FORMS:** Describe supported operations and request parameters through hypermedia forms.

## Architecture

The framework is implemented as a multi-module Gradle project using Java and Spring Boot.
The main modules are:

| Module               | Description                                         |
|----------------------|-----------------------------------------------------|
| `interactions`       | Defines interaction-related abstractions.           |
| `transport`          | Exposes the REST API and handles HTTP interactions. |
| `data-sources`       | Includes the implemented integration(s).            |
| `tariff-information` | Contains the main application.                      |

Each integration encapsulates its provider-specific configuration, API communication, request models, and tariff retrieval logic.

<img width="993" height="407" alt="Implicit Flexibilities" src="https://github.com/user-attachments/assets/17212246-8979-4ec4-8071-12701f43ecdd" />


For more information, see the [Architecture Guide](docs/guide/architecture.md).

## Technology Stack

- Java 21
- Spring Boot
- Gradle
- Spring Web
- Spring HATEOAS
- HAL-FORMS

Before running the application, ensure that you have access to the external provider API.

## Build and Run the Project

Use the Gradle wrapper to build the project and run its tests:

```bash
./gradlew build
```

Start the Spring Boot application using the Gradle wrapper:

```bash
./gradlew :tariff-information:bootRun
```

The application starts using the configured Spring profiles and application properties.
Unless configured otherwise, the examples below assume that the application is available at:

```
http://localhost:8080
```

## Contributing

Contributions are welcome.

When contributing:

1. Follow the existing module and coding conventions.
2. Add or update tests for your changes.
3. Run the Gradle build before submitting a pull request.
4. Update the documentation when changing the API or integration interfaces.

## License

This project is licensed under the Apache License 2.0.
See [LICENSE](LICENSE) for details.