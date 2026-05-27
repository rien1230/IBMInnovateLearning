// Sample achievements data (replace with backend data)
const achievements = [
    {
        id: 1,
        title: "LLM Explorer",
        description: "You've mastered the basics of Large Language Models! Use this knowledge to build smarter chatbots.",
        unlocked: false,
    },
    {
        id: 2,
        title: "Chatbot Creator",
        description: "You can now build your own chatbots! Try creating one for your business or personal projects.",
        unlocked: false,
    },
    {
        id: 3,
        title: "Data Classifier",
        description: "You can classify data like a pro! Use this skill to organize and analyze large datasets.",
        unlocked: false,
    },
    {
        id: 4,
        title: "Voice Synthesizer",
        description: "You've explored text-to-speech technology! Now bring AI-powered voices to life.",
        unlocked: false,
    },
    {
        id: 5,
        title: "AI-Powered Developer",
        description: "You understand how IBM Granite enhances software development. Use AI to streamline coding!",
        unlocked: false,
    },
    {
        id: 6,
        title: "Data Summarization Expert",
        description: "You can now summarize large amounts of data efficiently using IBM Granite.",
        unlocked: false,
    },
    {
        id: 7,
        title: "Generative AI Coder",
        description: "You've learned how to use AI for software development. Leverage AI to generate and optimize code!",
        unlocked: false,
    },
    {
        id: 8,
        title: "AI Foundations Graduate",
        description: "You've built a strong foundation in artificial intelligence concepts and applications.",
        unlocked: false,
    },
    {
        id: 9,
        title: "AI Fundamentals Expert",
        description: "You've explored AI ethics, applications, and industry use cases. You're ready to dive deeper!",
        unlocked: false,
    },
    {
        id: 10,
        title: "Advanced AI Builder",
        description: "You've built AI solutions using advanced algorithms and open-source frameworks. Take on more complex AI challenges!",
        unlocked: false,
    },
    {
        id: 11,
        title: "Ethical AI Advocate",
        description: "You understand the importance of ethical AI in business. Apply these principles in your projects!",
        unlocked: false,
    },
    {
        id: 12,
        title: "AI Code Optimizer",
        description: "You've explored AI-powered code generation and optimization using IBM Granite. Boost your efficiency with AI-assisted coding!",
        unlocked: false,
    },
    {
        id: 13,
        title: "Data Insights Master",
        description: "You've mastered AI-driven data classification and summarization techniques using IBM Granite. Apply these skills to analyze vast datasets!",
        unlocked: false,
    },
    {
        id: 14,
        title: "Generative AI Innovator",
        description: "You've gained hands-on experience with generative AI in action! Use this knowledge to create innovative AI solutions.",
        unlocked: false,
    },
    {
        id: 15,
        title: "Cybersecurity Explorer",
        description: "You've taken your first steps into cybersecurity. Learn how AI can enhance security practices!",
        unlocked: false,
    },
    {
        id: 16,
        title: "Cybersecurity Foundations Expert",
        description: "You've built a solid foundation in cybersecurity concepts. Apply these principles to protect systems and networks.",
        unlocked: false,
    },
    {
        id: 17,
        title: "Enterprise Security Strategist",
        description: "You understand how security works at an enterprise level. Leverage AI for better security management!",
        unlocked: false,
    },
    {
        id: 18,
        title: "Threat Intelligence Analyst",
        description: "You've explored AI-driven threat intelligence and hunting techniques. Strengthen your ability to detect and respond to cyber threats!",
        unlocked: false,
    },
    {
        id: 19,
        title: "Security Operations Master",
        description: "You've learned about Security Operations Centers (SOC). Use AI to enhance SOC workflows and threat response!",
        unlocked: false,
    },
    {
        id: 20,
        title: "Data Enthusiast",
        description: "You've started your journey into data science. Learn how AI can help you uncover insights!",
        unlocked: false,
    },
    {
        id: 21,
        title: "Data Fundamentals Expert",
        description: "You understand key data concepts and how they power AI. Use this knowledge to work with structured and unstructured data!",
        unlocked: false,
    },
    {
        id: 22,
        title: "Enterprise Data Scientist",
        description: "You've explored how data science is applied in enterprise settings. Use AI-driven insights for real-world problem-solving!",
        unlocked: false,
    },
    {
        id: 23,
        title: "Machine Learning Innovator",
        description: "You've gained hands-on experience in machine learning for data science projects. Use this expertise to build powerful ML models!",
        unlocked: false,
    },
    {
        id: 24,
        title: "Cloud Computing Expert",
        description: "You've mastered the fundamentals of cloud computing. Apply these skills to deploy AI models and data solutions in the cloud!",
        unlocked: false,
    },
    {
        id: 25,
        title: "Cloud AI Architect",
        description: "You understand how to design cloud-based AI solutions. Use this expertise to build scalable AI applications!",
        unlocked: false,
    }
];


