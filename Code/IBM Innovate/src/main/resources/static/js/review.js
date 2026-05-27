// List of inappropriate words
const badWords = ["ass", "crap", "bitch", "damn", "shit", "fuck", "fucking", "retard", "retarded", "pussy","bastard","motherfucker","asshole","dumb","stupid", "cunt"];

// Regular expression for bad words
function generateBadWordsRegex(words) {
    const pattern = words.map(word => `\\b${word}\\b`).join('|');
    return new RegExp(pattern, 'i');
}

// Function to check if the review contains bad words
function containsBadWords(reviewText) {
    const regex = generateBadWordsRegex(badWords);
    return regex.test(reviewText);
}

// Function to render stars
function renderStars(rating) {
    let stars = '';
    for (let i = 1; i <= 5; i++) {
        stars += i <= rating ? '<span class="star gold">&#9733;</span>' : '<span class="star">&#9733;</span>';
    }
    return stars;
}

// Function to get the current username from the backend
function getCurrentUsername() {
    return fetch('/api/reviews/current-username')
        .then(response => response.text()) // Expecting plain text (username)
        .catch(error => {
            console.error("Error fetching username:", error);
            return "Guest"; // Fallback to "Guest" if there's an issue
        });
}

// Function to display reviews
function displayReviews(reviews) {
    const reviewsSection = document.getElementById('reviewsSection');
    const reviewsList = reviewsSection.querySelector('ul');
    reviewsList.innerHTML = ''; // Clear existing reviews

    reviews.forEach(review => {
        const reviewDiv = document.createElement('li');
        reviewDiv.classList.add('review');
        reviewDiv.innerHTML = `
            <p><strong>${review.username}</strong>: ${renderStars(review.rating)}</p>
            <p><strong>Course</strong>: ${review.courseName}</p>
            <p>${review.text}</p>
        `;
        reviewsList.appendChild(reviewDiv);
    });
}

// Fetch stored reviews when the page loads
document.addEventListener('DOMContentLoaded', function () {
    fetch('/api/reviews')
        .then(response => response.json())
        .then(data => displayReviews(data))
        .catch(error => console.error("Error fetching reviews:", error));
});

// Handle star rating click
document.querySelectorAll('#rating .star').forEach(star => {
    star.addEventListener('click', function () {
        const selectedRating = parseInt(this.getAttribute('data-value'));

        // Update the visual display of stars
        document.querySelectorAll('#rating .star').forEach(star => {
            if (parseInt(star.getAttribute('data-value')) <= selectedRating) {
                star.classList.add('gold'); // Add gold class for selected stars
            } else {
                star.classList.remove('gold'); // Remove gold class for unselected stars
            }
        });

        // Store the selected rating
        document.querySelector('#rating').setAttribute('data-selected-rating', selectedRating);
    });
});

// Handle form submission
document.getElementById('reviewFormSubmit').addEventListener('submit', function (event) {
    event.preventDefault(); // Prevent page reload

    const reviewText = document.getElementById('review').value.trim();
    const rating = document.querySelector('#rating').getAttribute('data-selected-rating'); // Get the selected rating
    const courseNumber = document.getElementById('courseNumber').value; // Get the selected course number
    const courseName = document.getElementById('courseNumber').selectedOptions[0].text; // Get the selected course name

    if (!rating) {
        alert("Please select a rating.");
        return;
    }

    if (reviewText === "") {
        alert("Please write a review before submitting.");
        return;
    }

    if (containsBadWords(reviewText)) {
        alert("Your review contains inappropriate language. Please revise your review.");
        return;
    }

    const reviewData = {
        username: localStorage.getItem("userName"), // username that is fetched from  backend
        text: reviewText,
        rating: parseInt(rating),
        courseName: courseName // Include course name in review data
    };

    // Send review to the backend
    fetch('/api/reviews', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(reviewData)
    })
        .then(response => response.text())
        .then(() => {
            // Reload reviews after adding a new one
            return fetch('/api/reviews');
        })
        .then(response => response.json())
        .then(data => displayReviews(data))
        .catch(error => console.error("Error submitting review:", error));

    // Clear form fields
    document.getElementById('review').value = '';
    document.querySelectorAll('#rating .star').forEach(star => star.classList.remove('gold'));
});
