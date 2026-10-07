import puppeteer from 'puppeteer';

(async () => {
  console.log('Starting puppeteer...');
  const browser = await puppeteer.launch({ headless: 'new' });
  const page = await browser.newPage();
  
  page.on('console', msg => {
    if (msg.type() === 'error') {
      console.log('BROWSER_ERROR:', msg.text());
    }
  });
  page.on('pageerror', error => console.log('PAGE_ERROR:', error.message));
  page.on('requestfailed', request => {
    console.log('NETWORK_ERROR:', request.url(), request.failure()?.errorText);
  });

  try {
    console.log('Navigating to http://127.0.0.1:5173/');
    await page.goto('http://127.0.0.1:5173/', { waitUntil: 'domcontentloaded' });
    await new Promise(r => setTimeout(r, 2000));
    console.log('Done waiting.');
  } catch (e) {
    console.error('Test failed:', e);
  } finally {
    await browser.close();
  }
})();
