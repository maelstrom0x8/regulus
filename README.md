# Regulus Waste Management System
Regulus Waste Management System is a comprehensive Java EE application designed to revolutionize waste management operations with advanced features and intelligent algorithms. Built to optimize resource utilization, minimize environmental impact, and enhance overall efficiency, Regulus offers a robust platform for waste management organizations to streamline their processes and deliver sustainable solutions.

## Features
### Cluster Management
Regulus automatically groups waste disposal requests into clusters based on geographical proximity and time constraints. By intelligently organizing disposal requests, Regulus ensures efficient allocation of resources and minimizes response times for waste collection operations.

### Dispatch Handling
Efficient dispatch handling is essential for timely waste collection and disposal. Regulus utilizes advanced algorithms to dispatch collection agents to clusters, taking into account factors such as traffic conditions, agent availability, and waste disposal site capacity. By optimizing dispatch routes, Regulus maximizes resource utilization and minimizes operational costs.

### Dynamic Routing
Regulus leverages dynamic routing algorithms to calculate optimal routes for collection agents, adapting to real-time traffic conditions and environmental factors. By continuously optimizing routes based on live data, Regulus ensures efficient waste collection operations and reduces carbon emissions associated with transportation.

### Integration with GeoData Services
Seamless integration with external GeoData services allows Regulus to retrieve accurate location information and calculate optimal routes for waste collection operations. By leveraging GeoData APIs, Regulus provides precise location data and ensures reliable routing for collection agents, even in remote or dynamically changing environments.

### Configurable Wait Time
Regulus offers configurable wait times for waste disposal clusters, allowing organizations to define maximum wait durations before dispatching collection agents. By setting wait time thresholds, organizations can ensure timely response to disposal requests and prevent unnecessary delays in waste collection operations.

## Usage
### Installation
- Clone the repository to your local machine:
```shell
git clone https://github.com/your-username/regulus-waste-management.git
```
- Import the project into your preferred Java IDE (e.g., IntelliJ IDEA, Eclipse, VSCode).

- Configure your database connection settings in persistence.xml located in the src/main/resources/META-INF directory.

- Build and deploy the application to your Payara Server.
```shell
java -jar payara-micro-<version>.jar --deploy regulus/target/regulus.war 
```

## Getting Started
Ensure that the application is up and running on your application server.

Create a new user to access other endpoints in the application

Submit waste disposal requests through the provided user interface or API endpoints.

Monitor the cluster management and dispatch handling processes to track the status of waste collection operations.

Analyze and optimize waste collection routes and resource allocation based on real-time data and performance metrics.

## Contributing
Contributions to the Regulus Waste Management System project are welcome! If you have ideas for improvements, feature requests, or bug reports, please submit them through the GitHub issue tracker.

## License
This project is licensed under the Apache License 2.0. See the LICENSE file for details.
