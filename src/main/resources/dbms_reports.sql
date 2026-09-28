-- ===================================================================
-- SCHOLARTRACK: ADVANCED RELATIONAL DBMS SQL REPORTS
-- For College Project Evaluation & DBMS Viva Demonstrations
-- Concepts: Multi-table JOINs, Subqueries, GROUP BY, HAVING, CASE, Aggregates
-- ===================================================================

-- -------------------------------------------------------------
-- REPORT 1: Comprehensive Application Roster with Student, Scheme & Status
-- Concepts: Multiple INNER/LEFT JOINs, String Formatting, Ordering
-- -------------------------------------------------------------
SELECT 
    sa.application_number,
    st.student_roll_no,
    st.full_name AS student_name,
    st.institution_name,
    sc.code AS scholarship_code,
    sc.title AS scholarship_name,
    sc.provider_type,
    sa.status AS application_status,
    sa.eligibility_passed,
    sa.eligibility_score,
    sa.submitted_at
FROM scholarship_applications sa
INNER JOIN students st ON sa.student_id = st.id
INNER JOIN scholarships sc ON sa.scholarship_id = sc.id
ORDER BY sa.submitted_at DESC;

-- -------------------------------------------------------------
-- REPORT 2: Document Verification Bottleneck & Defect Detection
-- Problem Addressed: Manual verification delays and lack of visibility
-- Concepts: JOIN with Filtering, CASE Expression, Flag Tracking
-- -------------------------------------------------------------
SELECT 
    ad.id AS document_id,
    sa.application_number,
    st.full_name AS student_name,
    st.phone AS student_contact,
    ad.document_type,
    ad.document_name,
    ad.verification_status,
    COALESCE(ad.remarks, 'No remarks yet') AS verification_notes,
    CASE 
        WHEN ad.verification_status = 'RESUBMISSION_REQUESTED' THEN 'CRITICAL: Student Action Required'
        WHEN ad.verification_status = 'PENDING' THEN 'WARNING: Pending Officer Scrutiny'
        ELSE 'VERIFIED: Compliant'
    END AS reviewer_urgency
FROM application_documents ad
INNER JOIN scholarship_applications sa ON ad.application_id = sa.id
INNER JOIN students st ON sa.student_id = st.id
WHERE ad.verification_status IN ('PENDING', 'RESUBMISSION_REQUESTED')
ORDER BY ad.uploaded_at ASC;

-- -------------------------------------------------------------
-- REPORT 3: Scholarship Budget & Slot Utilization Analysis
-- Concepts: Aggregate Functions (COUNT, SUM, AVG), Mathematical Arithmetic
-- -------------------------------------------------------------
SELECT 
    sc.id AS scholarship_id,
    sc.code,
    sc.title,
    sc.financial_aid_amount AS grant_per_student,
    sc.total_slots,
    sc.slots_remaining,
    (sc.total_slots - sc.slots_remaining) AS slots_claimed,
    ROUND(((sc.total_slots - sc.slots_remaining) * 100.0 / sc.total_slots), 2) AS slot_utilization_pct,
    COUNT(sa.id) AS total_applicants,
    COALESCE(SUM(sa.disbursed_amount), 0.0) AS total_amount_disbursed,
    ROUND((sc.total_slots * sc.financial_aid_amount), 2) AS total_budget_allocated
FROM scholarships sc
LEFT JOIN scholarship_applications sa ON sc.id = sa.scholarship_id
GROUP BY sc.id, sc.code, sc.title, sc.financial_aid_amount, sc.total_slots, sc.slots_remaining
ORDER BY slot_utilization_pct DESC;

-- -------------------------------------------------------------
-- REPORT 4: Student Eligibility Compliance Matrix against Rules
-- Concepts: Multi-table JOIN with Conditional Evaluation and Rule Comparison
-- -------------------------------------------------------------
SELECT 
    st.full_name AS student_name,
    st.gpa_or_percentage AS student_gpa,
    st.family_annual_income AS annual_income,
    sc.title AS scholarship_applied,
    er.min_gpa_or_percentage AS required_min_gpa,
    er.max_family_income AS allowed_max_income,
    CASE 
        WHEN st.gpa_or_percentage >= er.min_gpa_or_percentage AND st.family_annual_income <= er.max_family_income 
            THEN 'ELIGIBLE - Criteria Fully Satisfied'
        WHEN st.gpa_or_percentage < er.min_gpa_or_percentage 
            THEN 'INELIGIBLE - GPA Below Threshold'
        WHEN st.family_annual_income > er.max_family_income 
            THEN 'INELIGIBLE - Income Exceeds Ceiling'
        ELSE 'INELIGIBLE - Category / Degree Mismatch'
    END AS eligibility_audit_verdict
FROM scholarship_applications sa
INNER JOIN students st ON sa.student_id = st.id
INNER JOIN scholarships sc ON sa.scholarship_id = sc.id
INNER JOIN eligibility_rules er ON sc.id = er.scholarship_id;

