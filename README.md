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
- Sam Touma
- Cameron Greer
- Nitish Grover

## License
This project is for academic use only (course submission). No warranty. Not for commercial distribution.
