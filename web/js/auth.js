const Auth = {
    async login(email, password, isAdmin = false) {
        const endpoint = isAdmin ? '/admin/login/' : '/auth/login/';
        try {
            const data = await Api.post(endpoint, { email, password });
            
            // Store session exactly as backend returns it
            // Backend returns: { user: {...}, access: "...", refresh: "..." }
            localStorage.setItem('accessToken', data.access);
            localStorage.setItem('refreshToken', data.refresh);
            localStorage.setItem('user', JSON.stringify(data.user));
            localStorage.setItem('isLoggedIn', 'true');
            localStorage.setItem('isAdminLoggedIn', isAdmin ? 'true' : 'false');
            
            return data;
        } catch (error) {
            throw error;
        }
    },

    async register(name, email, password) {
        try {
            const data = await Api.post('/auth/register/', { 
                full_name: name,
                email: email, 
                password: password 
            });
            return data;
        } catch (error) {
            throw error;
        }
    },

    logout() {
        localStorage.clear();
        // Determine redirection path based on current directory level
        const isAdminPath = window.location.pathname.includes('/admin/');
        window.location.href = isAdminPath ? '../index.html' : 'index.html';
    },

    getUser() {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    },

    isAuthenticated() {
        return localStorage.getItem('isLoggedIn') === 'true';
    },

    checkPortalAccess() {
        const user = this.getUser();
        if (!user) return;

        const isAdminPath = window.location.pathname.includes('/admin/');
        const isStaff = user.is_staff || user.is_superuser || user.is_admin;

        // If on admin path but not staff, kick out to home
        if (isAdminPath && !isStaff) {
            window.location.href = '../index.html';
        }
        
        // If on public home page and staff, optionally redirect to admin (user preference)
        // But let's keep it simple: just prevent illegal access for now.
        return true;
    },

    renderPremiumCrown() {
        return `<svg class="premium-crown" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M5 16L3 5L8.5 10L12 4L15.5 10L21 5L19 16H5Z" fill="#F59E0B" stroke="#D97706" stroke-width="2" stroke-linejoin="round"/>
            <path d="M5 18H19V20H5V18Z" fill="#F59E0B"/>
        </svg>`;
    },

    updateUserUI(user) {
        if (!user) return;
        
        const nameEls = ['sidebar-name', 'user-name', 'user_greeting_name', 'profile-display-name', 'welcome-msg'];
        const avatarEls = ['sidebar-avatar', 'user-avatar', 'profile-avatar'];
        const membershipEl = document.getElementById('sidebar-membership');
        
        const crownSVG = user.is_premium ? this.renderPremiumCrown() : '';
        
        nameEls.forEach(id => {
            const el = document.getElementById(id);
            if (el) {
                if (id === 'user_greeting_name') {
                    el.textContent = user.full_name ? user.full_name.split(' ')[0] : 'User';
                } else if (id === 'welcome-msg') {
                    const name = user.full_name ? user.full_name.split(' ')[0] : user.email.split('@')[0];
                    el.textContent = `Forge on, ${name}!`;
                } else {
                    el.textContent = user.full_name || user.email.split('@')[0];
                }
                
                if (user.is_premium) {
                    el.innerHTML += crownSVG;
                }
            }
        });

        avatarEls.forEach(id => {
            const el = document.getElementById(id);
            if (el) {
                el.textContent = (user.full_name || user.email || 'U').charAt(0).toUpperCase();
            }
        });

        if (membershipEl) {
            if (user.is_premium) {
                membershipEl.textContent = 'Premium Member';
                membershipEl.classList.add('text-gradient');
                membershipEl.style.fontWeight = '800';
            } else {
                membershipEl.textContent = 'Standard Member';
                membershipEl.classList.remove('text-gradient');
                membershipEl.style.fontWeight = 'normal';
            }
        }
    }
};

// Global Initialization
if (Auth.isAuthenticated()) {
    Auth.checkPortalAccess();
}

