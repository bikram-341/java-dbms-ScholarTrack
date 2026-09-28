// ===================================================================
// SCHOLARTRACK - FRONTEND INTERACTION CONTROLLER
// ===================================================================

let allScholarships = [];
let allStudents = [];
let currentUser = null;

// Notification State
let notifications = [];
let knownNotificationIds = new Set();
let isFirstNotifLoad = true;

// Pagination & Sorting State
let scholarshipPage = 0;
let scholarshipTotalPages = 1;
let committeePage = 0;
let committeeTotalPages = 1;

document.addEventListener('DOMContentLoaded', () => {
    initTabs();
    initEventListeners();
    initSession();
    loadDashboardSummary();
    loadScholarships();
    loadStudents();
    loadPendingDocuments();
    loadCommitteeApplications();

    // Start live status notification stream poller
    pollNotifications();
    setInterval(pollNotifications, 4000);

    // Close notification dropdown when clicking outside
    document.addEventListener('click', (e) => {
        const dropdown = document.getElementById('notification-dropdown');
        const bellBtn = document.getElementById('btn-notification-bell');
        if (dropdown && bellBtn && !dropdown.contains(e.target) && !bellBtn.contains(e.target)) {
            dropdown.classList.remove('open');
        }
    });

    // Auto-load demo application
    quickTrack('APP-2026-00101');
});

// SESSION & LOGIN MANAGEMENT
function initSession() {
    const saved = localStorage.getItem('scholartrack_user');
    if (saved) {
        try {
            currentUser = JSON.parse(saved);
            updateUserSessionUI(currentUser);
        } catch (e) {
            localStorage.removeItem('scholartrack_user');
        }
    }
}

function openLoginModal(mode = 'login') {
    document.getElementById('login-modal').classList.add('open');
    switchAuthMode(mode);
}

function closeLoginModal() {
    document.getElementById('login-modal').classList.remove('open');
}

function switchAuthMode(mode) {
    const tabLogin = document.getElementById('tab-btn-login');
    const tabSignup = document.getElementById('tab-btn-signup');
    const paneLogin = document.getElementById('auth-pane-login');
    const paneSignup = document.getElementById('auth-pane-signup');

    if (mode === 'signup') {
        if (tabSignup) tabSignup.classList.add('active');
        if (tabLogin) tabLogin.classList.remove('active');
        if (paneSignup) paneSignup.style.display = 'block';
        if (paneLogin) paneLogin.style.display = 'none';
        toggleSignupFields();
    } else {
        if (tabLogin) tabLogin.classList.add('active');
        if (tabSignup) tabSignup.classList.remove('active');
        if (paneLogin) paneLogin.style.display = 'block';
        if (paneSignup) paneSignup.style.display = 'none';
    }
}

function toggleSignupFields() {
    const roleEl = document.getElementById('signup-role');
    const studentFields = document.getElementById('signup-student-fields');
    if (roleEl && studentFields) {
        studentFields.style.display = (roleEl.value === 'ROLE_STUDENT') ? 'block' : 'none';
    }
}

function autoFillDemoStudent() {
    const rand = Math.floor(1000 + Math.random() * 9000);
    document.getElementById('signup-role').value = 'ROLE_STUDENT';
    toggleSignupFields();
    document.getElementById('signup-fullname').value = 'Maya Deshmukh';
    document.getElementById('signup-username').value = 'maya_' + rand;
    document.getElementById('signup-email').value = 'maya.' + rand + '@student.edu';
    document.getElementById('signup-password').value = 'admin123';
    document.getElementById('signup-phone').value = '+91 98' + rand + '1234';
    document.getElementById('signup-institution').value = 'Indian Institute of Technology';
    document.getElementById('signup-rollno').value = 'STU-2026-' + rand;
    document.getElementById('signup-branch').value = 'Computer Science & AI';
    document.getElementById('signup-degree').value = 'UNDERGRADUATE';
    document.getElementById('signup-gpa').value = '8.85';
    document.getElementById('signup-income').value = '240000';
    document.getElementById('signup-year').value = '2';
    document.getElementById('signup-category').value = 'OBC';
    document.getElementById('signup-gender').value = 'FEMALE';
    showToast('✨ Auto-filled sample student profile! Click "Register Account" to sign up.');
}

async function quickLogin(username, password) {
    document.getElementById('login-username').value = username;
    document.getElementById('login-password').value = password;
    await performLogin(username, password);
}

async function handleLoginSubmit(e) {
    e.preventDefault();
    const u = document.getElementById('login-username').value.trim();
    const p = document.getElementById('login-password').value;
    await performLogin(u, p);
}

async function handleSignupSubmit(e) {
    e.preventDefault();
    const role = document.getElementById('signup-role').value;
    const username = document.getElementById('signup-username').value.trim();
    const password = document.getElementById('signup-password').value;
    const email = document.getElementById('signup-email').value.trim();
    const fullName = document.getElementById('signup-fullname').value.trim();
    const phone = document.getElementById('signup-phone').value.trim();

    if (!username || !password || !email || !fullName) {
        showToast('Please fill all mandatory user fields (*)', 'error');
        return;
    }

    const payload = {
        username,
        password,
        email,
        fullName,
        phone,
        role
    };

    if (role === 'ROLE_STUDENT') {
        payload.studentRollNo = document.getElementById('signup-rollno').value.trim() || ('STU-2026-' + Math.floor(1000 + Math.random() * 9000));
        payload.institutionName = document.getElementById('signup-institution').value.trim() || 'State Technical University';
        payload.departmentBranch = document.getElementById('signup-branch').value.trim() || 'Engineering';
        payload.degreeLevel = document.getElementById('signup-degree').value;
        payload.currentYear = parseInt(document.getElementById('signup-year').value, 10) || 2;
        payload.gpaOrPercentage = parseFloat(document.getElementById('signup-gpa').value) || 8.0;
        payload.familyAnnualIncome = parseFloat(document.getElementById('signup-income').value) || 250000;
        payload.category = document.getElementById('signup-category').value;
        payload.gender = document.getElementById('signup-gender').value;
    }

    await performRegister(payload);
}

async function performRegister(payload) {
    try {
        const res = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            const data = await res.json();
            currentUser = data;
            localStorage.setItem('scholartrack_user', JSON.stringify(data));
            updateUserSessionUI(data);
            closeLoginModal();
            showToast(`🎉 Welcome, ${data.fullName}! Successfully registered as ${data.role.replace('ROLE_', '')}`);

            // Refresh student dropdown across portal
            await loadStudents();

            // If registered as student, select them and navigate to Apply tab
            if (data.role === 'ROLE_STUDENT') {
                const select = document.getElementById('apply-student-select');
                if (select && data.studentId) {
                    select.value = data.studentId;
                }
                const applyTabBtn = document.querySelector('[data-tab="tab-apply"]');
                if (applyTabBtn) applyTabBtn.click();
            } else if (data.role === 'ROLE_VERIFICATION_OFFICER') {
                document.querySelector('[data-tab="tab-verification"]').click();
                loadPendingDocuments();
            } else if (data.role === 'ROLE_COMMITTEE_MEMBER') {
                document.querySelector('[data-tab="tab-committee"]').click();
                loadCommitteeApplications();
            }
        } else {
            const err = await res.json();
            const errMsg = err.error || err.message || (err.fieldErrors ? Object.values(err.fieldErrors).join(', ') : 'Registration failed');
            showToast('Registration error: ' + errMsg, 'error');
        }
    } catch (e) {
        showToast('Registration connection failed: ' + e.message, 'error');
    }
}

