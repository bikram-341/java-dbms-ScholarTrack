# 🎓 ScholarTrack: Scholarship Application & Eligibility Tracker

**ScholarTrack** is an enterprise-grade academic and government scholarship lifecycle management system designed to eliminate manual verification bottlenecks, eradicate opaque delays, and deliver real-time transparency across student applications.

Built with **Java Spring Boot**, **Spring Data JPA**, **Spring Security**, and a **3NF-Normalized Relational Database (MySQL / H2)**, ScholarTrack features an automated **Eligibility Rules Engine**, **Verification Desk Feedback Workflow**, **Committee Sanction Workflow**, and **Direct Benefit Transfer (DBT)** disbursement audit tracking.

---

## 📌 Problem Statement & Solution

| Traditional Bottleneck | ScholarTrack Modern Solution |
| :--- | :--- |
| **Manual Document Scrutiny Delays**: Applications sit in verification queues for weeks with zero reviewer feedback. | **Digital Verification Desk**: Direct officer scrutiny console with 1-click compliance approval or specific discrepancy remarks. |
| **Zero Status Visibility ("Black Box")**: Students have no visibility into where their application is stalled. | **Transparent 5-Stage Visual Stepper**: Real-time stage timeline tracking from submission to bank DBT credit. |
| **Opaque Eligibility Criteria**: Students apply blindly to mismatched schemes, resulting in mass rejections. | **Automated Rules Intelligence Engine**: Evaluates GPA, income ceiling, category quotas, gender, and degrees in real time. |
| **Disputed Disbursements**: Lost track of granted awards and banking references. | **Direct Benefit Transfer (DBT) Audit**: Automatic generation of bank UTR transaction references and remaining quota deduction. |

---

## 🏗️ Architecture & Project Directory Structure

```
java project/
├── pom.xml                                   # Maven Dependencies & Build Configuration
├── README.md                                 # Full Architecture & Evaluation Documentation
├── .gitignore                                # Version control exclusions
└── src/
    ├── main/
    │   ├── java/com/scholartrack/
    │   │   ├── ScholarTrackApplication.java  # Main Bootstrapping & Security Filters
    │   │   ├── controller/                   # REST API Controllers & Error Handler
    │   │   │   ├── AuthController.java
    │   │   │   ├── ScholarshipController.java
    │   │   │   ├── StudentController.java
    │   │   │   ├── EligibilityController.java
    │   │   │   ├── ApplicationController.java
    │   │   │   ├── DocumentController.java
    │   │   │   ├── DashboardController.java
    │   │   │   ├── ReportController.java
    │   │   │   └── GlobalExceptionHandler.java
    │   │   ├── model/                        # Entities & Relational Data Models
    │   │   │   ├── User.java, Role.java, RoleType.java
    │   │   │   ├── Student.java, DegreeLevel.java, StudentCategory.java
    │   │   │   ├── Scholarship.java, ProviderType.java, ScholarshipCategory.java
    │   │   │   ├── EligibilityRule.java
    │   │   │   ├── ScholarshipApplication.java, ApplicationStatus.java
    │   │   │   ├── ApplicationDocument.java, DocumentType.java, DocumentStatus.java
    │   │   │   ├── ApplicationTimeline.java
    │   │   │   ├── AuditLog.java, AuditAction.java
    │   │   │   └── dto/                      # Requests, Responses, and View DTOs
    │   │   ├── repo/                         # Spring Data JPA Repositories
    │   │   └── service/                      # Business Logic & Rules Engine
    │   │       ├── AuthService / AuthServiceImpl
    │   │       ├── StudentService / StudentServiceImpl
    │   │       ├── ScholarshipService / ScholarshipServiceImpl
    │   │       ├── EligibilityService / EligibilityServiceImpl
    │   │       ├── ApplicationService / ApplicationServiceImpl
    │   │       ├── DocumentService / DocumentServiceImpl
    │   │       ├── ReportService / ReportServiceImpl
    │   │       └── DashboardService / DashboardServiceImpl
    │   └── resources/
    │       ├── application.properties        # Production & MySQL Configuration
    │       ├── application-h2.properties     # Standalone In-Memory H2 Profile
    │       ├── schema.sql                    # Relational Schema, Constraints & Views
    │       ├── data.sql                      # Seed Data for Demo & Evaluation
    │       ├── dbms_reports.sql              # 10 Advanced DBMS Analytical SQL Reports
    │       └── static/                       # Responsive Web Dashboard
    │           ├── index.html                # Interactive UI
    │           ├── index.css                 # Modern Glassmorphic Design System
    │           └── index.js                  # Dynamic API Client & State Manager
    └── test/
        └── java/com/scholartrack/
            └── ScholarTrackApplicationTests.java
```