// UI Interaction
if (document.getElementById('login-form')) {
    document.getElementById('login-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('login-email').value;
        const password = document.getElementById('login-password').value;
        const errorEl = document.getElementById('auth-error');
        const submitBtn = e.target.querySelector('button');

        try {
            errorEl.classList.add('hidden');
            submitBtn.disabled = true;
            submitBtn.textContent = 'Processing...';
            
            const data = await Auth.login(email, password);
            
            // Dynamic redirect based on user role
            if (data.user.is_staff || data.user.is_admin || data.user.is_superuser) {
                window.location.href = 'admin/commander.html';
            } else {
                window.location.href = 'dashboard.html';
            }
        } catch (error) {
            errorEl.textContent = error.message;
            errorEl.classList.remove('hidden');
            submitBtn.disabled = false;
            submitBtn.textContent = 'Start Forging';
        }
    });
}

if (document.getElementById('register-form')) {
    document.getElementById('register-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const name = document.getElementById('reg-name').value;
        const email = document.getElementById('reg-email').value;
        const password = document.getElementById('reg-password').value;
        const errorEl = document.getElementById('auth-error');
        const submitBtn = e.target.querySelector('button');

        // Final client-side password validation
        const isValid = (
            password.length >= 8 &&
            /\d/.test(password) &&
            /[!@#$%^&*(),.?":{}|<>]/.test(password) &&
            /[A-Z]/.test(password)
        );

        if (!isValid) {
            errorEl.textContent = 'Please fulfill all password requirements.';
            errorEl.classList.remove('hidden');
            return;
        }

        try {
            errorEl.classList.add('hidden');
            submitBtn.disabled = true;
            submitBtn.textContent = 'Creating Account...';
            
            await Auth.register(name, email, password);
            alert('Account created successfully! You can now log in.');
            if (window.switchAuth) window.switchAuth('login');
        } catch (error) {
            errorEl.textContent = error.message;
            errorEl.classList.remove('hidden');
            submitBtn.disabled = false;
            submitBtn.textContent = 'Create Account';
        }
    });

    // Real-time password validation for registration
    const regPasswordInput = document.getElementById('reg-password');
    if (regPasswordInput) {
        regPasswordInput.addEventListener('input', (e) => {
            const password = e.target.value;
            
            // Length check (8+)
            updateReq('req-length', password.length >= 8);
            
            // Number check
            updateReq('req-number', /\d/.test(password));
            
            // Special char check
            updateReq('req-special', /[!@#$%^&*(),.?":{}|<>]/.test(password));
            
            // Uppercase check
            updateReq('req-upper', /[A-Z]/.test(password));
        });
    }

    function updateReq(id, isValid) {
        const el = document.getElementById(id);
        if (!el) return;
        
        const icon = el.querySelector('.req-icon');
        if (isValid) {
            el.classList.add('valid');
            if (icon) icon.textContent = '✓';
        } else {
            el.classList.remove('valid');
            if (icon) icon.textContent = '✕';
        }
    }
}
if (document.getElementById('admin-login-form')) {
    console.log('Admin login form detected, attaching listener...');
    document.getElementById('admin-login-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        console.log('Admin login submission intercepted');
        const email = document.getElementById('admin-email').value;
        const password = document.getElementById('admin-password').value;
        const errorEl = document.getElementById('auth-error');
        const submitBtn = e.target.querySelector('button');

        try {
            errorEl.classList.add('hidden');
            submitBtn.disabled = true;
            submitBtn.textContent = 'Authorizing Access...';
            
            const data = await Auth.login(email, password, true); // isAdmin = true
            console.log('Admin authorization successful, redirecting...');
            window.location.href = 'dashboard.html';
        } catch (error) {
            console.error('Admin Auth Error:', error);
            errorEl.textContent = error.message;
            errorEl.classList.remove('hidden');
            submitBtn.disabled = false;
            submitBtn.textContent = 'Authorize Access';
        }
    });
}