-- -------------------------------------------------------------
-- REPORT 5: Demographic & Social Category Distribution of Applicants
-- Concepts: GROUP BY, Aggregates, Cross-Category Tabulation
-- -------------------------------------------------------------
SELECT 
    st.category AS social_category,
    st.gender,
    COUNT(DISTINCT st.id) AS total_registered_students,
    COUNT(sa.id) AS total_applications_submitted,
    SUM(CASE WHEN sa.status = 'APPROVED' OR sa.status = 'DISBURSED' THEN 1 ELSE 0 END) AS awarded_scholarships,
    ROUND(AVG(st.family_annual_income), 2) AS avg_family_income,
    ROUND(AVG(st.gpa_or_percentage), 2) AS avg_academic_score
FROM students st
LEFT JOIN scholarship_applications sa ON st.id = sa.student_id
GROUP BY st.category, st.gender
ORDER BY total_applications_submitted DESC;

-- -------------------------------------------------------------
-- REPORT 6: Institution-wise Participation & Success Rate
-- Concepts: GROUP BY with HAVING clause, Percentage calculation
-- -------------------------------------------------------------
SELECT 
    st.institution_name,
    COUNT(DISTINCT st.id) AS participating_students,
    COUNT(sa.id) AS applications_lodged,
    SUM(CASE WHEN sa.status IN ('APPROVED', 'DISBURSED') THEN 1 ELSE 0 END) AS successful_grants,
    ROUND((SUM(CASE WHEN sa.status IN ('APPROVED', 'DISBURSED') THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(sa.id), 0)), 1) AS success_rate_pct
FROM students st
LEFT JOIN scholarship_applications sa ON st.id = sa.student_id
GROUP BY st.institution_name
HAVING COUNT(sa.id) > 0
ORDER BY applications_lodged DESC;

-- -------------------------------------------------------------
-- REPORT 7: Direct Benefit Transfer (DBT) Banking Disbursement Audit
-- Concepts: Financial Auditing, Banking reconciliation, Coalesce
-- -------------------------------------------------------------
SELECT 
    sa.application_number,
    st.full_name AS beneficiary_name,
    st.bank_name,
    st.bank_account_no,
    st.bank_ifsc,
    sc.title AS scholarship_scheme,
    sa.disbursed_amount,
    sa.disbursement_reference,
    sa.disbursed_at
FROM scholarship_applications sa
INNER JOIN students st ON sa.student_id = st.id
INNER JOIN scholarships sc ON sa.scholarship_id = sc.id
WHERE sa.status = 'DISBURSED'
ORDER BY sa.disbursed_at DESC;

-- -------------------------------------------------------------
-- REPORT 8: Academic Merit Composite Index & Priority Ranking
-- Concepts: Computed Columns, CASE Weighting, Rank Simulation
-- -------------------------------------------------------------
SELECT 
    sa.application_number,
    st.full_name AS student_name,
    st.gpa_or_percentage,
    st.family_annual_income,
    sc.title AS scholarship_title,
    ROUND(
        (st.gpa_or_percentage * 10.0) + 
        (CASE 
            WHEN st.family_annual_income < 200000 THEN 20.0
            WHEN st.family_annual_income < 400000 THEN 10.0
            ELSE 5.0 
         END), 2
    ) AS composite_need_merit_score,
    sa.status AS current_status
FROM scholarship_applications sa
INNER JOIN students st ON sa.student_id = st.id
INNER JOIN scholarships sc ON sa.scholarship_id = sc.id
ORDER BY composite_need_merit_score DESC;

-- -------------------------------------------------------------
-- REPORT 9: Non-Applying Students Who Are Eligible for Active Scholarships
-- Concepts: Subqueries (NOT IN / NOT EXISTS), Cross Joins with Criteria
-- -------------------------------------------------------------
SELECT 
    st.student_roll_no,
    st.full_name AS student_name,
    st.gpa_or_percentage,
    st.family_annual_income,
    sc.title AS eligible_scholarship_unclaimed
FROM students st
CROSS JOIN scholarships sc
INNER JOIN eligibility_rules er ON sc.id = er.scholarship_id
WHERE sc.is_active = TRUE
  AND st.gpa_or_percentage >= er.min_gpa_or_percentage
  AND st.family_annual_income <= er.max_family_income
  AND NOT EXISTS (
      SELECT 1 FROM scholarship_applications sa 
      WHERE sa.student_id = st.id AND sa.scholarship_id = sc.id
  )
ORDER BY st.gpa_or_percentage DESC;

-- -------------------------------------------------------------
-- REPORT 10: System Audit Logs of Administrative & Verification Activities
-- Concepts: Audit trail, Chronological traceability, User linkage
-- -------------------------------------------------------------
SELECT 
    al.id AS log_id,
    al.username,
    al.action,
    al.entity_name,
    al.entity_id,
    al.details,
    al.timestamp
FROM audit_logs al
ORDER BY al.timestamp DESC;
