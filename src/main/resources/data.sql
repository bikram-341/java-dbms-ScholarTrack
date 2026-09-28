-- ===================================================================
-- SCHOLARTRACK: SEED DATA SCRIPT
-- Populates Roles, Users, Students, Scholarships, Rules, Applications, Documents
-- ===================================================================

-- 1. Roles
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_VERIFICATION_OFFICER');
INSERT INTO roles (id, name) VALUES (3, 'ROLE_COMMITTEE_MEMBER');
INSERT INTO roles (id, name) VALUES (4, 'ROLE_STUDENT');

-- 2. Users (BCrypt password for 'admin123' and 'student123')
-- '$2a$10$wO082YkO1R6s6yZvhfXbte3FjS3K46Xv2p4K0U1i9h.RnmT6H29F.' is 'password123'
-- '$2a$10$7R6v7UeX9N1oXqfV1zXW0u9f6wBvVpG8q0K.K.8O9o1E5n1tW2L5u' is 'admin123'
INSERT INTO users (id, username, password, email, full_name, phone, role_id)
VALUES 
(1, 'admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'admin@scholartrack.edu', 'System Administrator', '+91 9876543210', 1),
(2, 'officer_sharma', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'v.sharma@scholarships.gov.in', 'Dr. Vikram Sharma (Verification Officer)', '+91 9811223344', 2),
(3, 'committee_patel', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'dean.patel@university.edu', 'Prof. Arvind Patel (Scholarship Dean)', '+91 9877001122', 3),
(4, 'priya_sharma', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'priya.sharma@student.edu', 'Priya Sharma', '+91 9988776655', 4),
(5, 'rahul_verma', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'rahul.verma@student.edu', 'Rahul Verma', '+91 9876123450', 4),
(6, 'ananya_roy', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ananya.roy@student.edu', 'Ananya Roy', '+91 9123456789', 4),
(7, 'vikram_singh', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'vikram.singh@student.edu', 'Vikram Singh', '+91 9555667788', 4)
ON DUPLICATE KEY UPDATE username=username;

-- 3. Students
INSERT INTO students (id, user_id, student_roll_no, full_name, email, phone, institution_name, department_branch, degree_level, current_year, gpa_or_percentage, family_annual_income, category, gender, date_of_birth, bank_account_no, bank_ifsc, bank_name)
VALUES
(1, 4, 'STU-2024-001', 'Priya Sharma', 'priya.sharma@student.edu', '+91 9988776655', 'National Institute of Technology', 'Computer Science & Engineering', 'UNDERGRADUATE', 3, 9.20, 180000.0, 'OBC', 'FEMALE', '2004-05-14', '987654321012', 'SBIN0001234', 'State Bank of India'),
(2, 5, 'STU-2024-002', 'Rahul Verma', 'rahul.verma@student.edu', '+91 9876123450', 'Delhi Technological University', 'Mechanical Engineering', 'UNDERGRADUATE', 2, 7.80, 220000.0, 'SC', 'MALE', '2005-02-18', '112233445566', 'PUNB0123400', 'Punjab National Bank'),
(3, 6, 'STU-2024-003', 'Ananya Roy', 'ananya.roy@student.edu', '+91 9123456789', 'Indian Institute of Science', 'Biotechnology', 'POSTGRADUATE', 1, 8.90, 140000.0, 'GENERAL', 'FEMALE', '2002-11-20', '334455667788', 'HDFC0000543', 'HDFC Bank'),
(4, 7, 'STU-2024-004', 'Vikram Singh', 'vikram.singh@student.edu', '+91 9555667788', 'St. Xavier College', 'Commerce & Economics', 'UNDERGRADUATE', 3, 8.10, 310000.0, 'EWS', 'MALE', '2003-08-09', '556677889900', 'ICIC0001002', 'ICICI Bank')
ON DUPLICATE KEY UPDATE student_roll_no=student_roll_no;

-- 4. Scholarships
INSERT INTO scholarships (id, code, title, description, provider_type, scholarship_category, financial_aid_amount, total_slots, slots_remaining, application_deadline, academic_year, is_active)
VALUES
(1, 'GOV-CSSS-2026', 'Central Sector Scheme of Scholarship for College and University Students', 'Department of Higher Education initiative supporting brilliant students from economically challenged backgrounds across Indian colleges.', 'GOVERNMENT', 'MERIT_BASED', 50000.0, 500, 485, '2026-12-31', '2026-2027', true),
(2, 'GOV-PMS-2026', 'Post-Matric National Scholarship for SC/ST/OBC Students', 'Government funded financial assistance covering tuition fees, maintenance allowance, and book grants for marginalized communities.', 'GOVERNMENT', 'NEED_BASED', 65000.0, 1000, 970, '2026-11-30', '2026-2027', true),
(3, 'INST-CHANCELLOR-EXCELLENCE', 'Chancellor\'s Institutional Merit Fellowship', 'University grant awarded to undergraduate and postgraduate scholars maintaining distinction-level academic records.', 'INSTITUTIONAL', 'MERIT_BASED', 75000.0, 150, 142, '2026-10-31', '2026-2027', true),
(4, 'CORP-WOMEN-STEM', 'National Women in STEM Leadership Grant', 'Empowering exceptional female scholars pursuing Computer Science, Engineering, Biotechnology, and Mathematics.', 'CORPORATE_CSR', 'WOMEN_IN_STEM', 100000.0, 80, 72, '2026-12-15', '2026-2027', true),
(5, 'INST-NEED-EWS', 'Institutional Tuition Waiver & Book Grant for EWS', 'College internal assistance designed to eliminate drop-outs due to severe financial emergencies.', 'INSTITUTIONAL', 'NEED_BASED', 40000.0, 200, 195, '2026-11-15', '2026-2027', true)
ON DUPLICATE KEY UPDATE code=code;

-- 5. Eligibility Rules
INSERT INTO eligibility_rules (id, scholarship_id, min_gpa_or_percentage, max_family_income, eligible_categories, eligible_degrees, eligible_gender, min_age, max_age, required_documents, rule_description)
VALUES
(1, 1, 7.50, 450000.0, 'ALL', 'UNDERGRADUATE,POSTGRADUATE', 'ANY', 17, 26, 'INCOME_CERTIFICATE,GRADE_MARKSHEET,AADHAAR_IDENTITY,COLLEGE_FEE_RECEIPT', 'Min 7.5 GPA (or 75%), family income <= 4.5 Lakhs/year, open to all degree candidates.'),
(2, 2, 6.00, 250000.0, 'SC,ST,OBC', 'UNDERGRADUATE,POSTGRADUATE,DIPLOMA', 'ANY', 16, 30, 'INCOME_CERTIFICATE,CASTE_CERTIFICATE,GRADE_MARKSHEET,AADHAAR_IDENTITY,BANK_PASSBOOK', 'Reserved for SC, ST, and OBC candidates with family income <= 2.5 Lakhs/year and min 6.0 GPA.'),
(3, 3, 8.50, 800000.0, 'ALL', 'UNDERGRADUATE,POSTGRADUATE', 'ANY', 17, 28, 'GRADE_MARKSHEET,RECOMMENDATION_LETTER,COLLEGE_FEE_RECEIPT', 'Strict academic excellence award. Min 8.5 GPA or top 5% in department.'),
(4, 4, 8.00, 600000.0, 'ALL', 'UNDERGRADUATE,POSTGRADUATE,PHD', 'FEMALE', 17, 30, 'INCOME_CERTIFICATE,GRADE_MARKSHEET,RECOMMENDATION_LETTER,AADHAAR_IDENTITY', 'Female candidates enrolled in STEM domains with min 8.0 GPA and demonstrated research interest.'),
(5, 5, 6.00, 200000.0, 'ALL', 'UNDERGRADUATE,DIPLOMA', 'ANY', 16, 25, 'INCOME_CERTIFICATE,COLLEGE_FEE_RECEIPT,AADHAAR_IDENTITY', 'Income under 2 Lakhs/year with valid tehsildar income certificate.')
ON DUPLICATE KEY UPDATE scholarship_id=scholarship_id;

-- 6. Scholarship Applications
INSERT INTO scholarship_applications (id, application_number, student_id, scholarship_id, status, eligibility_score, eligibility_passed, eligibility_remarks, submitted_at, verified_at, verified_by_id, decision_at, decision_remarks, disbursed_amount, disbursed_at, disbursement_reference)
VALUES
(1, 'APP-2026-00101', 1, 4, 'UNDER_DOCUMENT_VERIFICATION', 92.5, true, 'Profile meets GPA (9.2 >= 8.0) and Income criteria. Documents under scrutiny by verification desk.', CURRENT_TIMESTAMP, NULL, 2, NULL, NULL, 0.0, NULL, NULL),
(2, 'APP-2026-00102', 2, 2, 'DOCUMENTS_FLAGGED', 78.0, true, 'Eligible for Post-Matric scheme. Income certificate requires recent renewal stamp.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, NULL, 'Income certificate older than 1 year. Upload updated certificate.', 0.0, NULL, NULL),
(3, 'APP-2026-00103', 3, 3, 'DISBURSED', 94.0, true, 'Top ranking PG scholar in Biotechnology. Verification and committee clearance completed.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, CURRENT_TIMESTAMP, 'Approved with highest recommendation by the Dean.', 75000.0, CURRENT_TIMESTAMP, 'UTR-SBI-2026-88992211'),
(4, 'APP-2026-00104', 4, 1, 'UNDER_COMMITTEE_REVIEW', 83.0, true, 'Document scrutiny cleared with 100% compliance. Pending final approval meeting.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, NULL, 'Shortlisted for final sanction.', 0.0, NULL, NULL)
ON DUPLICATE KEY UPDATE application_number=application_number;

-- 7. Application Documents
INSERT INTO application_documents (id, application_id, document_type, document_name, document_path, verification_status, remarks, verified_by_id, uploaded_at, verified_at)
VALUES
(1, 1, 'INCOME_CERTIFICATE', 'Income_Certificate_2026_Priya.pdf', '/uploads/docs/income_priya.pdf', 'VERIFIED', 'Valid certificate issued by Sub-Divisional Magistrate.', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, 'GRADE_MARKSHEET', 'BTech_Sem4_Marksheet.pdf', '/uploads/docs/marksheet_priya.pdf', 'VERIFIED', 'Verified against university exam portal: 9.20 CGPA confirmed.', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 1, 'AADHAAR_IDENTITY', 'Aadhaar_Priya_Sharma.pdf', '/uploads/docs/aadhaar_priya.pdf', 'PENDING', 'Pending biometric OCR match.', NULL, CURRENT_TIMESTAMP, NULL),

(4, 2, 'INCOME_CERTIFICATE', 'Tehsildar_Income_2024.pdf', '/uploads/docs/income_rahul.pdf', 'RESUBMISSION_REQUESTED', 'Certificate issued in 2024 is expired. Please submit current fiscal year (2026-27) document.', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 2, 'CASTE_CERTIFICATE', 'SC_Category_Certificate.pdf', '/uploads/docs/caste_rahul.pdf', 'VERIFIED', 'Authentic government caste certificate verified.', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(6, 3, 'GRADE_MARKSHEET', 'MSc_Sem2_Transcript.pdf', '/uploads/docs/marksheet_ananya.pdf', 'VERIFIED', 'Distinction verified by Department Head.', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 3, 'RECOMMENDATION_LETTER', 'HOD_Recommendation_Letter.pdf', '/uploads/docs/rec_ananya.pdf', 'VERIFIED', 'Strong endorsement on record.', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(8, 4, 'INCOME_CERTIFICATE', 'EWS_Income_Certificate_2026.pdf', '/uploads/docs/income_vikram.pdf', 'VERIFIED', 'Income verified under 3.5 Lakh bracket.', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 4, 'GRADE_MARKSHEET', 'BCom_Sem4_Transcript.pdf', '/uploads/docs/marksheet_vikram.pdf', 'VERIFIED', '8.10 CGPA confirmed.', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE document_name=document_name;

-- 8. Application Timelines (Step-by-step visibility for students)
INSERT INTO application_timelines (id, application_id, stage_name, status, comments, updated_by_name, timestamp)
VALUES
(1, 1, 'Application Submission', 'COMPLETED', 'Application received along with 3 uploaded verification files.', 'Priya Sharma (Student)', CURRENT_TIMESTAMP),
(2, 1, 'Document Verification Desk', 'IN_PROGRESS', 'Income certificate and marksheets verified. Aadhaar pending review.', 'Dr. Vikram Sharma (Officer)', CURRENT_TIMESTAMP),

(3, 2, 'Application Submission', 'COMPLETED', 'Application submitted for Post-Matric SC/ST/OBC scheme.', 'Rahul Verma (Student)', CURRENT_TIMESTAMP),
(4, 2, 'Document Verification Desk', 'REJECTED', 'Income certificate flagged: expired date. Resubmission requested.', 'Dr. Vikram Sharma (Officer)', CURRENT_TIMESTAMP),

(5, 3, 'Application Submission', 'COMPLETED', 'Application submitted for Chancellor Excellence Fellowship.', 'Ananya Roy (Student)', CURRENT_TIMESTAMP),
(6, 3, 'Document Verification Desk', 'COMPLETED', 'All certificates verified without discrepancies.', 'Dr. Vikram Sharma (Officer)', CURRENT_TIMESTAMP),
(7, 3, 'Committee Review & Sanction', 'COMPLETED', 'Application ranked in top 3 and sanctioned by Academic Senate.', 'Prof. Arvind Patel (Dean)', CURRENT_TIMESTAMP),
(8, 3, 'Direct Benefit Transfer (DBT)', 'COMPLETED', 'Grant of INR 75,000 credited to student account. Ref: UTR-SBI-2026-88992211.', 'Finance Department', CURRENT_TIMESTAMP),

(9, 4, 'Application Submission', 'COMPLETED', 'Application submitted for Central Sector Scheme.', 'Vikram Singh (Student)', CURRENT_TIMESTAMP),
(10, 4, 'Document Verification Desk', 'COMPLETED', 'Verification cleared. Forwarded to Evaluation Committee.', 'Dr. Vikram Sharma (Officer)', CURRENT_TIMESTAMP),
(11, 4, 'Committee Review & Sanction', 'IN_PROGRESS', 'Under final review in current sanction batch.', 'Prof. Arvind Patel (Dean)', CURRENT_TIMESTAMP);

-- 9. Audit Logs
INSERT INTO audit_logs (id, user_id, username, action, entity_name, entity_id, details, timestamp)
VALUES
(1, 4, 'priya_sharma', 'APPLICATION_SUBMITTED', 'ScholarshipApplication', 1, 'Applied for CORP-WOMEN-STEM. Automated eligibility check passed.', CURRENT_TIMESTAMP),
(2, 2, 'officer_sharma', 'DOCUMENT_VERIFIED', 'ApplicationDocument', 1, 'Verified Income Certificate for Priya Sharma.', CURRENT_TIMESTAMP),
(3, 2, 'officer_sharma', 'DOCUMENT_FLAGGED', 'ApplicationDocument', 4, 'Requested resubmission of expired income certificate for Rahul Verma.', CURRENT_TIMESTAMP),
(4, 3, 'committee_patel', 'APPLICATION_APPROVED', 'ScholarshipApplication', 3, 'Sanctioned Chancellor Excellence Fellowship for Ananya Roy.', CURRENT_TIMESTAMP),
(5, 1, 'admin', 'DISBURSEMENT_INITIATED', 'ScholarshipApplication', 3, 'Processed DBT payment of 75000 INR to SBI Account 334455667788.', CURRENT_TIMESTAMP);