// Fetch ongoing courses from the backend
// Fetch ongoing courses from the backend
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

// Fetch completed courses from the backend
async function fetchCompletedCourses(userId) {
    try {
        const response = await fetch(`/progress/completed-courses/${userId}`);
        if (!response.ok) {
            throw new Error('Failed to fetch completed courses');
        }
        const userCourses = await response.json();
        return userCourses.map(uc => ({
            id: uc.courseId,
            courseName: uc.courseName,
            startTime: new Date(uc.startTime).toLocaleString(),
            endTime: new Date(uc.endTime).toLocaleString(),
            duration: formatDuration(uc.startTime, uc.endTime),
            redoCount: uc.redoCount, // Include the redo count
        }));
    } catch (error) {
        console.error("Error fetching completed courses:", error);
        return [];
    }
}

// Update achievements list
function updateAchievementsList(achievements) {
    const achievementsList = document.getElementById('achievementsList');
    achievementsList.innerHTML = achievements
        .filter(achievement => achievement.unlocked) // Only show unlocked achievements
        .map(achievement => `
      <div class="achievement-card">
        <div class="front" style="background: linear-gradient(135deg, #ff9a9e, #fad0c4)">
          ${achievement.title}
        </div>
        <div class="back">
          ${achievement.description}
        </div>
      </div>
    `).join('');
}

// Update ongoing courses list in the DOM
async function updateOngoingCoursesList(ongoingCourses) {
    const ongoingCoursesList = document.getElementById('ongoingCoursesList');
    ongoingCoursesList.innerHTML = ''; // Clear existing content

    if (ongoingCourses.length === 0) {
        ongoingCoursesList.innerHTML = '<p>No ongoing courses found.</p>';
        return;
    }

    ongoingCourses.forEach(course => {
        const courseElement = document.createElement('div');
        courseElement.className = 'mb-3 border-solid';
        courseElement.innerHTML = `
      <div class="card mb-3 border-solid shadow-sm">
        <div class="card-body">
          <h5>${course.courseName}</h5>
          <p>Started: ${course.startTime}</p>
          <p>Duration: ${course.duration}</p>
          <p>Languages: ${course.languages.join(', ')}</p>
          ${course.pauseTime ? `<p class="text-danger pause-time-display">Paused on: ${new Date(course.pauseTime).toLocaleString()}</p>` : ''}
          <div class="d-flex gap-2">
            <!-- Pause Button (visible if status is not paused) -->
            <button class="btn btn-warning pause-btn" data-course-id="${course.id}" ${course.status === 'paused' ? 'style="display: none;"' : ''}>
              Pause
            </button>
            <!-- Resume Button (visible if status is paused) -->
            <button class="btn btn-warning resume-btn" data-course-id="${course.id}" ${course.status !== 'paused' ? 'style="display: none;"' : ''}>
              Resume
            </button>
            <button class="btn btn-primary continue-learning-btn" data-course-id="${course.id}">
              Continue Learning
            </button>
            <button class="btn btn-success mark-as-completed" data-course-id="${course.id}">
              Mark as Completed
            </button>
          </div>
        </div>
      </div>
    `;
        ongoingCoursesList.appendChild(courseElement);
    });

    // Add event listeners for pause buttons
    document.querySelectorAll('.pause-btn').forEach(button => {
        button.addEventListener('click', async () => {
            const courseId = button.getAttribute('data-course-id');

            // Show confirmation alert
            if (!confirm("Are you sure you want to pause this course?")) return;

            try {
                const response = await fetch('/progress/pause-course', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ userId, courseId }),
                });

                if (!response.ok) throw new Error('Failed to pause course');

                const data = await response.json();
                console.log("Paused:", data);

                // Hide Pause button, show Resume button
                button.style.display = 'none';
                const resumeButton = button.parentElement.querySelector('.resume-btn');
                resumeButton.style.display = 'inline-block';

                // Display pause time in red
                const courseCard = button.closest('.card-body');
                let pauseTimeElement = courseCard.querySelector('.pause-time-display');
                if (!pauseTimeElement) {
                    pauseTimeElement = document.createElement('p');
                    pauseTimeElement.className = 'text-danger pause-time-display';
                    courseCard.insertBefore(pauseTimeElement, courseCard.querySelector('p:nth-of-type(3)').nextSibling);
                }
                pauseTimeElement.textContent = `Paused on: ${new Date().toLocaleString()}`;

            } catch (error) {
                console.error("Error pausing course:", error);
                alert("Failed to pause the course. Please try again.");
            }
        });
    });


    // Add event listeners for resume buttons
    document.querySelectorAll('.resume-btn').forEach(button => {
        button.addEventListener('click', async () => {
            const courseId = button.getAttribute('data-course-id');

            try {
                const response = await fetch('/progress/resume-course', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ userId, courseId }),
                });

                if (!response.ok) throw new Error('Failed to resume course');

                const data = await response.json();
                console.log("Resumed:", data);

                // Hide Resume button, show Pause button
                button.style.display = 'none';
                const pauseButton = button.parentElement.querySelector('.pause-btn');
                pauseButton.style.display = 'inline-block';

                // Remove pause time display
                const pauseTimeDisplay = button.closest('.card-body').querySelector('.pause-time-display');
                if (pauseTimeDisplay) pauseTimeDisplay.remove();

            } catch (error) {
                console.error("Error resuming course:", error);
                alert("Failed to resume the course. Please try again.");
            }
        });
    });


    // Add event listener for continue learning buttons
    document.querySelectorAll('.continue-learning-btn').forEach(button => {
        button.addEventListener('click', () => {
            window.location.href = 'https://skills.yourlearning.ibm.com/progress/detail';
        });
    });

    // Add event listeners for "Mark as Completed" buttons
    document.querySelectorAll('.mark-as-completed').forEach(button => {
        button.addEventListener('click', async () => {
            const courseId = button.getAttribute('data-course-id');
            const confirmCompletion = confirm("Are you sure you want to mark this course as completed?");
            if (confirmCompletion) {
                // Disable the button and show a spinner
                button.disabled = true;
                button.innerHTML = `
                <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                Marking...
            `;

                try {
                    const startTime = new Date().getTime(); // Record start time
                    await markCourseAsCompleted(courseId, startTime); // Pass start time
                    await fetchData(userId); // Refresh the data
                } catch (error) {
                    console.error("Error marking course as completed:", error);
                    alert("Failed to mark the course as completed. Please try again.");
                } finally {
                    // Re-enable the button and restore its original text
                    button.disabled = false;
                    button.innerHTML = 'Mark as Completed';
                }
            }
        });
    });
}

