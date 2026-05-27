document.addEventListener("DOMContentLoaded", function () {
    const loginExpPopup = document.getElementById("daily-login-popup");
    const closePopupBtn = document.getElementById("close-daily-login-popup");
    const loginExpIcon = document.getElementById("daily-login-icon");
    const dailyLoginNotification = document.getElementById("daily-login-notification");

    // Function to disable buttons for days that don't match today
    function disableNonTodayButtons() {
        const today = new Date().getDay(); // 0 (Sunday) to 6 (Saturday)
        const buttons = document.querySelectorAll('#daily-login-popup button[data-day]');

        buttons.forEach((button, index) => {
            if (index !== today) {
                button.disabled = true;
                button.classList.remove('bg-blue-500');
                button.classList.add('bg-gray-500', 'cursor-not-allowed');
            } else {
                button.disabled = false;
                button.classList.remove('bg-gray-500', 'cursor-not-allowed');
                button.classList.add('bg-blue-500');
            }
        });
    }

    // Function to check if the user has claimed today's EXP
    function hasClaimedToday() {
        const lastClaimDate = localStorage.getItem('lastClaimDate');
        const today = new Date().toDateString();
        return lastClaimDate === today;
    }

    // Function to show/hide the notification icon
    function updateNotificationIcon() {
        if (hasClaimedToday()) {
            dailyLoginNotification.classList.add('hidden');
        } else {
            dailyLoginNotification.classList.remove('hidden');
        }
    }

    // Function to handle EXP claim
    function handleExpClaim(day) {
        const expGain = day === 6 ? 20 : 10; // Saturday (6) gives 20 EXP, others give 10
        updateEXP(expGain);
        localStorage.setItem('lastClaimDate', new Date().toDateString());

        // Disable the button for the current day
        const buttons = document.querySelectorAll('#daily-login-popup button[data-day]');
        buttons.forEach((button, index) => {
            if (index === day) {
                button.disabled = true;
                button.classList.remove('bg-blue-500');
                button.classList.add('bg-gray-500', 'cursor-not-allowed');
            }
        });

        updateNotificationIcon();
        loginExpPopup.classList.add('hidden'); // Close the popup after claiming EXP
    }

    // Show popup when the calendar icon is clicked
    loginExpIcon.addEventListener('click', () => {
        if (!hasClaimedToday()) {
            disableNonTodayButtons();
            loginExpPopup.classList.remove('hidden');
        } else {
            alert('You have already claimed your daily login EXP for today.');
        }
    });

    // Close popup when the close button is clicked
    closePopupBtn.addEventListener('click', () => {
        console.log("Close button clicked!");
        loginExpPopup.classList.add('hidden');
    });

    // Handle EXP claim when a button is clicked
    document.querySelectorAll('#daily-login-popup button[data-day]').forEach(button => {
        button.addEventListener('click', () => {
            const day = parseInt(button.getAttribute('data-day'));
            handleExpClaim(day);
        });
    });

    // Update the notification icon on page load
    updateNotificationIcon();
});

// EXP System Logic
let exp = 0;
let level = 1;
const expBar = document.getElementById('exp-bar');
const levelDisplay = document.getElementById('level');
const expDisplay = document.getElementById('exp-display'); // New element to display EXP
const levelColors = ['bg-green-500', 'bg-blue-500', 'bg-purple-500'];


// Function to disable the daily login button for the current day
function disableDailyLoginButton() {
    const today = new Date();
    const dayOfWeek = today.getDay(); // 0 (Sunday) to 6 (Saturday)
    const buttons = document.querySelectorAll('#daily-login-popup button[data-day]');

    buttons.forEach((button, index) => {
        if (index === dayOfWeek) {
            button.disabled = true; // Disable the current day's button
            button.classList.remove('bg-blue-500');
            button.classList.add('bg-gray-500', 'cursor-not-allowed');
        }
    });
}

function getExpNeededForLevel(currentLevel) {
    // Formula: Sum of 100 * level (e.g., Level 1: 100, Level 2: 100+200=300, Level 3: 100+200+300=600, etc.)
    return (currentLevel * (currentLevel + 1) / 2) * 100;
}

