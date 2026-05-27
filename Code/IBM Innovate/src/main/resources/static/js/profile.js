// profile.js
window.loadContent = loadContent;


// Function to load content dynamically
async function loadContent(section) {
    console.log(`Loading content for section: ${section}`);

    // Hide all tab content
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });

    // Show the selected tab content
    const tabContent = document.getElementById(`${section}-content`);
    if (tabContent) {
        tabContent.classList.add('active');
    }

    // If the section is "manage-profile", fetch the content from /update
    if (section === 'manage-profile') {
        await loadManageProfileContent();
    }

    // If the section is "avatar", initialize the avatar tabs and fetch user data
    if (section === 'avatar') {
        await fetchUserData(); // Add this line to fetch user data first
        initializeAvatarTabs();
    }
}

async function fetchUserData() {
    try {
        const token = localStorage.getItem('token');
        const userId = localStorage.getItem('userId');

        const response = await fetch(`http://localhost:8080/api/users/${userId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const userData = await response.json();
            // Call restrictAvatarTabs with the user's experience points
            restrictAvatarTabs(userData.exp || 0);
        } else {
            console.error('Failed to fetch user data');
        }
    } catch (error) {
        console.error('Error fetching user data:', error);
    }
}

// Function to initialize avatar tabs
function initializeAvatarTabs() {
    console.log("Initializing avatar tabs"); // Debugging

    const tabs = document.querySelectorAll('[data-tab-target]');
    const tabContents = document.querySelectorAll('[role="tabpanel"]');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const target = document.querySelector(tab.dataset.tabTarget);
            tabContents.forEach(tabContent => {
                tabContent.classList.add('hidden', 'opacity-0');
            });
            target.classList.remove('hidden', 'opacity-0');
        });
    });
}

// Function to load manage profile content from /update
async function loadManageProfileContent() {
    try {
        console.log("Loading manage profile content"); // Debugging

        const token = localStorage.getItem('token');
        const response = await fetch('/update', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const data = await response.text(); // Assuming the response is HTML
            document.getElementById('manage-profile-dynamic-content').innerHTML = data;

            // Execute JavaScript code from the loaded content
            const scriptTags = document.getElementById('manage-profile-dynamic-content').getElementsByTagName('script');
            for (let script of scriptTags) {
                const newScript = document.createElement('script');
                if (script.src) {
                    newScript.src = script.src; // Load external scripts
                } else {
                    newScript.textContent = script.textContent; // Execute inline scripts
                }
                document.body.appendChild(newScript).remove(); // Append and remove to execute
            }
        } else {
            console.error('Failed to fetch manage profile content');
        }
    } catch (error) {
        console.error('Error fetching manage profile content:', error);
    }
}

// Load profile image and avatar when the page loads
document.addEventListener('DOMContentLoaded', () => {
    console.log("DOM fully loaded and parsed"); // Debugging

    // Initialize Tippy tooltips
    tippy('[data-tooltip]', {
        content: (reference) => reference.getAttribute('data-tooltip'),
        placement: 'top',
        theme: 'material',
        arrow: true,
        onShow(instance) {
            // Only show tooltip if element is disabled
            return instance.reference.classList.contains('disabled');
        }
    });

    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userId');
    const username = localStorage.getItem('userName');

    if (!userId || !username || !token) {
        alert('Please log in again.');
        window.location.href = '/login';
    } else {
        document.getElementById('userIdDisplay').textContent = userId;
        document.getElementById('usernameDisplay').textContent = username;

        loadProfileImage();
        loadAvatar();
        loadExpData();
    }

    // Bind event listeners to buttons
    document.getElementById('save-avatar-url')?.addEventListener('click', saveAvatarUrl);
    document.getElementById('save-dicebear-avatar')?.addEventListener('click', saveDiceBearAvatar);
    document.getElementById('save-ready-player-me-avatar')?.addEventListener('click', saveReadyPlayerMeAvatar);
});

// Save avatar URL
async function saveAvatarUrl() {
    try {
        console.log("Saving avatar URL"); // Debugging

        const userId = localStorage.getItem('userId');
        const token = localStorage.getItem('token');

        // Get the image tag from the input field
        const imgTag = document.getElementById('avatar-img-tag').value.trim();
        console.log("Image Tag:", imgTag); // Debugging

        // Extract the src attribute from the image tag
        const srcRegex = /src=['"]([^'"]*)['"]/; // Matches src='...' or src="..."
        const srcMatch = imgTag.match(srcRegex);

        if (!srcMatch || !srcMatch[1]) {
            alert('Invalid image tag. Please paste a valid <img src="..."/> tag.');
            return;
        }

        const avatarUrl = srcMatch[1]; // Extract the URL from the src attribute
        console.log("Extracted Avatar URL:", avatarUrl); // Debugging

        // Send the avatar URL to the backend
        const response = await fetch(`http://localhost:8080/api/users/${userId}/avatar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ avatarUrl }),
        });

        console.log("Response status:", response.status); // Debugging

        if (response.ok) {
            alert('Avatar URL saved successfully!');
            loadAvatar(); // Refresh the avatar display
        } else if (response.status === 401) {
            alert('Your session has expired. Please log in again.');
            window.location.href = '/login';
        } else {
            const errorData = await response.text();
            console.error('Failed to save avatar URL:', errorData);
            alert('Failed to save avatar URL: ' + errorData);
        }
    } catch (error) {
        console.error('Error saving avatar URL:', error);
        alert('An error occurred while saving the avatar URL.');
    }
}

