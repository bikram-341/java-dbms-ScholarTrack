-- ===================================================================
-- SCHOLARTRACK: SCHOLARSHIP APPLICATION & ELIGIBILITY TRACKER
-- Relational Database Schema Definition (MySQL & H2 Compatible)
-- ===================================================================

-- 1. Roles Table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 2. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(60) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    phone VARCHAR(25),
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT
);

-- 3. Students Table
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE,
    student_roll_no VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(25) NOT NULL,
    institution_name VARCHAR(150) NOT NULL,
    department_branch VARCHAR(100) NOT NULL,
    degree_level VARCHAR(50) NOT NULL, -- UNDERGRADUATE, POSTGRADUATE, DIPLOMA, PHD
    current_year INT NOT NULL,
    gpa_or_percentage DOUBLE NOT NULL,
    family_annual_income DOUBLE NOT NULL,
    category VARCHAR(50) NOT NULL, -- GENERAL, OBC, SC, ST, EWS
    gender VARCHAR(20) NOT NULL, -- MALE, FEMALE, OTHER
    date_of_birth DATE,
    bank_account_no VARCHAR(50),
    bank_ifsc VARCHAR(30),
    bank_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 4. Scholarships Table
CREATE TABLE IF NOT EXISTS scholarships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    provider_type VARCHAR(50) NOT NULL, -- GOVERNMENT, INSTITUTIONAL, CORPORATE_CSR
    scholarship_category VARCHAR(50) NOT NULL, -- NEED_BASED, MERIT_BASED, WOMEN_IN_STEM, MINORITY_COMMUNITY, TALENT_SPORTS, DIFFERENTLY_ABLED
    financial_aid_amount DOUBLE NOT NULL,
    total_slots INT NOT NULL,
    slots_remaining INT NOT NULL,
    application_deadline DATE NOT NULL,
    academic_year VARCHAR(30) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Eligibility Rules Table
CREATE TABLE IF NOT EXISTS eligibility_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    scholarship_id BIGINT NOT NULL UNIQUE,
    min_gpa_or_percentage DOUBLE NOT NULL,
    max_family_income DOUBLE NOT NULL,
    eligible_categories VARCHAR(100) DEFAULT 'ALL', -- ALL or comma-separated e.g. SC,ST,OBC
    eligible_degrees VARCHAR(150) DEFAULT 'ALL', -- ALL or UNDERGRADUATE,POSTGRADUATE
    eligible_gender VARCHAR(30) DEFAULT 'ANY', -- ANY, FEMALE, MALE
    min_age INT DEFAULT 16,
    max_age INT DEFAULT 35,
    required_documents VARCHAR(255) NOT NULL, -- e.g. INCOME_CERTIFICATE,GRADE_MARKSHEET,AADHAAR_IDENTITY,COLLEGE_FEE_RECEIPT
    rule_description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rules_scholarship FOREIGN KEY (scholarship_id) REFERENCES scholarships(id) ON DELETE CASCADE
);

