import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;



// Abstraction + Encapsulation
abstract class Person {
    private static int ID_SEQ = 1000;

    private final int id;
    private String name;
    private String email;

    protected Person(String name, String email) {
        this.id = ++ID_SEQ;
        this.name = name;
        this.email = email;
    }

    public final int getId() { return id; }
    public final String getName() { return name; }
    public final String getEmail() { return email; }

    // Encapsulation via setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    // Dynamic polymorphism (overridden by subclasses)
    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("[%s] ID=%d, Name=%s, Email=%s",
                getRole(), id, name, email);
    }
}

class Student extends Person {
    private final Map<LocalDate, Boolean> attendance = new LinkedHashMap<>();
    private final Map<String, Integer> marksBySubject = new LinkedHashMap<>();
    private final List<Payment> payments = new ArrayList<>();

    public Student(String name, String email) {
        super(name, email);
    }

    @Override
    public String getRole() { return "Student"; }

    public Map<LocalDate, Boolean> getAttendance() { return attendance; }
    public Map<String, Integer> getMarksBySubject() { return marksBySubject; }
    public List<Payment> getPayments() { return payments; }
}

class Teacher extends Person {
    private String department;
    private final Map<LocalDate, Boolean> attendance = new LinkedHashMap<>();

    public Teacher(String name, String email, String department) {
        super(name, email);
        this.department = department;
    }

    @Override
    public String getRole() { return "Teacher"; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Map<LocalDate, Boolean> getAttendance() { return attendance; }

    @Override
    public String toString() {
        return super.toString() + String.format(", Department=%s", department);
    }
}

class ExaminationDetail {
    private final String subject;
    private final LocalDate date;
    private final int maxMarks;

    public ExaminationDetail(String subject, LocalDate date, int maxMarks) {
        this.subject = subject;
        this.date = date;
        this.maxMarks = maxMarks;
    }

    public String getSubject() { return subject; }
    public LocalDate getDate() { return date; }
    public int getMaxMarks() { return maxMarks; }

    @Override
    public String toString() {
        return String.format("Exam{subject='%s', date=%s, max=%d}", subject, date, maxMarks);
    }
}

class Payment {
    private final double amount;
    private final LocalDate date;

    public Payment(double amount, LocalDate date) {
        this.amount = amount;
        this.date = date;
    }

    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }

    @Override
    public String toString() {
        return String.format("Payment{amount=%.2f, date=%s}", amount, date);
    }
}



interface AttendanceManager {

    void markAttendance(Person person, boolean present); 
    void markAttendance(Person person, LocalDate date, boolean present); 
    Map<LocalDate, Boolean> viewAttendance(Person person);
}

interface ExamManager {
    void addExam(ExaminationDetail exam);
    List<ExaminationDetail> viewExamSchedule();
    void enterMarks(Student student, String subject, int marks);
    Map<String, Integer> viewMarks(Student student);
}

interface FeeManager {
    void registerPayment(Student student, double amount);
    List<Payment> viewPayments(Student student);
}



class SimpleAttendanceManager implements AttendanceManager {
    @Override
    public void markAttendance(Person person, boolean present) {
        markAttendance(person, LocalDate.now(), present);
    }

    @Override
    public void markAttendance(Person person, LocalDate date, boolean present) {
        if (person instanceof Student) {
            ((Student) person).getAttendance().put(date, present);
        } else if (person instanceof Teacher) {
            ((Teacher) person).getAttendance().put(date, present);
        } else {
            throw new IllegalArgumentException("Unsupported person type for attendance");
        }
    }

    @Override
    public Map<LocalDate, Boolean> viewAttendance(Person person) {
        if (person instanceof Student) return ((Student) person).getAttendance();
        if (person instanceof Teacher) return ((Teacher) person).getAttendance();
        return Collections.emptyMap();
    }
}

class SimpleExamManager implements ExamManager {
    private final List<ExaminationDetail> exams = new ArrayList<>();