async function performLogin(username, password) {
    try {
        const res = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (res.ok) {
            const data = await res.json();
            currentUser = data;
            localStorage.setItem('scholartrack_user', JSON.stringify(data));
            updateUserSessionUI(data);
            closeLoginModal();
            showToast(`Welcome back, ${data.fullName}! Logged in as ${data.role.replace('ROLE_', '')}`);

            // Smart navigation based on role
            if (data.role === 'ROLE_STUDENT') {
                document.querySelector('[data-tab="tab-tracker"]').click();
                if (data.studentId) {
                    loadStudentApplications(data.studentId);
                }
            } else if (data.role === 'ROLE_VERIFICATION_OFFICER') {
                document.querySelector('[data-tab="tab-verification"]').click();
                loadPendingDocuments();
            } else if (data.role === 'ROLE_COMMITTEE_MEMBER') {
                document.querySelector('[data-tab="tab-committee"]').click();
                loadCommitteeApplications();
            }
        } else {
            const err = await res.json();
            showToast(err.message || 'Login failed: Invalid credentials', 'error');
        }
    } catch (e) {
        showToast('Login server connection failed', 'error');
    }
}

function updateUserSessionUI(user) {
    const btnLogin = document.getElementById('btn-open-login');
    const btnSignup = document.getElementById('btn-open-signup');
    const badge = document.getElementById('user-profile-badge');
    const navName = document.getElementById('nav-user-name');
    const navRole = document.getElementById('nav-user-role');
    const navAvatar = document.getElementById('nav-user-avatar');

    if (user) {
        if (btnLogin) btnLogin.style.display = 'none';
        if (btnSignup) btnSignup.style.display = 'none';
        if (badge) badge.style.display = 'flex';
        if (navName) navName.textContent = user.fullName.split(' ')[0];
        if (navRole) navRole.textContent = user.role.replace('ROLE_', '');

        if (user.role === 'ROLE_STUDENT') navAvatar.innerHTML = '<i class="fa-solid fa-user-graduate"></i>';
        else if (user.role === 'ROLE_VERIFICATION_OFFICER') navAvatar.innerHTML = '<i class="fa-solid fa-stamp"></i>';
        else if (user.role === 'ROLE_COMMITTEE_MEMBER') navAvatar.innerHTML = '<i class="fa-solid fa-users"></i>';
        else navAvatar.innerHTML = '<i class="fa-solid fa-shield-halved"></i>';
    } else {
        if (btnLogin) btnLogin.style.display = 'inline-flex';
        if (btnSignup) btnSignup.style.display = 'inline-flex';
        if (badge) badge.style.display = 'none';
    }
}

async function loadStudentApplications(studentId) {
    try {
        const res = await fetch(`/api/applications/student/${studentId}`);
        if (res.ok) {
            const apps = await res.json();
            if (apps.length > 0) {
                quickTrack(apps[0].applicationNumber);
            }
        }
    } catch (e) {
        console.error('Error loading student applications', e);
    }
}

// TAB SWITCHING
function initTabs() {
    const tabButtons = document.querySelectorAll('.nav-item');
    const tabPanes = document.querySelectorAll('.tab-pane');

    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const targetTab = btn.getAttribute('data-tab');
            tabButtons.forEach(b => b.classList.remove('active'));
            tabPanes.forEach(p => p.classList.remove('active'));

            btn.classList.add('active');
            const targetEl = document.getElementById(targetTab);
            if (targetEl) targetEl.classList.add('active');

            if (targetTab === 'tab-verification') loadPendingDocuments();
            if (targetTab === 'tab-committee') loadCommitteeApplications();
        });
    });
}

// EVENT LISTENERS
function initEventListeners() {
    document.getElementById('btn-track').addEventListener('click', () => {
        const query = document.getElementById('tracker-search-input').value.trim();
        if (query) trackApplication(query);
    });

    document.getElementById('tracker-search-input').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            const query = e.target.value.trim();
            if (query) trackApplication(query);
        }
    });

    document.getElementById('catalog-provider-filter').addEventListener('change', (e) => {
        filterScholarships(e.target.value);
    });

    document.getElementById('eligibility-form').addEventListener('submit', (e) => {
        e.preventDefault();
        evaluateEligibilityTester();
    });

    document.getElementById('btn-run-report').addEventListener('click', () => {
        const reportId = document.getElementById('dbms-report-selector').value;
        runDbmsReport(reportId);
    });

    document.getElementById('login-form').addEventListener('submit', handleLoginSubmit);
    const signupForm = document.getElementById('signup-form');
    if (signupForm) signupForm.addEventListener('submit', handleSignupSubmit);
    document.getElementById('apply-form').addEventListener('submit', handleApplySubmit);
    document.getElementById('verify-form').addEventListener('submit', handleVerifySubmit);
    document.getElementById('resubmit-form').addEventListener('submit', handleResubmitSubmit);
}

// TOAST NOTIFICATIONS
function showToast(msg, type = 'success') {
    const toast = document.getElementById('toast');
    toast.className = `toast show ${type}`;
    toast.innerHTML = `<i class="fa-solid ${type === 'success' ? 'fa-circle-check' : 'fa-circle-exclamation'}"></i> <span>${msg}</span>`;
    setTimeout(() => {
        toast.className = 'toast';
    }, 4000);
}

// KPI SUMMARY
async function loadDashboardSummary() {
    try {
        const res = await fetch('/api/dashboard/summary');
        if (res.ok) {
            const data = await res.json();
            document.getElementById('kpi-schemes').textContent = data.totalScholarships;
            document.getElementById('kpi-students').textContent = data.totalStudents;
            document.getElementById('kpi-pending-docs').textContent = data.pendingVerifications;
            document.getElementById('kpi-approved').textContent = data.approvedApplications;
            document.getElementById('kpi-disbursed').textContent = '₹' + Number(data.totalDisbursedAmount).toLocaleString('en-IN');
            document.getElementById('pending-badge').textContent = data.pendingVerifications;
        }
    } catch (err) {
        console.error('Failed to load dashboard summary:', err);
    }
}

// SCHOLARSHIPS CATALOG & PAGINATION
async function loadScholarships() {
    try {
        const res = await fetch('/api/scholarships');
        if (res.ok) {
            allScholarships = await res.json();
            populateSchemeDropdowns(allScholarships);
        }
        await loadScholarshipsPaged();
    } catch (err) {
        console.error('Failed to load scholarships:', err);
    }
}

