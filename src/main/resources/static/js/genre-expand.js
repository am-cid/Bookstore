(function () {
    document.querySelectorAll(".list-item-details ul").forEach(ul => {
        const items = Array.from(ul.querySelectorAll("li"));
        const cutoff = ul.dataset.cutoff == null
            ? 6
            : parseInt(ul.dataset.cutoff, 10);
        if (items.length > cutoff) {
            const toggleLi = document.createElement("li");
            toggleLi.textContent = "More...";
            toggleLi.classList.add("toggle-genre");
            toggleLi.classList.add("more-less-link");
            toggleLi.classList.add("genre-oval");
            toggleLi.classList.add("genre-color-style-00");
            ul.appendChild(toggleLi);
            toggleLi.addEventListener("click", () => {
                const isExpanded = toggleLi.textContent === "Less...";
                if (isExpanded) {
                    // collapse
                    items.slice(cutoff).forEach(li => li.classList.add("hidden-genre"));
                    toggleLi.textContent = "More...";
                } else {
                    // expand
                    items.slice(cutoff).forEach(li => li.classList.remove("hidden-genre"));
                    toggleLi.textContent = "Less...";
                }
            });
        }
    });
})();