/**
 * toggles between book covers
 * @param {HTMLImageElement} imageElement - image element to toggle book covers of
 */
function toggleCover(imageElement) {
    const currentSrc = imageElement.src;
    const frontSrc = imageElement.dataset.frontImage;
    const backSrc = imageElement.dataset.backImage;
    const currentCover = imageElement.dataset.currentCover;
    const placeholderText = 'https://placehold.co/227x317?text='
        + (imageElement.dataset.title == null
            ? 'Book ' + imageElement.dataset.id
            : imageElement.dataset.title + '\\n' + imageElement.dataset.author);

    if (currentCover === 'front') {
        imageElement.src = backSrc == null
            ? placeholderText + '\\n(Back Cover)'
            : backSrc;
        imageElement.dataset.currentCover = 'back';
    } else if (currentCover === 'back') {
        imageElement.src = frontSrc == null
            ? placeholderText + '\\n(Front Cover)'
            : frontSrc;
        imageElement.dataset.currentCover = 'front';
    } else {
        throw new DOMException('Illegal cover: "' + currentSrc + '". Must either be "front" or "back"')
    }
}