async function loadScholarshipsPaged() {
    const providerEl = document.getElementById('catalog-provider-filter');
    const sortEl = document.getElementById('catalog-sort-by');
    const sizeEl = document.getElementById('catalog-page-size');

    const provider = providerEl ? providerEl.value : 'ALL';
    const sortVal = sortEl ? sortEl.value : 'financialAidAmount:DESC';
    const [sortBy, direction] = sortVal.split(':');
    const size = sizeEl ? parseInt(sizeEl.value, 10) : 6;

    let url = `/api/scholarships/paged?page=${scholarshipPage}&size=${size}&sortBy=${sortBy}&direction=${direction}`;
    if (provider && provider !== 'ALL') {
        url += `&providerType=${provider}`;
    }

    try {
        const res = await fetch(url);
        if (res.ok) {
            const pageData = await res.json();
            scholarshipTotalPages = Math.max(1, pageData.totalPages || 1);
            renderScholarships(pageData.content || []);
            updateScholarshipsPagination(pageData);
        }
    } catch (e) {
        console.error('Failed to load paged scholarships:', e);
    }
}

function onScholarshipFilterChange() {
    scholarshipPage = 0;
    loadScholarshipsPaged();
}

function changeScholarshipPage(delta) {
    const newPage = scholarshipPage + delta;
    if (newPage >= 0 && newPage < scholarshipTotalPages) {
        scholarshipPage = newPage;
        loadScholarshipsPaged();
    }
}

function setScholarshipPage(p) {
    scholarshipPage = p;
    loadScholarshipsPaged();
}

function updateScholarshipsPagination(pageData) {
    const info = document.getElementById('scholarships-page-info');
    const prevBtn = document.getElementById('btn-scholarship-prev');
    const nextBtn = document.getElementById('btn-scholarship-next');
    const numbersSpan = document.getElementById('scholarships-page-numbers');

    if (!info || !prevBtn || !nextBtn || !numbersSpan) return;

    const current = (pageData.page || 0) + 1;
    const total = Math.max(1, pageData.totalPages || 1);
    const totalItems = pageData.totalElements || 0;

    info.innerHTML = `Showing Page <strong>${current}</strong> of <strong>${total}</strong> (Total <strong>${totalItems}</strong> schemes)`;

    prevBtn.disabled = pageData.isFirst || pageData.page === 0;
    nextBtn.disabled = pageData.isLast || current >= total;

    let numsHtml = '';
    for (let i = 0; i < total; i++) {
        numsHtml += `<button class="page-btn ${i === pageData.page ? 'active' : ''}" onclick="setScholarshipPage(${i})">${i + 1}</button>`;
    }
    numbersSpan.innerHTML = numsHtml;
}

async function loadStudents() {
    try {
        const res = await fetch('/api/students');
        if (res.ok) {
            allStudents = await res.json();
            const select = document.getElementById('apply-student-select');
            if (select) {
                select.innerHTML = allStudents.map(s => `
                    <option value="${s.id}">${s.fullName} (${s.studentRollNo}) - ${s.departmentBranch}, GPA: ${s.gpaOrPercentage}</option>
                `).join('');
            }
        }
    } catch (err) {
        console.error('Failed to load students:', err);
    }
}

function renderScholarships(schemes) {
    const container = document.getElementById('scholarships-container');
    if (!schemes.length) {
        container.innerHTML = `<div class="empty-state"><p>No scholarships match the selected filter.</p></div>`;
        return;
    }

    container.innerHTML = schemes.map(s => {
        let typeBadgeClass = 'type-gov';
        if (s.providerType === 'INSTITUTIONAL') typeBadgeClass = 'type-inst';
        if (s.providerType === 'CORPORATE_CSR') typeBadgeClass = 'type-csr';

        return `
            <div class="scheme-card">
                <div>
                    <div class="scheme-header">
                        <span class="scheme-type-badge ${typeBadgeClass}">${s.providerType.replace('_', ' ')}</span>
                        <span class="scheme-amount">₹${Number(s.financialAidAmount).toLocaleString('en-IN')}</span>
                    </div>
                    <h3 class="scheme-title">${s.title}</h3>
                    <p class="scheme-desc">${s.description || 'Comprehensive financial aid scheme.'}</p>
                    
                    <div class="scheme-criteria-list">
                        <div class="criteria-item">
                            <i class="fa-solid fa-graduation-cap"></i>
                            <span>Min Academic Score: <strong>${s.minGpaOrPercentage || 7.0} GPA</strong></span>
                        </div>
                        <div class="criteria-item">
                            <i class="fa-solid fa-wallet"></i>
                            <span>Income Ceiling: <strong><= ₹${Number(s.maxFamilyIncome || 500000).toLocaleString('en-IN')} / yr</strong></span>
                        </div>
                        <div class="criteria-item">
                            <i class="fa-solid fa-users"></i>
                            <span>Quota Slots: <strong>${s.slotsRemaining} left</strong> of ${s.totalSlots}</span>
                        </div>
                    </div>
                </div>

                <div class="scheme-actions">
                    <button class="btn btn-primary btn-sm" onclick="openApplyModal(${s.id})">
                        <i class="fa-solid fa-paper-plane"></i> Apply Now
                    </button>
                    <button class="btn btn-secondary btn-sm" onclick="selectForEligibilityTest(${s.id})">
                        <i class="fa-solid fa-calculator"></i> Test Rules
                    </button>
                </div>
            </div>
        `;
    }).join('');
}

function populateSchemeDropdowns(schemes) {
    const sel = document.getElementById('elig-scheme-select');
    sel.innerHTML = schemes.map(s => `<option value="${s.id}">${s.code} - ${s.title}</option>`).join('');
}

function selectForEligibilityTest(schemeId) {
    const sel = document.getElementById('elig-scheme-select');
    sel.value = schemeId;
    document.querySelector('[data-tab="tab-catalog"]').click();
    document.getElementById('eligibility-form').scrollIntoView({ behavior: 'smooth' });
}

// ELIGIBILITY TESTER
async function evaluateEligibilityTester() {
    const schemeId = document.getElementById('elig-scheme-select').value;
    const gpa = parseFloat(document.getElementById('elig-gpa').value);
    const income = parseFloat(document.getElementById('elig-income').value);
    const category = document.getElementById('elig-category').value;
    const gender = document.getElementById('elig-gender').value;
    const degree = document.getElementById('elig-degree').value;

    const payload = {
        scholarshipId: schemeId,
        gpaOrPercentage: gpa,
        familyAnnualIncome: income,
        category: category,
        gender: gender,
        degreeLevel: degree,
        age: 20
    };

    try {
        const res = await fetch('/api/eligibility/check-custom', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            const data = await res.json();
            renderEligibilityResult(data);
        } else {
            const err = await res.json();
            showToast(err.message || 'Eligibility check failed', 'error');
        }
    } catch (e) {
        showToast('Server communication error', 'error');
    }
}

