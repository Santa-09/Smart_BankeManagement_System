# SmartBank - Secure Online Banking System

A comprehensive, secure, and user-friendly online banking system developed in Java using Swing for the graphical user interface. This project aims to provide a reliable and efficient banking experience with a professional and modern UI.

## Features

- **User Authentication:** Secure user registration and login with password hashing (jBCrypt).
- **Account Management:** Create and manage bank accounts with account details view.
- **Fund Transfers:** Seamlessly transfer funds between accounts.
- **Transaction History:** View a detailed history of all transactions.
- **Admin Panel:** An administrative backend for managing the system, including account approval.
- **Email Notifications:** Integrated email service for notifications.
- **Database Connectivity:** Uses MySQL for robust data storage.
- **Logging:** Logs application activity for monitoring and debugging.

## Technologies Used

- **Java:** Core programming language.
- **Swing:** For the graphical user interface.
- **MySQL:** Database for data persistence.
- **Maven:** For project management and dependencies.
- **jBCrypt:** For secure password hashing.
- **Java Mail API:** For sending emails.
- **JFreeChart:** For generating charts and reports.

## Getting Started

### Prerequisites

- Java JDK 11 or higher
- Apache Maven
- MySQL Server

### Setup and Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/SmartBank.git
    cd SmartBank
    ```

2.  **Database Setup:**
    - Create a new database in MySQL.
    - Import the `smartbank.sql` file located in `src/main/resources` to set up the required tables.
    - Configure the database connection details in `src/main/java/com/smartbank/utils/Config.java`.

3.  **Build the project:**
    ```bash
    mvn clean install
    ```

4.  **Run the application:**
    ```bash
    java -jar target/smartbank-1.0.0.jar
    ```

## Project Structure

The project follows a standard Maven directory structure:

-   `src/main/java/com/smartbank/`: Contains the main source code.
    -   `db/`: Database connection and query execution.
    -   `gui/`: Swing UI frames.
    -   `main/`: Main application entry point.
    -   `models/`: Data models (User, Account, etc.).
    -   `services/`: Business logic for various features.
    -   `threads/`: Background threads for tasks like email sending.
    -   `utils/`: Utility classes for configuration, logging, etc.
-   `src/main/resources/`: Application resources, including the SQL schema.
-   `pom.xml`: Maven project configuration.

## Contributing

Contributions are welcome! Please feel free to submit a pull request or open an issue for any bugs or feature requests.
