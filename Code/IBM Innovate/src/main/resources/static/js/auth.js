console.log("auth.js script loaded!");

function passwordVisible() {
    var x = document.getElementById("password");
    if (x.type === "password") {
        x.type = "text";
    } else {
        x.type = "password";
    }
}

async function handleLogin(email, password) {
    try {
        const response = await fetch('/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        const data = await response.json();

        if (response.ok) {
            // Store all user data in localStorage
            localStorage.setItem('token', data.token);
            localStorage.setItem('userId', data.userId);
            localStorage.setItem('userEmail', data.email);
            localStorage.setItem('userName', data.username);
            localStorage.setItem('current_user_type', data.userType || 'regular');

            // Redirect to dashboard
            window.location.href = '/dashboard';
        } else {
            alert(data.message || 'Login failed');
        }
    } catch (error) {
        console.error('Login error:', error);
        alert('An error occurred during login');
    }
}

function handleGoogleOAuthRedirect() {
    const urlParams = new URLSearchParams(window.location.search);

    // Log the full URL for debugging
    console.log("Current URL:", window.location.href);

    // Extract token and user details from query parameters
    const token = urlParams.get('token');
    const userId = urlParams.get('userId');
    const email = urlParams.get('email');
    const username = urlParams.get('username');
    const userType = urlParams.get('userType');


    if (token && userId && email && username) {
        // Save the token and user details to localStorage
        localStorage.setItem('token', token);
        localStorage.setItem('userId', userId);
        localStorage.setItem('userEmail', email);
        localStorage.setItem('userName', username);
        localStorage.setItem('current_user_type', userType || 'google');

        console.log("Google user details saved to localStorage:", {
            token: localStorage.getItem('token'),
            userId: localStorage.getItem('userId'),
            userEmail: localStorage.getItem('userEmail'),
            userName: localStorage.getItem('userName'),
            userType: localStorage.getItem('current_user_type'),
        });

        // Add a small delay before redirecting
        setTimeout(() => {
            window.location.href = "/dashboard";
        }, 100); // 100ms delay
    } else {
        console.error("Google OAuth failed: Missing token or user details in redirect URL");
    }
}

// Call the function when the page loads
window.handleLogin = handleLogin;
document.addEventListener('DOMContentLoaded', handleGoogleOAuthRedirect);