    @Override
    public void addExam(ExaminationDetail exam) {
        exams.add(exam);
    }

    @Override
    public List<ExaminationDetail> viewExamSchedule() {
        return Collections.unmodifiableList(exams);
    }

    @Override
    public void enterMarks(Student student, String subject, int marks) {
        Optional<ExaminationDetail> exam = exams.stream()
                .filter(e -> e.getSubject().equalsIgnoreCase(subject))
                .findFirst();
        if (!exam.isPresent()) {
            throw new IllegalArgumentException("No such subject in exam schedule: " + subject);
        }
        if (marks < 0 || marks > exam.get().getMaxMarks()) {
            throw new IllegalArgumentException("Marks must be between 0 and " + exam.get().getMaxMarks());
        }
        student.getMarksBySubject().put(subject, marks);
    }

    @Override
    public Map<String, Integer> viewMarks(Student student) {
        return Collections.unmodifiableMap(student.getMarksBySubject());
    }
}

class SimpleFeeManager implements FeeManager {
    @Override
    public void registerPayment(Student student, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        student.getPayments().add(new Payment(amount, LocalDate.now()));
    }

    @Override
    public List<Payment> viewPayments(Student student) {
        return Collections.unmodifiableList(student.getPayments());
    }
}



class University {
    private final String name;
    private final Map<Integer, Student> students = new LinkedHashMap<>();
    private final Map<Integer, Teacher> teachers = new LinkedHashMap<>();

    private final AttendanceManager attendanceManager;
    private final ExamManager examManager;
    private final FeeManager feeManager;

    public University(String name, AttendanceManager a, ExamManager e, FeeManager f) {
        this.name = name;
        this.attendanceManager = a;
        this.examManager = e;
        this.feeManager = f;
    }

    public String getName() { return name; }



    // Overload 1: Register student with both name & email
    public Student registerStudent(String name, String email) {
        Student s = new Student(name, email);
        students.put(s.getId(), s);
        return s;
    }

    // Overload 2: Register student with name only (email default)
    public Student registerStudent(String name) {
        return registerStudent(name, name.toLowerCase().replaceAll("\\s+", ".") + "@student.univ.edu");
    }

    public Teacher registerTeacher(String name, String email, String department) {
        Teacher t = new Teacher(name, email, department);
        teachers.put(t.getId(), t);
        return t;
    }




    public boolean updateStudent(int id, String newName) {
        Student s = students.get(id);
        if (s == null) return false;
        s.setName(newName);
        return true;
    }
    public boolean updateStudent(int id, String newName, String newEmail) {
        Student s = students.get(id);
        if (s == null) return false;
        s.setName(newName);
        s.setEmail(newEmail);
        return true;
    }


    public boolean updateTeacher(int id, String newName) {
        Teacher t = teachers.get(id);
        if (t == null) return false;
        t.setName(newName);
        return true;
    }

 
    public boolean updateTeacher(int id, String newName, String newEmail) {
        Teacher t = teachers.get(id);
        if (t == null) return false;
        t.setName(newName);
        t.setEmail(newEmail);
        return true;
    }

    public boolean updateTeacher(int id, String newName, String newEmail, String newDept) {
        Teacher t = teachers.get(id);
        if (t == null) return false;
        t.setName(newName);
        t.setEmail(newEmail);
        t.setDepartment(newDept);
        return true;
    }

  

    public Student findStudent(int id) { return students.get(id); }
    public Teacher findTeacher(int id) { return teachers.get(id); }
    public List<Student> listStudents() { return new ArrayList<>(students.values()); }
    public List<Teacher> listTeachers() { return new ArrayList<>(teachers.values()); }


    public void markAttendance(Person p, boolean present) { attendanceManager.markAttendance(p, present); }
    public void markAttendance(Person p, LocalDate date, boolean present) { attendanceManager.markAttendance(p, date, present); }
    public Map<LocalDate, Boolean> viewAttendance(Person p) { return attendanceManager.viewAttendance(p); }