function renderEligibilityResult(data) {
    const box = document.getElementById('eligibility-feedback');
    box.style.display = 'block';

    if (data.eligible) {
        box.className = 'eligibility-verdict-box verdict-eligible';
        box.innerHTML = `
            <div class="verdict-header">
                <span style="color:#34d399;"><i class="fa-solid fa-circle-check"></i> QUALIFIED & ELIGIBLE</span>
                <span style="color:#60a5fa;">Match Score: ${data.score}%</span>
            </div>
            <p>${data.recommendationVerdict}</p>
            <ul class="verdict-list">
                ${data.passedCriteria.map(c => `<li><i class="fa-solid fa-check" style="color:#10b981;"></i> ${c}</li>`).join('')}
            </ul>
            <div style="margin-top:0.75rem;">
                <span style="font-weight:600; font-size:0.75rem; color:#94a3b8;">Mandatory Documents Required:</span>
                <div class="docs-checklist" style="margin-top:0.35rem;">
                    ${data.requiredDocuments.map(d => `<span class="doc-tag"><i class="fa-regular fa-file"></i> ${d}</span>`).join('')}
                </div>
            </div>
        `;
    } else {
        box.className = 'eligibility-verdict-box verdict-ineligible';
        box.innerHTML = `
            <div class="verdict-header">
                <span style="color:#f87171;"><i class="fa-solid fa-circle-xmark"></i> INELIGIBLE DEFICIT</span>
                <span style="color:#f87171;">Score: ${data.score}%</span>
            </div>
            <p>${data.recommendationVerdict}</p>
            <ul class="verdict-list">
                ${data.failedCriteria.map(f => `<li><i class="fa-solid fa-xmark" style="color:#ef4444;"></i> ${f}</li>`).join('')}
            </ul>
        `;
    }
}

// STATUS TRACKER
function quickTrack(appNumber) {
    document.getElementById('tracker-search-input').value = appNumber;
    trackApplication(appNumber);
}

async function trackApplication(appNumber) {
    const resultBox = document.getElementById('tracker-result');
    resultBox.innerHTML = `<div class="empty-state"><i class="fa-solid fa-spinner fa-spin"></i><p>Retrieving live application journey...</p></div>`;

    try {
        const res = await fetch(`/api/applications/number/${encodeURIComponent(appNumber)}`);
        if (!res.ok) {
            resultBox.innerHTML = `
                <div class="empty-state">
                    <i class="fa-solid fa-triangle-exclamation" style="color:#ef4444;"></i>
                    <h3>Application '${appNumber}' Not Found</h3>
                    <p>Please double check the reference code (e.g. APP-2026-00101).</p>
                </div>
            `;
            return;
        }

        const app = await res.json();
        renderTrackerDetails(app);
    } catch (err) {
        resultBox.innerHTML = `<div class="empty-state"><p>Error retrieving tracking information.</p></div>`;
    }
}

