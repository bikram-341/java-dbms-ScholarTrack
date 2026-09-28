package com.scholartrack.service;

import com.scholartrack.model.dto.ReportResultDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    private final JdbcTemplate jdbcTemplate;

    private static final Map<String, ReportConfig> REPORT_MAP = new LinkedHashMap<>();

    private record ReportConfig(String title, String description, String sql) {}

    static {
        REPORT_MAP.put("report-1", new ReportConfig(
                "Comprehensive Application Roster with Student, Scheme & Status",
                "Demonstrates multi-table INNER JOINs, string formatting, and chronological sorting across applications, students, and scholarships.",
                "SELECT sa.application_number AS APP_NO, st.student_roll_no AS ROLL_NO, st.full_name AS STUDENT_NAME, st.institution_name AS COLLEGE, sc.code AS SCHOLARSHIP_CODE, sc.title AS SCHEME_TITLE, sc.provider_type AS PROVIDER, sa.status AS STATUS, sa.eligibility_score AS ELIG_SCORE, sa.submitted_at AS SUBMITTED_AT FROM scholarship_applications sa INNER JOIN students st ON sa.student_id = st.id INNER JOIN scholarships sc ON sa.scholarship_id = sc.id ORDER BY sa.submitted_at DESC"
        ));

        REPORT_MAP.put("report-2", new ReportConfig(
                "Document Verification Bottleneck & Reviewer Queue",
                "Highlights manual verification roadblocks, flagging documents that require student resubmission or officer review.",
                "SELECT ad.id AS DOC_ID, sa.application_number AS APP_NO, st.full_name AS STUDENT, st.phone AS PHONE, ad.document_type AS DOC_TYPE, ad.document_name AS FILENAME, ad.verification_status AS STATUS, COALESCE(ad.remarks, 'No remarks yet') AS REVIEW_NOTES, CASE WHEN ad.verification_status = 'RESUBMISSION_REQUESTED' THEN 'CRITICAL: Resubmission Req.' WHEN ad.verification_status = 'PENDING' THEN 'WARNING: Pending Review' ELSE 'VERIFIED' END AS ACTION_PRIORITY FROM application_documents ad INNER JOIN scholarship_applications sa ON ad.application_id = sa.id INNER JOIN students st ON sa.student_id = st.id WHERE ad.verification_status IN ('PENDING', 'RESUBMISSION_REQUESTED') ORDER BY ad.uploaded_at ASC"
        ));

        REPORT_MAP.put("report-3", new ReportConfig(
                "Scholarship Budget & Slot Utilization Metrics",
                "Aggregate queries using COUNT, SUM, AVG, and percentage calculations to analyze quota saturation and financial disbursements.",
                "SELECT sc.code AS SCHEME_CODE, sc.title AS SCHEME_NAME, sc.financial_aid_amount AS AID_PER_STUDENT, sc.total_slots AS TOTAL_SLOTS, sc.slots_remaining AS REMAINING, (sc.total_slots - sc.slots_remaining) AS CLAIMED, ROUND(((sc.total_slots - sc.slots_remaining) * 100.0 / NULLIF(sc.total_slots, 0)), 1) AS UTILIZATION_PCT, COUNT(sa.id) AS APPLICANTS, COALESCE(SUM(sa.disbursed_amount), 0.0) AS TOTAL_DISBURSED FROM scholarships sc LEFT JOIN scholarship_applications sa ON sc.id = sa.scholarship_id GROUP BY sc.id, sc.code, sc.title, sc.financial_aid_amount, sc.total_slots, sc.slots_remaining ORDER BY UTILIZATION_PCT DESC"
        ));

        REPORT_MAP.put("report-4", new ReportConfig(
                "Rule Evaluation Compliance Matrix",
                "Evaluates student GPA and annual income against rules to show pass/fail criteria and disqualification rationale.",
                "SELECT st.full_name AS STUDENT, st.gpa_or_percentage AS STUDENT_GPA, st.family_annual_income AS INCOME_INR, sc.title AS SCHEME_APPLIED, er.min_gpa_or_percentage AS MIN_GPA_REQ, er.max_family_income AS MAX_INCOME_ALLOW, CASE WHEN st.gpa_or_percentage >= er.min_gpa_or_percentage AND st.family_annual_income <= er.max_family_income THEN 'ELIGIBLE - Fully Satisfied' WHEN st.gpa_or_percentage < er.min_gpa_or_percentage THEN 'INELIGIBLE - GPA Deficit' WHEN st.family_annual_income > er.max_family_income THEN 'INELIGIBLE - Income Exceeded' ELSE 'INELIGIBLE - Mismatch' END AS AUDIT_VERDICT FROM scholarship_applications sa INNER JOIN students st ON sa.student_id = st.id INNER JOIN scholarships sc ON sa.scholarship_id = sc.id INNER JOIN eligibility_rules er ON sc.id = er.scholarship_id"
        ));

        REPORT_MAP.put("report-5", new ReportConfig(
                "Demographic & Social Category Inclusivity Distribution",
                "Analyzes application distribution and awards across caste categories, gender, and average family income brackets.",
                "SELECT st.category AS CATEGORY, st.gender AS GENDER, COUNT(DISTINCT st.id) AS TOTAL_STUDENTS, COUNT(sa.id) AS APPLICATIONS_LODGED, SUM(CASE WHEN sa.status IN ('APPROVED', 'DISBURSED') THEN 1 ELSE 0 END) AS GRANTS_AWARDED, ROUND(AVG(st.family_annual_income), 0) AS AVG_FAMILY_INCOME, ROUND(AVG(st.gpa_or_percentage), 2) AS AVG_GPA FROM students st LEFT JOIN scholarship_applications sa ON st.id = sa.student_id GROUP BY st.category, st.gender ORDER BY APPLICATIONS_LODGED DESC"
        ));

        REPORT_MAP.put("report-6", new ReportConfig(
                "Institution-wise Application Volume & Grant Success Rate",
                "Demonstrates GROUP BY with HAVING clause to evaluate collegiate participation and scholarship success rates.",
                "SELECT st.institution_name AS COLLEGE_NAME, COUNT(DISTINCT st.id) AS STUDENT_COUNT, COUNT(sa.id) AS APPLICATIONS_SUBMITTED, SUM(CASE WHEN sa.status IN ('APPROVED', 'DISBURSED') THEN 1 ELSE 0 END) AS GRANTS_SANCTIONED, ROUND((SUM(CASE WHEN sa.status IN ('APPROVED', 'DISBURSED') THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(sa.id), 0)), 1) AS SUCCESS_RATE_PCT FROM students st LEFT JOIN scholarship_applications sa ON st.id = sa.student_id GROUP BY st.institution_name HAVING COUNT(sa.id) > 0 ORDER BY APPLICATIONS_SUBMITTED DESC"
        ));

        REPORT_MAP.put("report-7", new ReportConfig(
                "Direct Benefit Transfer (DBT) Banking Disbursement Audit",
                "Reconciles payments disbursed to student bank accounts with UTR references and timestamps for financial compliance.",
                "SELECT sa.application_number AS APP_REF, st.full_name AS BENEFICIARY, st.bank_name AS BANK, st.bank_account_no AS ACCOUNT_NO, st.bank_ifsc AS IFSC_CODE, sc.title AS SCHEME, sa.disbursed_amount AS AMOUNT_INR, sa.disbursement_reference AS UTR_REFERENCE, sa.disbursed_at AS DISBURSED_ON FROM scholarship_applications sa INNER JOIN students st ON sa.student_id = st.id INNER JOIN scholarships sc ON sa.scholarship_id = sc.id WHERE sa.status = 'DISBURSED' ORDER BY sa.disbursed_at DESC"
        ));

        REPORT_MAP.put("report-8", new ReportConfig(
                "Academic Merit & Financial Need Priority Ranking Index",
                "Calculates composite priority index combining GPA and affirmative economic weights for ranking applications.",
                "SELECT sa.application_number AS APP_NO, st.full_name AS STUDENT, st.gpa_or_percentage AS GPA, st.family_annual_income AS INCOME_INR, sc.title AS SCHOLARSHIP, ROUND((st.gpa_or_percentage * 10.0) + (CASE WHEN st.family_annual_income < 200000 THEN 20.0 WHEN st.family_annual_income < 400000 THEN 10.0 ELSE 5.0 END), 2) AS COMPOSITE_INDEX, sa.status AS CURRENT_STAGE FROM scholarship_applications sa INNER JOIN students st ON sa.student_id = st.id INNER JOIN scholarships sc ON sa.scholarship_id = sc.id ORDER BY COMPOSITE_INDEX DESC"
        ));

        REPORT_MAP.put("report-9", new ReportConfig(
                "Unclaimed Opportunities: Eligible Students Without Applications",
                "DBMS Subquery utilizing NOT EXISTS and CROSS JOIN to discover students eligible for active schemes who haven't applied.",
                "SELECT st.student_roll_no AS ROLL_NO, st.full_name AS STUDENT, st.gpa_or_percentage AS GPA, st.family_annual_income AS INCOME_INR, sc.title AS ELIGIBLE_UNCLAIMED_SCHEME FROM students st CROSS JOIN scholarships sc INNER JOIN eligibility_rules er ON sc.id = er.scholarship_id WHERE sc.is_active = TRUE AND st.gpa_or_percentage >= er.min_gpa_or_percentage AND st.family_annual_income <= er.max_family_income AND NOT EXISTS (SELECT 1 FROM scholarship_applications sa WHERE sa.student_id = st.id AND sa.scholarship_id = sc.id) ORDER BY st.gpa_or_percentage DESC"
        ));

        REPORT_MAP.put("report-10", new ReportConfig(
                "System Audit Trail & Chronological Action Log",
                "Tracks all user and officer events, verification actions, and status updates for forensic auditability.",
                "SELECT id AS LOG_ID, username AS USER_ACTOR, action AS EVENT_ACTION, entity_name AS ENTITY, entity_id AS ENTITY_ID, details AS ACTION_DETAILS, timestamp AS LOGGED_AT FROM audit_logs ORDER BY timestamp DESC"
        ));
    }

    public ReportServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Map<String, String>> getAvailableReports() {
        List<Map<String, String>> list = new ArrayList<>();
        REPORT_MAP.forEach((id, cfg) -> {
            Map<String, String> item = new HashMap<>();
            item.put("id", id);
            item.put("title", cfg.title());
            item.put("description", cfg.description());
            list.add(item);
        });
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public ReportResultDto runReport(String reportId) {
        ReportConfig cfg = REPORT_MAP.get(reportId);
        if (cfg == null) {
            throw new IllegalArgumentException("Unknown report identifier: " + reportId);
        }

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(cfg.sql());
        List<String> columns = new ArrayList<>();
        if (!rows.isEmpty()) {
            columns.addAll(rows.get(0).keySet());
        }

        return new ReportResultDto(reportId, cfg.title(), cfg.description(), columns, rows);
    }
}