-- 6. Scholarship Applications Table
CREATE TABLE IF NOT EXISTS scholarship_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_number VARCHAR(50) NOT NULL UNIQUE,
    student_id BIGINT NOT NULL,
    scholarship_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL, -- SUBMITTED, UNDER_DOCUMENT_VERIFICATION, DOCUMENTS_FLAGGED, ELIGIBILITY_VERIFIED, ELIGIBILITY_FAILED, UNDER_COMMITTEE_REVIEW, APPROVED, REJECTED, DISBURSED
    eligibility_score DOUBLE DEFAULT 0.0,
    eligibility_passed BOOLEAN DEFAULT FALSE,
    eligibility_remarks VARCHAR(500),
    is_flagged BOOLEAN DEFAULT FALSE,
    flag_reason VARCHAR(500),
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    verified_at TIMESTAMP,
    verified_by_id BIGINT,
    decision_at TIMESTAMP,
    decision_remarks VARCHAR(500),
    disbursed_amount DOUBLE DEFAULT 0.0,
    disbursed_at TIMESTAMP,
    disbursement_reference VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_app_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_app_scholarship FOREIGN KEY (scholarship_id) REFERENCES scholarships(id) ON DELETE CASCADE,
    CONSTRAINT fk_app_verifier FOREIGN KEY (verified_by_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 7. Application Documents Table
CREATE TABLE IF NOT EXISTS application_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL, -- INCOME_CERTIFICATE, GRADE_MARKSHEET, CASTE_CERTIFICATE, AADHAAR_IDENTITY, COLLEGE_FEE_RECEIPT, RECOMMENDATION_LETTER
    document_name VARCHAR(150) NOT NULL,
    document_path VARCHAR(255) NOT NULL,
    verification_status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, VERIFIED, REJECTED, RESUBMISSION_REQUESTED
    remarks VARCHAR(500),
    verified_by_id BIGINT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    verified_at TIMESTAMP,
    CONSTRAINT fk_doc_application FOREIGN KEY (application_id) REFERENCES scholarship_applications(id) ON DELETE CASCADE,
    CONSTRAINT fk_doc_verifier FOREIGN KEY (verified_by_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 8. Application Timeline Table (Eliminating "Black Box" delays with complete stage audit)
CREATE TABLE IF NOT EXISTS application_timelines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    stage_name VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL, -- COMPLETED, IN_PROGRESS, REJECTED, PENDING
    comments VARCHAR(500),
    updated_by_name VARCHAR(100),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_timeline_application FOREIGN KEY (application_id) REFERENCES scholarship_applications(id) ON DELETE CASCADE
);

-- 9. Audit Logs Table
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(60),
    action VARCHAR(50) NOT NULL,
    entity_name VARCHAR(60) NOT NULL,
    entity_id BIGINT,
    details VARCHAR(1000),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for Query Performance and Relational Integrity
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_students_roll ON students(student_roll_no);
CREATE INDEX IF NOT EXISTS idx_students_email ON students(email);
CREATE INDEX IF NOT EXISTS idx_scholarships_code ON scholarships(code);
CREATE INDEX IF NOT EXISTS idx_scholarships_deadline ON scholarships(application_deadline);
CREATE INDEX IF NOT EXISTS idx_app_student_id ON scholarship_applications(student_id);
CREATE INDEX IF NOT EXISTS idx_app_scholarship_id ON scholarship_applications(scholarship_id);
CREATE INDEX IF NOT EXISTS idx_app_status ON scholarship_applications(status);
CREATE INDEX IF NOT EXISTS idx_docs_application_id ON application_documents(application_id);
CREATE INDEX IF NOT EXISTS idx_docs_status ON application_documents(verification_status);
CREATE INDEX IF NOT EXISTS idx_timelines_app_id ON application_timelines(application_id);
CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON audit_logs(timestamp);

-- ===================================================================
-- DATABASE VIEWS (Core DBMS Concept)
-- ===================================================================

-- View 1: Complete Application Overview with Student & Scholarship Details
CREATE OR REPLACE VIEW view_application_overview AS
SELECT 
    sa.id AS application_id,
    sa.application_number,
    st.full_name AS student_name,
    st.student_roll_no,
    st.institution_name,
    st.gpa_or_percentage,
    st.family_annual_income,
    st.category AS student_category,
    sc.title AS scholarship_title,
    sc.code AS scholarship_code,
    sc.provider_type,
    sc.financial_aid_amount,
    sa.status AS application_status,
    sa.eligibility_passed,
    sa.eligibility_score,
    sa.submitted_at,
    sa.verified_at,
    sa.decision_at,
    sa.disbursed_amount
FROM scholarship_applications sa
JOIN students st ON sa.student_id = st.id
JOIN scholarships sc ON sa.scholarship_id = sc.id;

-- View 2: Pending Document Verification Queue (Removes manual delays)
CREATE OR REPLACE VIEW view_pending_verifications AS
SELECT 
    ad.id AS document_id,
    sa.application_number,
    st.full_name AS student_name,
    st.institution_name,
    sc.title AS scholarship_title,
    ad.document_type,
    ad.document_name,
    ad.verification_status,
    ad.uploaded_at
FROM application_documents ad
JOIN scholarship_applications sa ON ad.application_id = sa.id
JOIN students st ON sa.student_id = st.id
JOIN scholarships sc ON sa.scholarship_id = sc.id
WHERE ad.verification_status IN ('PENDING', 'RESUBMISSION_REQUESTED');

-- View 3: Scholarship Award & Disbursement Summary
CREATE OR REPLACE VIEW view_disbursement_summary AS
SELECT 
    sc.id AS scholarship_id,
    sc.code AS scholarship_code,
    sc.title AS scholarship_title,
    sc.provider_type,
    COUNT(sa.id) AS total_applications,
    SUM(CASE WHEN sa.status = 'APPROVED' THEN 1 ELSE 0 END) AS approved_count,
    SUM(CASE WHEN sa.status = 'DISBURSED' THEN 1 ELSE 0 END) AS disbursed_count,
    COALESCE(SUM(sa.disbursed_amount), 0.0) AS total_disbursed_amount,
    sc.total_slots - sc.slots_remaining AS slots_awarded
FROM scholarships sc
LEFT JOIN scholarship_applications sa ON sc.id = sa.scholarship_id
GROUP BY sc.id, sc.code, sc.title, sc.provider_type, sc.total_slots, sc.slots_remaining;