// Function to update the EXP bar and display
function updateExpBar(exp, level) {
    const expNeededForCurrentLevel = getExpNeededForLevel(level);
    const expNeededForNextLevel = getExpNeededForLevel(level + 1);
    const expInCurrentLevel = exp - expNeededForCurrentLevel;

    // Calculate progress percentage (0% to 100%)
    const expRequiredToNextLevel = expNeededForNextLevel - expNeededForCurrentLevel;
    const progressPercentage = (expInCurrentLevel / expRequiredToNextLevel) * 100;

    expBar.style.width = `${progressPercentage}%`;
    expBar.classList.remove(...levelColors);
    expBar.classList.add(levelColors[Math.min(level - 1, levelColors.length - 1)]); // Prevent out-of-bounds
    expDisplay.textContent = `${expInCurrentLevel}/${expRequiredToNextLevel}`;
    levelDisplay.textContent = level;
}

// Function to update EXP and send it to the backend
async function updateEXP(gain) {
    try {
        // Fetch current EXP and level from backend
        const { exp: currentExp, level: currentLevel } = await fetchCurrentExpAndLevel();
        exp = currentExp;
        level = currentLevel;

        // Add gained EXP
        exp += gain;

        // Check if user leveled up
        while (exp >= getExpNeededForLevel(level + 1)) {
            level++;
        }

        // Update UI
        updateExpBar(exp, level);

        // Save to backend
        await saveExpToBackend(exp, level);
    } catch (error) {
        console.error('Error updating EXP:', error);
    }
}

// Function to fetch the user's current EXP and level from the backend
async function fetchCurrentExpAndLevel() {
    try {
        const userId = localStorage.getItem('userId');
        const response = await fetch(`/api/get-exp?userId=${userId}`);
        if (!response.ok) {
            throw new Error('Failed to fetch user EXP and level');
        }
        const data = await response.json();
        return { exp: data.exp, level: data.level }; // Return the user's current EXP and level
    } catch (error) {
        console.error('Error fetching user EXP and level:', error);
        return { exp: 0, level: 1 }; // Default to 0 EXP and level 1 if fetching fails
    }
}

// Function to save EXP and level to the backend
async function saveExpToBackend(exp, level) {
    try {
        const userId = localStorage.getItem('userId');
        const response = await fetch('/api/save-exp', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ userId, exp, level }),
        });
        if (!response.ok) {
            throw new Error('Failed to save EXP');
        }
    } catch (error) {
        console.error('Error saving EXP:', error);
    }
}

// Show daily login popup immediately after redirect
document.addEventListener('DOMContentLoaded', () => {
    // Fetch the user's current EXP and level
    fetchExp();

});


// Handle EXP claim
document.querySelectorAll('#daily-login-popup button[data-day]').forEach(button => {
    button.addEventListener('click', () => {
        const day = parseInt(button.getAttribute('data-day'));
        const expGain = day === 6 ? 20 : 10; // Saturday (6) gives 20 EXP, others give 10
        updateEXP(expGain);
        dailyLoginPopup.classList.add('hidden');
        disableDailyLoginButton(); // Disable the button after claiming EXP
    });
});

// Fetch the user's current EXP and level on page load
async function fetchExp() {
    try {
        const userId = localStorage.getItem('userId');
        const response = await fetch(`/api/get-exp?userId=${userId}`);
        if (!response.ok) {
            throw new Error('Failed to fetch EXP');
        }
        const data = await response.json();
        exp = data.exp;
        level = data.level;
        levelDisplay.textContent = level;
        updateExpBar(exp, level); // Update the EXP bar and display
    } catch (error) {
        console.error('Error fetching EXP:', error);
    }
}

// Initialize the page
document.addEventListener('DOMContentLoaded', fetchExp);

