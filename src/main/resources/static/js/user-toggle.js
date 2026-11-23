/*
 * User toggle: "pretend to log in" by picking a user from the header dropdown.
 * - Loads all users from GET /api/users
 * - Saves the picked user's id in localStorage so it survives page refreshes
 * - Other scripts can read the current user with Amazin.getCurrentUserId()
 */
(function () {
    const STORAGE_KEY = 'amazin.currentUserId';
    const select = document.getElementById('user-select');
    if (!select) return;

    function getSavedUserId() {
        try { return localStorage.getItem(STORAGE_KEY); } catch (e) { return null; }
    }

    function saveUserId(userId) {
        try {
            if (userId) localStorage.setItem(STORAGE_KEY, userId);
            else localStorage.removeItem(STORAGE_KEY);
        } catch (e) { /* storage blocked (e.g. private mode): toggle still works for this page */ }
    }

    function label(user) {
        return user.role === 'ROLE_ADMIN' ? user.firstName + ' (Admin)' : user.firstName;
    }

    fetch('/api/users')
        .then(function (response) {
            if (!response.ok) throw new Error('HTTP ' + response.status);
            return response.json();
        })
        .then(function (users) {
            users.forEach(function (user) {
                const option = document.createElement('option');
                option.value = user.id;
                option.textContent = label(user);
                select.appendChild(option);
            });

            // Restore the saved user, but only if they still exist
            const savedId = getSavedUserId();
            if (savedId && users.some(function (u) { return u.id === savedId; })) {
                select.value = savedId;
            } else if (savedId) {
                saveUserId(null);
            }
        })
        .catch(function (error) {
            console.error('Could not load users for the user toggle:', error);
        });

    select.addEventListener('change', function () {
        saveUserId(select.value || null);
        window.dispatchEvent(new CustomEvent('amazin:userchange', { detail: { userId: select.value || null } }));
    });

    window.Amazin = window.Amazin || {};
    window.Amazin.getCurrentUserId = getSavedUserId;
})();
