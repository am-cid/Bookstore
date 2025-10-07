/**
 * Sync field A's color value to css var's color value
 * @param {string} watchFieldId - id of field to watch changes of
 * @param {string} syncCssVar - css variable name of to sync changes to
 * @param {string} styleTargetId - id of the element where the CSS variable should be set
 *                                 (aka where the changes should be contained under)
 */
function syncInputFieldWithCssVar(watchFieldId, syncCssVar, styleTargetId) {
    const watchField = document.getElementById(watchFieldId);
    const styleTarget = document.getElementById(styleTargetId);
    if (!watchField) {
        console.error(`Input field with ID "${watchFieldId}" not found.`);
        return;
    }
    if (!styleTarget) {
        console.error(`Style target element with ID "${styleTargetId}" not found.`);
        return;
    }

    watchField.addEventListener('input', function () {
        styleTarget.style.setProperty(syncCssVar, watchField.value);
    });
}

for (let i = 0; i < 8; i++) {
    syncInputFieldWithCssVar('base0' + i, '--color-0' + i, 'theme-preview-render');
}