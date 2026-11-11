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


## Authors
- Thomas Selwyn (101183163)
- Kemal Sogut
- Sam Touma (101033509)
- Cameron Greer
- Nitish Grover


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

To run the application locally:

.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"

The database can be inspected at:
http://localhost:8080/h2-console

## License
This project is for academic use only (course submission). No warranty. Not for commercial distribution.
