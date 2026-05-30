<div align="center">

# ⚡ WattWatch : Energy Usage Monitoring System

![Status](https://img.shields.io/badge/Status-Active-success)
![Tech](https://img.shields.io/badge/Java%20Swing-Desktop%20App-blue)
![License](https://img.shields.io/badge/License-Educational-orange)

<h3 align="center">
📂 GitHub:
<a href="https://github.com/Kalpesh0389">View Repository</a>
</h3>

</div>

---

## 📌 About WattWatch

**WattWatch** is a desktop-based Energy Usage Monitoring System that simulates real-world electricity billing and account management. Built for both consumers and administrators, it covers the full energy billing lifecycle — from user registration and secure login to bill generation, payment, receipt downloads, and support management.

WattWatch focuses on **bill management**, **payment tracking**, and **role-based dashboards**, making it a practical simulation of a utility management platform used by energy providers.

---

## ✨ Key Highlights

* 🔐 Secure login and registration for users and admins
* 🧾 Bill generation, payment, and receipt download support
* 📜 Complete bill history with receipt records per user
* 🔔 Notification system for billing and payment alerts
* 🛠️ Support & feedback system for user queries
* 🛡️ Admin dashboard to manage bills, payments & support tickets
* 🗄️ MySQL database for secure and persistent data storage

---

## 🚀 Features

### 👤 User Features
* Register and log in securely
* View current energy usage and outstanding bill
* Make deposits and pay energy bills
* Download and view bill receipts
* Access complete bill payment history
* Receive notifications for new bills and payment confirmations
* Submit support requests and feedback

### 🛡️ Admin Features
* Admin dashboard with full platform overview
* Generate energy bills for users
* Track bill payments across all accounts
* View complete payment history system-wide
* Manage and resolve user support tickets
* Monitor user accounts and activity

### 🖥️ Application Features
* Java Swing desktop UI with clean, user-friendly layout
* Role-based access control (User / Admin)
* Receipt generation saved to user bill history
* Real-time notification delivery within the app
* MySQL-backed persistent storage for all records
* Lightweight standalone desktop application

---

## 🏗️ Tech Stack

| Layer        | Technologies                      |
| ------------ | --------------------------------- |
| UI Framework | Java Swing                        |
| Language     | Java                              |
| Database     | MySQL                             |
| Connectivity | JDBC (Java Database Connectivity) |

---

## 📁 Project Structure

```
EnergyMonitoringSystem/
│
├── src/
│   ├── auth/
│   │   ├── Login.java
│   │   └── Register.java
│   ├── user/
│   │   ├── Dashboard.java
│   │   ├── Deposit.java
│   │   ├── BillPayment.java
│   │   ├── BillHistory.java
│   │   ├── Notifications.java
│   │   └── SupportFeedback.java
│   ├── admin/
│   │   ├── AdminDashboard.java
│   │   ├── GenerateBill.java
│   │   ├── PaymentTracker.java
│   │   ├── PaymentHistory.java
│   │   └── SupportTickets.java
│   ├── utils/
│   │   └── ReceiptGenerator.java
│   ├── db/
│   │   └── DBConnection.java
│   └── Main.java
│
├── database/
│   └── energy_monitoring.sql
│
└── README.md
```

---

## ⚙️ Installation & Setup

### Prerequisites
* Java JDK (v8+)
* MySQL Server
* MySQL Connector/J (JDBC Driver)
* Any Java IDE (IntelliJ IDEA / Eclipse / NetBeans)

### Clone Repository
```bash
git clone https://github.com/Kalpesh0389/energy-monitoring-system.git
cd energy-monitoring-system
```

### Database Setup
```sql
-- Open MySQL and run the provided SQL file
source database/energy_monitoring.sql;
```

### Configure Database Connection
Update the credentials in `src/db/DBConnection.java`:
```java
String url = "jdbc:mysql://localhost:3306/energy_monitoring";
String user = "your_mysql_username";
String password = "your_mysql_password";
```

### Run the Application
```bash
# Compile
javac -cp .;mysql-connector-java.jar src/**/*.java

# Run
java -cp .;mysql-connector-java.jar src.Main
```

Or simply open the project in your IDE and run `Main.java`.

---

## 🗄️ Database Schema

| Table            | Description                                  |
| ---------------- | -------------------------------------------- |
| `users`          | Stores user credentials and account details  |
| `bills`          | Holds generated bills and usage records      |
| `payments`       | Logs all bill payments and deposit history   |
| `receipts`       | Saved receipt records linked to payments     |
| `notifications`  | Stores user notifications and alerts         |
| `support_tickets`| Tracks user support requests and feedback    |
| `admins`         | Admin login credentials                      |

---

## 🔮 Future Enhancements

* 📊 Graphical energy usage analytics and monthly trend charts
* 📧 Email notifications for bill generation and payment confirmation
* 📱 Mobile version using Android / React Native
* 🔒 Password encryption with BCrypt
* 🌐 Web version using Spring Boot & React
* 📄 PDF receipt export with branded template

---

## 👨‍💻 Author

**Kalpesh Remje**

Full Stack Developer

📧 Email: [remjekalpesh486@gmail.com](mailto:remjekalpesh486@gmail.com)

🔗 GitHub: [https://github.com/Kalpesh0389](https://github.com/Kalpesh0389)

---

## 📜 License

This project is developed for **educational purposes** and is open for learning and improvement.

---

<div align="center">
⭐ <em>If you like this project, don't forget to star the repository!</em> ⭐
</div>