---

## 🗄️ Relational Database (DBMS) Design & 3NF Schema

### 1. Relational Entities
- **`roles`**: Master table for role-based access control (`ROLE_ADMIN`, `ROLE_VERIFICATION_OFFICER`, `ROLE_COMMITTEE_MEMBER`, `ROLE_STUDENT`).
- **`users`**: Authentication credentials with BCrypt password hashing.
- **`students`**: Academic profile (Roll No, CGPA/Score, Degree Level, Department, Family Annual Income, Social Category, Banking Details).
- **`scholarships`**: Scheme master (Provider Type: Govt/Institutional/CSR, Total Slots, Remaining Slots, Financial Aid Amount, Deadline).
- **`eligibility_rules`**: Formal rules engine criteria (Min GPA, Max Income, Allowed Categories, Allowed Degrees, Eligible Gender, Min/Max Age, Mandatory Document List).
- **`scholarship_applications`**: Central transactional table tracking application state, eligibility score, approval decision, and disbursement particulars.
- **`application_documents`**: Uploaded evidence files with officer verification status (`PENDING`, `VERIFIED`, `RESUBMISSION_REQUESTED`, `REJECTED`) and reviewer feedback notes.
- **`application_timelines`**: Audit log of each step with timestamps and actor identities.
- **`audit_logs`**: System audit trail of all operational modifications.

### 2. Database Views (DBMS Concept)
- **`view_application_overview`**: Multi-table joined view providing combined student, scheme, and status metadata.
- **`view_pending_verifications`**: Focused worklist for document verifiers to resolve application bottlenecks.
- **`view_disbursement_summary`**: Aggregate grant amounts and slot utilization metrics per scholarship.

---

## 📊 10 Advanced DBMS SQL Reports (for Project Demonstration)

1. **Comprehensive Application Roster**: Multi-table INNER JOIN across applications, students, and scholarships.
2. **Document Verification Bottleneck Queue**: Identifies pending documents and items flagged for student resubmission.
3. **Scholarship Budget & Slot Utilization Metrics**: Uses `COUNT`, `SUM`, `AVG`, and arithmetic percentage formulas.
4. **Student Eligibility Compliance Matrix**: Conditional rule auditing with `CASE` expressions.
5. **Demographic & Social Category Inclusivity**: `GROUP BY` cross-tabulation across caste categories and gender.
6. **Institution-wise Participation & Success Rate**: `GROUP BY` with `HAVING COUNT(sa.id) > 0` and percentage ratios.
7. **Direct Benefit Transfer (DBT) Banking Audit**: Financial reconciliation with UTR references and bank credentials.
8. **Academic Merit & Need Composite Ranking Index**: Weighted algorithm for ranking applications.
9. **Unclaimed Scholarship Opportunities**: Subqueries with `NOT EXISTS` and `CROSS JOIN` to discover eligible non-applicants.
10. **System Audit Trail**: Chronological event logs for accountability.

---

## 🚀 How to Run ScholarTrack

### Prerequisites
- **Java 17 or higher** (Java 21 / 25 compatible)
- **Apache Maven 3.8+**

### Step 1: Clone / Navigate to Directory
```bash
cd "/Users/bikram/Desktop/java project"
```

### Step 2: Compile & Package
```bash
mvn clean compile
```

### Step 3: Run the Application
```bash
mvn spring-boot:run
```

### Step 4: Open in Web Browser
- 🌐 **Interactive Web Portal**: [http://localhost:8080/](http://localhost:8080/)
- 📑 **Swagger 3 OpenAPI Documentation**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- 🗃️ **H2 Database Web Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:mem:scholartrack_db`
  - User Name: `sa`
  - Password: *(leave blank)*

---

## 👥 Demo User Accounts

| Username | Password | Role | Description |
| :--- | :--- | :--- | :--- |
| `admin` | `admin123` | `ROLE_ADMIN` | System Administrator & Scheme Manager |
| `officer_sharma` | `admin123` | `ROLE_VERIFICATION_OFFICER` | Document Scrutiny & Verification Desk |
| `committee_patel` | `admin123` | `ROLE_COMMITTEE_MEMBER` | Scholarship Committee & Award Sanction |
| `priya_sharma` | `admin123` | `ROLE_STUDENT` | Student Applicant (Under Verification) |
| `rahul_verma` | `admin123` | `ROLE_STUDENT` | Student Applicant (Flagged for Resubmission) |
| `ananya_roy` | `admin123` | `ROLE_STUDENT` | Student Beneficiary (Disbursed DBT Grant) |
| `vikram_singh` | `admin123` | `ROLE_STUDENT` | Student Applicant (Under Committee Review) |
# java-dbms-ScholarTrack
