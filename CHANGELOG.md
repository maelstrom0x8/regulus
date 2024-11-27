### **Changelog**

All notable changes to the Regulus API will be documented in this file.

### **[Unreleased]**
* Add support for real-time tracking of waste collection vehicles.
* Introduce enhanced reporting capabilities for waste management organizations.
* Improve routing algorithms to optimize collection routes based on live traffic data.
* Enhance integration with external GeoData services for more accurate location information.
* Refactor code structure to follow a modular architecture:

  * Account Module:
  Manages user accounts and profiles.
  Includes repositories, services, and resources for account management.
  * Commons Package:
  Contains common data entities and utilities shared across modules.
  * Configuration Package:
  Handles configuration for external services.
  * Control Package:
  Coordinates waste collection and payload operations.
  Manages collector agents, collections, clusters, and dispatch handling.
  * Generator Package:
  Generates waste payload requests and manages payload operations.
  Handles payload information, labels, and payload requests.
  * Integration Package:
  Integrates with external GeoData services for location-based functionalities.
  Provides clients and utilities for accessing GeoCoder and Google Map services.
  * Security Package:
  Handles user authentication, authorization, and role management.
