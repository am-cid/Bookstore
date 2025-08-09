/**
 * Toggles the visibility of a list of items and the "show more/less" buttons.
 * @param {string} listId - ID of parent container holding the list items
 * @param {string} moreButtonClass - class of the show more button
 * @param {string} lessButtonClass - class of the show less button
 */
function toggleVisibility(listId, moreButtonClass, lessButtonClass) {
    const listContainer = document.getElementById(listId);
    if (!listContainer) {
        console.error(`List container with ID "${listId}" not found.`);
        return;
    }
    const itemsToToggle = listContainer.querySelectorAll('.more-item');
    console.log("itemsToToggle: ", itemsToToggle);
    const readMoreButton = listContainer.querySelector(`.${moreButtonClass}`);
    const readLessButton = listContainer.querySelector(`.${lessButtonClass}`);
    if (!readMoreButton || !readLessButton) {
        console.error('Read more or read less buttons not found.');
        return;
    }
    const isMoreButtonVisible = readMoreButton.style.display !== 'none';
    if (isMoreButtonVisible) {
        itemsToToggle.forEach(item =>{
            item.style.display = 'block'
        });
        readMoreButton.style.display = 'none';
        readLessButton.style.display = 'block';
    } else {
        itemsToToggle.forEach(item =>{
            item.style.display = 'none'
        });
        readMoreButton.style.display = 'block';
        readLessButton.style.display = 'none';
    }
}