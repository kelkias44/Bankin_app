# 🏦 Simple Banking App
 
*A Mobile banking application for managing accounts and transactions*

## 🧩 Modules

| Module         | Description                          |
|----------------|--------------------------------------|
| Authentication | Login/Registration flows             |
| Accounts       | Account listing and details          |
| Transactions   | Transaction history and filtering    |
| Transfers      | Fund transfer between accounts       |

## 🛠️ Technology Stack

**Core Components**
- Kotlin
- Clean Architecture (MVVM)
- XML Views with ViewBinding
- Retrofit + OkHttp for networking 

## Architecture 

```text
com.example.simplebankingapp/
├── data/
│   ├── model/          
│   ├── remote/         
│   └── repository/     
├── domain/
│   ├── entity/         
│   ├── repository/     
│   └── usecase/        
└── presentation/
    ├── activities/     
    ├── fragments/      
    └── adapters/
```

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/kelkias44/Bankin_app.git 
2. Navigate to the project directory:
   ```bash
   cd Bankin_app
3. Add the BASE_URL in the com/example/simplebankingapp/utils/ folder    
4. Build your app
   ```bash
   ./gradlew build
5. Run the app
