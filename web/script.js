const hints = [
    "Take a deep breath.",
    "Drink some water.",
    "Stretch your body.",
    "Smile at someone today.",
    "Write down one thing you are grateful for.",
    "Focus on the present moment.",
    "Be kind to yourself.",
    "Take a short walk.",
    "Listen to your favorite song.",
    "Read a page of a book."
];

function updateHint() {
    const hintElement = document.getElementById('hint-text');
    const randomIndex = Math.floor(Math.random() * hints.length);
    hintElement.textContent = hints[randomIndex];
}

document.getElementById('widget-container').addEventListener('click', updateHint);

// Initialize with a random hint
updateHint();
