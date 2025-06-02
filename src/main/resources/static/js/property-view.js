let images = [];
let currentIndex = 0;

document.addEventListener("DOMContentLoaded", () => {
    images = Array.from(document.querySelectorAll("img[onclick^='openModal']"))
        .map(img => img.src);
});

function openModal(index) {
    currentIndex = index;
    document.getElementById("modalImage").src = images[currentIndex];
    document.getElementById("imageModal").style.display = "flex";
}

function closeModal() {
    document.getElementById("imageModal").style.display = "none";
}

function prevImage() {
    currentIndex = (currentIndex - 1 + images.length) % images.length;
    document.getElementById("modalImage").src = images[currentIndex];
}

function nextImage() {
    currentIndex = (currentIndex + 1) % images.length;
    document.getElementById("modalImage").src = images[currentIndex];
}