function renderTrackerDetails(app) {
    const resultBox = document.getElementById('tracker-result');

    // Determine Stepper Node States
    const stageMap = {
        'SUBMITTED': 1,
        'UNDER_DOCUMENT_VERIFICATION': 2,
        'DOCUMENTS_FLAGGED': 2,
        'ELIGIBILITY_FAILED': 3,
        'ELIGIBILITY_VERIFIED': 3,
        'UNDER_COMMITTEE_REVIEW': 4,
        'APPROVED': 4,
        'REJECTED': 4,
        'DISBURSED': 5
    };

    const currentStageIdx = stageMap[app.status] || 1;
    const isDocFlagged = app.status === 'DOCUMENTS_FLAGGED';
    const isEligibilityFlagged = app.status === 'ELIGIBILITY_FAILED' || (app.isFlagged && app.eligibilityPassed === false);
    const isRejected = app.status === 'REJECTED';
    const isDisbursed = app.status === 'DISBURSED';

    const getNodeClass = (nodeIdx) => {
        if (nodeIdx < currentStageIdx) return 'completed';
        if (nodeIdx === currentStageIdx) {
            if (isDocFlagged && nodeIdx === 2) return 'flagged';
            if (isEligibilityFlagged && nodeIdx === 3) return 'flagged';
            if (isRejected && nodeIdx === 4) return 'rejected';
            if (isDisbursed && nodeIdx === 5) return 'completed';
            return 'in_progress';
        }
        return '';
    };

    let statusBadgeClass = 'status-submitted';
    if (app.status === 'UNDER_DOCUMENT_VERIFICATION') statusBadgeClass = 'status-verification';
    if (app.status === 'DOCUMENTS_FLAGGED' || app.status === 'ELIGIBILITY_FAILED' || app.isFlagged) statusBadgeClass = 'status-flagged';
    if (app.status === 'UNDER_COMMITTEE_REVIEW') statusBadgeClass = 'status-review';
    if (app.status === 'APPROVED') statusBadgeClass = 'status-approved';
    if (app.status === 'DISBURSED') statusBadgeClass = 'status-disbursed';

    resultBox.innerHTML = `
        <div class="tracker-header-box">
            <div>
                <span class="tracker-id-badge">${app.applicationNumber}</span>
                <h3 style="font-size:1.3rem; margin-top:0.4rem; color:#fff;">${app.scholarshipTitle}</h3>
                <p style="font-size:0.85rem; color:#9ca3af;">Scheme Code: <strong>${app.scholarshipCode}</strong> | Provider: <strong>${app.providerType}</strong></p>
                ${app.isFlagged ? `<div class="callout callout-warning" style="margin-top:0.6rem; padding:0.5rem 0.8rem; font-size:0.8rem;">
                    <i class="fa-solid fa-triangle-exclamation"></i> <strong>FLAGGED BEFORE MANUAL REVIEW:</strong> ${app.flagReason || app.eligibilityRemarks}
                </div>` : ''}
            </div>
            <div style="text-align:right;">
                <span class="status-pill ${statusBadgeClass}"><i class="fa-solid fa-circle-dot"></i> ${app.status.replace(/_/g, ' ')}</span>
                <p style="font-size:0.78rem; color:#9ca3af; margin-top:0.4rem;">Submitted: ${new Date(app.submittedAt).toLocaleDateString()}</p>
            </div>
        </div>

        <!-- 5-Stage Stepper Timeline -->
        <div class="stepper-timeline">
            <div class="step-node ${getNodeClass(1)}">
                <div class="step-circle"><i class="fa-solid fa-paper-plane"></i></div>
                <span class="step-label">Application</span>
                <span class="step-sub">Lodged Online</span>
            </div>
            <div class="step-node ${getNodeClass(2)}">
                <div class="step-circle"><i class="fa-solid fa-file-shield"></i></div>
                <span class="step-label">Doc Scrutiny</span>
                <span class="step-sub">${isDocFlagged ? 'Action Needed' : 'Officer Desk'}</span>
            </div>
            <div class="step-node ${getNodeClass(3)}">
                <div class="step-circle"><i class="fa-solid fa-award"></i></div>
                <span class="step-label">Eligibility Match</span>
                <span class="step-sub">${isEligibilityFlagged ? '⚠️ Flagged' : 'Score: ' + app.eligibilityScore + '%'}</span>
            </div>
            <div class="step-node ${getNodeClass(4)}">
                <div class="step-circle"><i class="fa-solid fa-users-gear"></i></div>
                <span class="step-label">Committee Sanction</span>
                <span class="step-sub">${app.status === 'APPROVED' || app.status === 'DISBURSED' ? 'Sanctioned' : (isEligibilityFlagged ? 'Blocked' : 'Evaluation')}</span>
            </div>
            <div class="step-node ${getNodeClass(5)}">
                <div class="step-circle"><i class="fa-solid fa-money-check-dollar"></i></div>
                <span class="step-label">Direct Disbursement</span>
                <span class="step-sub">${isDisbursed ? 'Credited to Bank' : 'Finance Dept'}</span>
            </div>
        </div>

        <!-- Two Column Meta Details -->
        <div class="grid-2col">
            <div class="info-card">
                <h4><i class="fa-solid fa-user-graduate" style="color:#38bdf8;"></i> Applicant Profile</h4>
                <div class="info-row"><span class="label">Candidate Name:</span><span class="val">${app.studentName}</span></div>
                <div class="info-row"><span class="label">Roll / Reg No:</span><span class="val">${app.studentRollNo}</span></div>
                <div class="info-row"><span class="label">Institution:</span><span class="val">${app.institutionName}</span></div>
                <div class="info-row"><span class="label">Department:</span><span class="val">${app.departmentBranch}</span></div>
                <div class="info-row"><span class="label">Academic CGPA / %:</span><span class="val" style="color:#34d399;">${app.gpaOrPercentage}</span></div>
                <div class="info-row"><span class="label">Family Annual Income:</span><span class="val">₹${Number(app.familyAnnualIncome).toLocaleString('en-IN')}</span></div>
                <div class="info-row"><span class="label">Category & Gender:</span><span class="val">${app.studentCategory} | ${app.gender}</span></div>
            </div>

            <div class="info-card">
                <h4><i class="fa-solid fa-receipt" style="color:#10b981;"></i> Award & Financial Particulars</h4>
                <div class="info-row"><span class="label">Grant Amount:</span><span class="val" style="color:#34d399; font-size:1.1rem;">₹${Number(app.financialAidAmount).toLocaleString('en-IN')}</span></div>
                <div class="info-row"><span class="label">Rule Engine Evaluation:</span><span class="val" style="color:#60a5fa;">Score ${app.eligibilityScore}% (Passed)</span></div>
                <div class="info-row"><span class="label">Decision Date:</span><span class="val">${app.decisionAt ? new Date(app.decisionAt).toLocaleString() : 'Pending final approval meeting'}</span></div>
                <div class="info-row"><span class="label">Committee Remarks:</span><span class="val">${app.decisionRemarks || 'Awaiting sanction review.'}</span></div>
                <div class="info-row"><span class="label">Disbursed Grant:</span><span class="val">${app.disbursedAmount ? '₹' + Number(app.disbursedAmount).toLocaleString('en-IN') : '₹0 (Pending release)'}</span></div>
                <div class="info-row"><span class="label">Bank UTR Reference:</span><span class="val" style="font-family:'JetBrains Mono'; color:#38bdf8;">${app.disbursementReference || 'N/A'}</span></div>
            </div>
        </div>

        <!-- Document Verification Breakdown -->
        <div class="info-card" style="margin-bottom:1.75rem;">
            <h4><i class="fa-solid fa-file-circle-check" style="color:#f59e0b;"></i> Submitted Documents & Officer Scrutiny</h4>
            <div class="table-container" style="margin-top:0.75rem;">
                <table class="custom-table">
                    <thead>
                        <tr>
                            <th>Document Type</th>
                            <th>File Name</th>
                            <th>Verification Status</th>
                            <th>Officer Remarks / Discrepancies</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${app.documents.map(d => {
                            let badge = '<span class="status-pill status-verification">PENDING</span>';
                            if (d.verificationStatus === 'VERIFIED') badge = '<span class="status-pill status-approved"><i class="fa-solid fa-check"></i> VERIFIED</span>';
                            if (d.verificationStatus === 'RESUBMISSION_REQUESTED') badge = '<span class="status-pill status-flagged"><i class="fa-solid fa-triangle-exclamation"></i> ACTION REQUIRED</span>';
                            if (d.verificationStatus === 'REJECTED') badge = '<span class="status-pill status-flagged"><i class="fa-solid fa-xmark"></i> REJECTED</span>';

                            return `
                                <tr>
                                    <td><strong>${d.documentType.replace(/_/g, ' ')}</strong></td>
                                    <td><i class="fa-regular fa-file-pdf" style="color:#ef4444;"></i> ${d.documentName}</td>
                                    <td>${badge}</td>
                                    <td style="color:${d.verificationStatus === 'RESUBMISSION_REQUESTED' ? '#fde047' : '#9ca3af'};">
                                        ${d.remarks || 'Under verification queue.'}
                                    </td>
                                    <td>
                                        ${d.verificationStatus === 'RESUBMISSION_REQUESTED' ?
                                            `<button class="btn btn-primary btn-sm" onclick="openResubmitModal(${d.id}, '${d.remarks}')"><i class="fa-solid fa-cloud-arrow-up"></i> Re-upload</button>` :
                                            `<span style="color:#6b7280; font-size:0.75rem;">Compliant</span>`
                                        }
                                    </td>
                                </tr>
                            `;
                        }).join('')}
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Real-time Journey Audit Log -->
        <div class="info-card">
            <h4><i class="fa-solid fa-clock-rotate-left" style="color:#8b5cf6;"></i> Real-Time Stage Audit Trail</h4>
            <ul style="list-style:none; padding:0; margin-top:0.75rem;">
                ${app.timeline.map(t => `
                    <li style="display:flex; justify-content:space-between; padding:0.6rem 0; border-bottom:1px solid rgba(255,255,255,0.05); font-size:0.85rem;">
                        <div>
                            <span style="font-weight:700; color:#fff;">${t.stageName}</span>
                            <span style="color:#9ca3af; margin-left:0.5rem;">— ${t.comments}</span>
                            <div style="font-size:0.75rem; color:#6b7280; margin-top:0.15rem;">Updated by: ${t.updatedByName || 'System'}</div>
                        </div>
                        <div style="text-align:right; font-size:0.75rem; color:#9ca3af;">
                            ${new Date(t.timestamp).toLocaleString()}
                        </div>
                    </li>
                `).join('')}
            </ul>
        </div>
    `;
}

// APPLY MODAL
function openApplyModal(schemeId) {
    const scheme = allScholarships.find(s => s.id === schemeId);
    if (!scheme) return;

    document.getElementById('apply-scheme-id').value = scheme.id;
    document.getElementById('modal-scheme-title').textContent = `${scheme.title} (₹${Number(scheme.financialAidAmount).toLocaleString('en-IN')})`;

    const reqDocsList = document.getElementById('modal-required-docs-list');
    const reqDocs = scheme.requiredDocuments ? scheme.requiredDocuments.split(',') : ['INCOME_CERTIFICATE', 'GRADE_MARKSHEET'];
    reqDocsList.innerHTML = reqDocs.map(d => `<span class="doc-tag"><i class="fa-regular fa-file"></i> ${d.trim()}</span>`).join('');

    document.getElementById('apply-modal').classList.add('open');
}