// Connecting backend with frontend to display the user's streak
async function getUserStreak() {
    try {
        const userId = localStorage.getItem('userId');

        if (!userId) {
            console.error('User ID is not available');
            return;
        }

        // GET request to the backend endpoint
        const response = await fetch(`/progress/streak/${userId}`);
        const data = await response.json();

        // Checking the responses structure for debugging
        console.log(data);

        // Error handling to check if variable is valid
        const streak = isNaN(data.streak) ? 'N/A' : data.streak;

        // Display the streak if it has been fetched and exists
        const streakElement = document.getElementById('streakDisplay');
        streakElement.innerText = `Your current streak: ${streak}`;
    } catch (error) {
        console.error('Error fetching streak:', error);
        const streakElement = document.getElementById('streakDisplay');
        streakElement.innerText = 'Error loading streak';
    }
}

console.log('LocalStorage:', localStorage);
document.addEventListener('DOMContentLoaded', function () {
    const allCoursesLink = document.querySelector('a[href="/courses"]');
    const loadingAnimation = document.getElementById('loadingAnimation');
    const loadingVideo = loadingAnimation.querySelector('video');

    if (allCoursesLink) {
        allCoursesLink.addEventListener('click', function (event) {
            event.preventDefault(); // Prevent the default link behavior

            // Show the loading animation by adding the .visible class
            loadingAnimation.classList.add('visible');

            // Play the video
            loadingVideo.play();

            // Wait for 3 seconds (or 1 second) before navigating to the courses page
            setTimeout(function () {
                window.location.href = "/courses";
            }, 3000); // 3000 milliseconds = 3 seconds (or 1000 for 1 second)
        });
    }
});

const userId = localStorage.getItem('userId');
const username = localStorage.getItem('userName');
const token = localStorage.getItem('token');
console.log(userId, username, token);


// For loading the current number of completed and ongoing courses
document.addEventListener('DOMContentLoaded', async function () {
    const userId = localStorage.getItem('userId');

    if (userId) {
        try {
            // Fetch ongoing courses and update the count
            const ongoingCourses = await fetchOngoingCourses(userId);
            document.getElementById('ongoingCount').textContent = ongoingCourses.length;

            // Fetch completed courses and update the count
            const completedCourses = await fetchCompletedCourses(userId);
            document.getElementById('completedCount').textContent = completedCourses.length;
        } catch (error) {
            console.error('Error fetching course counts:', error);
        }
    } else {
        console.error('User ID not found in localStorage');
    }
});

// Function to fetch ongoing courses
async function fetchOngoingCourses(userId) {
    try {
        const response = await fetch(`/progress/ongoing-courses/${userId}`);
        if (!response.ok) {
            throw new Error('Failed to fetch ongoing courses');
        }
        const userCourses = await response.json();
        console.log("Fetched ongoing courses:", userCourses); // Log the fetched data

        const formattedCourses = userCourses.map(uc => ({
            id: uc.courseId,
            courseName: uc.courseName,
            startTime: new Date(uc.startTime).toLocaleString(),
            duration: formatDuration(uc.startTime),
            languages: uc.languages,
            status: uc.status, // Include the course status
        }));

        console.log("Formatted ongoing courses:", formattedCourses); // Log the formatted data
        return formattedCourses;
    } catch (error) {
        console.error("Error fetching ongoing courses:", error);
        return [];
    }
}

// Function to fetch completed courses
async function fetchCompletedCourses(userId) {
    try {
        const response = await fetch(`/progress/completed-courses/${userId}`);
        if (!response.ok) {
            throw new Error('Failed to fetch completed courses');
        }
        const userCourses = await response.json();
        console.log("Fetched completed courses:", userCourses); // Log the fetched data

        const formattedCourses = userCourses.map(uc => ({
            id: uc.courseId,
            courseName: uc.courseName,
            startTime: new Date(uc.startTime).toLocaleString(),
            duration: formatDuration(uc.startTime),
            languages: uc.languages,
            status: uc.status, // Include the course status
        }));

        console.log("Formatted completed courses:", formattedCourses); // Log the formatted data
        return formattedCourses;
    } catch (error) {
        console.error("Error fetching completed courses:", error);
        return [];
    }
}