// Load avatar
async function loadAvatar() {
    try {
        console.log("Loading avatar"); // Debugging

        const userId = localStorage.getItem('userId');
        const token = localStorage.getItem('token');

        // Fetch the avatar URL from the backend
        const response = await fetch(`http://localhost:8080/api/users/${userId}/avatar`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
        });

        if (response.ok) {
            const data = await response.json();
            const avatarUrl = data.avatarUrl;
            console.log("Fetched Avatar URL:", avatarUrl); // Debugging

            // Display the avatar
            const avatarSection = document.getElementById('avatar-section');
            avatarSection.innerHTML = ''; // Clear previous content

            if (avatarUrl.endsWith('.glb')) {
                // Render the 3D avatar
                renderGLBAvatar(avatarUrl, avatarSection);
            } else {
                // Display the 2D avatar
                avatarSection.innerHTML = `<img src="${avatarUrl}" alt="Avatar" class="w-full h-full object-cover">`;
            }
        } else if (response.status === 401) {
            alert('Your session has expired. Please log in again.');
            window.location.href = '/login';
        } else {
            console.error('Failed to fetch avatar URL');
        }
    } catch (error) {
        console.error('Error loading avatar:', error);
    }
}

async function loadExpData() {
    try {
        const token = localStorage.getItem('token');
        const userId = localStorage.getItem('userId');

        // Make sure we have the required values
        if (!userId || !token) {
            console.error('User ID or token not found');
            return;
        }

        const response = await fetch(`/api/get-exp?userId=${userId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const data = await response.json();
            const exp = data.exp;
            const level = data.level;

            // Calculate max XP for current level (assuming 120 XP per level)
            const maxExp = level * 120;

            // Update the main display
            document.getElementById('exp-bar').style.width = `${(exp / maxExp) * 100}%`;
            document.getElementById('exp-display').textContent = `${exp}/${maxExp}`;
            document.getElementById('level').textContent = level;

            // Update the avatar section display
            document.getElementById('avatar-exp-bar').style.width = `${(exp / maxExp) * 100}%`;
            document.getElementById('avatar-exp').textContent = `${exp}/${maxExp}`;
            document.getElementById('avatar-level').textContent = level;
        } else {
            console.error('Failed to fetch EXP data:', response.statusText);
        }
    } catch (error) {
        console.error('Error loading EXP data:', error);
    }
}

// Function to render a .glb file using Three.js
function renderGLBAvatar(glbUrl, container) {
    console.log("Rendering GLB avatar:", glbUrl); // Debugging

    // Check if THREE is defined
    if (typeof THREE === 'undefined') {
        console.error('Three.js is not loaded. Make sure the Three.js script is included before profile.js.');
        return;
    }

    // Clear any previous renderers to prevent duplicates
    container.innerHTML = '';

    // Set up scene
    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(75, container.clientWidth / container.clientHeight, 0.1, 1000);
    const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });

    // Set renderer size to match the container
    renderer.setSize(container.clientWidth, container.clientHeight);
    container.appendChild(renderer.domElement);

    // Add lighting
    const ambientLight = new THREE.AmbientLight(0xffffff, 1);
    scene.add(ambientLight);

    const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
    directionalLight.position.set(2, 2, 2);
    scene.add(directionalLight);

    // Load GLB model
    const loader = new THREE.GLTFLoader();
    loader.load(glbUrl, (gltf) => {
        const model = gltf.scene;
        scene.add(model);

        // Adjust model scaling
        const box = new THREE.Box3().setFromObject(model);
        const size = new THREE.Vector3();
        box.getSize(size); // Get the size of the model

        // Calculate the scaling factor to fit the model within the container
        const maxDimension = Math.max(size.x, size.y, size.z);
        const scaleFactor = 6 / maxDimension; // Adjust this value to control the size
        model.scale.set(scaleFactor, scaleFactor, scaleFactor);

        // Center the model
        const center = new THREE.Vector3();
        box.getCenter(center); // Get the center of the model
        model.position.sub(center); // Center the model at (0, 0, 0)

        // Move the model slightly downward (adjust the Y-axis)
        model.position.y -= 2; // Move the model 3 units downward

        // Adjust camera position
        camera.position.set(0, 0, 5); // Move the camera closer or farther as needed
        camera.lookAt(0, 0, 0); // Make the camera look at the center of the scene

        // Animation loop
        function animate() {
            requestAnimationFrame(animate);
            model.rotation.y += 0.01; // Rotate the model
            renderer.render(scene, camera);
        }
        animate();
    }, undefined, (error) => {
        console.error('Error loading .glb file:', error);
    });

    // Resize handler (to keep the model responsive)
    window.addEventListener('resize', () => {
        const width = container.clientWidth;
        const height = container.clientHeight;
        renderer.setSize(width, height);
        camera.aspect = width / height;
        camera.updateProjectionMatrix();
    });
}

// Intermediate Tab (DiceBear)
function updateDiceBearAvatar() {
    const seed = document.getElementById("seed").value;
    const style = document.getElementById("style").value;
    const dicebearUrl = `https://api.dicebear.com/7.x/${style}/svg?seed=${seed}`;
    document.getElementById("dicebear-avatar").src = dicebearUrl;
}

async function saveDiceBearAvatar() {
    try {
        console.log("Saving DiceBear avatar"); // Debugging

        const userId = localStorage.getItem('userId');
        const token = localStorage.getItem('token');
        const dicebearUrl = document.getElementById("dicebear-avatar").src;

        const response = await fetch(`http://localhost:8080/api/users/${userId}/avatar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ avatarUrl: dicebearUrl }),
        });

        if (response.ok) {
            alert('Avatar saved successfully!');
        } else {
            alert('Failed to save avatar.');
        }
    } catch (error) {
        console.error('Error saving avatar:', error);
        alert('An error occurred while saving the avatar.');
    }
}

// Pro Tab (Ready Player Me)
async function saveReadyPlayerMeAvatar() {
    try {
        console.log("Saving Ready Player Me avatar"); // Debugging

        const userId = localStorage.getItem('userId');
        const token = localStorage.getItem('token');

        // Get the .glb URL from the input field
        const glbUrl = document.getElementById('glb-url').value.trim();

        if (!glbUrl) {
            alert('Please paste a valid .glb URL.');
            return;
        }

        // Send the .glb URL to the backend
        const response = await fetch(`http://localhost:8080/api/users/${userId}/avatar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ avatarUrl: glbUrl }),
        });

        if (response.ok) {
            alert('Avatar saved successfully!');
            loadAvatar(); // Refresh the avatar display
        } else if (response.status === 401) {
            alert('Your session has expired. Please log in again.');
            window.location.href = '/login';
        } else {
            const errorData = await response.text();
            console.error('Failed to save avatar:', errorData);
            alert('Failed to save avatar: ' + errorData);
        }
    } catch (error) {
        console.error('Error saving avatar:', error);
        alert('An error occurred while saving the avatar.');
    }
}

function restrictAvatarTabs(exp) {
    const intermediateTabButton = document.getElementById('intermediate-tab-button');
    const proTabButton = document.getElementById('pro-tab-button');

    // Intermediate Tab (50 EXP required)
    if (exp < 50) {
        if (intermediateTabButton) {
            intermediateTabButton.classList.add('disabled');

            // Update Tippy tooltip content
            if (intermediateTabButton._tippy) {
                intermediateTabButton._tippy.setContent(`Unlock at 50 EXP (You have ${exp})`);
            } else {
                intermediateTabButton.setAttribute('data-tooltip', `Unlock at 50 EXP (You have ${exp})`);
            }

            // Prevent clicking
            intermediateTabButton.onclick = (e) => {
                e.preventDefault();
                alert(`You need 50 EXP to unlock this feature. You currently have ${exp} EXP.`);
            };
        }
    } else {
        if (intermediateTabButton) {
            intermediateTabButton.classList.remove('disabled');
            if (intermediateTabButton._tippy) {
                intermediateTabButton._tippy.setContent('');
            }
            intermediateTabButton.onclick = null;
        }
    }

    // Pro Tab (100 EXP required)
    if (exp < 100) {
        if (proTabButton) {
            proTabButton.classList.add('disabled');

            // Update Tippy tooltip content
            if (proTabButton._tippy) {
                proTabButton._tippy.setContent(`Unlock at 100 EXP (You have ${exp})`);
            } else {
                proTabButton.setAttribute('data-tooltip', `Unlock at 100 EXP (You have ${exp})`);
            }

            // Prevent clicking
            proTabButton.onclick = (e) => {
                e.preventDefault();
                alert(`You need 100 EXP to unlock this feature. You currently have ${exp} EXP.`);
            };
        }
    } else {
        if (proTabButton) {
            proTabButton.classList.remove('disabled');
            if (proTabButton._tippy) {
                proTabButton._tippy.setContent('');
            }
            proTabButton.onclick = null;
        }
    }
}


// Load saved profile image from backend
async function loadProfileImage() {
    try {
        console.log("Loading profile image");
        const token = localStorage.getItem('token');
        const username = localStorage.getItem('userName');

        const response = await fetch(`http://localhost:8080/get-profile-image?username=${username}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const data = await response.json();
            const profileImage = document.getElementById('profile-image');

            if (data.imageUrl) {
                profileImage.src = data.imageUrl;
            } else {
                // Set a default/fallback image if none exists
                profileImage.src = 'fallback-image.png';
                console.log('No profile image found, using fallback');
            }
        } else if (response.status === 401) {
            alert('Your session has expired. Please log in again.');
            window.location.href = '/login';
        } else {
            const errorData = await response.json();
            console.error('Failed to fetch profile image:', errorData.error || response.statusText);
        }
    } catch (error) {
        console.error('Error fetching profile image:', error);
        // Ensure we always show something, even if there's an error
        document.getElementById('profile-image').src = 'fallback-image.png';
    }
}

// Handle profile image upload
async function handleImageUpload(event) {
    const file = event.target.files[0];
    if (file) {
        if (file.type !== 'image/png') {
            alert('Only PNG files are allowed.');
            return;
        }

        const reader = new FileReader();
        reader.onload = function (e) {
            document.getElementById('profile-image').src = e.target.result;
        };
        reader.readAsDataURL(file);

        const formData = new FormData();
        formData.append('image', file);
        // Add username to the form data
        formData.append('username', localStorage.getItem('userName'));

        try {
            const token = localStorage.getItem('token');
            const response = await fetch('http://localhost:8080/upload-profile-image', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                body: formData
            });

            const data = await response.json();
            if (response.ok) {
                alert('Profile picture updated successfully!');
                document.getElementById('profile-image').src = data.imageUrl;

                // Update the profile image display immediately
                loadProfileImage();
            } else {
                alert('Error uploading image: ' + (data.error || 'Unknown error'));
            }
        } catch (error) {
            console.error('Upload error:', error);
            alert('An error occurred while uploading the image.');
        }
    }
}
// Badge System Implementation
class BadgeManager {
    constructor() {
        this.badgeDefinitions = {
            courseCompleter: {
                name: "Course Completer",
                description: "Complete courses to earn these badges",
                levels: [
                    { threshold: 1, name: "Course Starter", image: "Course_Completer_1.png" },
                    { threshold: 3, name: "Course Explorer", image: "Course_Completer_2.png" },
                    { threshold: 5, name: "Course Master", image: "Course_Completer_3.png" }
                ],
                attribute: "completedCourses"
            },
            friend: {
                name: "Social Butterfly",
                description: "Make friends to earn these badges",
                levels: [
                    { threshold: 1, name: "First Friend", image: "Friend_1.png" },
                    { threshold: 5, name: "Socialite", image: "Friend_2.png" }
                ],
                attribute: "friendsCount"
            },
            multitasker: {
                name: "Multitasker",
                description: "Work on multiple courses simultaneously",
                levels: [
                    { threshold: 2, name: "Task Juggler", image: "Multitasker_1.png" },
                    { threshold: 3, name: "Multitasker", image: "Multitasker_2.png" },
                    { threshold: 5, name: "Ultimate Multitasker", image: "Multitasker_3.png" }
                ],
                attribute: "ongoingCourses"
            },
            weekendWarrior: {
                name: "Weekend Warrior",
                description: "Active on weekends",
                levels: [
                    { threshold: 1, name: "Weekend Warrior", image: "WeekendWarrior.png" }
                ],
                attribute: "weekendActivity",
                specialCheck: true
            }
        };

        this.notificationQueue = [];
        this.isShowingNotification = false;
    }

    async initialize() {
        await this.loadUserBadges();
        this.renderBadges();
        this.startMonitoring();
    }

    async loadUserBadges() {
        try {
            const token = localStorage.getItem('token');
            const userId = localStorage.getItem('userId');

            const response = await fetch(`http://localhost:8080/api/users/${userId}/badges`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                this.userBadges = await response.json();
            } else {
                console.error('Failed to load user badges');
                this.userBadges = {};
            }
        } catch (error) {
            console.error('Error loading user badges:', error);
            this.userBadges = {};
        }
    }

    renderBadges() {
        const container = document.getElementById('badge-categories-container');
        container.innerHTML = '';

        // First, check if we already have a profile badges container
        let profileBadgesContainer = document.getElementById('profile-badges-container');

        // If it doesn't exist, create it
        if (!profileBadgesContainer) {
            profileBadgesContainer = document.createElement('div');
            profileBadgesContainer.className = 'mt-6';
            profileBadgesContainer.id = 'profile-badges-container';
            profileBadgesContainer.innerHTML = `
            <h3 class="text-lg font-semibold mb-2">Unlocked Badges</h3>
            <div class="flex flex-wrap gap-4" id="profile-badges"></div>
        `;
            document.getElementById('profile-content').querySelector('.flex-1').appendChild(profileBadgesContainer);
        } else {
            // If it exists, just clear the badges
            document.getElementById('profile-badges').innerHTML = '';
        }

        Object.entries(this.badgeDefinitions).forEach(([badgeKey, badgeDef]) => {
            const categoryDiv = document.createElement('div');
            categoryDiv.className = 'mb-8 p-4 bg-gray-50 rounded-lg';

            const categoryTitle = document.createElement('h3');
            categoryTitle.className = 'text-xl font-semibold mb-2';
            categoryTitle.textContent = badgeDef.name;

            const categoryDescription = document.createElement('p');
            categoryDescription.className = 'text-gray-600 mb-4';
            categoryDescription.textContent = badgeDef.description;

            const badgesDiv = document.createElement('div');
            badgesDiv.className = 'flex flex-wrap gap-6';

            badgeDef.levels.forEach(level => {
                const badgeId = `${badgeKey}-${level.threshold}`;
                const isUnlocked = this.userBadges && this.userBadges[badgeId];

                const badgeElement = document.createElement('div');
                badgeElement.className = 'flex flex-col items-center w-24';
                badgeElement.dataset.badgeId = badgeId;

                const badgeImageContainer = document.createElement('div');
                badgeImageContainer.className = 'relative w-20 h-20';

                const img = document.createElement('img');
                img.src = `/badges/${level.image}`;
                img.alt = level.name;
                img.className = 'w-full h-full object-contain';

                if (!isUnlocked) {
                    img.classList.add('opacity-50', 'grayscale');

                    const lockIcon = document.createElement('div');
                    lockIcon.className = 'absolute inset-0 flex items-center justify-center text-2xl';
                    lockIcon.innerHTML = '🔒';
                    badgeImageContainer.appendChild(lockIcon);
                } else {
                    // Add to main profile page if unlocked
                    const profileBadgeContainer = document.createElement('div');
                    profileBadgeContainer.className = 'flex flex-col items-center w-16';

                    const profileBadge = img.cloneNode();
                    profileBadge.className = 'w-12 h-12 object-contain';

                    const profileBadgeName = document.createElement('span');
                    profileBadgeName.className = 'text-xs text-center mt-1';
                    profileBadgeName.textContent = level.name;

                    profileBadgeContainer.appendChild(profileBadge);
                    profileBadgeContainer.appendChild(profileBadgeName);
                    document.getElementById('profile-badges').appendChild(profileBadgeContainer);
                }

                const badgeName = document.createElement('span');
                badgeName.className = 'text-sm text-center mt-2';
                badgeName.textContent = level.name;

                badgeImageContainer.appendChild(img);
                badgeElement.appendChild(badgeImageContainer);
                badgeElement.appendChild(badgeName);

                // Add tooltip with description
                const tooltipContent = `<strong>${level.name}</strong><br>${badgeDef.description}<br>Requirement: ${badgeDef.attribute} ≥ ${level.threshold}`;
                badgeElement.setAttribute('data-tooltip', tooltipContent);
                tippy(badgeElement, {
                    content: badgeElement.getAttribute('data-tooltip'),
                    placement: 'top',
                    theme: 'material',
                    allowHTML: true
                });

                badgesDiv.appendChild(badgeElement);
            });

            categoryDiv.appendChild(categoryTitle);
            categoryDiv.appendChild(categoryDescription);
            categoryDiv.appendChild(badgesDiv);
            container.appendChild(categoryDiv);
        });
    }

    startMonitoring() {
        // In a real app, you would set up WebSocket or polling to monitor for badge achievements
        // For this example, we'll simulate with setTimeout

        // Check every 30 seconds
        setInterval(() => this.checkForNewBadges(), 30000);
        this.checkForNewBadges(); // Initial check
    }

    async checkForNewBadges() {
        try {
            const token = localStorage.getItem('token');
            const userId = localStorage.getItem('userId');

            const response = await fetch(`http://localhost:8080/api/users/${userId}/check-badges`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    currentBadges: this.userBadges
                })
            });

            if (response.ok) {
                const newBadges = await response.json();
                if (newBadges && newBadges.length > 0) {
                    newBadges.forEach(badgeId => {
                        this.userBadges[badgeId] = true;
                        this.showBadgeNotification(badgeId);
                    });
                    this.renderBadges();
                }
            }
        } catch (error) {
            console.error('Error checking for new badges:', error);
        }
    }

    showBadgeNotification(badgeId) {
        const badgeDef = this.findBadgeDefinition(badgeId);
        if (!badgeDef) return;

        this.notificationQueue.push(badgeDef);
        this.processNotificationQueue();
    }

    processNotificationQueue() {
        if (this.isShowingNotification || this.notificationQueue.length === 0) return;

        this.isShowingNotification = true;
        const badgeDef = this.notificationQueue.shift();

        // Create notification element
        const notification = document.createElement('div');
        notification.className = 'fixed top-4 right-4 z-50 p-4 bg-white rounded-lg shadow-lg border-l-4 border-blue-500 flex items-center animate-fade-in';

        notification.innerHTML = `
            <img src="/badges/${badgeDef.image}" alt="${badgeDef.name}" class="w-12 h-12 mr-3">
            <div>
                <h4 class="font-bold">New Badge Unlocked!</h4>
                <p>${badgeDef.name}</p>
            </div>
            <button class="ml-4 text-gray-500 hover:text-gray-700" onclick="this.parentElement.remove()">
                &times;
            </button>
        `;

        document.body.appendChild(notification);

        // Auto-remove after 5 seconds
        setTimeout(() => {
            notification.remove();
            this.isShowingNotification = false;
            this.processNotificationQueue();
        }, 5000);
    }

    findBadgeDefinition(badgeId) {
        const [badgeKey, threshold] = badgeId.split('-');
        if (!this.badgeDefinitions[badgeKey]) return null;

        const level = this.badgeDefinitions[badgeKey].levels.find(l => l.threshold == threshold);
        if (!level) return null;

        return {
            id: badgeId,
            name: level.name,
            image: level.image,
            category: this.badgeDefinitions[badgeKey].name
        };
    }
}