    public void addExam(ExaminationDetail exam) { examManager.addExam(exam); }
    public List<ExaminationDetail> viewExamSchedule() { return examManager.viewExamSchedule(); }
    public void enterMarks(Student s, String subject, int marks) { examManager.enterMarks(s, subject, marks); }
    public Map<String, Integer> viewMarks(Student s) { return examManager.viewMarks(s); }

    public void registerPayment(Student s, double amount) { feeManager.registerPayment(s, amount); }
    public List<Payment> viewPayments(Student s) { return feeManager.viewPayments(s); }


    public void printPersonCard(Person p) {
       
        System.out.println("---- Person Card ----");
        System.out.println(p.toString());
        if (p instanceof Teacher) {
            System.out.println("Department: " + ((Teacher) p).getDepartment());
        }
        System.out.println("---------------------");
    }
}


public class Main {
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        AttendanceManager attendance = new SimpleAttendanceManager();
        ExamManager exams = new SimpleExamManager();
        FeeManager fees = new SimpleFeeManager();
        University uni = new University("Arcadia University", attendance, exams, fees);

        int choice;
        do {
            System.out.println("\n=== UNIVERSITY MANAGEMENT SYSTEM ===");
            System.out.println("1. Register Student");
            System.out.println("2. Register Teacher");
            System.out.println("3. Update Student");
            System.out.println("4. Update Teacher");
            System.out.println("5. Add Exam");
            System.out.println("6. View Exams");
            System.out.println("7. Enter Marks");
            System.out.println("8. View Student Marks");
            System.out.println("9. Mark Attendance");
            System.out.println("10. View Attendance");
            System.out.println("11. Register Fee Payment");
            System.out.println("12. View Payments");
            System.out.println("13. List Students");
            System.out.println("14. List Teachers");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1 -> registerStudent(uni);
                case 2 -> registerTeacher(uni);
                case 3 -> updateStudent(uni);
                case 4 -> updateTeacher(uni);
                case 5 -> addExam(uni);
                case 6 -> viewExams(uni);
                case 7 -> enterMarks(uni);
                case 8 -> viewMarks(uni);
                case 9 -> markAttendance(uni);
                case 10 -> viewAttendance(uni);
                case 11 -> registerFee(uni);
                case 12 -> viewPayments(uni);
                case 13 -> listStudents(uni);
                case 14 -> listTeachers(uni);
                case 0 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 0);
    }

    private static void registerStudent(University uni) {
        System.out.print("Enter student name: ");
        String name = sc.nextLine();
        System.out.print("Enter student email (or leave blank): ");
        String email = sc.nextLine();
        Student s = email.isEmpty() ? uni.registerStudent(name) : uni.registerStudent(name, email);
        System.out.println("Registered: " + s);
    }

    private static void registerTeacher(University uni) {
        System.out.print("Enter teacher name: ");
        String name = sc.nextLine();
        System.out.print("Enter teacher email: ");
        String email = sc.nextLine();
        System.out.print("Enter department: ");
        String dept = sc.nextLine();
        Teacher t = uni.registerTeacher(name, email, dept);
        System.out.println("Registered: " + t);
    }

    private static void updateStudent(University uni) {
        System.out.print("Enter student ID: ");
        int id = sc.nextInt(); sc.nextLine();
        System.out.print("Enter new name: ");
        String name = sc.nextLine();
        System.out.print("Enter new email (or leave blank): ");
        String email = sc.nextLine();
        boolean ok = email.isEmpty() ? uni.updateStudent(id, name) : uni.updateStudent(id, name, email);
        System.out.println(ok ? "Updated." : "Student not found.");
    }

    private static void updateTeacher(University uni) {
        System.out.print("Enter teacher ID: ");
        int id = sc.nextInt(); sc.nextLine();
        System.out.print("Enter new name: ");
        String name = sc.nextLine();
        System.out.print("Enter new email (or leave blank): ");
        String email = sc.nextLine();
        System.out.print("Enter new department (or leave blank): ");
        String dept = sc.nextLine();
        boolean ok;
        if (!email.isEmpty() && !dept.isEmpty())
            ok = uni.updateTeacher(id, name, email, dept);
        else if (!email.isEmpty())
            ok = uni.updateTeacher(id, name, email);
        else
            ok = uni.updateTeacher(id, name);
        System.out.println(ok ? "Updated." : "Teacher not found.");
    }

    private static void addExam(University uni) {
        System.out.print("Enter subject: ");
        String subject = sc.nextLine();
        System.out.print("Enter exam date (yyyy-mm-dd): ");
        String date = sc.nextLine();
        System.out.print("Enter max marks: ");
        int max = sc.nextInt(); sc.nextLine();
        uni.addExam(new ExaminationDetail(subject, LocalDate.parse(date), max));
        System.out.println("Exam added.");
    }

    private static void viewExams(University uni) {
        uni.viewExamSchedule().forEach(System.out::println);
    }

    private static void enterMarks(University uni) {
        System.out.print("Enter student ID: ");
        int id = sc.nextInt(); sc.nextLine();
        Student s = uni.findStudent(id);
        if (s == null) { System.out.println("Student not found."); return; }
        System.out.print("Enter subject: ");
        String subject = sc.nextLine();
        System.out.print("Enter marks: ");
        int marks = sc.nextInt(); sc.nextLine();
        try {
            uni.enterMarks(s, subject, marks);
            System.out.println("Marks entered.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewMarks(University uni) {
        System.out.print("Enter student ID: ");
        int id = sc.nextInt(); sc.nextLine();
        Student s = uni.findStudent(id);
        if (s == null) { System.out.println("Student not found."); return; }
        System.out.println("Marks: " + uni.viewMarks(s));
    }

    private static void markAttendance(University uni) {
        System.out.print("Enter ID: ");
        int id = sc.nextInt(); sc.nextLine();
        Person p = uni.findStudent(id);
        if (p == null) p = uni.findTeacher(id);
        if (p == null) { System.out.println("Person not found."); return; }
        System.out.print("Enter date (yyyy-mm-dd or blank for today): ");
        String date = sc.nextLine();
        System.out.print("Present? (y/n): ");
        boolean present = sc.nextLine().equalsIgnoreCase("y");
        if (date.isEmpty())
            uni.markAttendance(p, present);
        else
            uni.markAttendance(p, LocalDate.parse(date), present);
        System.out.println("Attendance marked.");
    }

    private static void viewAttendance(University uni) {
        System.out.print("Enter ID: ");
        int id = sc.nextInt(); sc.nextLine();
        Person p = uni.findStudent(id);
        if (p == null) p = uni.findTeacher(id);
        if (p == null) { System.out.println("Person not found."); return; }
        System.out.println("Attendance: " + uni.viewAttendance(p));
    }

    private static void registerFee(University uni) {
        System.out.print("Enter student ID: ");
        int id = sc.nextInt(); sc.nextLine();
        Student s = uni.findStudent(id);
        if (s == null) { System.out.println("Student not found."); return; }
        System.out.print("Enter amount: ");
        double amt = sc.nextDouble(); sc.nextLine();
        uni.registerPayment(s, amt);
        System.out.println("Payment registered.");
    }

    private static void viewPayments(University uni) {
        System.out.print("Enter student ID: ");
        int id = sc.nextInt(); sc.nextLine();
        Student s = uni.findStudent(id);
        if (s == null) { System.out.println("Student not found."); return; }
        System.out.println("Payments: " + uni.viewPayments(s));
    }

    private static void listStudents(University uni) {
        uni.listStudents().forEach(System.out::println);
    }

    private static void listTeachers(University uni) {
        uni.listTeachers().forEach(System.out::println);
    }
}