function formatDuration(startTime, endTime) {
    const start = new Date(startTime);
    const end = endTime ? new Date(endTime) : new Date();

    if (end < start) {
        return "Invalid duration"; // Handle invalid time range
    }

    const durationInMilliseconds = end - start;
    const days = Math.floor(durationInMilliseconds / (1000 * 60 * 60 * 24));
    const hours = Math.floor((durationInMilliseconds % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((durationInMilliseconds % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((durationInMilliseconds % (1000 * 60)) / 1000);
    return `${days}d ${hours}h ${minutes}m ${seconds}s`;
}

// Function to open the profile popup and fetch user details
document.getElementById('nav-profile-picture').addEventListener('click', async function () {
    const profilePopup = document.getElementById('profile-popup');
    const profileName = document.getElementById('profile-name');
    const profileEmail = document.getElementById('profile-email');
    const popupProfilePicture = document.getElementById('popup-profile-picture');
    const popupAvatar = document.getElementById('popup-avatar');

    // Fetch user details from localStorage
    const userName = localStorage.getItem('userName');
    const userEmail = localStorage.getItem('userEmail');
// Update the profile name and email
    if (userName) {
        profileName.textContent = userName;
    } else {
        profileName.textContent = 'Unknown User'; // Fallback if userName is not available
    }

    if (userEmail) {
        profileEmail.textContent = userEmail;
    } else {
        profileEmail.textContent = 'No Email'; // Fallback if UserEmail is not available
    }
    // Set the popup profile picture to match the navigation bar profile picture
    popupProfilePicture.src = document.getElementById('nav-profile-picture').src;

    try {
        const response = await fetch('http://localhost:8080/get-profile-details');
        if (response.ok) {
            const data = await response.json();
            profileName.textContent = data.username || 'Unknown User';
            profileEmail.textContent = data.email || 'No Email';
            popupAvatar.src = data.avatarUrl || 'uploads/default-avatar.png'; // Fetch saved avatar URL
        } else {
            console.error('Failed to fetch profile details');
        }
    } catch (error) {
        console.error('Error fetching profile details:', error);
    }

    profilePopup.classList.remove('hidden');
});

// Function to close the profile popup
document.getElementById('close-popup').addEventListener('click', function () {
    document.getElementById('profile-popup').classList.add('hidden');
});

// Function to load the profile picture in the navigation bar
async function loadNavProfilePicture() {
    try {
        const response = await fetch('http://localhost:8080/get-profile-image');
        if (response.ok) {
            const data = await response.json();
            if (data.imageUrl) {
                // Update the src attribute of the profile picture
                document.getElementById('nav-profile-picture').src = data.imageUrl;
            } else {
                console.error('No image URL found in response:', data);
            }
        } else {
            const errorData = await response.json();
            console.error('Failed to fetch profile image:', errorData.error || response.statusText);
        }
    } catch (error) {
        console.error('Error fetching profile image:', error);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const logoutLink = document.getElementById('logoutLink');

    if (logoutLink) {
        logoutLink.addEventListener('click', async (e) => {
            e.preventDefault(); // Prevent default link behavior

            // Show confirmation dialog
            const confirmLogout = confirm("Are you sure you want to log out?");
            if (!confirmLogout) return;

            try {
                // Call the logout endpoint
                const response = await fetch('/logout', {
                    method: 'POST',
                    credentials: 'include' // Include cookies for session-based auth
                });

                if (response.ok) {
                    // Clear localStorage
                    localStorage.removeItem('token');
                    localStorage.removeItem('userId');
                    localStorage.removeItem('userEmail');
                    localStorage.removeItem('userName');
                    localStorage.removeItem('current_user_type');
                    localStorage.removeItem('lastClaimDate')

                    // Redirect to login page
                    window.location.href = "/login";
                } else {
                    console.error('Logout failed');
                }
            } catch (error) {
                console.error('Error during logout:', error);
            }
        });
    }
});

// Load the profile picture when the page loads
document.addEventListener('DOMContentLoaded', loadNavProfilePicture);
getUserStreak();