// Update completed courses list in the DOM
function updateCompletedCoursesList(completedCourses) {
    const completedCoursesList = document.getElementById('completedCoursesList');
    completedCoursesList.innerHTML = ''; // Clear existing content

    if (completedCourses.length === 0) {
        completedCoursesList.innerHTML = '<p>No completed courses found.</p>';
        return;
    }

    completedCourses.forEach(course => {
        const courseElement = document.createElement('div');
        courseElement.className = 'mb-3 shadow-md';
        courseElement.innerHTML = `
<div class="card mb-3 border-solid shadow-sm">
  <div class="card-body">
      <h5>${course.courseName}</h5>
      <p>Started: ${course.startTime}</p>
      <p>Completed: ${course.endTime}</p>
      <p>Duration: ${course.duration}</p>
      <p>Redo Count: ${course.redoCount}</p>
      </div>
      </div>
    `;
        completedCoursesList.appendChild(courseElement);
    });
}

// Get course duration
async function getCourseDuration(courseId) {
    try {
        const response = await fetch(`/course-duration/${courseId}`);
        if (!response.ok) {
            throw new Error('Failed to fetch course duration');
        }
        const duration = await response.text();
        return duration;
    } catch (error) {
        console.error("Error fetching course duration:", error);
        return null;
    }
}
// Mark a course as completed
async function markCourseAsCompleted(courseId, startTime) {
    try {
        // Send request to mark course as completed
        const response = await fetch('/progress/complete-course', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                userId: userId,
                courseId: courseId,
                startTime: startTime, // Include the start time
            }),
        });

        if (!response.ok) {
            throw new Error('Failed to mark course as completed');
        }

        const data = await response.json();
        console.log(data.message); // Log the success message

        // Notify the user about the EXP awarded
        alert(`Course completed successfully! You earned ${data.expAwarded} EXP.`);
    } catch (error) {
        console.error("Error marking course as completed:", error);
        alert("Failed to mark the course as completed. Please try again.");
    }
}

// Helper function to parse course duration into milliseconds
function parseDurationToMillis(duration) {
    if (duration.includes('minutes')) {
        const minutes = parseInt(duration.replace(/\D/g, ''), 10);
        return minutes * 60 * 1000; // Convert minutes to milliseconds
    } else if (duration.includes('hours')) {
        const hours = parseInt(duration.replace(/\D/g, ''), 10);
        return hours * 60 * 60 * 1000; // Convert hours to milliseconds
    } else if (duration.includes('+')) {
        const hours = parseInt(duration.replace(/\D/g, ''), 10);
        return hours * 60 * 60 * 1000; // Convert hours to milliseconds
    } else {
        throw new Error('Invalid duration format: ' + duration);
    }
}