function closeApplyModal() {
    document.getElementById('apply-modal').classList.remove('open');
}

async function handleApplySubmit(e) {
    e.preventDefault();
    const schemeId = document.getElementById('apply-scheme-id').value;
    const studentId = document.getElementById('apply-student-select').value;
    const notes = document.getElementById('apply-notes').value;

    const payload = {
        studentId: parseInt(studentId),
        scholarshipId: parseInt(schemeId),
        notes: notes
    };

    try {
        const res = await fetch('/api/applications/apply', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            const data = await res.json();
            closeApplyModal();
            showToast(`Application ${data.applicationNumber} lodged successfully!`);
            loadDashboardSummary();
            loadScholarships();
            quickTrack(data.applicationNumber);
            document.querySelector('[data-tab="tab-tracker"]').click();
            pollNotifications();
        } else {
            const err = await res.json();
            showToast(err.message || 'Application submission failed', 'error');
        }
    } catch (e) {
        showToast('Server error while applying', 'error');
    }
}

// VERIFIER DESK
async function loadPendingDocuments() {
    const tbody = document.getElementById('verifier-tbody');
    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding:2rem;"><i class="fa-solid fa-spinner fa-spin"></i> Loading verification queue...</td></tr>`;

    try {
        const res = await fetch('/api/documents/pending');
        if (res.ok) {
            const docs = await res.json();
            if (!docs.length) {
                tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding:2rem; color:#10b981;"><i class="fa-solid fa-circle-check"></i> Verification queue clear! All documents verified.</td></tr>`;
                return;
            }

            tbody.innerHTML = docs.map(d => `
                <tr>
                    <td><code>#DOC-${d.id}</code></td>
                    <td><strong>APP-REF</strong></td>
                    <td>Applicant Certificate</td>
                    <td><span class="doc-tag">${d.documentType}</span></td>
                    <td><i class="fa-regular fa-file-pdf" style="color:#ef4444;"></i> ${d.documentName}</td>
                    <td><span class="status-pill status-verification">${d.verificationStatus}</span></td>
                    <td>
                        <button class="btn btn-primary btn-sm" onclick="openVerifyModal(${d.id}, '${d.documentType}', '${d.documentName}')">
                            <i class="fa-solid fa-stamp"></i> Scrutinize
                        </button>
                    </td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error('Failed to load pending docs:', e);
    }
}

function openVerifyModal(docId, docType, docName) {
    document.getElementById('verify-doc-id').value = docId;
    document.getElementById('modal-doc-info').textContent = `${docType} (${docName})`;
    document.getElementById('verify-remarks').value = '';
    document.getElementById('verify-modal').classList.add('open');
}

function closeVerifyModal() {
    document.getElementById('verify-modal').classList.remove('open');
}

async function handleVerifySubmit(e) {
    e.preventDefault();
    const docId = document.getElementById('verify-doc-id').value;
    const status = document.getElementById('verify-status-select').value;
    const remarks = document.getElementById('verify-remarks').value;

    const payload = {
        documentId: parseInt(docId),
        status: status,
        remarks: remarks,
        verifierId: 2 // Dr. Vikram Sharma
    };

    try {
        const res = await fetch('/api/documents/verify', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            closeVerifyModal();
            showToast('Document review updated successfully!');
            loadDashboardSummary();
            loadPendingDocuments();
            pollNotifications();
        } else {
            const err = await res.json();
            showToast(err.message || 'Verification update failed', 'error');
        }
    } catch (e) {
        showToast('Communication error', 'error');
    }
}

// RESUBMISSION
function openResubmitModal(docId, remarks) {
    document.getElementById('resubmit-doc-id').value = docId;
    document.getElementById('resubmit-officer-remark').innerHTML = `<strong>Officer Remark:</strong> ${remarks}`;
    document.getElementById('resubmit-filename').value = '';
    document.getElementById('resubmit-modal').classList.add('open');
}

function closeResubmitModal() {
    document.getElementById('resubmit-modal').classList.remove('open');
}

async function handleResubmitSubmit(e) {
    e.preventDefault();
    const docId = document.getElementById('resubmit-doc-id').value;
    const filename = document.getElementById('resubmit-filename').value;

    try {
        const res = await fetch(`/api/documents/${docId}/resubmit`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                documentName: filename,
                documentPath: '/uploads/docs/' + filename
            })
        });

        if (res.ok) {
            closeResubmitModal();
            showToast('Corrected document re-uploaded. Back in verification queue!');
            const activeApp = document.getElementById('tracker-search-input').value;
            if (activeApp) trackApplication(activeApp);
            loadDashboardSummary();
            pollNotifications();
        }
    } catch (e) {
        showToast('Resubmission failed', 'error');
    }
}

// COMMITTEE & DBT WITH PAGINATION & SORTING
async function loadCommitteeApplications() {
    const filterEl = document.getElementById('committee-status-filter');
    const sortEl = document.getElementById('committee-sort-by');
    const sizeEl = document.getElementById('committee-page-size');

    const filter = filterEl ? filterEl.value : '';
    const sortVal = sortEl ? sortEl.value : 'submittedAt:DESC';
    const [sortBy, direction] = sortVal.split(':');
    const size = sizeEl ? parseInt(sizeEl.value, 10) : 5;

    const tbody = document.getElementById('committee-tbody');
    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding:2rem;"><i class="fa-solid fa-spinner fa-spin"></i> Loading committee applications...</td></tr>`;

    try {
        let url = `/api/applications/paged?page=${committeePage}&size=${size}&sortBy=${sortBy}&direction=${direction}`;
        if (filter) {
            url += `&status=${filter}`;
        }
        const res = await fetch(url);
        if (res.ok) {
            const pageData = await res.json();
            const apps = pageData.content || [];
            committeeTotalPages = Math.max(1, pageData.totalPages || 1);
            updateCommitteePagination(pageData);

            if (!apps.length) {
                tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding:2rem; color:#9ca3af;">No applications in this review stage.</td></tr>`;
                return;
            }

            tbody.innerHTML = apps.map(a => {
                let actionHtml = '';
                const isItemFlagged = a.isFlagged || a.status === 'ELIGIBILITY_FAILED';

                if (isItemFlagged) {
                    actionHtml = `
                        <button class="btn btn-secondary btn-sm" disabled style="opacity:0.45; cursor:not-allowed;" title="FLAGGED: Failed eligibility rules before manual review (${a.flagReason || a.eligibilityRemarks})"><i class="fa-solid fa-ban"></i> Flagged</button>
                        <button class="btn btn-secondary btn-sm" style="color:#ef4444;" onclick="committeeDecision(${a.id}, 'REJECTED')"><i class="fa-solid fa-xmark"></i> Reject</button>
                    `;
                } else if (a.status === 'UNDER_COMMITTEE_REVIEW' || a.status === 'UNDER_DOCUMENT_VERIFICATION') {
                    actionHtml = `
                        <button class="btn btn-primary btn-sm" onclick="committeeDecision(${a.id}, 'APPROVED')"><i class="fa-solid fa-check"></i> Sanction</button>
                        <button class="btn btn-secondary btn-sm" style="color:#ef4444;" onclick="committeeDecision(${a.id}, 'REJECTED')"><i class="fa-solid fa-xmark"></i> Reject</button>
                    `;
                } else if (a.status === 'APPROVED') {
                    actionHtml = `
                        <button class="btn btn-primary btn-sm" style="background:#10b981;" onclick="triggerDisbursement(${a.id}, ${a.financialAidAmount})">
                            <i class="fa-solid fa-money-bill-transfer"></i> Release DBT
                        </button>
                    `;
                } else if (a.status === 'DISBURSED') {
                    actionHtml = `<span style="color:#10b981; font-weight:700;"><i class="fa-solid fa-check-double"></i> Disbursed</span>`;
                } else {
                    actionHtml = `<span style="color:#9ca3af; font-size:0.75rem;">${a.status}</span>`;
                }

                let badgeClass = `status-${a.status.toLowerCase()}`;
                if (isItemFlagged) badgeClass = 'status-flagged';

                return `
                    <tr style="${isItemFlagged ? 'background:rgba(239, 68, 68, 0.05);' : ''}">
                        <td><a href="#" onclick="quickTrack('${a.applicationNumber}'); document.querySelector('[data-tab=tab-tracker]').click();" style="color:#38bdf8; font-weight:700;">${a.applicationNumber}</a></td>
                        <td>${a.studentName} (${a.studentCategory || 'General'})</td>
                        <td><strong>${a.scholarshipTitle}</strong><br><span style="color:#34d399;">₹${Number(a.financialAidAmount).toLocaleString('en-IN')}</span></td>
                        <td><span style="color:#60a5fa; font-weight:700;">${a.gpaOrPercentage} CGPA</span></td>
                        <td>₹${Number(a.familyAnnualIncome).toLocaleString('en-IN')}</td>
                        <td><span class="status-pill ${badgeClass}"><i class="fa-solid ${isItemFlagged ? 'fa-triangle-exclamation' : 'fa-circle-dot'}"></i> ${isItemFlagged ? 'FLAGGED (INELIGIBLE)' : a.status.replace(/_/g, ' ')}</span></td>
                        <td><div style="display:flex; gap:0.4rem;">${actionHtml}</div></td>
                    </tr>
                `;
            }).join('');
        }
    } catch (e) {
        console.error('Failed to load committee applications:', e);
    }
}

function onCommitteeFilterChange() {
    committeePage = 0;
    loadCommitteeApplications();
}

function changeCommitteePage(delta) {
    const newPage = committeePage + delta;
    if (newPage >= 0 && newPage < committeeTotalPages) {
        committeePage = newPage;
        loadCommitteeApplications();
    }
}

function setCommitteePage(p) {
    committeePage = p;
    loadCommitteeApplications();
}

function updateCommitteePagination(pageData) {
    const info = document.getElementById('committee-page-info');
    const prevBtn = document.getElementById('btn-committee-prev');
    const nextBtn = document.getElementById('btn-committee-next');
    const numbersSpan = document.getElementById('committee-page-numbers');

    if (!info || !prevBtn || !nextBtn || !numbersSpan) return;

    const current = (pageData.page || 0) + 1;
    const total = Math.max(1, pageData.totalPages || 1);
    const totalItems = pageData.totalElements || 0;

    info.innerHTML = `Showing Page <strong>${current}</strong> of <strong>${total}</strong> (Total <strong>${totalItems}</strong> applications)`;

    prevBtn.disabled = pageData.isFirst || pageData.page === 0;
    nextBtn.disabled = pageData.isLast || current >= total;

    let numsHtml = '';
    for (let i = 0; i < total; i++) {
        numsHtml += `<button class="page-btn ${i === pageData.page ? 'active' : ''}" onclick="setCommitteePage(${i})">${i + 1}</button>`;
    }
    numbersSpan.innerHTML = numsHtml;
}

async function committeeDecision(appId, decision) {
    const remark = prompt(`Enter evaluation remark for ${decision}:`, decision === 'APPROVED' ? 'Approved based on academic distinction and financial criteria.' : 'Quota exhausted or criteria mismatch.');
    if (remark === null) return;

    try {
        const res = await fetch('/api/applications/decision', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                applicationId: appId,
                decision: decision,
                remarks: remark,
                reviewerId: 3 // Prof. Arvind Patel
            })
        });

        if (res.ok) {
            showToast(`Application successfully marked as ${decision}!`);
            loadDashboardSummary();
            loadCommitteeApplications();
            pollNotifications();
        } else {
            const err = await res.json();
            showToast(err.message || 'Decision failed', 'error');
        }
    } catch (e) {
        showToast('Error recording decision', 'error');
    }
}