// Add this CSS for the notification animation
const style = document.createElement('style');
style.textContent = `
    @keyframes fade-in {
        from { opacity: 0; transform: translateY(-20px); }
        to { opacity: 1; transform: translateY(0); }
    }
    .animate-fade-in {
        animation: fade-in 0.3s ease-out;
    }
`;
document.head.appendChild(style);

// Initialize the badge manager when the page loads
document.addEventListener('DOMContentLoaded', () => {
    // ... existing DOMContentLoaded code ...

    // Initialize badge manager if we're on the badges page
    if (window.location.pathname.includes('/profile')) {
        window.badgeManager = new BadgeManager();
        badgeManager.initialize();
    }
});

// Logout functionality
document.addEventListener('DOMContentLoaded', () => {
    const logoutLink = document.getElementById('logoutLink');

    if (logoutLink) {
        logoutLink.addEventListener('click', async (e) => {
            e.preventDefault();
            const confirmLogout = confirm("Are you sure you want to log out?");
            if (!confirmLogout) return;

            try {
                const token = localStorage.getItem('token');
                const response = await fetch('/logout', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    credentials: 'include'
                });

                if (response.ok) {
                    localStorage.removeItem('userId');
                    localStorage.removeItem('userEmail');
                    localStorage.removeItem('userName');
                    localStorage.removeItem('current_user_type');
                    localStorage.removeItem('token');
                    localStorage.removeItem('lastClaimDate')
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