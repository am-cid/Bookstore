(function () {
    const sliderRoot = document.getElementById('sliding-banner');
    if (!sliderRoot) return;

    const slidesWrap = document.querySelector('#sliding-banner-images ul');

    const thumbs = Array.from(document.querySelectorAll('#sliding-banner-thumbs li'));
    const realSlidesCount = slidesWrap.children.length; // need because of clones
    if (realSlidesCount === 0) {
        return;
    } else if (realSlidesCount <= 1) {
        thumbs.forEach(t => t.classList.add('active'));
        return;
    }

    // clone for seamless loop
    const first = slidesWrap.children[0].cloneNode(true);
    const last  = slidesWrap.children[realSlidesCount - 1].cloneNode(true);
    slidesWrap.appendChild(first);
    slidesWrap.insertBefore(last, slidesWrap.firstChild);

    let slideWidth = slidesWrap.querySelector('li').getBoundingClientRect().width;
    let index = 1; // because last clone is prepended, real first is at 1
    let autoId = null;
    const transitionTime = 500; // ms

    function setTransform(noTransition = false) {
        if (noTransition) {
            slidesWrap.style.transition = 'none';
        } else {
            slidesWrap.style.transition = 'transform 0.5s ease';
        }
        slidesWrap.style.transform = `translateX(-${index * slideWidth}px)`;
        // re-enable transition after a frame if we removed it
        if (noTransition) {
            requestAnimationFrame(() => requestAnimationFrame(() => {
                slidesWrap.style.transition = 'transform 0.5s ease';
            }));
        }
    }

    function updateThumbs() {
        const activeIdx = ((index - 1) % realSlidesCount + realSlidesCount) % realSlidesCount; // 0-based
        thumbs.forEach((thumb, i) => thumb.classList.toggle('active', i === activeIdx));
    }

    function moveNext() {
        index++;
        updateThumbs();
        setTransform(false);
    }
    function movePrev() {
        index--;
        updateThumbs();
        setTransform(false);
    }

    // on transition end, if we're at a clone jump to corresponding real slide (no transition).
    // I do this because if I don't have a clone, the jump would be too big going from first
    // to last and vice versa.
    slidesWrap.addEventListener('transitionend', () => {
        if (index === realSlidesCount + 1) {
            // if moved to clone at the end (index === realSlidesCount + 1) -> jump to 1
            index = 1;
            setTransform(true);
            updateThumbs();
        } else if (index === 0) {
            // if moved to clone at the front (index === 0) -> jump to real last (realSlidesCount)
            index = realSlidesCount;
            setTransform(true);
            updateThumbs();
        }
    });

    // thumb clicks
    thumbs.forEach((thumb, i) => {
        thumb.addEventListener('click', () => {
            index = i + 1; // thumbs are 0..N-1, real slides 1..N
            setTransform(false);
            updateThumbs();
            resetAuto();
        });
    });

    // responsive: recalc width on resize and reposition without transition
    function recalc() {
        slideWidth = slidesWrap.querySelector('li').getBoundingClientRect().width;
        setTransform(true);
    }
    window.addEventListener('resize', recalc);

    // auto slide
    function startAuto() {
        stopAuto();
        autoId = setInterval(() => { moveNext(); }, 3000);
    }
    function stopAuto() {
        if (autoId) { clearInterval(autoId); autoId = null; }
    }
    function resetAuto() { stopAuto(); startAuto(); }

    // pause on hover/focus
    sliderRoot.addEventListener('mouseenter', stopAuto);
    sliderRoot.addEventListener('mouseleave', startAuto);

    // init position
    setTransform(true);
    updateThumbs();
    startAuto();

    // keyboard controls (left/right)
    sliderRoot.addEventListener('keydown', (e) => {
        if (e.key === 'ArrowLeft') { movePrev(); resetAuto(); }
        if (e.key === 'ArrowRight') { moveNext(); resetAuto(); }
    });

    // make container focusable for keyboard
    sliderRoot.tabIndex = 0;
})();