async function triggerDisbursement(appId, amount) {
    if (!confirm(`Confirm releasing DBT disbursement of ₹${Number(amount).toLocaleString('en-IN')} to student's bank account?`)) {
        return;
    }

    try {
        const res = await fetch('/api/applications/disburse', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                applicationId: appId,
                disbursedAmount: amount,
                disbursementReference: 'UTR-SBI-' + Math.floor(10000000 + Math.random() * 90000000),
                adminId: 1
            })
        });

        if (res.ok) {
            showToast('DBT Funds Disbursed Successfully!');
            loadDashboardSummary();
            loadCommitteeApplications();
            pollNotifications();
        } else {
            const err = await res.json();
            showToast(err.message || 'Disbursement failed', 'error');
        }
    } catch (e) {
        showToast('Communication error', 'error');
    }
}

// DBMS REPORTS
async function runDbmsReport(reportId) {
    const thead = document.getElementById('report-thead');
    const tbody = document.getElementById('report-tbody');
    const title = document.getElementById('report-title');
    const desc = document.getElementById('report-desc');

    title.textContent = 'Executing SQL report...';
    desc.textContent = 'Querying relational schema...';
    thead.innerHTML = '';
    tbody.innerHTML = `<tr><td style="text-align:center; padding:2rem;"><i class="fa-solid fa-spinner fa-spin"></i> Processing query...</td></tr>`;

    try {
        const res = await fetch(`/api/reports/${reportId}`);
        if (res.ok) {
            const data = await res.json();
            title.textContent = data.title;
            desc.textContent = data.description;

            if (!data.columns.length) {
                tbody.innerHTML = `<tr><td style="text-align:center; padding:2rem;">Query executed successfully. 0 rows returned.</td></tr>`;
                return;
            }

            thead.innerHTML = `<tr>${data.columns.map(c => `<th>${c.replace(/_/g, ' ')}</th>`).join('')}</tr>`;
            tbody.innerHTML = data.rows.map(row => `
                <tr>${data.columns.map(c => `<td>${row[c] !== null ? row[c] : '<span style="color:#6b7280;">NULL</span>'}</td>`).join('')}</tr>
            `).join('');
        }
    } catch (e) {
        showToast('Failed to execute report', 'error');
    }
}

