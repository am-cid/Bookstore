/**
 * Sync field A's value to input B
 * @param {string} watchFieldId - id of field to watch changes of
 * @param {string} syncFieldId - id of field to sync changes to
 */
function syncInputFields(watchFieldId, syncFieldId) {
    const watchField = document.getElementById(watchFieldId);
    if (!watchField) {
        console.error(`Input field with ID "${watchFieldId}" not found.`);
        return;
    }
    const syncField = document.getElementById(syncFieldId);
    if (!watchField) {
        console.error(`Input field with ID "${syncFieldId}" not found.`);
        return;
    }
    watchField.addEventListener('input', function () {
        syncField.value = watchField.value;
    });
}

syncInputFields('username', 'displayName');