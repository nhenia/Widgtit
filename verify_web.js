const { chromium } = require('playwright');
const path = require('path');

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage();
  const filePath = 'file://' + path.resolve('web/index.html');
  await page.goto(filePath);

  // Check initial state
  await page.screenshot({ path: 'initial_web.png' });

  // Open settings
  await page.click('#settings-toggle');
  await page.waitForSelector('#settings-panel:not(.hidden)');

  // Change theme to Retro (index 6)
  await page.selectOption('#theme-select', '6');

  // Change font size
  await page.fill('#font-size-range', '2.5');

  // Change border width to Large
  await page.selectOption('#border-width-select', '8px');

  // Change pattern to Stars
  await page.selectOption('#bg-pattern-select', 'stars');

  await page.screenshot({ path: 'customized_web.png' });

  await browser.close();
})();
