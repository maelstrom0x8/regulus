# Changelog

All notable changes to the Regulus API will be documented in this file.

## [Unreleased]

### New Features
- **Real-Time Tracking**:  
  Add support for real-time tracking of waste collection vehicles.

- **Enhanced Reporting**:  
  Introduce advanced reporting capabilities for waste management organizations.

- **Optimized Routing**:  
  Improve routing algorithms to optimize collection routes based on live traffic data.

- **GIS Integration**:  
  Enhance integration with external GIS for more accurate location-based information.

---

### Refactor and Modularization
Refactor the code structure to align with a modular architecture for improved maintainability and scalability:

- **Account Module (`regulus/user`)**:  
  Manages user accounts, roles, and profiles.  
  Contains services, repositories, DTOs, and web controllers for account management.

- **Commons Module (`regulus/core`)**:  
  Includes shared data entities, utility classes, and commonly used components.

- **Configuration Module (`config`)**:  
  Manages external service configurations such as GIS services, payment gateways, and mailing.

- **Control Module (`regulus/core/dispatch`)**:  
  Handles waste collection coordination and payload operations.  
  Manages collector agents, collection clusters, and dispatch handling.

- **Generator Module (`regulus/core/generator`)**:  
  Manages the creation and processing of waste payloads.  
  Handles payload information, labels, and requests.

- **Integration Module (`gis`)**:  
  Facilitates external service integration for location-based functionalities.  
  Provides geolocation and routing clients and utilities.

- **Security Module (`config/security`)**:  
  Implements user authentication, authorization, and role-based access management.
- 
- **Subscription Module (`regulus/core/subscription`)**:  
  Manages organizational subscriptions, including standard and premium pricing models.  
  Handles subscription plans, billing cycles, and feature access management. 