// ===================================================================
// NOTIFICATION SYSTEM & REAL-TIME STATUS STREAM
// ===================================================================
async function pollNotifications() {
    try {
        let url = '/api/notifications/recent';
        const res = await fetch(url);
        if (res.ok) {
            const list = await res.json();
            notifications = list;

            // Check for new notifications
            let newNotifications = [];
            for (const n of list) {
                if (!knownNotificationIds.has(n.id)) {
                    knownNotificationIds.add(n.id);
                    if (!isFirstNotifLoad) {
                        newNotifications.push(n);
                    }
                }
            }

            isFirstNotifLoad = false;

            // Trigger real-time status change toast for new updates
            if (newNotifications.length > 0) {
                for (const newNotif of newNotifications.slice(0, 3)) {
                    showStatusNotificationToast(newNotif);
                }
                // Refresh KPI and summary
                loadDashboardSummary();
            }

            // Update badge
            const unreadCount = list.filter(n => !n.isRead).length;
            const badge = document.getElementById('notification-badge');
            if (badge) {
                if (unreadCount > 0) {
                    badge.textContent = unreadCount > 99 ? '99+' : unreadCount;
                    badge.style.display = 'flex';
                } else {
                    badge.style.display = 'none';
                }
            }

            // If dropdown open, update content
            const dropdown = document.getElementById('notification-dropdown');
            if (dropdown && dropdown.classList.contains('open')) {
                renderNotificationItems();
            }
        }
    } catch (e) {
        console.error('Notification polling error:', e);
    }
}

function toggleNotificationDropdown() {
    const dropdown = document.getElementById('notification-dropdown');
    if (!dropdown) return;
    dropdown.classList.toggle('open');
    if (dropdown.classList.contains('open')) {
        renderNotificationItems();
    }
}

function renderNotificationItems() {
    const container = document.getElementById('notification-items-container');
    if (!container) return;

    if (!notifications || !notifications.length) {
        container.innerHTML = `<div class="notification-empty"><i class="fa-regular fa-bell-slash" style="font-size:1.5rem; margin-bottom:0.5rem; display:block;"></i>No recent notifications</div>`;
        return;
    }

    container.innerHTML = notifications.map(n => {
        let icon = '<i class="fa-solid fa-bell" style="color:#60a5fa;"></i>';
        if (n.type === 'SUCCESS') icon = '<i class="fa-solid fa-circle-check" style="color:#10b981;"></i>';
        else if (n.type === 'WARNING') icon = '<i class="fa-solid fa-triangle-exclamation" style="color:#f59e0b;"></i>';
        else if (n.type === 'DANGER') icon = '<i class="fa-solid fa-circle-xmark" style="color:#ef4444;"></i>';

        const timeStr = formatRelativeTime(n.createdAt);

        return `
            <div class="notification-item ${n.isRead ? 'read' : 'unread'}" onclick="handleNotificationClick(${n.id}, '${n.applicationNumber || ''}')">
                <div class="notification-item-top">
                    <span class="notification-item-title">${icon} ${n.title}</span>
                    <span class="notification-item-time">${timeStr}</span>
                </div>
                <p class="notification-item-msg">${n.message}</p>
                ${n.applicationNumber ? `<span class="notification-item-app"><i class="fa-solid fa-hashtag"></i> ${n.applicationNumber}</span>` : ''}
            </div>
        `;
    }).join('');
}

function formatRelativeTime(dateStr) {
    if (!dateStr) return 'Just now';
    try {
        const d = new Date(dateStr);
        const diffMs = Date.now() - d.getTime();
        const diffSec = Math.floor(diffMs / 1000);
        if (diffSec < 60) return 'Just now';
        const diffMin = Math.floor(diffSec / 60);
        if (diffMin < 60) return `${diffMin}m ago`;
        const diffHr = Math.floor(diffMin / 60);
        if (diffHr < 24) return `${diffHr}h ago`;
        return `${Math.floor(diffHr / 24)}d ago`;
    } catch (e) {
        return 'Recently';
    }
}

async function handleNotificationClick(id, appNumber) {
    await markNotificationAsRead(id);
    if (appNumber) {
        const dropdown = document.getElementById('notification-dropdown');
        if (dropdown) dropdown.classList.remove('open');
        quickTrack(appNumber);
        document.querySelector('[data-tab="tab-tracker"]').click();
    }
}

async function markNotificationAsRead(id) {
    try {
        await fetch(`/api/notifications/${id}/read`, { method: 'POST' });
        const notif = notifications.find(n => n.id === id);
        if (notif) notif.isRead = true;
        pollNotifications();
    } catch (e) {
        console.error('Failed to mark notification read', e);
    }
}

async function markAllNotificationsAsRead() {
    try {
        await fetch('/api/notifications/read-all', { method: 'POST' });
        notifications.forEach(n => n.isRead = true);
        pollNotifications();
        showToast('All notifications marked as read');
    } catch (e) {
        console.error('Failed to mark all notifications read', e);
    }
}

function showStatusNotificationToast(notif) {
    const toast = document.getElementById('toast');
    if (!toast) return;

    let icon = '🔔';
    if (notif.type === 'SUCCESS') icon = '🎉';
    else if (notif.type === 'WARNING') icon = '⚠️';

    toast.innerHTML = `
        <div style="display:flex; align-items:flex-start; gap:0.6rem;">
            <span style="font-size:1.2rem;">${icon}</span>
            <div>
                <strong style="display:block; font-size:0.85rem; color:#fff;">${notif.title}</strong>
                <span style="font-size:0.75rem; color:#e0e7ff;">${notif.message}</span>
            </div>
        </div>
    `;
    toast.className = `toast show ${notif.type === 'WARNING' ? 'error' : ''}`;
    setTimeout(() => { toast.className = 'toast'; }, 5000);
}

// SIMULATE RAPID DATA GROWTH & MULTIPLE STATUS CHANGES
async function simulateDataGrowthClick() {
    const btn = document.querySelector('.btn-simulate-growth');
    if (btn) btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Generating 12 Apps...';

    showToast('⚡ Simulating rapid student application growth and status transitions...');

    try {
        const res = await fetch('/api/applications/simulate-growth?count=12', { method: 'POST' });
        if (res.ok) {
            const created = await res.json();
            showToast(`🚀 ${created.length} new applications generated across all workflow stages!`);
            
            // Re-fetch all data to show growth, pagination & notifications
            await loadDashboardSummary();
            await loadCommitteeApplications();
            await pollNotifications();

            // Open notification dropdown to highlight incoming status stream
            const dropdown = document.getElementById('notification-dropdown');
            if (dropdown) dropdown.classList.add('open');
        } else {
            showToast('Simulation request failed', 'error');
        }
    } catch (e) {
        showToast('Simulation failed to connect', 'error');
    } finally {
        if (btn) btn.innerHTML = '<i class="fa-solid fa-bolt"></i> Simulate Rapid Data Growth';
    }
}
