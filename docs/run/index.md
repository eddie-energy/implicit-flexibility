# Tariff Information Service

The EDDIE Implicit Flexibility framework provides a modular foundation for accessing tariff information from energy providers through a common API.

It allows country-specific data sources to integrate with a shared framework while keeping provider-specific API communication, request models, and tariff retrieval logic within their respective integrations.

## Guide

This section explains the existing framework, its architecture and supported data sources.

- [Overview](./overview):
  Introduction to the framework, its purpose, and its main capabilities.
- [Architecture](./architecture):
  Understand the modules, integration points, and main components.
- Data Sources:
  Discover the available provider integrations.
    - [E-Control (Austria)](./datasources/datasource-at-econtrol):
      Learn about the Austrian E-Control integration and its configuration.
    - [ESIOS (Spain)](./datasources/datasource-es-esios):
      Learn about the Spanish ESIOS integration and its configuration.