document.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch('/api/leaderboard/users');

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const data = await response.json();

        const tableBody = document.getElementById('leaderboard-body');
        tableBody.innerHTML = data.map((user, index) => `
            <tr>
                <td class="border border-gray-300 px-6 py-2">${index + 1}</td>
                <td class="border border-gray-300 px-6 py-2">${user.username}</td>
                <td class="border border-gray-300 px-6 py-2">${user.exp}</td>
            </tr>
        `).join('');
    } catch (error) {
        console.error("Leaderboard error:", error);
        document.getElementById('leaderboard-body').innerHTML = `
            <tr><td colspan="3" class="text-red-500">Error: ${error.message}</td></tr>
        `;
    }
});