//The pause button
// Add event listeners for pause/resume buttons
document.querySelectorAll('.pause-resume-btn').forEach(button => {
    button.addEventListener('click', async () => {
        const courseId = button.getAttribute('data-course-id');
        const isPaused = button.textContent.trim() === 'Resume';

        try {
            const endpoint = isPaused ? '/progress/resume-course' : '/progress/pause-course';
            const response = await fetch(endpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    userId: userId,
                    courseId: courseId,
                }),
            });

            if (!response.ok) {
                throw new Error('Failed to update course status');
            }

            // Refresh the data to update the UI
            await fetchData(userId);
        } catch (error) {
            console.error("Error updating course status:", error);
            alert("Failed to update course status. Please try again.");
        }
    });
});

// Add event listener for continue learning buttons
document.querySelectorAll('.continue-learning-btn').forEach(button => {
    button.addEventListener('click', () => {
        window.location.href = 'https://skills.yourlearning.ibm.com/progress/detail';
    });
});

// Update treasure map with randomized pin positions
function updateTreasureMap(achievements) {
    const treasureMap = document.querySelector('.treasure-map');
    treasureMap.innerHTML = ''; // Clear existing pins

    // Define the number of rows and columns for pin placement
    const rows = 5;
    const cols = 5;

    achievements.forEach((achievement, index) => {
        const pin = document.createElement('button');
        pin.className = `map-pin ${achievement.unlocked ? 'unlocked' : 'locked'}`;

        // Calculate position based on index
        const row = Math.floor(index / cols);
        const col = index % cols;

        // Base position with 15% padding and 15% spacing
        const baseTop = 19 + row * 15;
        const baseLeft = 18 + col * 15;

        // Add a random offset to the base position (e.g., ±5%)
        const randomTopOffset = (Math.random() * 10) - 5; // Random value between -5% and +5%
        const randomLeftOffset = (Math.random() * 10) - 5; // Random value between -5% and +5%

        // Apply the random offset to the base position
        pin.style.top = `${baseTop + randomTopOffset}%`;
        pin.style.left = `${baseLeft + randomLeftOffset}%`;

        // Use a gold bag icon for unlocked achievements
        pin.textContent = achievement.unlocked ? "💰" : "🔒";
        treasureMap.appendChild(pin);
    });
}

// Get the current user type
const currentUserType = localStorage.getItem('current_user_type');
console.log('Current User Type:', currentUserType);

let userId = localStorage.getItem("userId");


if (!userId) {
    console.error('User ID not found in localStorage');
    alert('Please log in again.');
    window.location.href = '/login'; // Redirect to login page
} else {
    console.log('User ID found, proceeding to student progress tracker.');
}

// Fetch data for the user
async function fetchData(userId) {
    try {
        // Show loading state with a spinner
        document.getElementById('ongoingCoursesList').innerHTML = `
                <div class="d-flex justify-content-center">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
            `;
        document.getElementById('completedCoursesList').innerHTML = `
                <div class="d-flex justify-content-center">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
            `;

        // Fetch data
        const ongoingCourses = await fetchOngoingCourses(userId);
        const completedCourses = await fetchCompletedCourses(userId);

        // Update the UI with fetched data
        updateOngoingCoursesList(ongoingCourses);
        updateCompletedCoursesList(completedCourses);

        // Unlock achievements for completed courses
        achievements.forEach((achievement) => {
            achievement.unlocked = completedCourses.some(course => course.id === achievement.id);
        });

        updateAchievementsList(achievements);
        updateTreasureMap(achievements);
    } catch (error) {
        console.error("Error fetching data:", error);
        // Show error message
        document.getElementById('ongoingCoursesList').innerHTML = '<p class="text-danger">Failed to load ongoing courses.</p>';
        document.getElementById('completedCoursesList').innerHTML = '<p class="text-danger">Failed to load completed courses.</p>';
    }
}

// Fetch data for the user
fetchData(userId);

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

function formatDuration(startTime, endTime, pauseTime) {
    const start = new Date(startTime);
    const end = endTime ? new Date(endTime) : new Date();
    const pause = pauseTime ? new Date(pauseTime) : null;

    if (end < start) {
        return "Invalid duration"; // Handle invalid time range
    }

    let durationInMilliseconds = end - start;
    if (pause) {
        durationInMilliseconds = (pause - start) + (end - pause);
    }

    const days = Math.floor(durationInMilliseconds / (1000 * 60 * 60 * 24));
    const hours = Math.floor((durationInMilliseconds % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((durationInMilliseconds % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((durationInMilliseconds % (1000 * 60)) / 1000);
    return `${days}d ${hours}h ${minutes}m ${seconds}s`;
}