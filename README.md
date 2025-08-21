


# University Management Using OOP Concepts

A Java-based **console application** that demonstrates a University Management System using core **Object-Oriented Programming** principles: **abstraction**, **inheritance**, **encapsulation**, **interfaces**, and both **static** and **dynamic polymorphism**.

---

##  Features

- **Register Entities**  
  - Add `Student` (with auto-generated or custom email)  
  - Add `Teacher` (with name, email, department)

- **Manage Attendance** (for both Students and Teachers)  
  - Mark attendance for today or a specific date  
  - View attendance history

- **Examination Management**  
  - Schedule exams with subject, date, and maximum marks  
  - Enter subject-wise marks for students  
  - View student-specific exam marks

- **Update Details**  
  - Update Student: name and/or email  
  - Update Teacher: name, email, and/or department

- **Tuition Fee Management**  
  - Record fee payments with timestamp  
  - View payment history for each student

- **OOP Principles in Action**  
  - **Abstraction & Encapsulation**: `Person` abstract class with private fields  
  - **Inheritance**: `Student` and `Teacher` extend `Person`  
  - **Interfaces**: `AttendanceManager`, `ExamManager`, `FeeManager` for modular behaviors  
  - **Polymorphism**  
    - *Static*: Method overloading (e.g., overloaded registration, attendance, and update methods)  
    - *Dynamic*: Method overriding (`getRole()`, `toString()`) with runtime dispatch

---

##  Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Terminal or Command Prompt

### Running the Program

1. **Clone the repository**:

   ```bash
   git clone https://github.com/bhoomika1714/University_Management_Using_OOPS_Concepts.git
   cd University_Management_Using_OOPS_Concepts

2. **Compile the code**:

   ```bash
   javac Main.java University.java
   ```

3. **Run the application**:

   ```bash
   java Main
   ```

4. **Interact via console**:
   Use the text-based menu to register users, mark attendance, enter exam marks, update details, register fee payments, view reports, and more.

---



## How It Demonstrates OOP Concepts

* **Abstraction & Encapsulation**: Shared fields and behaviors are encapsulated in `Person`, preventing direct external manipulation.
* **Inheritance & Overriding**: `Student` and `Teacher` extend `Person`; override methods like `getRole()` and `toString()` for customized behavior.
* **Interfaces & Modularity**: Separation of concerns using `AttendanceManager`, `ExamManager`, and `FeeManager` ensures clean, swappable components.
* **Static Polymorphism**: Method overloading (e.g., `registerStudent(name)`, `registerStudent(name, email)`).
* **Dynamic Polymorphism**: Polymorphic method calls like `printPersonCard(Person p)` utilize overridden behavior based on actual type (Student vs Teacher).

---

## Sample Workflow

```text
=== UNIVERSITY MANAGEMENT SYSTEM ===
1. Register Student
2. Register Teacher
3. Update Student
4. Update Teacher
...
9. Mark Attendance
10. View Attendance
...
11. Register Fee Payment
12. View Payments
13. List Students
14. List Teachers
0. Exit
Enter choice: 1

Enter student name: Alice Johnson
Enter student email (or leave blank): 
Registered: [Student] ID=1001, Name=Alice Johnson, Email=alice.johnson@student.univ.edu
```

---


Happy coding & learning!

