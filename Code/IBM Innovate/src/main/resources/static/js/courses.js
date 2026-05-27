let exp = 0; // Current EXP
let level = 1; // Current level
const expBar = document.getElementById('exp-bar'); // EXP bar element
const levelDisplay = document.getElementById('level'); // Level display element

if (!expBar || !levelDisplay) {
    console.error('EXP bar or level display element not found');
    throw new Error('EXP bar or level display element not found');
}

const userId = localStorage.getItem('userId');

const levelLimits = [1200, 2500, 5100]; // EXP limits for each level
const levelColors = ['bg-green-500', 'bg-blue-500', 'bg-purple-500']; // Colors for each level

// Function to fetch the user's current EXP and level from the backend
async function fetchCurrentExpAndLevel() {
    try {
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

// Function to update EXP and level
async function updateEXP(gain) {
    try {
        // Fetch the current EXP and level from the backend
        const { exp: currentExp, level: currentLevel } = await fetchCurrentExpAndLevel();
        exp = currentExp; // Set the current EXP
        level = currentLevel; // Set the current level

        // Add the gained EXP
        exp += gain;

        // Check if the user levels up
        if (exp >= levelLimits[level - 1]) {
            exp -= levelLimits[level - 1]; // Carry over remaining EXP
            level++; // Increase level
            levelDisplay.textContent = level; // Update level display
            expBar.classList.remove(levelColors[level - 2]); // Remove previous level color
            expBar.classList.add(levelColors[level - 1]); // Add new level color
        }

        // Update the EXP bar width
        expBar.style.width = `${(exp / levelLimits[level - 1]) * 100}%`;

        // Save the updated EXP and level to the backend
        await saveExpToBackend(exp, level);
    } catch (error) {
        console.error('Error updating EXP:', error);
    }
}

// Function to save EXP and level to the backend
async function saveExpToBackend(exp, level) {
    try {
        const response = await fetch('/api/save-exp', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                userId: userId,
                exp: exp,
                level: level,
            }),
        });
        if (!response.ok) {
            throw new Error('Failed to save EXP');
        }
    } catch (error) {
        console.error('Error saving EXP:', error);
    }
}

// Function to initialize the EXP bar and level display
async function initializeExpBar() {
    try {
        const { exp: currentExp, level: currentLevel } = await fetchCurrentExpAndLevel();
        exp = currentExp;
        level = currentLevel;

        // Update the level display
        levelDisplay.textContent = level;

        // Update the EXP bar width
        expBar.style.width = `${(exp / levelLimits[level - 1]) * 100}%`;

        // Update the EXP bar color based on the level
        expBar.classList.add(levelColors[level - 1]);
    } catch (error) {
        console.error('Error initializing EXP bar:', error);
    }
}

document.addEventListener('DOMContentLoaded', function () {
    // Initialize the EXP bar and level display
    initializeExpBar();

    // Hamburger menu toggle
    document.getElementById('hamburger').addEventListener('click', function () {
        const navLinks = document.getElementById('navLinks');
        navLinks.classList.toggle('hidden'); // Toggle visibility
    });

    // Toggle search form visibility
    document.getElementById('searchToggle').addEventListener('click', function () {
        const searchForm = document.getElementById('searchForm');
        searchForm.classList.toggle('hidden'); // Toggle the 'hidden' class
    });

    // Filter courses based on search term and tags
    const searchInput = document.getElementById("searchTerm");
    const tagCheckboxes = document.querySelectorAll('input[name="tags"]');
    const courses = document.querySelectorAll('[data-tags]');

    function filterCourses() {
        const query = searchInput.value.toLowerCase();
        const duration = document.getElementById("duration").value.toLowerCase();
        const language = document.getElementById("languages").value.toLowerCase();
        const selectedTags = Array.from(tagCheckboxes)
            .filter(checkbox => checkbox.checked)
            .map(checkbox => checkbox.value.toLowerCase());

        courses.forEach(course => {
            const courseName = course.dataset.name.toLowerCase();
            const courseTags = course.dataset.tags.toLowerCase().split(" ");
            const courseDuration = course.querySelector('span:first-child').textContent.toLowerCase();
            const courseLanguage = course.querySelector('span:last-child').textContent.toLowerCase();

            const matchesSearch = query === "" || courseName.includes(query);
            const matchesTags = selectedTags.length === 0 || selectedTags.some(tag => courseTags.includes(tag));
            const matchesDuration = duration === "" || courseDuration.includes(duration);
            const matchesLanguage = language === "" || courseLanguage.includes(language);

            if (matchesSearch && matchesTags && matchesDuration && matchesLanguage) {
                course.style.display = "block";
            } else {
                course.style.display = "none";
            }
        });
    }

    searchInput.addEventListener("input", filterCourses);
    tagCheckboxes.forEach(checkbox => {
        checkbox.addEventListener("change", filterCourses);
    });

    document.getElementById("searchFilterForm").addEventListener("submit", function (e) {
        e.preventDefault();
        filterCourses();
    });

    // Start Learning button handlers
    const startButtons = document.querySelectorAll('[data-course-id]');

    // Function to update button state based on course status
    function updateButtonState(button, status) {
        if (status === "ongoing") {
            button.disabled = true;
            button.textContent = 'In Progress';
        } else {
            button.disabled = false;
            button.textContent = 'Start Learning';
        }
    }

    // Fetch course status for all buttons when the page loads
    startButtons.forEach(async button => {
        const courseId = button.dataset.courseId;

        try {
            const response = await fetch(`/progress/course-status/${userId}/${courseId}`);
            if (!response.ok) {
                throw new Error('Failed to fetch course status');
            }

            const data = await response.json();
            updateButtonState(button, data.status); // Update button state based on status
        } catch (error) {
            console.error('Error fetching course status:', error);
        }
    });

    // Handle "Start Learning" button clicks
    startButtons.forEach(button => {
        button.addEventListener('click', async function () {
            const courseId = this.dataset.courseId;
            const courseName = this.closest('[data-name]').dataset.name;
            const tags = this.closest('[data-tags]').dataset.tags.split(" ");
            const duration = this.closest('.bg-white').querySelector('span:first-child').textContent.replace('Duration: ', '');
            const languages = this.closest('.bg-white').querySelector('span:last-child').textContent.replace('Language: ', '').split(", ");

            // Ask for confirmation
            const confirmStart = confirm("Are you sure you want to start this course?");
            if (!confirmStart) return;

            // Disable the button and show "In Progress"
            this.disabled = true;
            this.textContent = 'In Progress';

            try {
                // Start the course
                const startResponse = await fetch(`/progress/start`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        userId: userId,
                        courseId: courseId,
                        courseName: courseName,
                        tags: tags,
                        duration: duration,
                        languages: languages,
                    }),
                });

                if (!startResponse.ok) {
                    throw new Error('Failed to start the course');
                }

                const startData = await startResponse.json();
                console.log(startData.message); // Log the success message

                // Award 2 EXP for starting the course
                await updateEXP(2); // Update the frontend EXP and send to backend
                alert("You've received 2 EXP for starting the course!"); // Notify the user

                // Update the button state based on the response
                updateButtonState(this, startData.status);
            } catch (error) {
                console.error('Error:', error);
                alert('Failed to start the course. Please try again.');
                this.disabled = false; // Re-enable the button if the request fails
                this.textContent = 'Start Learning'; // Restore the original text
            }
        });
    });
});

// Logout functionality
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