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

const themes = [
    { name: "Classic Dark", textColor: "#FFFFFF", bgColor: "rgba(0, 0, 0, 0.5)", borderColor: "rgba(255, 255, 255, 0.8)" },
    { name: "Classic Light", textColor: "#000000", bgColor: "rgba(255, 255, 255, 0.8)", borderColor: "rgba(0, 0, 0, 0.5)" },
    { name: "High Contrast", textColor: "#FFFF00", bgColor: "#000000", borderColor: "#FFFF00" },
    { name: "Soft Sepia", textColor: "#5F4B32", bgColor: "#F4ECD8", borderColor: "#5F4B32" },
    { name: "Ocean", textColor: "#FFFFFF", bgColor: "#0077BE", borderColor: "#FFFFFF" },
    { name: "Forest", textColor: "#FFFFFF", bgColor: "#228B22", borderColor: "#FFFFFF" },
    { name: "Retro", textColor: "#39FF14", bgColor: "#000000", borderColor: "#39FF14" },
    { name: "Vaporwave", textColor: "#FF71CE", bgColor: "#01CDFE", borderColor: "#B967FF" },
    { name: "Zen", textColor: "#4A4A4A", bgColor: "#ECECEC", borderColor: "#4A4A4A" }
];

let currentSettings = {
    themeIndex: 0,
    fontFamily: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif",
    fontSize: 1.4,
    borderWidth: "2px",
    bgPattern: "none"
};

function updateHint() {
    const hintElement = document.getElementById('hint-text');
    const randomIndex = Math.floor(Math.random() * hints.length);
    hintElement.textContent = hints[randomIndex];
}

function applySettings() {
    const widget = document.getElementById('widget-container');
    const hintText = document.getElementById('hint-text');
    const theme = themes[currentSettings.themeIndex];

    widget.style.backgroundColor = theme.bgColor;
    widget.style.borderColor = theme.borderColor;
    widget.style.borderWidth = currentSettings.borderWidth;

    hintText.style.color = theme.textColor;
    hintText.style.fontFamily = currentSettings.fontFamily;
    hintText.style.fontSize = `${currentSettings.fontSize}rem`;

    // Apply background pattern
    widget.className = 'widget-card';
    if (currentSettings.bgPattern !== 'none') {
        widget.classList.add(`pattern-${currentSettings.bgPattern}`);
        widget.style.color = theme.textColor; // For currentColor in CSS
    }

    localStorage.setItem('widgtit_settings', JSON.stringify(currentSettings));
}

function loadSettings() {
    const saved = localStorage.getItem('widgtit_settings');
    if (saved) {
        currentSettings = JSON.parse(saved);
    }

    // Sync UI
    document.getElementById('theme-select').value = currentSettings.themeIndex;
    document.getElementById('font-select').value = currentSettings.fontFamily;
    document.getElementById('font-size-range').value = currentSettings.fontSize;
    document.getElementById('border-width-select').value = currentSettings.borderWidth;
    document.getElementById('bg-pattern-select').value = currentSettings.bgPattern;

    applySettings();
}

// Populate Themes
const themeSelect = document.getElementById('theme-select');
themes.forEach((theme, index) => {
    const option = document.createElement('option');
    option.value = index;
    option.textContent = theme.name;
    themeSelect.appendChild(option);
});

// Event Listeners
document.getElementById('widget-container').addEventListener('click', updateHint);

document.getElementById('settings-toggle').addEventListener('click', () => {
    document.getElementById('settings-panel').classList.remove('hidden');
});

document.getElementById('close-settings').addEventListener('click', () => {
    document.getElementById('settings-panel').classList.add('hidden');
});

themeSelect.addEventListener('change', (e) => {
    currentSettings.themeIndex = parseInt(e.target.value);
    applySettings();
});

document.getElementById('font-select').addEventListener('change', (e) => {
    currentSettings.fontFamily = e.target.value;
    applySettings();
});

document.getElementById('font-size-range').addEventListener('input', (e) => {
    currentSettings.fontSize = parseFloat(e.target.value);
    applySettings();
});

document.getElementById('border-width-select').addEventListener('change', (e) => {
    currentSettings.borderWidth = e.target.value;
    applySettings();
});

document.getElementById('bg-pattern-select').addEventListener('change', (e) => {
    currentSettings.bgPattern = e.target.value;
    applySettings();
});

// Initialize
loadSettings();
updateHint();
