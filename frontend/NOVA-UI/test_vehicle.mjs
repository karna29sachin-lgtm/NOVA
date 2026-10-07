import puppeteer from 'puppeteer';

(async () => {
  const browser = await puppeteer.launch({ headless: 'new' });
  const page = await browser.newPage();
  
  page.on('console', msg => {
    if (msg.type() === 'error') {
      console.log('BROWSER_ERROR:', msg.text());
    }
  });

  try {
    await page.goto('http://127.0.0.1:5173/driver/vehicle', { waitUntil: 'domcontentloaded' });
    await new Promise(r => setTimeout(r, 3000));
  } catch (e) {
    console.error('Test failed:', e);
  } finally {
    await browser.close();
  }
})();
