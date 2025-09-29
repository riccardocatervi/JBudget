# JBudget – Personal budget manager 📊

**JBudget** is a Java application designed for personal budget management.  
The project was developed as part of the **Programming Methodologies** course at the University of Camerino and provides a modern interface to create and manage multiple accounts, record income and expense transactions, organize spending through tags, and schedule recurring transactions.  
On the first launch the application initializes the database, sets up services and controllers, and can generate sample data to help you explore.

## Key features

JBudget offers several features to help you keep track of your finances:

- **Multi‑account management** – You can create, view, edit or delete multiple accounts, each with its own currency.  
  Account information is stored in the database and can be retrieved at any time.
- **Recording transactions** – For each account you can record income (credit) or expense (debit) movements, specifying date, amount, direction and description.  
  You can also assign one or more tags to the transaction and link it to a recurrence for periodic transactions.
- **Filters and searches** – The service allows you to fetch transactions filtered by tag, direction, date range or recurrence, as well as perform paginated searches.  
  Methods are available to list only future or past transactions and to calculate current balances and totals.
- **Categorization through tags** – Tags let you organize your spending into custom categories.  
  Through the service interface you can create new tags, update them, delete them and arrange them hierarchically.
- **Recurring transactions** – For repeated income or expense (e.g., salary, rent) you can define a recurrence with start date, optional end date and frequency (daily, weekly, monthly or yearly).  
  Recurrences can be retrieved, deleted and filtered by reference date.
- **Statistics and dashboard** – The “Dashboard” view shows summary indicators for the selected account: total balance, total income, total expenses, net amount and breakdown of spending by category.  
  It also displays the most recent transactions and the total number of transactions considered.
- **Modern user interface** – The application uses JavaFX 21 to provide a smooth and responsive experience.  
  The main layout includes a sidebar with navigation and statistics, a top bar to choose the account and exit the application, and a scrollable central area.  
  UI management is handled by `UIManager`, which initializes the components, manages navigation between views (Dashboard, Accounts, Transactions, Tags and Recurring) and coordinates events.
- **Sample data** – On startup, if no accounts are present, the application creates some sample accounts, predefined tags and sample transactions.  
  These records allow you to quickly explore the application’s features.

## Architecture

JBudget adopts a layered architecture that separates the user interface from the domain logic and data persistence:

- **User interface (UI)** – Based on JavaFX, displays data and receives user input.  
  The `MainLayoutManager` builds the main structure with sidebar, top bar and content area, while `UIManager` initializes components, handles navigation and orchestrates the lifecycle of controllers.
- **Controllers** – Classes in the `controller` package act as intermediaries between the UI and the business logic, forwarding requests to the services and updating the views.
- **Services** – Domain logic is encapsulated in a series of services (AccountService, TransactionService, TagService, RecurrenceService).  
  Each service exposes methods to create, retrieve, update, delete and search domain objects.
- **Persistence** – Data access is managed via Jakarta Persistence (JPA) and Hibernate.  
  Entities (Account, Transaction, Tag, Recurrence) are mapped in the `persistence.xml` file, which defines the `jbudget-unit` persistence unit and the Hibernate configuration with H2 file‑based database.
- **Database** – The application uses an embedded H2 database (file `data/jbudget`) that is created and updated automatically.  
  The configuration enables the local connection and connection pool.

Below is a conceptual diagram of the architecture:

<img width="256" height="384" alt="image" src="https://github.com/user-attachments/assets/8d1cf8c7-750c-460e-a93e-9a90f58ba06f" />

## Technologies used

- **Java 21** – The project is built with the latest Java version; the toolchain configuration in the Gradle file ensures compilation with Java 21.
- **JavaFX 21.0.1** – Used for the graphical user interface; the Gradle plugin `org.openjfx.javafxplugin` includes the `javafx.controls`, `javafx.fxml` and `javafx.web` modules.
- **Hibernate ORM 7.1** and **Jakarta Persistence** – Provide the JPA implementation for object persistence, configured in the `jbudget-unit` persistence unit.
- **H2 Database** – Embedded relational database that stores data locally; the connection is defined in the `persistence.xml` file.
- **MapStruct 1.5** – Automatically generates mappers to convert entities to DTOs and vice versa.
- **JUnit 5** – Test suite used for unit tests (dependencies declared in the Gradle file).

## Installation

1. **Prerequisites**:
   - Java 21 installed on your system.
   - Git to clone the repository.
2. **Clone the project**:
   ```bash
   git clone https://github.com/riccardocatervi/JBudget.git
   cd JBudget/JBudget
   ```
3. **Build and run**
   Unix/macOS
   ```bash
   ./gradlew build      # builds the project
   ./gradlew run        # runs the application
   ```
   Windows
   ```bash
   gradlew.bat build
   gradlew.bat run
   ```

   The ```run``` task uses the Gradle application plugin and launches the main class:
   ```bash
   it.unicam.cs.mpgc.jbudget126139.Main
   ```

   On first launch, if no data exist, the application will create sample accounts, tags, and transactions.
   Data are stored in the ```data/jbudget.mv.db``` file in the execution directory.


## Using the Application

Once the application is running:

1. **Select an account** – In the top bar, choose the account you want to work on. You can add new accounts from the Accounts section.

2. **Navigation** – Use the sidebar to access different sections:
   - **Dashboard**: overview of the balance, statistics, and recent transactions.
   - **Accounts**: list of accounts, with the ability to create, edit, or delete an account.
   - **Transactions**: manage movements. Add transactions specifying amount, direction (income/expense), description, date, and tags; existing transactions can be filtered and searched.
   - **Tags**: create and organize spending/income categories. Tags can be nested to better structure your budgets.
   - **Recurring**: configure recurring transactions such as salaries or payments. Set frequency (daily, weekly, monthly, yearly) and optional end date.

3. **Statistics** – The Dashboard displays charts and summaries for the selected account: current balance, total income/expenses, net amount, and the distribution of spending by category.

4. **Export/Import** – Currently the application saves data locally in the H2 database. To migrate data, copy the `data/jbudget.mv.db` file to another system.

## Project Structure

The source tree follows the Gradle convention. Below is an overview of the main packages and resources:

| Path | Brief Description |
|------|-------------------|
| `controller` | Interfaces and implementations of controllers that mediate between the UI and the services |
| `model` | Domain entities (Account, Transaction, Tag, Recurrence) and enums like TransactionDirection, RecurrenceFrequency |
| `service` | Service interfaces and DTOs; contains business logic plus search/statistics methods |
| `persistence (impl)` | JPA/Hibernate data access implementations |
| `ui` | JavaFX components: UIManager, UI controllers, layout manager, events, and navigators |
| `resources/META-INF/persistence.xml` | Configuration of the persistence unit and H2 database parameters |
| `resources/styles.css` | CSS stylesheet for the interface |

## Contributing

Contributions and suggestions are welcome! To propose a change:

1. Fork the repository and create a new branch for your work.
2. Implement the feature or fix following the project structure and Java conventions.
3. Verify the project builds and that tests (if any) run correctly.
4. Submit a pull request describing in detail the changes you made.

To report bugs or request new features, open an issue on the project page describing the problem and, if possible, the steps to reproduce it.

## License

This project is distributed under the MIT License. You may use, modify, and distribute the software freely, provided you keep the copyright and license notice in substantial copies of the software.
