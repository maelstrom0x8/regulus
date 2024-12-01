# Changelog

All notable changes to the Regulus API will be documented in this file.

## [Unreleased]

### New Features
- **Real-Time Tracking**:  
  Support for real-time tracking of waste collection vehicles.

- **Enhanced Reporting**:  
  Advanced reporting capabilities for waste management organizations.

- **Optimized Routing**:  
  Improved routing algorithms for optimized collection routes based on live traffic data.

- **GIS Integration**:  
  Enhanced integration with external GIS for more accurate location-based information.

- **Subscription Management**:  
  Introduction of subscription plans (Standard and Premium) for organizations, enabling tiered feature access and pricing flexibility.

---

### Refactor and Modularization
The code structure has been refactored to align with a modular architecture for better maintainability and scalability:

- **Account (`regulus/user`)**:  
  Manages user accounts, roles, and profiles.  
  Contains services, repositories, DTOs, and web controllers for account management.

- **Commons (`regulus/core`)**:  
  Provides shared data entities, utility classes, and commonly used components.

- **Configuration (`config`)**:  
  Manages external service configurations, including GIS services, payment gateways, and mailing.

- **Cluster (`regulus/core/cluster`)**:  
  Manages operational clusters for waste collection.  
  Facilitates grouping of collection units, ensuring efficient dispatching and route planning.

- **Collector (`regulus/core/collector`)**:  
  Manages collector agents, their operations, and associated payloads.  
  Handles the coordination of agents for efficient waste collection.

- **Dispatch (`regulus/core/dispatch`)**:  
  Coordinates waste collection activities and payload operations.  
  Manages collection clusters and dispatch processes.

- **Generator (`regulus/core/generator`)**:  
  Handles the creation and processing of waste payloads.  
  Manages payload information, labels, and requests.

- **Processing (`regulus/core/processing`)**:  
  Oversees waste processing and recycling operations.  
  Manages integration with landfill and recycling operators, as well as the categorization of waste types.

- **GIS Integration (`gis`)**:  
  Facilitates integration with external services for location-based functionalities.  
  Provides geolocation, routing clients, and utilities.

- **Security (`config/security`)**:  
  Implements user authentication, authorization, and role-based access management.

- **Subscription (`regulus/subscription`)**:  
  Manages organizational subscriptions, including tier-based pricing models.  
  Handles subscription plans, billing cycles, and feature access management.

- **Event (`regulus/event`)**:  
  Handles domain events for inter-service communication.  
  Manages event publication, subscription, and notification systems.

- **Analytics (`regulus/core/analytics`)**:  
  Aggregates and processes data from multiple modules for detailed reporting.  
  Provides dashboards for key performance metrics.

- **Admin (`regulus/admin`)**:  
  Offers tools for system administrators to manage users, roles, and configurations.  
  Includes interfaces for subscription management, reports, and system monitoring.

- **Payment (`payment`)**:  
  Manages all payment-related operations, including invoicing and integration with external payment gateways.


---

### Additional Improvements
- Improved code organization and separation of concerns.  
- Enhanced modularity for easier integration and extension of functionalities.
