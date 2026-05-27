console.log("login.ts script loaded!");

// Regular login form submission
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault(); // Prevent form reload

        const emailInput = document.getElementById('email') as HTMLInputElement;
        const passwordInput = document.getElementById('password') as HTMLInputElement;

        if (!emailInput || !passwordInput) {
            console.error('Email or password input not found!');
            return;
        }

        const email = emailInput.value;
        const password = passwordInput.value;

        try {
            const response = await fetch('/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
            });

            const data = await response.json();

            if (data.token) {
                // Save normal login details to localStorage
                localStorage.setItem('token', data.token);
                localStorage.setItem('userId', data.userId);
                localStorage.setItem('userEmail', data.email);
                localStorage.setItem('userName', data.username);

                console.log("Normal login details saved to localStorage:", localStorage);

                // Redirect to dashboard
                window.location.href = "/dashboard";
            } else {
                console.error("Login failed:", data.message || "Unknown error");
            }
        } catch (error) {
            console.error('Fetch error:', error);
        }
    });
}