package com.scholartrack.service;

import com.scholartrack.model.*;
import com.scholartrack.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ScholarshipRepository scholarshipRepository;
    private final EligibilityRuleRepository eligibilityRuleRepository;
    private final ScholarshipApplicationRepository applicationRepository;
    private final ApplicationDocumentRepository documentRepository;
    private final ApplicationTimelineRepository timelineRepository;
    private final AuditLogRepository auditLogRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           StudentRepository studentRepository,
                           ScholarshipRepository scholarshipRepository,
                           EligibilityRuleRepository eligibilityRuleRepository,
                           ScholarshipApplicationRepository applicationRepository,
                           ApplicationDocumentRepository documentRepository,
                           ApplicationTimelineRepository timelineRepository,
                           AuditLogRepository auditLogRepository,
                           NotificationRepository notificationRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.scholarshipRepository = scholarshipRepository;
        this.eligibilityRuleRepository = eligibilityRuleRepository;
        this.applicationRepository = applicationRepository;
        this.documentRepository = documentRepository;
        this.timelineRepository = timelineRepository;
        this.auditLogRepository = auditLogRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // 1. Roles
        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(RoleType.ROLE_ADMIN)));
        Role officerRole = roleRepository.findByName(RoleType.ROLE_VERIFICATION_OFFICER)
                .orElseGet(() -> roleRepository.save(new Role(RoleType.ROLE_VERIFICATION_OFFICER)));
        Role committeeRole = roleRepository.findByName(RoleType.ROLE_COMMITTEE_MEMBER)
                .orElseGet(() -> roleRepository.save(new Role(RoleType.ROLE_COMMITTEE_MEMBER)));
        Role studentRole = roleRepository.findByName(RoleType.ROLE_STUDENT)
                .orElseGet(() -> roleRepository.save(new Role(RoleType.ROLE_STUDENT)));

        // 2. Staff Users
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", passwordEncoder.encode("admin123"), "admin@scholartrack.edu", "System Administrator", "+91 9876543210", adminRole);
            userRepository.save(admin);
        }

        User officer = userRepository.findByUsername("officer_sharma").orElseGet(() -> {
            User u = new User("officer_sharma", passwordEncoder.encode("admin123"), "v.sharma@scholarships.gov.in", "Dr. Vikram Sharma", "+91 9811223344", officerRole);
            return userRepository.save(u);
        });

        User committee = userRepository.findByUsername("committee_patel").orElseGet(() -> {
            User u = new User("committee_patel", passwordEncoder.encode("admin123"), "dean.patel@university.edu", "Prof. Arvind Patel", "+91 9877001122", committeeRole);
            return userRepository.save(u);
        });

        // 3. Scholarships & Eligibility Rules (if not present)
        if (scholarshipRepository.count() == 0) {
            // Scholarship 1: Central Sector Scheme
            Scholarship s1 = new Scholarship();
            s1.setCode("GOV-CSSS-2026");
            s1.setTitle("Central Sector Scheme of Scholarship for College Students");
            s1.setDescription("Department of Higher Education initiative supporting brilliant students from economically challenged backgrounds across colleges.");
            s1.setProviderType(ProviderType.GOVERNMENT);
            s1.setScholarshipCategory(ScholarshipCategory.MERIT_BASED);
            s1.setFinancialAidAmount(50000.0);
            s1.setTotalSlots(500);
            s1.setSlotsRemaining(485);
            s1.setApplicationDeadline(LocalDate.now().plusMonths(3));
            s1.setAcademicYear("2026-2027");
            s1.setIsActive(true);
            s1 = scholarshipRepository.save(s1);

            EligibilityRule r1 = new EligibilityRule();
            r1.setScholarship(s1);
            r1.setMinGpaOrPercentage(7.50);
            r1.setMaxFamilyIncome(450000.0);
            r1.setEligibleCategories("ALL");
            r1.setEligibleDegrees("UNDERGRADUATE,POSTGRADUATE");
            r1.setEligibleGender("ANY");
            r1.setMinAge(17);
            r1.setMaxAge(26);
            r1.setRequiredDocuments("INCOME_CERTIFICATE,GRADE_MARKSHEET,AADHAAR_IDENTITY,COLLEGE_FEE_RECEIPT");
            r1.setRuleDescription("Min 7.5 GPA / 75%, family income <= 4.5 Lakhs/year, open to all degree candidates.");
            eligibilityRuleRepository.save(r1);

            // Scholarship 2: Post-Matric SC/ST/OBC
            Scholarship s2 = new Scholarship();
            s2.setCode("GOV-PMS-2026");
            s2.setTitle("Post-Matric National Scholarship for SC/ST/OBC Students");
            s2.setDescription("Financial assistance covering tuition fees, maintenance allowance, and book grants for marginalized social communities.");
            s2.setProviderType(ProviderType.GOVERNMENT);
            s2.setScholarshipCategory(ScholarshipCategory.NEED_BASED);
            s2.setFinancialAidAmount(65000.0);
            s2.setTotalSlots(1000);
            s2.setSlotsRemaining(970);
            s2.setApplicationDeadline(LocalDate.now().plusMonths(2));
            s2.setAcademicYear("2026-2027");
            s2.setIsActive(true);
            s2 = scholarshipRepository.save(s2);

            EligibilityRule r2 = new EligibilityRule();
            r2.setScholarship(s2);
            r2.setMinGpaOrPercentage(6.00);
            r2.setMaxFamilyIncome(250000.0);
            r2.setEligibleCategories("SC,ST,OBC");
            r2.setEligibleDegrees("UNDERGRADUATE,POSTGRADUATE,DIPLOMA");
            r2.setEligibleGender("ANY");
            r2.setMinAge(16);
            r2.setMaxAge(30);
            r2.setRequiredDocuments("INCOME_CERTIFICATE,CASTE_CERTIFICATE,GRADE_MARKSHEET,AADHAAR_IDENTITY");
            r2.setRuleDescription("Reserved for SC, ST, and OBC candidates with family income <= 2.5 Lakhs/year and min 6.0 GPA.");
            eligibilityRuleRepository.save(r2);

            // Scholarship 3: Chancellor's Institutional Merit Fellowship
            Scholarship s3 = new Scholarship();
            s3.setCode("INST-CHANCELLOR-2026");
            s3.setTitle("Chancellor's Institutional Merit Fellowship");
            s3.setDescription("University grant awarded to undergraduate and postgraduate scholars maintaining distinction-level academic records.");
            s3.setProviderType(ProviderType.INSTITUTIONAL);
            s3.setScholarshipCategory(ScholarshipCategory.MERIT_BASED);
            s3.setFinancialAidAmount(75000.0);
            s3.setTotalSlots(150);
            s3.setSlotsRemaining(142);
            s3.setApplicationDeadline(LocalDate.now().plusMonths(1));
            s3.setAcademicYear("2026-2027");
            s3.setIsActive(true);
            s3 = scholarshipRepository.save(s3);

            EligibilityRule r3 = new EligibilityRule();
            r3.setScholarship(s3);
            r3.setMinGpaOrPercentage(8.50);
            r3.setMaxFamilyIncome(800000.0);
            r3.setEligibleCategories("ALL");
            r3.setEligibleDegrees("UNDERGRADUATE,POSTGRADUATE");
            r3.setEligibleGender("ANY");
            r3.setMinAge(17);
            r3.setMaxAge(28);
            r3.setRequiredDocuments("GRADE_MARKSHEET,RECOMMENDATION_LETTER,COLLEGE_FEE_RECEIPT");
            r3.setRuleDescription("Strict academic excellence award. Minimum 8.5 GPA or top 5% in department.");
            eligibilityRuleRepository.save(r3);

            // Scholarship 4: Women in STEM
            Scholarship s4 = new Scholarship();
            s4.setCode("CORP-WOMEN-STEM");
            s4.setTitle("National Women in STEM Leadership Grant");
            s4.setDescription("Empowering exceptional female scholars pursuing Computer Science, Engineering, Biotechnology, and Mathematics.");
            s4.setProviderType(ProviderType.CORPORATE_CSR);
            s4.setScholarshipCategory(ScholarshipCategory.WOMEN_IN_STEM);
            s4.setFinancialAidAmount(100000.0);
            s4.setTotalSlots(80);
            s4.setSlotsRemaining(72);
            s4.setApplicationDeadline(LocalDate.now().plusMonths(3));
            s4.setAcademicYear("2026-2027");
            s4.setIsActive(true);
            s4 = scholarshipRepository.save(s4);

            EligibilityRule r4 = new EligibilityRule();
            r4.setScholarship(s4);
            r4.setMinGpaOrPercentage(8.00);
            r4.setMaxFamilyIncome(600000.0);
            r4.setEligibleCategories("ALL");
            r4.setEligibleDegrees("UNDERGRADUATE,POSTGRADUATE,PHD");
            r4.setEligibleGender("FEMALE");
            r4.setMinAge(17);
            r4.setMaxAge(30);
            r4.setRequiredDocuments("INCOME_CERTIFICATE,GRADE_MARKSHEET,RECOMMENDATION_LETTER,AADHAAR_IDENTITY");
            r4.setRuleDescription("Female candidates enrolled in STEM domains with min 8.0 GPA and demonstrated research interest.");
            eligibilityRuleRepository.save(r4);

            // Scholarship 5: Institutional Need Aid
            Scholarship s5 = new Scholarship();
            s5.setCode("INST-NEED-EWS");
            s5.setTitle("Institutional Tuition Waiver & Book Grant for EWS");
            s5.setDescription("College internal assistance designed to eliminate drop-outs due to severe financial emergencies.");
            s5.setProviderType(ProviderType.INSTITUTIONAL);
            s5.setScholarshipCategory(ScholarshipCategory.NEED_BASED);
            s5.setFinancialAidAmount(40000.0);
            s5.setTotalSlots(200);
            s5.setSlotsRemaining(195);
            s5.setApplicationDeadline(LocalDate.now().plusMonths(2));
            s5.setAcademicYear("2026-2027");
            s5.setIsActive(true);
            s5 = scholarshipRepository.save(s5);

            EligibilityRule r5 = new EligibilityRule();
            r5.setScholarship(s5);
            r5.setMinGpaOrPercentage(6.00);
            r5.setMaxFamilyIncome(200000.0);
            r5.setEligibleCategories("ALL");
            r5.setEligibleDegrees("UNDERGRADUATE,DIPLOMA");
            r5.setEligibleGender("ANY");
            r5.setMinAge(16);
            r5.setMaxAge(25);
            r5.setRequiredDocuments("INCOME_CERTIFICATE,COLLEGE_FEE_RECEIPT,AADHAAR_IDENTITY");
            r5.setRuleDescription("Income under 2 Lakhs/year with valid tehsildar income certificate.");
            eligibilityRuleRepository.save(r5);

            // 4. Demo Students & Applications
            // Student 1: Priya Sharma
            User uPriya = new User("priya_sharma", passwordEncoder.encode("admin123"), "priya.sharma@student.edu", "Priya Sharma", "+91 9988776655", studentRole);
            uPriya = userRepository.save(uPriya);

            Student stu1 = new Student();
            stu1.setUser(uPriya);
            stu1.setStudentRollNo("STU-2024-001");
            stu1.setFullName("Priya Sharma");
            stu1.setEmail("priya.sharma@student.edu");
            stu1.setPhone("+91 9988776655");
            stu1.setInstitutionName("National Institute of Technology");
            stu1.setDepartmentBranch("Computer Science & Engineering");
            stu1.setDegreeLevel(DegreeLevel.UNDERGRADUATE);
            stu1.setCurrentYear(3);
            stu1.setGpaOrPercentage(9.20);
            stu1.setFamilyAnnualIncome(180000.0);
            stu1.setCategory(StudentCategory.OBC);
            stu1.setGender("FEMALE");
            stu1.setDateOfBirth(LocalDate.of(2004, 5, 14));
            stu1.setBankAccountNo("987654321012");
            stu1.setBankIfsc("SBIN0001234");
            stu1.setBankName("State Bank of India");
            stu1 = studentRepository.save(stu1);

            // Application 1: Priya -> Women in STEM (UNDER_DOCUMENT_VERIFICATION)
            ScholarshipApplication app1 = new ScholarshipApplication();
            app1.setApplicationNumber("APP-2026-00101");
            app1.setStudent(stu1);
            app1.setScholarship(s4);
            app1.setStatus(ApplicationStatus.UNDER_DOCUMENT_VERIFICATION);
            app1.setEligibilityScore(92.5);
            app1.setEligibilityPassed(true);
            app1.setEligibilityRemarks("Profile meets GPA (9.2 >= 8.0) and Income limit (₹1.8L <= ₹6.0L). Documents under scrutiny at verifier desk.");
            app1.setSubmittedAt(LocalDateTime.now().minusDays(2));
            app1 = applicationRepository.save(app1);

            createDoc(app1, DocumentType.INCOME_CERTIFICATE, "Income_Certificate_2026_Priya.pdf", DocumentStatus.VERIFIED, "Valid certificate issued by Sub-Divisional Magistrate.", officer);
            createDoc(app1, DocumentType.GRADE_MARKSHEET, "BTech_Sem4_Marksheet.pdf", DocumentStatus.VERIFIED, "Verified against university exam portal: 9.20 CGPA confirmed.", officer);
            createDoc(app1, DocumentType.AADHAAR_IDENTITY, "Aadhaar_Priya_Sharma.pdf", DocumentStatus.PENDING, "Pending biometric OCR verification.", null);

            timelineRepository.save(new ApplicationTimeline(app1, "Application Submission", "COMPLETED", "Application lodged online with automated rules pass (Score 92.5%).", "Priya Sharma (Student)"));
            timelineRepository.save(new ApplicationTimeline(app1, "Document Verification Desk", "IN_PROGRESS", "Income certificate and marksheets verified. Aadhaar pending review.", "Dr. Vikram Sharma (Officer)"));

            // Student 2: Rahul Verma
            User uRahul = new User("rahul_verma", passwordEncoder.encode("admin123"), "rahul.verma@student.edu", "Rahul Verma", "+91 9876123450", studentRole);
            uRahul = userRepository.save(uRahul);

            Student stu2 = new Student();
            stu2.setUser(uRahul);
            stu2.setStudentRollNo("STU-2024-002");
            stu2.setFullName("Rahul Verma");
            stu2.setEmail("rahul.verma@student.edu");
            stu2.setPhone("+91 9876123450");
            stu2.setInstitutionName("Delhi Technological University");
            stu2.setDepartmentBranch("Mechanical Engineering");
            stu2.setDegreeLevel(DegreeLevel.UNDERGRADUATE);
            stu2.setCurrentYear(2);
            stu2.setGpaOrPercentage(7.80);
            stu2.setFamilyAnnualIncome(220000.0);
            stu2.setCategory(StudentCategory.SC);
            stu2.setGender("MALE");
            stu2.setDateOfBirth(LocalDate.of(2005, 2, 18));
            stu2.setBankAccountNo("112233445566");
            stu2.setBankIfsc("PUNB0123400");
            stu2.setBankName("Punjab National Bank");
            stu2 = studentRepository.save(stu2);

            // Application 2: Rahul -> Post-Matric SC/ST/OBC (DOCUMENTS_FLAGGED)
            ScholarshipApplication app2 = new ScholarshipApplication();
            app2.setApplicationNumber("APP-2026-00102");
            app2.setStudent(stu2);
            app2.setScholarship(s2);
            app2.setStatus(ApplicationStatus.DOCUMENTS_FLAGGED);
            app2.setEligibilityScore(78.0);
            app2.setEligibilityPassed(true);
            app2.setEligibilityRemarks("Eligible for Post-Matric scheme. Income certificate requires recent renewal stamp.");
            app2.setSubmittedAt(LocalDateTime.now().minusDays(4));
            app2.setVerifiedAt(LocalDateTime.now().minusDays(1));
            app2.setVerifiedBy(officer);
            app2.setDecisionRemarks("Income certificate issued in 2024 is expired. Upload current fiscal year document.");
            app2 = applicationRepository.save(app2);

            createDoc(app2, DocumentType.INCOME_CERTIFICATE, "Tehsildar_Income_2024.pdf", DocumentStatus.RESUBMISSION_REQUESTED, "Certificate issued in 2024 is expired. Please submit current fiscal year (2026-27) document.", officer);
            createDoc(app2, DocumentType.CASTE_CERTIFICATE, "SC_Category_Certificate.pdf", DocumentStatus.VERIFIED, "Authentic government caste certificate verified.", officer);

            timelineRepository.save(new ApplicationTimeline(app2, "Application Submission", "COMPLETED", "Application submitted for Post-Matric SC/ST/OBC scheme.", "Rahul Verma (Student)"));
            timelineRepository.save(new ApplicationTimeline(app2, "Document Verification Desk", "REJECTED", "Discrepancy: Income certificate expired. Student resubmission requested.", "Dr. Vikram Sharma (Officer)"));

            // Student 3: Ananya Roy
            User uAnanya = new User("ananya_roy", passwordEncoder.encode("admin123"), "ananya.roy@student.edu", "Ananya Roy", "+91 9123456789", studentRole);
            uAnanya = userRepository.save(uAnanya);

            Student stu3 = new Student();
            stu3.setUser(uAnanya);
            stu3.setStudentRollNo("STU-2024-003");
            stu3.setFullName("Ananya Roy");
            stu3.setEmail("ananya.roy@student.edu");
            stu3.setPhone("+91 9123456789");
            stu3.setInstitutionName("Indian Institute of Science");
            stu3.setDepartmentBranch("Biotechnology");
            stu3.setDegreeLevel(DegreeLevel.POSTGRADUATE);
            stu3.setCurrentYear(1);
            stu3.setGpaOrPercentage(8.90);
            stu3.setFamilyAnnualIncome(140000.0);
            stu3.setCategory(StudentCategory.GENERAL);
            stu3.setGender("FEMALE");
            stu3.setDateOfBirth(LocalDate.of(2002, 11, 20));
            stu3.setBankAccountNo("334455667788");
            stu3.setBankIfsc("HDFC0000543");
            stu3.setBankName("HDFC Bank");
            stu3 = studentRepository.save(stu3);

            // Application 3: Ananya -> Chancellor Merit Fellowship (DISBURSED)
            ScholarshipApplication app3 = new ScholarshipApplication();
            app3.setApplicationNumber("APP-2026-00103");
            app3.setStudent(stu3);
            app3.setScholarship(s3);
            app3.setStatus(ApplicationStatus.DISBURSED);
            app3.setEligibilityScore(94.0);
            app3.setEligibilityPassed(true);
            app3.setEligibilityRemarks("Top ranking PG scholar in Biotechnology. Verification and committee clearance completed.");
            app3.setSubmittedAt(LocalDateTime.now().minusDays(10));
            app3.setVerifiedAt(LocalDateTime.now().minusDays(5));
            app3.setVerifiedBy(officer);
            app3.setDecisionAt(LocalDateTime.now().minusDays(3));
            app3.setDecisionRemarks("Approved with highest recommendation by the Dean.");
            app3.setDisbursedAmount(75000.0);
            app3.setDisbursedAt(LocalDateTime.now().minusDays(1));
            app3.setDisbursementReference("UTR-SBI-2026-88992211");
            app3 = applicationRepository.save(app3);

            createDoc(app3, DocumentType.GRADE_MARKSHEET, "MSc_Sem2_Transcript.pdf", DocumentStatus.VERIFIED, "Distinction verified by Department Head.", officer);
            createDoc(app3, DocumentType.RECOMMENDATION_LETTER, "HOD_Recommendation_Letter.pdf", DocumentStatus.VERIFIED, "Strong endorsement on record.", officer);

            timelineRepository.save(new ApplicationTimeline(app3, "Application Submission", "COMPLETED", "Application submitted for Chancellor Excellence Fellowship.", "Ananya Roy (Student)"));
            timelineRepository.save(new ApplicationTimeline(app3, "Document Verification Desk", "COMPLETED", "All certificates verified without discrepancies.", "Dr. Vikram Sharma (Officer)"));
            timelineRepository.save(new ApplicationTimeline(app3, "Committee Review & Sanction", "COMPLETED", "Application sanctioned by Academic Senate.", "Prof. Arvind Patel (Dean)"));
            timelineRepository.save(new ApplicationTimeline(app3, "Direct Benefit Transfer (DBT)", "COMPLETED", "Grant of ₹75,000 credited to HDFC account. Ref: UTR-SBI-2026-88992211.", "Finance Department"));

            // Student 4: Vikram Singh
            User uVikram = new User("vikram_singh", passwordEncoder.encode("admin123"), "vikram.singh@student.edu", "Vikram Singh", "+91 9555667788", studentRole);
            uVikram = userRepository.save(uVikram);

            Student stu4 = new Student();
            stu4.setUser(uVikram);
            stu4.setStudentRollNo("STU-2024-004");
            stu4.setFullName("Vikram Singh");
            stu4.setEmail("vikram.singh@student.edu");
            stu4.setPhone("+91 9555667788");
            stu4.setInstitutionName("St. Xavier College");
            stu4.setDepartmentBranch("Commerce & Economics");
            stu4.setDegreeLevel(DegreeLevel.UNDERGRADUATE);
            stu4.setCurrentYear(3);
            stu4.setGpaOrPercentage(8.10);
            stu4.setFamilyAnnualIncome(310000.0);
            stu4.setCategory(StudentCategory.EWS);
            stu4.setGender("MALE");
            stu4.setDateOfBirth(LocalDate.of(2003, 8, 9));
            stu4.setBankAccountNo("556677889900");
            stu4.setBankIfsc("ICIC0001002");
            stu4.setBankName("ICICI Bank");
            stu4 = studentRepository.save(stu4);

            // Application 4: Vikram -> Central Sector Scheme (UNDER_COMMITTEE_REVIEW)
            ScholarshipApplication app4 = new ScholarshipApplication();
            app4.setApplicationNumber("APP-2026-00104");
            app4.setStudent(stu4);
            app4.setScholarship(s1);
            app4.setStatus(ApplicationStatus.UNDER_COMMITTEE_REVIEW);
            app4.setEligibilityScore(83.0);
            app4.setEligibilityPassed(true);
            app4.setEligibilityRemarks("Document scrutiny cleared with 100% compliance. Pending final approval meeting.");
            app4.setSubmittedAt(LocalDateTime.now().minusDays(6));
            app4.setVerifiedAt(LocalDateTime.now().minusDays(2));
            app4.setVerifiedBy(officer);
            app4.setDecisionRemarks("Shortlisted for final committee sanction.");
            app4 = applicationRepository.save(app4);

            createDoc(app4, DocumentType.INCOME_CERTIFICATE, "EWS_Income_Certificate_2026.pdf", DocumentStatus.VERIFIED, "Income verified under 3.5 Lakh bracket.", officer);
            createDoc(app4, DocumentType.GRADE_MARKSHEET, "BCom_Sem4_Transcript.pdf", DocumentStatus.VERIFIED, "8.10 CGPA confirmed.", officer);

            timelineRepository.save(new ApplicationTimeline(app4, "Application Submission", "COMPLETED", "Application submitted for Central Sector Scheme.", "Vikram Singh (Student)"));
            timelineRepository.save(new ApplicationTimeline(app4, "Document Verification Desk", "COMPLETED", "Verification cleared. Forwarded to Evaluation Committee.", "Dr. Vikram Sharma (Officer)"));
            timelineRepository.save(new ApplicationTimeline(app4, "Committee Review & Sanction", "IN_PROGRESS", "Under final review in current sanction batch.", "Prof. Arvind Patel (Dean)"));

            // 5. Initial Audit Logs
            auditLogRepository.save(new AuditLog(uPriya.getId(), "priya_sharma", AuditAction.APPLICATION_SUBMITTED, "ScholarshipApplication", app1.getId(), "Applied for CORP-WOMEN-STEM. Automated eligibility check passed."));
            auditLogRepository.save(new AuditLog(officer.getId(), "officer_sharma", AuditAction.DOCUMENT_VERIFIED, "ApplicationDocument", 1L, "Verified Income Certificate for Priya Sharma."));
            auditLogRepository.save(new AuditLog(officer.getId(), "officer_sharma", AuditAction.DOCUMENT_FLAGGED, "ApplicationDocument", 4L, "Requested resubmission of expired income certificate for Rahul Verma."));
            auditLogRepository.save(new AuditLog(committee.getId(), "committee_patel", AuditAction.APPLICATION_APPROVED, "ScholarshipApplication", app3.getId(), "Sanctioned Chancellor Excellence Fellowship for Ananya Roy."));
            auditLogRepository.save(new AuditLog(adminRole.getId(), "admin", AuditAction.DISBURSEMENT_INITIATED, "ScholarshipApplication", app3.getId(), "Processed DBT payment of ₹75,000 to HDFC Account 334455667788."));

            // 6. Initial Seed Notifications
            notificationRepository.save(new Notification(uPriya.getId(), stu1.getId(), app1.getApplicationNumber(), "Application Submitted Successfully", "Your application APP-2026-00101 is submitted and under document scrutiny.", "INFO"));
            notificationRepository.save(new Notification(uRahul.getId(), stu2.getId(), app2.getApplicationNumber(), "⚠️ Document Discrepancy Flagged", "Income Certificate for APP-2026-00102 was flagged: Expired FY 2023-24 certificate. Please resubmit.", "WARNING"));
            notificationRepository.save(new Notification(uAnanya.getId(), stu3.getId(), app3.getApplicationNumber(), "💰 DBT Grant Disbursed!", "Grant of ₹75,000 disbursed to HDFC Account. Ref: UTR-SBI-2026-88992211.", "SUCCESS"));
            notificationRepository.save(new Notification(uVikram.getId(), stu4.getId(), app4.getApplicationNumber(), "Verification Cleared", "Document scrutiny cleared for APP-2026-00104. Shortlisted for committee sanction.", "INFO"));
        }
    }

    private void createDoc(ScholarshipApplication app, DocumentType type, String filename, DocumentStatus status, String remarks, User officer) {
        ApplicationDocument doc = new ApplicationDocument();
        doc.setApplication(app);
        doc.setDocumentType(type);
        doc.setDocumentName(filename);
        doc.setDocumentPath("/uploads/docs/" + filename);
        doc.setVerificationStatus(status);
        doc.setRemarks(remarks);
        doc.setVerifiedBy(officer);
        doc.setUploadedAt(LocalDateTime.now().minusDays(3));
        if (status == DocumentStatus.VERIFIED || status == DocumentStatus.RESUBMISSION_REQUESTED) {
            doc.setVerifiedAt(LocalDateTime.now().minusDays(1));
        }
        documentRepository.save(doc);
    }
}
