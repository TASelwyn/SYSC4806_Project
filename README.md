# SYSC 4806 Project 
## "Amazin online bookstore"

### Azure deployment link
https://4806bookstore-g4axbnebhdf5h5da.canadacentral-01.azurewebsites.net/

## Project Structure
```bash
src/main/
   - java/                  # Program Root
      .../sysc4806project/  # Spring Code
         - controller/      # REST Endpoints 
         - dto/             # Data Transfer Objects (FE <-> BE)
         - exception/       # REST Exception handler
         - model/           # ORM Entity mappings
         - repository/      # ORM Repos Interfaces (to talk to db)
         - service/         # Application Logic
   - resources/             # Program Setup
     - db.changelog/        # Database Seed
     - application.yml      # Spring Boot configuration
mvnw                        # Maven Runtime
```

## Running the project
Clone the repository
```bash
git clone git@github.com:TASelwyn/SYSC4806_Project.git
```

Enter the project folder
```bash
cd SYSC4806_Project
```

Build & Run the application
```bash
./mvnw spring-boot:run
```


## Project Status
Summary of the [Kanban board](https://github.com/users/TASelwyn/projects) as of 2026-11-12.

### Todo
| Issue | Task | Assignee |
|---|---|---|
| #14 | Shopping cart | TASelwyn |
| #15 | Checkout controllers | |
| #31 | Inventory updates for simulated purchases | |
| #32 | Inventory and checkout integration tests | |
| #33 | Thymeleaf pages | |

### In Progress
Nothing currently in progress.

### Done
| Issue | Task | Assignee | PR |
|---|---|---|---|
| #1 | Add initial Spring setup | | #2 |
| #3 | Add Maven CI | | #4 |
| #5 | Implement Book JPA entity | SamTouma8 | #11 |
| #6 | Configure H2 database | SamTouma8 | #12 |
| #7 | Implement BookRepository | SamTouma8 | #16 |
| #8 | Populate sample book data | SamTouma8 | #17 |
| #9 | Write repository and integration tests | SamTouma8 | #26 |
| #13 | Azure deployment | | |
| #18 | Create Thymeleaf pages (2/2 sub-issues) | | |
| #22 | Book controller: list, search and detail endpoints | initishgrover | #23 |
| #29 | Database seeding and book retrieval across environments | | |
| #30 | Resolve book catalog integration issues | | |

**Next up:** shopping cart, checkout and inventory updates for simulated purchases, followed by integration tests and Thymeleaf pages.

## Authors
- Thomas Selwyn (101183163)
- Kemal Sogut (101280677)
- Sam Touma (101033509)
- Cameron Greer (101309871)
- Nitish Grover (101324174)


### Database Schema

![Database schema](https://i.devil.wtf/2026/xKZzvWQfDkUvTCV4)

### Database Seeding

The application uses an H2 database to store book information.

Sample data is provided in:
`src/main/resources/db/seed-books.sql`

The seed script contains 10 books with:
- Unique 13-digit ISBNs without hyphens
- Titles, authors, and publishers
- Descriptions and cover image URLs
- Prices stored as decimal values
- Different inventory quantities, including an out-of-stock book

When the `local` Spring profile is active, Spring Boot
executes the seed script after Hibernate initializes
the database schema.

Each INSERT statement uses WHERE NOT EXISTS to prevent
duplicate ISBNs when the script is executed repeatedly.

The in-memory H2 database resets when the application stops.
Sample books are automatically reloaded on the next startup.

The database can be inspected at:
http://localhost:8080/h2-console

## License
This project is for academic use only (course submission). No warranty. Not for commercial distribution.
