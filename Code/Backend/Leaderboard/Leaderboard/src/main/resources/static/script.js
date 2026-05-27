document.addEventListener("DOMContentLoaded", () => {
    fetch("/api/leaderboard/users")  // Relative URL (works inside Spring Boot)
        .then(response => response.json())
        .then(data => {
            const leaderboard = document.getElementById("leaderboard");
            leaderboard.innerHTML = ""; // Clear existing data

            // Sort users by EXP in descending order
            data.sort((a, b) => b.exp - a.exp);

            data.forEach((user, index) => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${index + 1}</td>
                    <td>${user.username}</td>
                    <td>${user.exp}</td>
                `;
                leaderboard.appendChild(row);
            });
        })
        .catch(error => console.error("Error fetching leaderboard:", error));
});