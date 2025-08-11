(function () {
    document.querySelectorAll(".book-list-item-details ul").forEach(ul => {
        const items = Array.from(ul.querySelectorAll("li"));
        if (items.length > 9) {
            const toggleLi = document.createElement("li");
            toggleLi.textContent = "More...";
            toggleLi.classList.add("toggle-genre");
            toggleLi.classList.add("more-less-link");
            ul.appendChild(toggleLi);
            toggleLi.addEventListener("click", () => {
                const isExpanded = toggleLi.textContent === "Less...";
                if (isExpanded) {
                    // collapse
                    items.slice(8).forEach(li => li.classList.add("hidden-genre"));
                    toggleLi.textContent = "More...";
                } else {
                    // expand
                    items.slice(8).forEach(li => li.classList.remove("hidden-genre"));
                    toggleLi.textContent = "Less...";
                }
            });
        }
    });
})();